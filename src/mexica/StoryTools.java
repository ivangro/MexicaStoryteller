package mexica;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.api.MexicaSimpleAPI;
import mexica.core.Action;
import mexica.core.CompositeAction;
import mexica.core.Condition;
import mexica.core.ConditionType;
import mexica.core.EmotionType;
import mexica.core.TensionType;
import mexica.social.SocialStatus;
import mexica.story.*;
import mexica.story.filter.StoryFilterException;
import mexica.tools.InvalidCharacterException;

/**
 *
 * @author Ivan Guerrero
 */
public class StoryTools {
    public static ActionInstantiated addActionToStory(Story story, String actionStr, CharacterName... characters) {
        ActionInstantiated addAction = null;
        try {
            actionStr = actionStr.replaceAll(" ", "_");
            Action action = MexicaSimpleAPI.getInstance().findAction(actionStr);
            addAction = story.addAction(action, characters);
        } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
        }
        
        return addAction;
    }
    
    public static ActionInstantiated addActionToStoryExceptions(Story story, String actionStr, CharacterName... characters) throws InvalidCharacterException, DeadAvatarException, StoryFilterException {
        ActionInstantiated addAction;
        actionStr = actionStr.replaceAll(" ", "_");
        Action action = MexicaSimpleAPI.getInstance().findAction(actionStr);
        addAction = story.addAction(action, characters);
        
        return addAction;
    }
    
    public static ActionInstantiated addSpecialActionToStory(Story story, ActionInstantiated instance, CharacterName... characters) {
        ActionInstantiated addAction = null;
        try {
            Action realised = new Action();
            realised.setActionName("Realised");
            realised.setNoCharacters(1);
            Action composite = new CompositeAction(realised, instance, story.getAvatarFactory());
            addAction = story.addAction(composite, characters);
        } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
        }
        
        return addAction;
    }
    
    public static ActionInstantiated addActionToStory(Story story, ActionInstantiated nextAction, String actionStr, CharacterName... characters) {
        ActionInstantiated addAction = null;
        try {
            actionStr = actionStr.replaceAll(" ", "_");
            Action action = MexicaSimpleAPI.getInstance().findAction(actionStr);
            addAction = story.addAction(nextAction, action, characters);
        } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
        }
        
        return addAction;
    }
    
    public static void printContext(Story story, CharacterName character) {
        Avatar avatar = story.getAvatarFactory().getAvatar(character);
        System.out.print("Context of " + character.name() + " [" + avatar.getCurrentPosition() + "]");
        System.out.println((avatar.isAlive()) ? "" : "+");
        for (ConditionInstantiated c : avatar.getContext().getFacts()) {
            System.out.printf("\t%s\n", c);
        }
    }
    
    public static void printStory(Story story) {
        System.out.println("Story");
        for (ActionInstantiated action : story.getActions()) {
            System.out.println(action);
        }
    }
    
    public static ConditionInstantiated instantiateEmotion(EmotionType type, int intensity, CharacterName... characters) {
        Condition cond = new Condition();
        cond.setCharacterA("a");
        if (characters.length > 1)
            cond.setCharacterB("b");
        cond.setConditionType(ConditionType.Emotion);
        cond.setEmotionType(type);
        cond.setIntensity(intensity);
        ConditionInstantiated instance = new ConditionInstantiated(cond, characters);
        return instance;
    }
    
    public static ConditionInstantiated instantiateTension(TensionType type, boolean presenceConditioned, CharacterName... characters) {
        Condition cond = new Condition();
        cond.setCharacterA("a");
        if (characters.length > 1)
            cond.setCharacterB("b");
        cond.setConditionType(ConditionType.Tension);
        cond.setTensionType(type);
        cond.setPresenceConditioned(presenceConditioned);
        ConditionInstantiated instance = new ConditionInstantiated(cond, characters);
        return instance;
    }
    
    public static void assertMultipleStatus(Story st, SocialStatus... socialStatus) {
        List<ActionInstantiated> actions = st.getActions();
        for (int i=0; i<actions.size(); i++) {
            ActionInstantiated instance = actions.get(i);
            System.out.println("Testing action: " + instance);
            //assertEquals(socialStatus[i], instance.getSocialStatus());
        }
    }
}
