package mexica.engagement;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Class to store clusters of atoms, groups of atoms which are similar between them.<br>
 * @author Ivan Guerrero
 */
public class Cluster {
    private int id;
    private List<Atom> atomList;
    private Map<Atom.ConnectivityClass, Integer> atoms;
    private ClusterType clusterType;    
    public enum ClusterType {ISLAND, TOWN, CITY, UNDEFINED};
    /** Determines the subclassification of the cluster according to the type of atoms inside it */
    private ClusterAge clusterAge;
    public enum ClusterAge {MATURE, IN_DEV, IMMATURE, UNDEFINED};    
    /** The number of atoms inside the cluster */
    private int atomCount;
    /** The number of atoms inside the knowledge base */
    private int totalAtoms;
    private static DecimalFormat formatter;
    /** The maximal of the minimum distances between any two nodes inside the cluster */
    private double diameter;
    /** The average of the minimum distances between any two nodes inside the cluster */
    private double averageMinDistance;
    
    static {
        formatter = new DecimalFormat("0.00");
    }
    
    public Cluster(int totalAtoms) {
        clusterType = ClusterType.UNDEFINED;
        atoms = new HashMap<>();
        atomList = new ArrayList<>();
        this.totalAtoms = totalAtoms;
    }
    
    public void addAtom(Atom atom) {
        Atom.ConnectivityClass connClass = atom.getNeighborClass();
        if (getAtoms().containsKey(connClass))
            getAtoms().put(connClass, getAtoms().get(connClass) + 1);
        else
            getAtoms().put(connClass, 1);
        atomCount++;
        atomList.add(atom);
        setClusterType();
        setClusterAge();
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the atoms
     */
    public Map<Atom.ConnectivityClass, Integer> getAtoms() {
        return atoms;
    }
    
    public ClusterType getClusterType() {
        return clusterType;
    }
    
    private void setClusterType() {
        double atomRatio = 1.0 * atomCount/totalAtoms;
        if (atomRatio < 0.2)
            clusterType = ClusterType.ISLAND;
        else if (atomRatio < 0.5)
            clusterType = ClusterType.TOWN;
        else if (atomRatio >= 0.5)
            clusterType = ClusterType.CITY;
    }
    
    private void setClusterAge() {
        int regular, focal;
        regular = atoms.containsKey(Atom.ConnectivityClass.REGULAR) ? atoms.get(Atom.ConnectivityClass.REGULAR) : 0;
        focal = atoms.containsKey(Atom.ConnectivityClass.FOCAL) ? atoms.get(Atom.ConnectivityClass.FOCAL) : 0;
        if (focal > 0)
            clusterAge = ClusterAge.MATURE;
        else if (regular > 0)
            clusterAge = ClusterAge.IN_DEV;
        else
            clusterAge = ClusterAge.IMMATURE;
        
        /*
        int maxAtoms = 0;
        Atom.ConnectivityClass maxClass = Atom.ConnectivityClass.UNDEFINED;
        for (Atom.ConnectivityClass connClass : atoms.keySet()) {
            if (atoms.get(connClass) > maxAtoms){
                maxAtoms = atoms.get(connClass);
                maxClass = connClass;
            }
        }
        switch (maxClass){
            case ISOLATED: clusterAge = ClusterAge.IMMATURE; break;
            case REGULAR: clusterAge = ClusterAge.IN_DEV; break;
            case FOCAL: clusterAge = ClusterAge.MATURE; break;
        }*/
    }
    
    public ClusterAge getClusterAge() {
        return clusterAge;
    }
    
    public boolean containsAtom(Atom a) {
        return atomList.contains(a);
    }
    
    @Override
    public String toString() {
        return "Cluster " + id + " [" + getClusterType().name() + "] (" + atomCount + ")";
    }
    
    /**
     * Obtains the types of atoms inside the cluster and their number
     * @return A textual representation of the types and number of atoms inside the cluster
     */
    public String getDescription() {
        String text = toString() + "\n";
        for (Atom.ConnectivityClass connClass : atoms.keySet()) {
            text += "\t" + connClass.name() + "-" + atoms.get(connClass) + "\n";
        }
        
        return text;
    }
    
    /**
     * Obtains a list with the atom percentages of each type and the subclassification of the cluster
     * @return A textual representation of the atoms inside the cluster displaying their percetanges
     */
    public String getDetails() {
        String text = toString() + "\n";
        double eval = 0;
        for (Atom.ConnectivityClass connClass : atoms.keySet()) {
            int noAtoms = atoms.get(connClass);
            double ratio =  1.0 * noAtoms / totalAtoms;
            text += "\tNode type: " + connClass.name() + "- Atoms: " + noAtoms + "- Ratio: " + formatter.format(ratio) + "\n";
        }
        text += "\tEval: " + formatter.format(eval) + "\tAge: " + clusterAge.name();
        
        return text;
    }
    
    public int getAtomCount(Atom.ConnectivityClass connClass) {
        if (atoms.containsKey(connClass))
            return atoms.get(connClass);
        else
            return 0;
    }
    
    public List<Atom> getAtomList() {
        return atomList;
    }
    
    public int getAtomCount() {
        return atomCount;
    }
    
    /**
     * Obtains the number of next actions inside the cluster
     * @return 
     */
    public int getNextActionsCount() {
        int count = 0;
        for (Atom a : atomList) {
            count += a.getNextActions().size();
        }
        return count;
    }
    
    /**
     * @return the diameter
     */
    public double getDiameter() {
        return diameter;
    }

    /**
     * @param diameter the diameter to set
     */
    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

    /**
     * @return the averageMinDistance
     */
    public double getAverageMinDistance() {
        return averageMinDistance;
    }

    /**
     * @param averageMinDistance the averageMinDistance to set
     */
    public void setAverageMinDistance(double averageMinDistance) {
        this.averageMinDistance = averageMinDistance;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Cluster) {
            Cluster c = (Cluster)obj;
            return c.id == id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.id;
        return hash;
    }
}