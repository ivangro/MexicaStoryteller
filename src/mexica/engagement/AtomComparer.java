package mexica.engagement;

import edu.uci.ics.jung.graph.Graph;
import java.util.*;
import mexica.context.ContextBuilder;
import mexica.core.*;
import mexica.story.ConditionInstantiated;
import subgraph.IEdge;
import subgraph.INode;
import subgraph.SimilarityEvaluator;
import subgraph.Solution;

/**
 * Class for making comparisons between atoms and contexts
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class AtomComparer {
    
    /**
     * Analyzes how much of the context is present in the atom
     * @param context Context to analyze
     * @param atom Atom to compare against
     * @return 
     */
    public static Solution compare(List<ConditionInstantiated> context, Atom atom) {
        Graph<INode, IEdge> contextGraph = ContextBuilder.generateGraphFromContext(context);
        Graph<INode, IEdge> atomGraph = ContextBuilder.generateGraphFromAtom(atom);
        
        SimilarityEvaluator evaluator = new SimilarityEvaluator();
        Solution result = evaluator.evaluate(atomGraph, contextGraph);
        
        return result;
    }
    
    /**
     * Determines the percentage of elements included of the atom with less facts in the other atom
     * @param atom
     * @param atom2
     * @return 
     */
    public static Solution compare(Atom atom, Atom atom2) {
        Graph<INode, IEdge> atomGraph = ContextBuilder.generateGraphFromAtom(atom);
        Graph<INode, IEdge> atom2Graph = ContextBuilder.generateGraphFromAtom(atom2);
        
        SimilarityEvaluator evaluator = new SimilarityEvaluator();
        Solution result = evaluator.evaluate(atomGraph, atom2Graph);
        
        return result;
    }
    
    /**
     * Determines if the first atom's elements are included in the second atom
     * @param atom
     * @param atom2
     * @return 
     */
    public static Solution compareInclusion(Atom atom, Atom atom2) {
        Graph<INode, IEdge> atomGraph = ContextBuilder.generateGraphFromAtom(atom);
        Graph<INode, IEdge> atom2Graph = ContextBuilder.generateGraphFromAtom(atom2);
        
        SimilarityEvaluator evaluator = new SimilarityEvaluator();
        Solution result = evaluator.evaluateGraphs(atomGraph, atom2Graph);
        
        return result;
    }
    
    /**
     * Determines if the two given atoms are equal or not.<br>
     * Two atoms are considered equal when all their emotions and tensions are equal including their characters
     * @param atom1
     * @param atom2
     * @return TRUE if the atoms are equal
     */
    public static boolean areEqual(Atom atom1, Atom atom2) {
        if (atom1.getEmotions().size() != atom2.getEmotions().size() ||
            atom1.getTensions().size() != atom2.getTensions().size())
            return false;
        
        List<Condition> emotions1, emotions2;
        List<Tension> tensions1, tensions2;
        emotions1 = new ArrayList<>();
        emotions2 = new ArrayList<>();
        tensions1 = new ArrayList<>();
        tensions2 = new ArrayList<>();
        
        for (Condition c : atom1.getEmotions()) {
            emotions1.add(c);
        }
        for (Condition c : atom2.getEmotions()) {
            emotions2.add(c);
        }
        for (Tension t : atom1.getTensions()) {
            tensions1.add(t);
        }
        for (Tension t : atom2.getTensions()) {
            tensions2.add(t);
        }
        //Compares the emotions
        for (Condition c1 : emotions1) {
            boolean equivalentFound = false;
            for (int i=0; i<emotions2.size() && !equivalentFound; i++) {
                Condition c2 = emotions2.get(i);
                if ((c1.getEmotion() == c2.getEmotion()) && (c1.getIntensity() == c2.getIntensity()) &&
                    (c1.getCharacterA().equals(c2.getCharacterA())) && (c1.getCharacterB().equals(c2.getCharacterB()))) {
                    emotions2.remove(i);
                    equivalentFound = true;
                }
            }
            if (!equivalentFound)
                return false;
        }
        
        //Compares the tensions
        for (Tension t1 : tensions1) {
            boolean equivalentFound = false;
            for (int i=0; i<tensions2.size() && !equivalentFound; i++) {
                Tension t2 = tensions2.get(i);
                if ((t1.getTension() == t2.getTension()) && 
                    (t1.getCharacterA().equals(t2.getCharacterA())) && (t1.getCharacterB().equals(t2.getCharacterB()))) {
                    tensions2.remove(i);
                    equivalentFound = true;
                }
            }
            if (!equivalentFound)
                return false;
        }
        
        return true;
    }
}