package mexica.api;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.CharacterName;
import mexica.MexicaParameters;
import mexica.context.AtomSimilarityResult;
import mexica.core.Action;
import mexica.core.Position;
import mexica.engagement.EngagementUtils;
import mexica.reflection.Guideline;
import mexica.story.*;
import mexica.story.filter.StoryFilterException;
import mexica.tools.InvalidCharacterException;

/**
 * Class with the available funcionality provided by the system related to social norms.
 * @author Ivan Guerrero
 */
public class MexicaSocialAPI {
    /**
     * Creates a Story object with the given actions
     * @param storyActions The actions for the story
     * @param defaultPosition The initial location of the characters
     * @return A story with the given actions
     */
    public static Story createStory(List<ActionInstantiated> storyActions, Position defaultPosition) {
        Story story = new Story();
        story.setDefaultPosition(defaultPosition);
        
        boolean storyExceptions = MexicaParameters.ENABLE_STORY_EXCEPTIONS;
        MexicaParameters.ENABLE_STORY_EXCEPTIONS = false;
        for (ActionInstantiated action : storyActions) {
            try {
                story.addAction(action.getAction(), action.getCharactersList().toArray(new CharacterName[0]));
            } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) {
                Logger.getGlobal().log(Level.SEVERE, ex.getMessage());
            }
        }
        MexicaParameters.ENABLE_STORY_EXCEPTIONS = storyExceptions;
        return story;
    }
    
    /**
     * Creates a story object with the given actions. The default location is City.
     * @param storyActions
     * @return 
     */
    public static Story createStory(List<ActionInstantiated> storyActions) {
        return createStory(storyActions, Position.City);
    }
    
    /**
     * Identifies which actions broke a social norm and marks them
     * @param actions Story actions
     * @return The story actions marked when they break a social norm
     */
    public static List<ActionInstantiated> performSocialAnalysis(List<ActionInstantiated> storyActions) {
        return createStory(storyActions).getActions();
    }
    
    /**
     * Suggest an action to continue with the given story depending on the given parameters
     * @param storyActions The current story actions
     * @param breakSocialNorm TRUE if the next action breaks a social norm
     * @return A list of possible continuations fulfilling the given parameters
     */
    public static List<ActionInstantiated> suggestNextAction(List<ActionInstantiated> storyActions, boolean breakSocialNorm) {
        Story story = createStory(storyActions);
        story.getGuidelines().addGuideline(Guideline.tendencyNeutral);
        if (breakSocialNorm)
            story.getGuidelines().addGuideline(Guideline.breakSocialNorm);
        else
            story.getGuidelines().addGuideline(Guideline.notBreakSocialNorm);
        
        boolean partialCharacterInstantiation = MexicaParameters.PARTIAL_CHARACTER_INSTANTIATION;
        Collection<AtomSimilarityResult> availableAtoms = EngagementUtils.obtainAvailableAtoms(story.getAvatarFactory().getActiveCharacters());
        List<ActionInstantiated> actions = EngagementUtils.getActionsFromAtoms(story, availableAtoms);
        //Restore the parameter value for character instantiation
        MexicaParameters.PARTIAL_CHARACTER_INSTANTIATION = partialCharacterInstantiation;
        
        return actions;
    }

    /**
     * Suggest an action to continue with the given story depending on the given parameters
     * @param storyActions The current story actions
     * @param action The action to be instantiated
     * @param breakSocialNorm TRUE if the next action breaks a social norm
     * @return A list of possible continuations fulfilling the given parameters
     */
    public static List<ActionInstantiated> suggestCharactersForAction(List<ActionInstantiated> storyActions, Action action, boolean breakSocialNorm) {
        List<ActionInstantiated> socialActions = suggestNextAction(storyActions, breakSocialNorm);
        List<ActionInstantiated> result = new ArrayList<>();
        
        for (ActionInstantiated a : socialActions) {
            if (a.getAction().equals(action))
                result.add(a);
        }
        
        return result;
    }
    
    /**
     * Creates a new story to justify why the given action broke a social norm
     * @param storyActions The current story
     * @param action The action to be justified
     * @return A modified story where the given action inside the given story is justified
     */
    public static Story justifySocialNorm(List<ActionInstantiated> storyActions, ActionInstantiated action) {
        Story story = createStory(storyActions);
        //TODO: Pending code
        return story;
    }
}