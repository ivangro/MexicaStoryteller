package mexica.tools.avatar;

import java.util.*;
import mexica.story.Avatar;
/**
 * State to be stored with the active avatars in a story
 * @author Ivan Guerrero
 */
public class AvatarMemento {
    private List<Avatar> savedState;

    /**
     * Clones every element in the list and stores a copy of it
     * @param savedState 
     */
    public AvatarMemento(List<Avatar> savedState) {
        this.savedState = new ArrayList<>();
        for (Avatar a : savedState) {
            this.savedState.add((Avatar)a.clone());
        }
    }
    
    /**
     * Obtains the saved avatars
     * @return 
     */
    public List<Avatar> getState() {
        return savedState;
    }
}
