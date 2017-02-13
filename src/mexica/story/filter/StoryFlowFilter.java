package mexica.story.filter;

import java.util.List;
import mexica.CharacterName;
import mexica.MexicaParameters;
import mexica.core.Position;
import mexica.core.TensionType;
import mexica.social.SocialAction;
import mexica.story.*;
import mexica.tools.StoryFlowException;

/**
 * Class to determine if the given action adds some new element into the story
 * @author Ivan Guerrero
 */
public class StoryFlowFilter implements IStoryFilter {

    /**
     * Determines if the given action adds some new element into the story
     * @param action The action to be analyzed
     * @return TRUE if the action adds some new element into the story
     * @throws StoryFilterException When the action doesn't add a new element to the story
     */
    @Override
    public boolean analyzeStory(Story story, ActionInstantiated action) throws StoryFilterException {
        //If is a social action, add it to the story
        if (action.getAction() instanceof SocialAction)
            return true;
        
        Avatar performer, receiver;
        performer = story.getAvatarFactory().getAvatar(action.getCharactersList().get(0));
        receiver = (action.getAction().getNoCharacters() == 2) ? 
                story.getAvatarFactory().getAvatar(action.getCharactersList().get(1)) : null;
        
        List<CharacterName> availableCharacters = story.getAvatarFactory().getAvailableCharacterNames();
        //Determine if the performer's context contains all the new conditions
        //TODO: Validate with linked characters
        for (ConditionInstantiated cond : action.getPosconditions()) {
            switch (cond.getType()) {
                case Emotion:
                    if (availableCharacters.contains(cond.getCharacterA()) && 
                        availableCharacters.contains(cond.getCharacterB())) {
                        if (!performer.getContext().containsCondition(cond))
                            return true;
                    }
                    break;
                case Position:
                    Position position;
                    if (cond.getPosition() == Position.OtherCharactersPosition) {
                        Avatar avatar = story.getAvatarFactory().getAvatar(cond.getCharacterB());
                        position = avatar.getCurrentPosition();
                        if (performer.getCurrentPosition() != position)
                            return true;
                    }
                    else {
                        position = cond.getPosition();
                        if ((performer.getCurrentPosition() != position) ||
                            (receiver != null && receiver.getCurrentPosition() != position))
                            return true;
                    }
                    break;
            }
        }
        //Determine if the performer's context contains all the new tensions
        for (ConditionInstantiated cond : action.getTensions()) {
            //It's not necessary to validate if the character is available, since a character can discover facts from dead characters
            //if (availableCharacters.contains(cond.getCharacterA())) {
                if ((TensionType.isTensionTrigger(cond.getCondition().getTension()) && !performer.getContext().containsCondition(cond)) ||
                    (TensionType.isTensionDeactivator(cond.getCondition().getTension()) && performer.getContext().removesTension(cond)))
                    return true;
            //}
        }
        
        if (MexicaParameters.ENABLE_STORY_EXCEPTIONS) {
            if (!action.getAction().isNewAction())
                throw new StoryFlowException("The story already contains all the elements of the action: " + action);
        }
        
        return false;
    }
}