package mexica.engagement;

import java.util.*;
import mexica.MexicaParameters;
import mexica.story.ConditionInstantiated;
import subgraph.Solution;

/**
 *
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class Cell {
    private String cellID;
    private int tensionCount, emotionCount;
    private List<Atom> atoms;
    
    public Cell(String tensionCount, String emotionCount)
    {
        this(Integer.parseInt(tensionCount), Integer.parseInt(emotionCount));
    }
    
    public Cell(int tensionCount, int emotionCount) {
        this.tensionCount = tensionCount;
        this.emotionCount = emotionCount;
        this.cellID = tensionCount + "-" + emotionCount;
        atoms = new ArrayList<>();
    }

    /**
     * @return the atoms
     */
    public List<Atom> getAtoms() {
        return atoms;
    }
    
    /**
     * Obtains the atoms similar to the given context.<br>
     * If the similarity avaluation of the atom is higher than the ACAS_CONSTANT the atom is added to the list.
     * @param context The context to compare against
     * @return A list of the atoms similar to the context
     */
    public Map<Atom, Solution> getAtoms(List<ConditionInstantiated> context) {        
        return getAtoms(context, MexicaParameters.ACAS_CONSTANT);
    }

    /**
     * Obtains the atoms similar to the given context.<br>
     * If the similarity avaluation of the atom is higher than the given similarity, the atom is added to the list.
     * @param context The context to compara against
     * @param minimumSimilarity The minimum similarity value to preserve an atom
     * @return A list of the similar atoms to the given context
     */
    public Map<Atom, Solution> getAtoms(List<ConditionInstantiated> context, int minimumSimilarity) {
        Map<Atom, Solution> list = new HashMap<>();
        for (Atom atom : atoms) {
            Solution result = AtomComparer.compare(context, atom);
            if (result != null && result.getSimilarity() >= minimumSimilarity && atom.getNextActions().size() > 0) {
                list.put(atom, result);
            }
        }
        return list;
    }
    
    public void addAtom(Atom atom) {
        atoms.add(atom);
    }
    
    @Override
    public String toString() {
        String text = "";
        text += atoms.size() + " atoms (" + getTensionCount() + " tensions, " + getEmotionCount() + " emotions)";
        return text;
    }
    
    /**
     * @return the tensionCount
     */
    public int getTensionCount() {
        return tensionCount;
    }

    /**
     * @return the emotionCount
     */
    public int getEmotionCount() {
        return emotionCount;
    }
    
    public String getCellID() {
        return cellID;
    }
}
