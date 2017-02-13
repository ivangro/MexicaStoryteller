package mexica.tools.avatar;

import java.util.*;

/**
 * Class to provide the memento functionality for the active avatars in a story
 * @author Ivan Guerrero
 */
public abstract class AvatarCaretaker {
    private List<AvatarMemento> savedStates;
    
    public AvatarCaretaker() {
        savedStates = new ArrayList<>();
    }
    
    public void restart() {
        savedStates = new ArrayList<>();
    }
    
    public void addMemento(AvatarMemento memento) {
        savedStates.add(memento);
    }
    
    public AvatarMemento getMemento(int year) {
        if (year < savedStates.size() && year > 0)
            return savedStates.get(year);
        else
            return null;
    }
    
    public AvatarMemento getLastMemento() {
        return getMemento(savedStates.size()-1);
    }
    
    /**
     * Stores the current avatar's state in memory
     */
    public abstract void saveToMemento();
    
    /**
     * Retrieves the avatar's latest state from memory
     */
    public abstract void restoreFromMemento();
}
