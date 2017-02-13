package mexica.groups;

import java.util.*;
import mexica.CharacterName;

/**
 * Class representing a group of characters inside a hierarchical group
 * @author Ivan Guerrero
 */
public class Hierarchy {
    private HierarchicalGroup group;
    private String hierarchyName;
    private int classification;
    private Set<CharacterName> elements;
    
    public Hierarchy() {
        elements = EnumSet.noneOf(CharacterName.class);
    }

    /**
     * @return the hierarchyName
     */
    public String getHierarchyName() {
        return hierarchyName;
    }

    /**
     * @param hierarchyName the hierarchyName to set
     */
    public void setHierarchyName(String hierarchyName) {
        this.hierarchyName = hierarchyName;
    }

    /**
     * @return the classification
     */
    public int getClassification() {
        return classification;
    }

    /**
     * @param classification the classification to set
     */
    public void setClassification(int classification) {
        this.classification = classification;
    }

    /**
     * @return the elements
     */
    public Set<CharacterName> getElements() {
        return EnumSet.copyOf(elements);
    }

    /**
     * @param elements the elements to set
     */
    public void addElement(CharacterName element) {
        elements.add(element);
    }
    
    /**
     * @return the group
     */
    public HierarchicalGroup getHierarchicalGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setHierarchicalGroup(HierarchicalGroup group) {
        this.group = group;
    }
    
    @Override
    public String toString() {
        return hierarchyName + " " + classification + " " + elements.toString();
    }

    public void parseCharacters(String charactersStr) {
        String[] characters = charactersStr.split(" ");
        for (String character : characters) {
            elements.add(CharacterName.valueOf(character));
        }
    }
}