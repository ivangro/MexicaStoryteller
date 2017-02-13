package mexica.story;

import mexica.core.Action;
import mexica.engagement.Atom;

/**
 * Class to store an atom and its action employed
 * @author Ivan Guerrero
 */
public class AtomActionPair {
    private Atom atom;
    private Action action;
    
    public AtomActionPair(Atom atom, Action action) {
        this.atom = atom;
        this.action = action;
    }

    /**
     * @return the atom
     */
    public Atom getAtom() {
        return atom;
    }

    /**
     * @param atom the atom to set
     */
    public void setAtom(Atom atom) {
        this.atom = atom;
    }

    /**
     * @return the action
     */
    public Action getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(Action action) {
        this.action = action;
    }
}