package mexica.context;

import mexica.engagement.*;
import mexica.story.Avatar;
import subgraph.Solution;

/**
 * Class to store the result of comparing an atom with a context
 * @author Ivan Guerrero
 */
public class AtomSimilarityResult {
    /** The atom to be compared */
    private Atom atom;
    /** The solution obtained */
    private Solution solution;
    /** The avatar owner of the context employed for the comparison */
    private Avatar contextOwner;
    
    public AtomSimilarityResult() {}
    
    public AtomSimilarityResult(Atom atom, Solution solution, Avatar contextOwner) {
        this.atom = atom;
        this.solution = solution;
        this.contextOwner = contextOwner;
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
     * @return the solution
     */
    public Solution getSolution() {
        return solution;
    }

    /**
     * @param solution the solution to set
     */
    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    /**
     * @return the contextOwner
     */
    public Avatar getContextOwner() {
        return contextOwner;
    }

    /**
     * @param contextOwner the contextOwner to set
     */
    public void setContextOwner(Avatar contextOwner) {
        this.contextOwner = contextOwner;
    }
    
    @Override
    public String toString() {
        return "From " + contextOwner + ": " + atom + "\n" + solution;
    }
}