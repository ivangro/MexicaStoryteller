package mexica.social;

import java.util.*;
import mexica.CharacterName;
import mexica.core.Condition;
import mexica.core.ConditionType;
import mexica.core.EmotionType;
import mexica.story.ActionInstantiated;
import mexica.story.ConditionInstantiated;

/**
 *
 * @author Ivan Guerrero
 */
public class SocialNormsUtils {
    public static ConditionInstantiated createSocialLink(CharacterName... names) {
        Condition cond = new Condition();
        cond.setConditionType(ConditionType.Emotion);
        cond.setEmotionType(EmotionType.getSocialEmotion());
        cond.setIntensity(-1);
        cond.setCharacterA("a");
        cond.setCharacterB("b");
        ConditionInstantiated instance = new ConditionInstantiated(cond, names);
        return instance;
    }

    /**
     * Creates a social link based on the information obtained from a social action instantiated. 
     * Also updates the status of the social action according its current mode.
     * @param owner
     * @param action
     * @return 
     */
    public static ConditionInstantiated createSocialLink(CharacterName owner, ActionInstantiated action, SocialAction socialAction) {
        Condition poscondition;
        poscondition = (!socialAction.getPosconditions().isEmpty()) ? socialAction.getPosconditions().get(0) :
                                                                      socialAction.getTensions().get(0);
        Condition cond = new Condition();
        cond.setConditionType(poscondition.getConditionType());
        List<String> posChars = new ArrayList<>();
        switch (poscondition.getConditionType()) {
            case Emotion:
                cond.setEmotionType(poscondition.getEmotion());
                cond.setIntensity(poscondition.getIntensity());
                posChars.add(poscondition.getCharacterB());
                posChars.add(poscondition.getCharacterA());
                break;
            case Tension:
                cond.setTensionType(poscondition.getTension());
                posChars.add(poscondition.getCharacterA());
                posChars.add(poscondition.getCharacterB());
                break;
        }
        cond.setCharacterA("b");
        cond.setCharacterB("a");
        //Look for the appropriate characters according to the characters of the social action
        List<CharacterName> selectedChars = new ArrayList<>();
        //Look for the first character of the social action
            //A -> First character of the action
            //B -> Second character of the action
            //* -> The context owner
        switch (posChars.get(0)) {
            case "a": selectedChars.add(action.getCharactersList().get(0));break;
            case "b": selectedChars.add(action.getCharactersList().get(1));break;
            default: selectedChars.add(owner);break;
        }
        if (socialAction.getNoCharacters() > 1) {
            switch (posChars.get(1)) {
                case "a": selectedChars.add(action.getCharactersList().get(0));break;
                case "b": selectedChars.add(action.getCharactersList().get(1));break;
                default: selectedChars.add(owner);break;
            }
        }
        
        ConditionInstantiated instance = new ConditionInstantiated(cond, selectedChars.toArray(new CharacterName[0]));
        instance.setAction(action);
        //Update the status of the social action
        switch (socialAction.getSocialPosconditionMode()) {
            case insert: action.setSocialStatus(SocialStatus.specialSocialNorm); break;
            case justify: action.setSocialStatus(SocialStatus.specialActionJustified);break;
        }
        return instance;
    }
}
