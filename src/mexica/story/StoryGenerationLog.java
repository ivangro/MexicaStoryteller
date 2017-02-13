package mexica.story;

import java.util.*;
import mexica.core.Action;
import mexica.engagement.Atom;

/**
 * Class to store all the elements utilised during the generation of a story
 * @author Ivan Guerrero
 */
public class StoryGenerationLog {
    private List<AtomActionPair> atoms;
    private List<ActionInstantiated> actions;
    
    public StoryGenerationLog() {
        atoms = new ArrayList<>();
        actions = new ArrayList<>();
    }
    
    public List<AtomActionPair> getAtoms() {
        return atoms;
    }
    
    public List<ActionInstantiated> getActions() {
        return actions;
    }

    public void addAtom(Atom atom, Action action) {
        atoms.add(new AtomActionPair(atom, action));
    }
    
    public void addAction(ActionInstantiated action) {
        actions.add(action);
    }
}

