package mexica.engagement;

import java.util.*;
import mexica.core.*;

/**
 * Class representing a generic context
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class Atom {
    public static final int MIN_REGULAR_ATOMS = 4;
    public static final int MIN_FOCAL_ATOMS = 8;
    
    private int ID;
    private String cellID, atomLabel;
    private Map<TensionType, Integer> tensionCount;
    private Map<EmotionType, Integer> emotionCount;
    private List<Tension> tensions;
    private List<Condition> emotions;
    private List<Action> previousActions, nextActions;
    //private int percentage;
    /** The number of action inside the story that originated the atom */
    private int order;
    /** List with the IDs of the stories from where this atom was extracted */
    private List<Integer> storyIDs;
    /** The number of similar nodes divided by the total number of nodes, taking care of the FAs */
    private double connectivityCount;
    /** The number of similar nodes divided by the total number of nodes */
    private double neighborCount;
    private ConnectivityClass connectivityClass, neighborClass;
    /** Determines the cluster to which the atom belongs */
    private int clusterID;
    private Cluster.ClusterType clusterType;    
    /** Determines the type of node according to the number of connections with similar atoms */
    public enum ConnectivityClass {UNDEFINED, ISOLATED, REGULAR, FOCAL};
    
    public Atom() {
        this(-1);
    }
    
    public Atom(int ID) {
        setID(ID);
        tensionCount = new EnumMap<>(TensionType.class);
        emotionCount = new HashMap<>();
        //emotionCount = new EnumMap<>(EmotionType.class);
        tensions = new ArrayList<>();
        emotions = new ArrayList<>();
        previousActions = new ArrayList<>();
        nextActions = new ArrayList<>();
        storyIDs = new ArrayList<>();
        connectivityClass = ConnectivityClass.UNDEFINED;
        neighborClass = ConnectivityClass.UNDEFINED;
    }
    
    public final void setID(int ID) {
        this.ID = ID;
        atomLabel = "Atom " + cellID + "-" + ID;
    }
    
    /**
     * Adds the tension counters read from the file in the following order:
     *  AD, Lr, Hr, Pd, Pr, Ce, Lc
     * @param counters 
     */
    public void addTensionCounters(String... counters) {
        int[] values = new int[counters.length];
        for (int i=0; i<counters.length; i++) {
            values[i] = Integer.parseInt(counters[i]);
        }
        getTensionCount().put(TensionType.ActorDead, values[0]);
        getTensionCount().put(TensionType.LifeAtRisk, values[1]);
        getTensionCount().put(TensionType.HealthAtRisk, values[2]);
        getTensionCount().put(TensionType.PotencialDanger, values[3]);
        getTensionCount().put(TensionType.Prisoner, values[4]);
        getTensionCount().put(TensionType.ClashingEmotions, values[5]);
        getTensionCount().put(TensionType.LoveCompetition, values[6]);
    }
    
    /**
     * Adds the emotion counters read from the file in the following order:
     *  type1, type2, type3, type4
     * @param counters 
     */
    public void addEmotionCounters(String... counters) {
        int[] values = new int[counters.length];
        for (int i=0; i<counters.length; i++) {
            values[i] = Integer.parseInt(counters[i]);
            emotionCount.put(EmotionType.getEmotionFromValue(i+1), values[i]);
        }
//        getEmotionCount().put(EmotionType.getEmotionFromValue(1), values[0]);
//        getEmotionCount().put(EmotionType.getEmotionFromValue(2), values[1]);
//        getEmotionCount().put(EmotionType.getEmotionFromValue(3), values[2]);
//        getEmotionCount().put(EmotionType.getEmotionFromValue(4), values[3]);
    }
    
    /**
     * Adds the given emotion to the atom
     * @param elements character, intensity, emotionType, character?
     */
    public void addEmotion(String... elements) {
        Condition cond = new Condition();
        cond.setConditionType(ConditionType.Emotion);
        cond.setCharacterA(elements[0]);
        int intensity = cond.analyzeNumber(elements[1]);
        cond.setIntensity(intensity);
        int emotionType = Integer.parseInt(elements[2]);
        cond.setEmotionType(emotionType);
        cond.setCharacterB(elements[3]);
        
        getEmotions().add(cond);
    }
    
    /**
     * Adds the given tension to the atom
     * @param elements tensionType, character, character?
     */
    public void addTension(String... elements) {
        Tension tension = new Tension();
        tension.setConditionType(ConditionType.Tension);
        tension.setTensionType(elements[0]);
        tension.setCharacterA(elements[1]);
        tension.setCharacterB(elements[2]);
        getTensions().add(tension);
    }
    
    public void addTension(TensionType type, String... characters) {
        Tension tension = new Tension();
        tension.setConditionType(ConditionType.Tension);
        tension.setTensionType(type);
        tension.setCharacterA(characters[0]);
        tension.setCharacterB(characters[1]);
        getTensions().add(tension);
    }
    
    /**
     * Adds the given action as one of the possible following actions
     * @param elements character actionName character?
     */
    public void addNextAction(String... elements) {
        Action action = new Action();
        action.setActionName(elements[1]);
        action.setCharacters(elements[0], elements[2]);
        getNextActions().add(action);
    }
    
    /**
     * Adds the given action as one of the possible previous actions
     * @param elements character actionName character?
     */
    public void addPreviousAction(String... elements) {
        Action action = new Action();
        action.setActionName(elements[1]);
        action.setCharacters(elements[0], elements[2]);
        getPreviousActions().add(action);
    }
    
    /**
     * @return the tensionCount
     */
    public Map<TensionType, Integer> getTensionCount() {
        return tensionCount;
    }

    /**
     * @return the emotionCount
     */
    public Map<EmotionType, Integer> getEmotionCount() {
        return emotionCount;
    }

    /**
     * @return the tensions
     */
    public List<Tension> getTensions() {
        return tensions;
    }

    /**
     * @return the emotions
     */
    public List<Condition> getEmotions() {
        return emotions;
    }

    /**
     * @return the previousActions
     */
    public List<Action> getPreviousActions() {
        return previousActions;
    }

    /**
     * @return the nextActions
     */
    public List<Action> getNextActions() {
        return nextActions;
    }
    
    /**
     * @return the cellID
     */
    public String getCellID() {
        return cellID;
    }

    /**
     * @param cellID the cellID to set
     */
    public void setCellID(String cellID) {
        this.cellID = cellID;
        atomLabel = "Atom " + cellID + "-" + ID;
    }
    
    @Override
    public String toString() {
        return atomLabel;
    }
        
    public String toStringWithContext() {
        String text = toString() + "\nEmotions\n";
        for (Condition c : emotions) {
            text += c.toStringWithStatus() + "\n";
        }
        text += "Tensions\n";
        for (Condition c : tensions) {
            text += c.toStringWithStatus() + "\n";
        }
        return text;
    }
    
    public String toStringWithNextActions() {
        String text = atomLabel + "\nNext Actions\n";
        for (Action act : nextActions) {
            List<String> characters = act.getCharacters();
            text += characters.get(0) + " " + act.getActionName() + ((characters.size() > 1) ? " " + characters.get(1) : "") + "\n";
        }
        return text;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Atom) {
            Atom a = (Atom)obj;
            return a.ID == ID && (a.cellID == null ? cellID == null : a.cellID.equals(cellID));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.ID;
        hash = 53 * hash + Objects.hashCode(this.cellID);
        return hash;
    }
    
    public void setAtomLabel(String label) {
        this.atomLabel = label;
    }
    
    public String getAtomLabel() {
        return atomLabel;
    }
    
    public int getOrder() {
        return order;
    }
    
    public void setOrder(int order) {
        this.order = order;
    }
    
    public void addStoryID(int storyID) {
        storyIDs.add(storyID);
    }
    
    public List<Integer> getStoryIDs() {
        return storyIDs;
    }
    
    public void addAllStoryIDs(List<Integer> storyIDs) {
        for (Integer id : storyIDs) {
            this.storyIDs.add(id);
        }
    }
    
    public boolean containsAnyId(ArrayList<Integer> storyIDs) {
        boolean res = false;
        
        for (Integer id : storyIDs) {
            if (this.storyIDs.contains(id))
                return true;
        }
        
        return res;
    }
    
    public void setConnectivityCount(Collection<Atom> atoms, int totalNextActions, int totalAtoms) {
        connectivityCount = 0;
        for (Atom a : atoms) {
            int na = a.getNextActions().size();
            connectivityCount += na;
        }
        connectivityCount /= totalNextActions;
        setConnectivityClass(connectivityCount);
        
        //neighborCount = (totalAtoms > 0) ? 1.0 * atoms.size() / totalAtoms : 0;
        neighborCount = atoms.size();
        setNeighborClass(neighborCount);
    }
    
    public double getNeighborCount() {
        return neighborCount;
    }
    
    private void setNeighborClass(double neighbors) {
        if (neighbors < MIN_REGULAR_ATOMS)
            neighborClass = ConnectivityClass.ISOLATED;
        else if (neighbors < MIN_FOCAL_ATOMS)
            neighborClass = ConnectivityClass.REGULAR;
        else
            neighborClass = ConnectivityClass.FOCAL;
    }
    
    @Deprecated
    private void setNeighborClassOld(double neighbors) {
        if (neighbors < 0.05)
            neighborClass = ConnectivityClass.ISOLATED;
        else if (neighbors < 0.1)
            neighborClass = ConnectivityClass.REGULAR;
        else
            neighborClass = ConnectivityClass.FOCAL;
    }

    @Deprecated
    private void setConnectivityClass(double count) {
        if (count < 0.05)
            connectivityClass = ConnectivityClass.ISOLATED;
        else if (count < 0.1)
            connectivityClass = ConnectivityClass.REGULAR;
        else
            connectivityClass = ConnectivityClass.FOCAL;
    }
    
    public double getConnectivityCount() {
        return connectivityCount;
    }

    @Deprecated
    public ConnectivityClass getConnectivityClass() {
        return connectivityClass;
    }
    
    public ConnectivityClass getNeighborClass() {
        return neighborClass;
    }
    
    public int getClusterID() {
        return clusterID;
    }
    
    public void setClusterID(int clusterID) {
        this.clusterID = clusterID;
    }
    
    public Cluster.ClusterType getClusterType() {
        return clusterType;
    }
    
    public void setClusterType(Cluster.ClusterType type) {
        this.clusterType = type;
    }
}