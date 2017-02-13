package mexica.story.filter;

import mexica.MexicaParameters;
import mexica.core.*;
import mexica.story.*;
import slant.SlantGuidelines;

/**
 * Class to determine if the added action is illogical or not.<br>
 * An action is considered illogical when character A has strong feelings (+2, +3) towards B
 * and the action triggers strong contrary feelings (-2, -3) from B towards A.
 * @author Ivan Guerrero
 */
public class IllogicalActionFilter implements IStoryFilter {

    @Override
    public boolean analyzeStory(Story story, ActionInstantiated action) throws StoryFilterException {
        for (ConditionInstantiated cond : action.getPosconditions()) {
            if (cond.getType() == ConditionType.Emotion) {
                if (Math.abs(cond.getCondition().getIntensity()) >= 2) {
                    Condition clone = null;
                    try {
                        clone = (Condition)cond.getCondition().clone();
                        clone.setIntensity((clone.getIntensity() > 0) ? -2 : 2);
                        clone.setEmotionType(EmotionType.getAnyEmotion());
                    } catch (CloneNotSupportedException ex) {}
                    ConditionInstantiated newCond = new ConditionInstantiated(clone, cond.getCharacterB(), cond.getCharacterA());
                    Avatar avatar = story.getAvatarFactory().getAvatar(action.getCharactersList().get(0));
                    AvatarContext context = avatar.getContext();
                    if (context.containsCondition(newCond))
                        if (MexicaParameters.ENABLE_STORY_EXCEPTIONS && !MexicaParameters.ENABLE_ILLOGICAL_ACTIONS)
                            throw new StoryFilterException("Illogical action added: " + action + "\nCondition: " + cond);
                }
            }
        }
        if (MexicaParameters.ENABLE_SLANT_MODE && !((SlantGuidelines)story.getGuidelines()).satisfiesSlantGuidelines(action))
            throw new StoryFilterException("Action invalidated due to Slant guidelines: " + action);
        return true;
    }

}
