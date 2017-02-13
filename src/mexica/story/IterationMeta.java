package mexica.story;

import java.util.HashMap;
import java.util.Map;
import mexica.engagement.Atom;

/**
 * Class to store the metadata of the story 
 * @author Ivan Guerrero
 */
public class IterationMeta {
    /** Determines the ID of the knowledge base utilised during the iteration */
    private int mexicaID;
    /** Determines the number of ER cycles of the story */
    private int iteration;
    /** Determines the number of illogical actions found in the story */
    private int illogicalActions;
    /** Determines the number of irrelevant actions found in the story */
    private int irrelevantActions;
    /** Determines the number of missing conditions of the actions found in the story */
    private int missingConditions;
    /** Stores the atoms utilised during the generation of the story */
    private Map<Atom.ConnectivityClass, Integer> atomsEmployed;
    private Map<String, Integer> atoms;
    /** Stores the clusterID and the times it has been utilised */
    private Map<Integer, Integer> clustersEmployed;
    
    public IterationMeta(int iteration) {
        this.iteration = iteration;
        atomsEmployed = new HashMap<>();
        atoms = new HashMap<>();
        clustersEmployed = new HashMap<>();
    }
    
    public int getMexicaID() {
        return mexicaID;
    }
    
    public void setMexicaID(int mexicaID) {
        this.mexicaID = mexicaID;
    }

    /**
     * @return the iteration
     */
    public int getIteration() {
        return iteration;
    }

    /**
     * @return the illogicalActions
     */
    public int getIllogicalActions() {
        return illogicalActions;
    }

    /**
     * @param illogicalActions the illogicalActions to set
     */
    public void addIllogicalAction() {
        this.illogicalActions++;
    }

    /**
     * @return the irrelevantActions
     */
    public int getIrrelevantActions() {
        return irrelevantActions;
    }

    /**
     * @param irrelevantActions the irrelevantActions to set
     */
    public void addIrrelevantActions() {
        this.irrelevantActions++;
    }

    public void setIllogicalActions(int illogicalActions) {
        this.illogicalActions = illogicalActions;
    }

    /**
     * @param iteration the iteration to set
     */
    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    /**
     * @param irrelevantActions the irrelevantActions to set
     */
    public void setIrrelevantActions(int irrelevantActions) {
        this.irrelevantActions = irrelevantActions;
    }

    /**
     * @return the missingConditions
     */
    public int getMissingConditions() {
        return missingConditions;
    }

    /**
     * @param missingConditions the missingConditions to set
     */
    public void setMissingConditions(int missingConditions) {
        this.missingConditions = missingConditions;
    }
    
    public void addAtom(Atom.ConnectivityClass atomClass, int clusterID, String atomID) {
        if (atoms.containsKey(atomID))
            atoms.put(atomID, atoms.get(atomID) + 1);
        else
            atoms.put(atomID, 1);
        
        if (atomsEmployed.containsKey(atomClass))
            atomsEmployed.put(atomClass, atomsEmployed.get(atomClass)+1);
        else
            atomsEmployed.put(atomClass, 1);
        
        if (clustersEmployed.containsKey(clusterID))
            clustersEmployed.put(clusterID, clustersEmployed.get(clusterID) + 1);
        else
            clustersEmployed.put(clusterID, 1);
    }
    
    public Map<Atom.ConnectivityClass, Integer> getAtomsEmployed() {
        return atomsEmployed;
    }
    
    /** 
     * Obtains a list with the IDs of the atoms employed during the story 
     * @return A map with atomIDs and the number of times it was utilized inside the story
     */
    public Map<String, Integer> getAtoms() {
        return atoms;
    }
    
    public Map<Integer, Integer> getClustersEmployed() {
        return clustersEmployed;
    }
}