package mexica.story;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.CharacterName;
import mexica.MexicaParameters;
import mexica.MexicaRepository;
import mexica.core.Action;
import mexica.core.CompositeAction;
import mexica.core.Position;
import mexica.reflection.Guideline;
import mexica.reflection.Guidelines;
import mexica.story.filter.StoryFilterException;
import mexica.story.log.StoryLog;
import mexica.tools.AvatarFactory;
import mexica.tools.InvalidCharacterException;
import slant.SlantGuidelines;

/**
 * Class to store all the data related to a story
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class Story extends StoryContextListener {
    /** Character factory */
    private AvatarFactory avatarFactory;
    /** Contains activities performed when an action is added or removed from the story */
    private StoryHelper helper;
    /** Contains all the information of the story */
    private StoryDAO storyData;
    /** Guidelines' manager */
    private Guidelines guidelines;
    /** Repository of the elements employed for the current story */
    private ElementsEmployed repository;
    /** Metadata of the current story */
    private StoryMeta storyMeta;
    /** Object to store all the story generation information */
    private StoryGenerationLog storyLog;
    
    /**
     * Initializes the arrays for actions, characters, missing conditions and story text
     */
    public Story() {
        storyLog = new StoryGenerationLog();
        avatarFactory = new AvatarFactory(this);
        storyData = new StoryDAO();
        storyMeta = new StoryMeta();
        helper = new StoryHelper(this);
        guidelines = (MexicaParameters.ENABLE_SLANT_MODE) ? new SlantGuidelines(new Guidelines(this)) : new Guidelines(this);
        //TEST
        guidelines.addGuideline(mexica.reflection.Guideline.tendencyNeutral);
        repository = new ElementsEmployed();
        addStoryContextListener(repository, IStoryContextListener.ListType.Actions);
    }
    
    /**
     * Resets all the values and notifies to every listener
     * @param notify If TRUE triggers the listeners for actions, characters, conditions, story, facts and tensions
     * @param cleanActions If TRUE removes all the actions from the actions list
     */
    public void restart(boolean notify, boolean cleanActions) {
        storyData.restart(cleanActions);
        avatarFactory.restart();
        if (cleanActions)
            repository.restart();
        MexicaRepository.getInstance().getStoryAnalyzer().restart();
        
        if (notify) {
            notifyListeners(IStoryContextListener.ListType.Tensions);
            notifyListeners(IStoryContextListener.ListType.Actions);
            notifyListeners(IStoryContextListener.ListType.Characters);
            notifyListeners(IStoryContextListener.ListType.Conditions);
            notifyListeners(IStoryContextListener.ListType.Story);
            notifyListeners(IStoryContextListener.ListType.Facts);
            notifyListeners(IStoryContextListener.ListType.StoryUpdated);
        }
    }
    
    /**
     * Adds a new action to the story and instantiates it before the previous action
     * NOTE: When an action is added in previous stages of the story all the character contexts
     *  are recalculated
     * @param followingAction The action with the missing condition
     * @param action The action selected to fulfill the missing condition
     * @param avatars The characters to instantiate the new action
     */
    public ActionInstantiated addAction(ActionInstantiated followingAction, Action action, CharacterName... avatars) 
            throws InvalidCharacterException, DeadAvatarException, StoryFilterException {
        ActionInstantiated newAction = null;
        
        //Stores the current list of actions. In case an error occurs, the actions are restablished
        List<ActionInstantiated> actionsBackup = new ArrayList<>();
        for (ActionInstantiated ins : storyData.getActions()) {
            actionsBackup.add(ins);
        }
        
        try {
            //Assigns the avatar factory in case the action is composite
            if (action instanceof CompositeAction) {
                CompositeAction composite = (CompositeAction)action;
                composite.setAvatarFactory(avatarFactory);
                newAction = new ActionInstantiated(composite, avatars);
            }
            else {
                newAction = new ActionInstantiated(action, avatars);
            }
            
            newAction.setInsertionMode(InsertionMode.Reflection);
            newAction.setNewCharacterPosition(getAvatarFactory());
            
            int index = storyData.getActions().indexOf(followingAction);
            storyData.addAction(index, newAction);
            restart(false, false);
            helper.applyActions();
            repository.addAction(newAction);
            notifyListeners(IStoryContextListener.ListType.Conditions);
            notifyListeners(IStoryContextListener.ListType.Characters);
        } catch (DeadAvatarException | StoryFilterException ex) {
            storyData.setActions(actionsBackup);
            restart(false, false);
            helper.applyActions();
            throw ex;
        } catch (InvalidCharacterException ice) {
            storyData.setActions(actionsBackup);
            restart(false, false);
            helper.applyActions();
            Action act = MexicaRepository.getInstance().getActions().getChangePositionAction();
            //Since the inserted action, originates characters in different locations, 
            //retry the action and move the characters of the following action to the same location
            boolean storyExceptions = MexicaParameters.ENABLE_STORY_EXCEPTIONS;
            MexicaParameters.ENABLE_STORY_EXCEPTIONS = false;
            newAction = addAction(followingAction, action, avatars);
            MexicaParameters.ENABLE_STORY_EXCEPTIONS = storyExceptions;
            //If characters are not in the same location, add an action to locate them on the same place
            addAction(followingAction, act, followingAction.getCharactersList().toArray(new CharacterName[0]));
            StoryLog.addMessage(this, "(R) Action added to match characters' location: " + act + " " + followingAction);
            //Logger.getGlobal().log(Level.INFO, "Action added to match characters' location: {0}-{1}", new Object[]{act, followingAction});
        }

        notifyListeners(IStoryContextListener.ListType.StoryUpdated);
        notifyListeners(IStoryContextListener.ListType.Actions);
        //Logger.getGlobal().log(Level.INFO, "Action added in reflection: {0}", newAction);
        return newAction;
    }
    
    /**
     * Adds a new action to the story and instantiates it with the selected characters
     * Also the action is analyzed and missing conditions, facts and tensions are updated
     *  for every selected character
     * @param action Action to be added to the story
     * @param avatars Selected characters to perform the action
     */
    public ActionInstantiated addAction(Action action, CharacterName... avatars)
            throws InvalidCharacterException, DeadAvatarException, StoryFilterException {
        ActionInstantiated newAction;
        if (action instanceof CompositeAction) {
            CompositeAction composite = (CompositeAction)action;
            composite.setAvatarFactory(avatarFactory);
            newAction = new ActionInstantiated(composite, avatars);
        }
        else
            newAction = new ActionInstantiated(action, avatars);
        
        newAction.setInsertionMode((storyData.getActions().isEmpty()) ? InsertionMode.InitialAction : InsertionMode.Engagement);
        //Saves the current state of the avatars
        avatarFactory.saveToMemento();
        
        try {
            //If the action is realised, is not necessary to validate if the characters of the composite action are valid
            if (action.getActionName().equalsIgnoreCase("realised"))
                helper.validateCharactersAlive(avatars[0]);
            else
                helper.validateCharactersAlive(avatars);
            boolean multiplePositions = helper.validateMultiplePositions(newAction);
            try {
                avatarFactory.activateCharacters(avatars, multiplePositions, action.getActionName().equalsIgnoreCase("realised"));
            } catch (InvalidCharacterException icle) {
                helper.matchCharacterPositions(avatars);
                helper.updateTensionList();
            }

            helper.validateStoryFilters(newAction);
            storyData.addAction(newAction);
            helper.performAddAction(newAction, avatars);
            helper.addText(newAction, newAction.getTexts(), storyData.getStoryText().size());

            avatarFactory.validateAdditionalTensions();
        } catch (DeadAvatarException | InvalidCharacterException | StoryFilterException e) {
            //Restablish the avatars previous to the action application
            Logger.getGlobal().log(Level.INFO, e.getMessage());
            avatarFactory.restoreFromMemento();
            throw e; //TODO: Throw the exception but notifying that has been corrected, restoring the previous state
        }
        
        notifyListeners(IStoryContextListener.ListType.Conditions);
        notifyListeners(IStoryContextListener.ListType.Characters);
        
        
        repository.addAction(newAction);
        helper.updateTensionList();
        //Logger.getGlobal().log(Level.INFO, "Action added: {0}", newAction);
        
        //Notifies that the story has been updated
        notifyListeners(IStoryContextListener.ListType.StoryUpdated);
        
        return newAction;
    }
    
    /**
     * 
     * @param action Action to be removed from the story
     * @throws InvalidCharacterLocationsException
     * @throws DeadAvatarException
     */
    public void removeAction(ActionInstantiated action) 
            throws InvalidCharacterException, DeadAvatarException, StoryFilterException {
        List<ActionInstantiated> actionsBackup = new ArrayList<>();
        for (ActionInstantiated ins : storyData.getActions()) {
            actionsBackup.add(ins);
        }
        
        try {
            storyData.getActions().remove(action);
            restart(false, false);
            helper.applyActions();
            repository.removeAction(action);
            notifyListeners(IStoryContextListener.ListType.Conditions);
            notifyListeners(IStoryContextListener.ListType.Characters);
            Logger.getGlobal().log(Level.INFO, "Action removed {0}", action);
        } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException icle) {
            storyData.setActions(actionsBackup);
            restart(false, false);
            helper.applyActions();
            throw icle;
        }
        
        notifyListeners(IStoryContextListener.ListType.StoryUpdated);
    }
    
    /**
     * Removes an action from the story
     * @param index The index of the action to be removed
     * @throws InvalidCharacterLocationsException
     * @throws DeadAvatarException 
     */
    public void removeAction(int index) 
        throws InvalidCharacterException, DeadAvatarException, StoryFilterException {
        removeAction(storyData.getActions().get(index));
    }
            
    /**
     * Determines de default position where all the characters start
     * When a character inside an action is already active, the position of the
     *  new character employed inside the same action is the same position
     *  of the active character
     * @param defaultPosition 
     */
    public void setDefaultPosition(Position defaultPosition) {
        avatarFactory.setDefaultPosition(defaultPosition);
    }
    
    public List<ActionInstantiated> getActions() {
        return storyData.getActions();
    }
    
    public List<ConditionInstantiated> getMissingConditions() {
        return storyData.getMissingConditions();
    }
    
    public boolean hasMissingConditions() {
        return storyData.getMissingConditions().size() > 0;
    }
    
    public List<TextInstantiated> getStory() {
        return storyData.getStoryText();
    }
    
    public Position getDefaultPosition() {
        return avatarFactory.getDefaultPosition();
    }
    
    /**
     * Obtains a list with the active characers in the story
     * @return 
     */
    public List<Avatar> getCharacters() {
        return avatarFactory.getActiveCharacters();
    }

    /**
     * Obtains the year being analyzed in the story
     */
    public int getCurrentYear() {
        return storyData.getCurrentYear();
    }
    
    public void setCurrentYear(int year) {
        storyData.setCurrentYear(year);
    }
    
    /**
     * Establishes the current year to the year where the given action was added
     * @param action An action with unsatisfied preconditions
     */
    public void notifyPreviousAction(ActionInstantiated action) {
        int index = storyData.getActions().indexOf(action);
        storyData.setCurrentYear(index);    //The current year is set at the time before the action ocurred
    }

    public Guidelines getGuidelines() {
        return guidelines;
    }

    /**
     * @return the repository
     */
    public ElementsEmployed getElementsEmployed() {
        return repository;
    }
    
    public AvatarFactory getAvatarFactory() {
        return avatarFactory;
    }

    public StoryDAO getStoryData() {
        return storyData;
    }
    
    public void setStoryID(int storyID) {
        storyMeta.setStoryID(storyID);
    }
    
    public int getStoryID() {
        return storyMeta.getStoryID();
    }
    
    public void setFinished(boolean finished, String reason) {
        storyMeta.setFinished(finished);
        storyMeta.setReason(reason);
    }
    
    public boolean isFinished() {
        return storyMeta.isFinished();
    }
    
    /**
     * Obtains the reason why the story was finished
     * @return The reason
     */
    public String getFinishingReason() {
        return storyMeta.getReason();
    }
    
    public void setStoryName(String name) {
        storyMeta.setName(name);
    }
    
    public String getStoryName() {
        return storyMeta.getName();
    }
    
    @Override
    public String toString() {
        if (storyMeta.getName() == null) {
            return "Story " + storyMeta.getStoryID() + " (" + getDefaultPosition().name() + ")";
        }
        else {
            return storyMeta.getName();
        }
    }

    public void addIteration() {
        storyMeta.addIteration();
    }
    
    public int getIterations() {
        return storyMeta.getIteration();
    }

    public void setIteration(String iterationStr) {
        try {
            storyMeta.setIteration(Integer.parseInt(iterationStr));
        }catch (Exception ex) {
            storyMeta.setIteration(0);
        }
    }

    public void setImpasseDetected(boolean impasseDetected) {
        storyMeta.setImpasseDetected(impasseDetected);
        if (storyMeta.getImpasses() > MexicaParameters.MAX_IMPASSES)
            getGuidelines().addGuideline(Guideline.endStory, "Multiple impasses during the story");
    }
    
    public boolean getImpasseDetected() {
        return storyMeta.isImpasseDetected();
    }
    
    public void setImpasses(String impassesStr) {
        try {
            storyMeta.setImpasses(Integer.parseInt(impassesStr));
        } catch (Exception ex) {
            storyMeta.setImpasses(0);
        }
    }
    
    public int getImpasses() {
        return storyMeta.getImpasses();
    }
    
    public StoryGenerationLog getStoryLog() {
        return storyLog;
    }
    
    public void addIllogicalAction(int iteration) {
        storyMeta.addIllogicalAction(iteration);
    }
    
    public int getIllogicalActions(int iteration) {
        return storyMeta.getIllogicalActions(iteration);
    }
    
    public void addIrrelevantAction(int iteration) {
        storyMeta.addIrrelevantAction(iteration);
    }
    
    public int getIrrelevantActions(int iteration) {
        return storyMeta.getIrrelevantActions(iteration);
    }

    public void setIrrelevantActions(int iteration, int irrelevantActions) {
        storyMeta.setIrrelevantActions(iteration, irrelevantActions);
    }

    public void setMissingConditions(int iteration, int missingConditions) {
        storyMeta.setMissingConditions(iteration, missingConditions);
    }

    public void setIllogicalActions(int iteration, int illogicalActions) {
        storyMeta.setIllogicalActions(iteration, illogicalActions);
    }
    
    public StoryMeta getStoryMeta() {
        return storyMeta;
    }
}