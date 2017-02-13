package mexica.engagement;

import java.util.Comparator;
import mexica.context.AtomSimilarityResult;

/**
 * Class to compare two atom comparison results
 * @author Ivan Guerrero
 */
public class AtomComparator implements Comparator<AtomSimilarityResult> {

    /**
     * Compares the similarity results from each given atom
     * @param t
     * @param t1
     * @return 
     */
    @Override
    public int compare(AtomSimilarityResult t, AtomSimilarityResult t1) {
        if (t.getSolution().getSimilarity() > t1.getSolution().getSimilarity())
            return 1;
        else if (t.getSolution().getSimilarity() < t1.getSolution().getSimilarity())
            return -1;
        else 
            return 0;
    }

}
