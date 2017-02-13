package mexica.groups;

import java.util.*;
import mexica.CharacterName;

/**
 * Class representing a set of hierarchies making a partition of the available characters
 * @author Ivan Guerrero
 */
public class HierarchicalGroup {
    private String groupName;
    private List<Hierarchy> hierarchies;
    
    public HierarchicalGroup() {
        hierarchies = new ArrayList<>();
    }

    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName the groupName to set
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return the hierarchies
     */
    public List<Hierarchy> getHierarchies() {
        List<Hierarchy> list = new ArrayList<>();
        list.addAll(hierarchies);
        return list;
    }

    /**
     * @param hierarchies the hierarchies to set
     */
    public void addHierarchy(Hierarchy hierarchy) {
        hierarchies.add(hierarchy);
    }
    
    public Hierarchy getHierarchy(CharacterName name) {
        for (Hierarchy h : hierarchies) {
            if (h.getElements().contains(name))
                return h;
        }
        
        return null;
    }
    
    @Override
    public String toString() {
        return groupName;
    }
    
    public String toStringWithHierarchies() {
        return groupName + "\n" + hierarchies;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HierarchicalGroup) {
            HierarchicalGroup gp = (HierarchicalGroup)obj;
            return gp.getGroupName().equalsIgnoreCase(this.groupName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.groupName);
        return hash;
    }
}