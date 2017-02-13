package subgraph;

import java.util.Objects;

/**
 *
 * @author Ivan Guerrero
 */
public class Node implements INode {
    private String ID;
    
    public Node() {}
    
    public Node(String ID) {
        this.ID = ID;
    }

    /**
     * @return the ID
     */
    @Override
    public String getID() {
        return ID;
    }

    /**
     * @param ID the ID to set
     */
    public void setID(String ID) {
        this.ID = ID;
    }
    
    @Override
    public String toString() {
        return ID;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.ID);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            return ((Node)obj).getID().equals(ID);
        }
        return false;
    }
}
