package mexica.social.engagement;

import java.util.*;
import mexica.MexicaParameters;
import mexica.MexicaRepository;
import mexica.engagement.Atom;
import mexica.engagement.AtomComparer;
import mexica.engagement.AtomsStore;
import mexica.engagement.Cell;
import mexica.social.SocialAtom;
import subgraph.Solution;

/**
 * Utils for the social engagement process
 * @author Ivan Guerrero
 */
public class SocialEngagementUtils {
    private static SocialEngagementUtils instance = new SocialEngagementUtils();
    private AtomsStore socialAtoms;
    
    private SocialEngagementUtils() {
        socialAtoms = MexicaRepository.getInstance().getSocialAtoms();
    }
    
    public static SocialEngagementUtils getInstance() {
        return instance;
    }
    
    /**
     * Obtains the atoms with socially similar to the given atom
     * @param atom Atom representing the context of a character
     * @return 
     */
    public Collection<Solution> obtainAvailableAtoms(Atom atom) {
        Collection<Solution> solutions = new ArrayList<>();
        
        for (Cell c : socialAtoms.getCells()) {
            for (Atom a : c.getAtoms()) {
                Solution solution = AtomComparer.compareInclusion(a, atom);
                if (solution != null) {
                    double similarity = solution.getNoRemovedEdges() * 100.0 / (a.getEmotions().size() + a.getTensions().size());
                    //If a social atom exists, then the action breaks a social norm
                    if (similarity >= MexicaParameters.SOCIAL_ACAS_CONSTANT) {
                        solution.setSocialSimilarity(similarity);
                        solutions.add(solution);
                    }
                }
            }
        }
        
        return solutions;
    }
    
    /**
     * Select one social atom from the given collection
     * @param socialAtoms Collection of social atoms
     * @param context An atom representing the context employed to obtain similar atoms
     * @return The selected social atom
     */
    public Solution selectSocialAtom(Collection<Solution> socialAtoms, SocialAtom context) {
        Solution selection = null;
        
        for (Solution s : socialAtoms) {
            
        }
        
        return selection;
    }
}