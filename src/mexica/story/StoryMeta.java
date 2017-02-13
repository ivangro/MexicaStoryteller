package mexica.story;

import java.util.*;
import mexica.engagement.Atom;

/**
 * Class to store the metadata of a story
 * @author Ivan Guerrero
 */
public class StoryMeta {
    private int storyID;
    /** Determines if the story has been concluded or not */
    private boolean finished;
    /** Determines the reason why the story has been ended */
    private String reason;
    /** Determines the name of the file from where the current story was obtained */
    private String name;
    /** Determines if in the current iteration an impassed has been detected */
    private boolean impasseDetected;
    /** Determines the number of impasses ocurred during the story */
    private int impasses;
    /** Determines the number of ER cycles of the story */
    private int currentIteration;
    /** Stores the information of each of the ER cycles of the story */
    private Map<Integer, IterationMeta> iterations;
    
    public StoryMeta() {
        iterations = new HashMap<>();
        currentIteration = 0;
    }

    /**
     * @return the storyID
     */
    public int getStoryID() {
        return storyID;
    }

    /**
     * @param storyID the storyID to set
     */
    public void setStoryID(int storyID) {
        this.storyID = storyID;
    }

    /**
     * @return the finished
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * @param finished the finished to set
     */
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the current iteration
     */
    public int getIteration() {
        return currentIteration;
    }

    /**
     * @param iteration the iteration to set
     */
    public void setIteration(int iteration) {
        iterations.clear();
        this.currentIteration = iteration;
    }

    /**
     * @return the impasseDetected
     */
    public boolean isImpasseDetected() {
        return impasseDetected;
    }

    /**
     * @param impasseDetected the impasseDetected to set
     */
    public void setImpasseDetected(boolean impasseDetected) {
        this.impasseDetected = impasseDetected;
        if (impasseDetected)
            addImpasse();
    }

    /**
     * @return the impasses
     */
    public int getImpasses() {
        return impasses;
    }

    /**
     * @param impasses the impasses to set
     */
    public void setImpasses(int impasses) {
        this.impasses = impasses;
    }

    /**
     * Adds a new iteration to the story
     */
    public final void addIteration() {
        currentIteration++;
        iterations.put(currentIteration, new IterationMeta(currentIteration));
    }

    public void addImpasse() {
        impasses++;
    }
    
    public int getIllogicalActions(int iteration) {
        IterationMeta meta = iterations.get(iteration);
        if (meta != null)
            return meta.getIllogicalActions();
        else return 0;
    }
    
    public void addIllogicalAction(int iteration) {
        IterationMeta meta = iterations.get(iteration);
        if (meta == null) {
            meta = new IterationMeta(iteration);
            iterations.put(iteration, meta);
        }
        meta.addIllogicalAction();
    }
    
    public int getIrrelevantActions(int iteration) {
        IterationMeta meta = iterations.get(iteration);
        if (meta != null)
            return meta.getIrrelevantActions();
        else return 0;
    }
    
    public void addIrrelevantAction(int iteration) {
        IterationMeta meta = iterations.get(iteration);
        if (meta == null) {
            meta = new IterationMeta(iteration);
            iterations.put(iteration, meta);
        }
        meta.addIrrelevantActions();
    }
    
    public void setMexicaID(int iteration, int mexicaID) {
        IterationMeta meta = iterations.get(iteration);
        if (meta == null) {
            meta = new IterationMeta(iteration);
            iterations.put(iteration, meta);
        }
        meta.setMexicaID(mexicaID);
    }
    
    public int getMexicaID(int iteration) {
        IterationMeta meta = iterations.get(iteration);
        if (meta == null) {
            meta = new IterationMeta(iteration);
            iterations.put(iteration, meta);
        }
        return meta.getMexicaID();
    }

    public void setIllogicalActions(int iteration, int illogicalActions) {
        IterationMeta meta = iterations.get(iteration);
        if (meta == null) {
            meta = new IterationMeta(iteration);
            iterations.put(iteration, meta);
        }
        meta.setIllogicalActions(illogicalActions);
    }

    public void setMissingConditions(int iteration, int missingConditions) {
        IterationMeta meta = iterations.get(iteration);
        if (meta == null) {
            meta = new IterationMeta(iteration);
            iterations.put(iteration, meta);
        }
        meta.setMissingConditions(missingConditions);
    }

    public void setIrrelevantActions(int iteration, int irrelevantActions) {
        IterationMeta meta = iterations.get(iteration);
        if (meta == null) {
            meta = new IterationMeta(iteration);
            iterations.put(iteration, meta);
        }
        meta.setIrrelevantActions(irrelevantActions);
    }
    
    public void addAtom(int iteration, Atom.ConnectivityClass atomClass, int clusterID, String atomID) {
        IterationMeta meta = iterations.get(iteration);
        if (meta == null) {
            meta = new IterationMeta(iteration);
            iterations.put(iteration, meta);
        }
        meta.addAtom(atomClass, clusterID, atomID);
    }
    
    public Map<Atom.ConnectivityClass, Integer> getAtomsEmployed(int iteration) {
        IterationMeta meta = iterations.get(iteration);
        if (meta == null) {
            meta = new IterationMeta(iteration);
            iterations.put(iteration, meta);
        }
        return meta.getAtomsEmployed();
    }
    
    public Map<Integer, Integer> getClustersEmployed(int iteration) {
        IterationMeta meta = iterations.get(iteration);
        if (meta == null) {
            meta = new IterationMeta(iteration);
            iterations.put(iteration, meta);
        }
        return meta.getClustersEmployed();
    }
    
    public Map<String, Integer> getAtoms(int iteration) {
        IterationMeta meta = iterations.get(iteration);
        if (meta == null) {
            meta = new IterationMeta(iteration);
            iterations.put(iteration, meta);
        }
        
        return meta.getAtoms();
    }
    
    public IterationMeta getIterationMeta(int iteration) {
        if (iterations.containsKey(iteration))
            return iterations.get(iteration);
        else
            return new IterationMeta(iteration);
    }
}