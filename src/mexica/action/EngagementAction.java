package mexica.action;

import java.awt.event.ActionEvent;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import mexica.*;
import mexica.CharacterName;
import mexica.context.*;
import mexica.core.Action;
import mexica.engagement.*;
import mexica.story.*;
import mexica.story.filter.StoryFilterException;
import mexica.story.log.StoryLog;
import mexica.tools.InvalidCharacterException;

/**
 * Class to perform an engagement action step
 * @author Ivan Guerrero
 */
public class EngagementAction extends AbstractAction{
    private Story story;
    private Random random;
    private ElementsEmployed repository;
    
    public EngagementAction(Story story) {
        random = new Random();
        this.story = story;
        repository = story.getElementsEmployed();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        boolean partialCharacterInstantiation = MexicaParameters.PARTIAL_CHARACTER_INSTANTIATION;
        //Obtain the character's context in the current story
        List<Avatar> characters = story.getCharacters();
        //Obtain the available atoms for each context
        Collection<AtomSimilarityResult> availableAtoms = EngagementUtils.obtainAvailableAtoms(characters);
        //Apply the most suitable action picked from an atom
        boolean actionAdded = applyActionFromAtoms(availableAtoms);
        //Restore the parameter value for character instantiation
        MexicaParameters.PARTIAL_CHARACTER_INSTANTIATION = partialCharacterInstantiation;
        
        if (!actionAdded) {
            story.setImpasseDetected(true);
            throw new Error("Impasse reached");
        }
    }
    
    /**
     * Try to use every action available in the atoms that matched the given context
     * @param availableAtoms
     * @return 
     */
    public boolean applyActionFromAtoms(Collection<AtomSimilarityResult> availableAtoms) {
        boolean actionAdded = false;
        Action action = null;
        
        List<Action> actionList = MexicaRepository.getInstance().getActions().getActionList();
        
        //Take each atom and try to instantiate an action associated to each one of them taken in descending order
        //in accordance to the similarity with the context
        for (AtomSimilarityResult atomResult : availableAtoms) {
            Logger.getGlobal().log(Level.FINE, "Attempting {0}", atomResult.getAtom());
            List<Action> nextActions = atomResult.getAtom().getNextActions();
            int size = nextActions.size();
            int offset = random.nextInt(size);
            for (int i=0; i<size && !actionAdded; i++) {
                action = nextActions.get((i + offset) % size);
                int index = actionList.indexOf(action);
                if (index >= 0) {
                    //Action employed to validate guidelines, cannot overwrite the action element, because its characters depend on the atom
                    Action testAction = actionList.get(index);
                    if (!story.getGuidelines().satisfiesGuidelines(testAction)) {
                        Logger.getGlobal().log(Level.FINE, "Action banned due to guidelines: {0}", action);
                    }
                    else if (MexicaParameters.ENABLE_POSITION_ACTIONS || 
                             !MexicaRepository.getInstance().getActions().isChangePositionAction(action)) {
                        actionAdded = addActionToStory(action, atomResult);
                    }
                }
                else
                    Logger.getGlobal().log(Level.WARNING, "Action not found: {0}", action);
            }
            if (actionAdded) {
                Logger.getGlobal().log(Level.INFO, "Atom successfully employed: {0} with action {1} in context {2}", 
                                                    new Object[] {atomResult.getAtom(), action, atomResult.getContextOwner()});
                StoryLog.addMessage(story, "(E) Atom successfully employed: " + atomResult.getAtom() + 
                                           " with action " + action + " in context " + atomResult.getContextOwner());
                story.getStoryLog().addAtom(atomResult.getAtom(), action);
                break;
            }
            else {
                Logger.getGlobal().log(Level.FINE, "Action banned: {0}", action);
            }
        }
        
        return actionAdded;
    }   

    /**
     * Adds the given action to the story. The characters are instantiated depending on the mapping found in the atomResult
     * @param action Action to be added
     * @param atomResult Result of comparing the context with an atom
     * @return TRUE if the action was successfully added to the story
     */
    public boolean addActionToStory(Action action, AtomSimilarityResult atomResult) {
        if (!repository.isActionAvailable(action))
            return false;
        
        boolean actionAdded = false;
        ActionInstantiated newAction;
        
        List<Action> actionList = MexicaRepository.getInstance().getActions().getActionList();
        int index = actionList.indexOf(action);
        
        if (action.getNoCharacters() == 2) {
            List<CharacterName> characters = EngagementCharacterInstantiator.instantiateCharacters(story, action, atomResult);
            if (characters.get(0) == null || characters.get(1) == null)
                return false;
            action = actionList.get(index);
            newAction = new ActionInstantiated(action, characters.get(0), characters.get(1));
            if (repository.isActionAvailable(newAction)) {
                try {
                    if (MexicaParameters.ENABLE_SOCIAL_ANALYSIS && !story.getGuidelines().satisfiesSocialGuidelines(newAction))
                        throw new StoryFilterException("Action " + newAction + " banned by social filter");
                    newAction = story.addAction(action, characters.get(0), characters.get(1));
                    Logger.getGlobal().log(Level.INFO, "Action added: {0}", newAction);
                    StoryLog.addMessage(story, "(E) Action added " + newAction);
                    story.getStoryLog().addAction(newAction);
                    actionAdded = true;
                } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) {
                    Logger.getGlobal().log(Level.WARNING, "Action error: {0}, {1}", new Object[] {action, ex.getMessage()});
                    StoryLog.addMessage(story, "(E) Action invalidated: " + newAction + ", " + ex.getMessage());
                }
            }
        }
        else {
            List<CharacterName> characters = EngagementCharacterInstantiator.instantiateCharacters(story, action, atomResult);
            if (characters.get(0) == null)
                return false;
            action = actionList.get(index);
            newAction = new ActionInstantiated(action, characters.get(0));
            if (repository.isActionAvailable(newAction)) {
                try {
                    if (MexicaParameters.ENABLE_SOCIAL_ANALYSIS && !story.getGuidelines().satisfiesSocialGuidelines(newAction))
                        throw new StoryFilterException("Action " + newAction + " banned by social filter");
                    if (action.getActionName().equalsIgnoreCase("realised"))
                        throw new StoryFilterException("Realised not implemented in engagement");
                    newAction = story.addAction(action, characters.get(0));
                    Logger.getGlobal().log(Level.INFO, "Action added: {0}", newAction);
                    StoryLog.addMessage(story, "(E) Action added " + newAction);
                    story.getStoryLog().addAction(newAction);
                    actionAdded = true;
                } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) {
                    Logger.getGlobal().log(Level.WARNING, "Action error: {0}, {1}", new Object[] {action, ex.getMessage()});
                    StoryLog.addMessage(story, "(E) Action invalidated: " + newAction + ", " + ex.getMessage());
                }
            }
        }
        
        return actionAdded;
    }
}