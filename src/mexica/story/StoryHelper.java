package mexica.story;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.CharacterName;
import mexica.MexicaParameters;
import mexica.MexicaRepository;
import mexica.core.*;
import mexica.story.filter.*;
import mexica.tools.*;

/**
 * Class with helper methods for managing the story data
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class StoryHelper {
    private StoryDAO storyData;
    private List<IStoryFilter> storyFilters;
    private AvatarFactory avatarFactory;
    private Story story;
    
    public StoryHelper(Story story) {
        this.story = story;
        this.avatarFactory = story.getAvatarFactory();
        this.storyData = story.getStoryData();
        storyFilters = new ArrayList<>();
        storyFilters.add(new StoryFlowFilter());
        storyFilters.add(new IllogicalActionFilter());
    }
    
    public void performAddAction(ActionInstantiated newAction, CharacterName... avatars) 
            throws InvalidCharacterException, DeadAvatarException {
        
        for (ConditionInstantiated cond : newAction.getPreconditions()) {
            addMissingCondition(cond, newAction);
        }
        for (ConditionInstantiated ten: newAction.getTensions()) {
            addTension(ten, newAction);
        }
        for (ConditionInstantiated cond : newAction.getPosconditions()) {
            cond.setAction(newAction);
            addFact(cond, avatars, newAction);
        }
        
        story.notifyListeners(IStoryContextListener.ListType.Actions);
        storyData.incrementCurrentYear();
    }
    
    /**
     * Adds the new condition to the list of pending conditions 
     * that must be satisfied for the story to be coherent
     * @param condition Missing condition of a given action
     */
    private void addMissingCondition(ConditionInstantiated condition, ActionInstantiated action) {
        boolean addCondition = true;
        //Validates the missing condition against the context of the character performing the action
        Avatar avatar = avatarFactory.getAvatar(action.getCharactersList().get(0));
        for (ConditionInstantiated cond : avatar.getContext().getFacts()) {
            ComparisonResult res = ConditionComparer.compare(cond, condition);
            if (res.isEqualOrEquivalent(true, true)) {
                addCondition = false;
                break;
            }
        }
        if (addCondition) {
            condition.setAction(action);
            storyData.addMissingCondition(condition);
            story.notifyListeners(IStoryContextListener.ListType.Conditions);
        }
    }

    /**
     * Validates which characters should be aware of the fact
     * and adds it to their context.
     * NOTE: The condition is added to all the characters at the same location unless the action is secret
     * @param cond New fact to be analyzed
     * @param characters The characters employed in the action
     * @param newAction Action to whom belongs the condition
     */
    private void addFact(ConditionInstantiated cond, CharacterName[] characters, ActionInstantiated newAction) {
        
        Avatar avatarA, avatarB;
        List<Avatar> avatars = new ArrayList<>();

        //If the condition moves the character's position, store the current location of the characters
//        if (cond.getType() == ConditionType.Position) {
//            Position position = story.getAvatarFactory().getAvatar(characters[0]).getCurrentPosition();
//            newAction.setPreviousPosition(position);
//        }
        
        switch (cond.getCharacterA()) {
            case Linked_Characters_A:
                avatarA = avatarFactory.getAvatar(characters[0]);
                avatars = avatarA.getContext().getLinkedCharacters();
                for (Avatar avatar : avatars) {
                    avatar.getContext().createCondition(cond, characters[0], characters[1]);
                }
                break;
            case Linked_Characters_B:
                avatarB = avatarFactory.getAvatar(characters[1]);
                avatars = avatarB.getContext().getLinkedCharacters();
                for (Avatar avatar : avatars) {
                    avatar.getContext().createCondition(cond, characters[1], characters[0]);
                }
                break;
            default:
                avatarA = avatarFactory.getAvatar(cond.getCharacterA());
                if (!newAction.isSecret()) {
                    Position actionPosition = avatarA.getCurrentPosition();
                    avatars = avatarFactory.getAvatarsInPosition(actionPosition);
                } else {
                    for (CharacterName name : newAction.getCharactersList()) {
                        avatars.add(avatarFactory.getAvatar(name));
                    }
                }
                for (Avatar avatar : avatars) {
                    avatar.getContext().addCondition(cond);
                }
                break;
        }
        
        story.notifyListeners(IStoryContextListener.ListType.Facts);
    }

    /**
     * Validates which characters should be aware of the tension
     * and adds it to their context
     * NOTE: The condition is added to all the characters at the same location
     * @param cond New tension to be analyzed
     * @param newAction The action to whom belongs the condition
     */
    private void addTension(ConditionInstantiated cond, ActionInstantiated newAction) {
        Avatar avatarA = avatarFactory.getAvatar(newAction.getCharactersList().get(0));
        Position actionPosition = avatarA.getCurrentPosition();
        
        //Creates a link between the condition and the action who generated it
        cond.setAction(newAction);
        
        Set<Avatar> avatars = new HashSet<>();
        for (CharacterName name : newAction.getCharactersList()) {
            avatars.add(avatarFactory.getAvatar(name));
        }
        
        //If the action is secret, only the characters involved in it will be aware
        if (!newAction.isSecret()) {
            for (Avatar avatar : avatarFactory.getAvatarsInPosition(actionPosition)) {
                avatars.add(avatar);
            }
        }
        for (Avatar avatar : avatars) {
            avatar.getContext().addCondition(cond);
        }
        
        story.notifyListeners(IStoryContextListener.ListType.Tensions);
    }
    
    /**
     * Picks one of the available texts and adds it to the story
     * @param texts Set of available texts asociated to an action
     */
    public void addText(ActionInstantiated action, List<TextInstantiated> texts, int insertIndex) {
        int index = texts.size();
        if (index > 0) {
            index = (int)Math.floor(Math.random() * index);
            TextInstantiated text = texts.get(index);
            if (text.getText().contains("PENDING TEXT"))
                text.setText(action.toString() + ".");
            storyData.getStoryText().add(insertIndex, text);
            story.notifyListeners(IStoryContextListener.ListType.Story);
        }
        else {
            Logger.getGlobal().log(Level.WARNING, null, "Action with no text associated: " + action);
        }
    }
    
    /**
     * Determines if the action will move one charater to the location of the other
     * @param newAction Current action
     * @return TRUE if the characters are in different locations and the action is valid
     */
    public boolean validateMultiplePositions(ActionInstantiated newAction) {
        for (ConditionInstantiated cond : newAction.getPosconditions()) {
            if (cond.getType().equals(ConditionType.Position)) {
                if (cond.getCondition().getCharactersPosition() != null)
                    return true;
            }
        }
        
        return false;
    }
    
    /**
     * Validates that every character is alive
     * @param avatars
     * @throws DeadAvatarException 
     */
    public void validateCharactersAlive(CharacterName... avatars) throws DeadAvatarException {
        for (CharacterName name : avatars) {
            Avatar av = avatarFactory.getAvatar(name);
            if (!av.isAlive())
                throw new DeadAvatarException("The character " + name + " is dead");
        }
    }
    
    /**
     * Updates the character tension's list with all the added tensions.<br>
     * Also store a previous version of the facts.
     */
    public void updateTensionList() {
        for (Avatar avatar : avatarFactory.getActiveCharacters()) {
            for (ConditionInstantiated cond : avatar.getContext().getFacts()) {
                avatar.getContext().getAvatarTensions().addTension(story.getCurrentYear()-1, cond);
            }
            avatar.getContext().storePreviousFacts();
        }
    }
    
    /**
     * Executes all the actions inside the actions list
     * This method is called when an action is added in reflection mode, since all the context are recalculated
     * @throws InvalidCharacterLocationsException
     * @throws DeadAvatarException 
     */
    public void applyActions()
        throws InvalidCharacterException, DeadAvatarException, StoryFilterException {
        
        for (ActionInstantiated act : storyData.getActions()) {
            if (MexicaParameters.ENABLE_SOCIAL_ANALYSIS) {
                //Reset the social norm analysis result
                act.resetSocialStatus();
            }
            if (act.getInsertionMode() == InsertionMode.Reflection) {
                Position lastPosition = avatarFactory.getDefaultPosition();
                if (act.getNewCharacterPosition() != Position.NotDefined)
                    avatarFactory.setDefaultPosition(act.getNewCharacterPosition());

                validateCharactersAlive(act.getCharactersList().toArray(new CharacterName[0]));
                boolean multiplePositions = validateMultiplePositions(act);   
                avatarFactory.activateCharacters(act.getCharactersList().toArray(new CharacterName[0]), multiplePositions);
                validateStoryFilters(act);
                performAddAction(act, act.getCharactersList().toArray(new CharacterName[0]));

                avatarFactory.setDefaultPosition(lastPosition);
            }
            else {
                validateCharactersAlive(act.getCharactersList().toArray(new CharacterName[0]));
                boolean multiplePositions = validateMultiplePositions(act);   
                avatarFactory.activateCharacters(act.getCharactersList().toArray(new CharacterName[0]), multiplePositions);
                validateStoryFilters(act);
                performAddAction(act, act.getCharactersList().toArray(new CharacterName[0]));
            }
            addText(act, act.getTexts(), storyData.getStoryText().size());
            avatarFactory.validateAdditionalTensions();

            updateTensionList();
        }
    }

    /**
     * Method to validate if the given action adds at least one fact to the context, otherwise the action is discarded
     * @param action Action to be analyzed
     * @throws StoryFlowException When the action doesn't add any fact to the context
     */
    public boolean validateStoryFilters(ActionInstantiated action) throws StoryFilterException {
        boolean res = true;
        for (IStoryFilter filter : storyFilters) {
            res &= filter.analyzeStory(story, action);
        }
        
        return res;
    }
 
    /**
     * Obtains an action to change the first character to the same location as the second character
     * @param avatars Character names
     * @throws DeadAvatarException Thrown when one of the characters is dead
     * @throws InvalidCharacterException 
     */
    public void matchCharacterPositions(CharacterName[] avatars) throws DeadAvatarException, InvalidCharacterException {
        //Moves the characters to the same position
        Action act = MexicaRepository.getInstance().getActions().getChangePositionAction();
        ActionInstantiated posAction = new ActionInstantiated(act, avatars);
        posAction.setInsertionMode(InsertionMode.Reflection);
        storyData.addAction(posAction);
        performAddAction(posAction, avatars);
        addText(posAction, posAction.getTexts(), storyData.getStoryText().size());
    }
}