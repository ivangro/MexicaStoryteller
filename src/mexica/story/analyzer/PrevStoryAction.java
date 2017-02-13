package mexica.story.analyzer;

import java.util.*;
import mexica.CharacterName;
import mexica.core.Action;

/**
 *
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class PrevStoryAction {
    private Action action;
    private List<CharacterName> avatars;
    
    public PrevStoryAction(Action action, String... characters) {
        this.action = action;
        avatars = new ArrayList<CharacterName>();
        
        for (String avatar : characters) {
            if (avatar != null) {
                avatar = avatar.toUpperCase().substring(0, 1) + avatar.toLowerCase().substring(1);
                avatars.add(CharacterName.valueOf(avatar));
            }
        }
    }
    
    public Action getAction() {
        return action;
    }
    
    public List<CharacterName> getAvatars() {
        return avatars;
    }
    
    @Override
    public String toString() {
        String text = "";
        for (CharacterName name : avatars) {
            text += name + ", ";
        }
        if (text.length() > 2)
            text = text.substring(0, text.length()-2);
        text = action.getActionName() + "(" + text + ")";
        return text;
    }
}
