package mexica.groups;

import java.util.*;
import mexica.CharacterName;

/**
 * Repository for the hierarchical groups in the system
 * @author Ivan Guerrero
 */
public class HierarchyStore {
    private List<HierarchicalGroup> groups;
    private HierarchyParser parser;
    private final String GENDER_NAME = "Gender_Hierarchy", SOCIAL_NAME = "Social_Class_Hierarchy";
    
    public HierarchyStore(String path) {
        parser = new HierarchyParser(path);
    }
    
    public void loadData() {
        parser.parseFile();
        groups = parser.getGroups();
    }
    
    public List<HierarchicalGroup> getGroups() {
        List<HierarchicalGroup> list = new ArrayList<>();
        list.addAll(groups);
        
        return list;
    }
    
    public HierarchicalGroup getSocialGroup() {
        return getGroup(SOCIAL_NAME);
    }
    
    public HierarchicalGroup getGenderGroup() {
        return getGroup(GENDER_NAME);
    }
    
    public HierarchicalGroup getGroup(String name) {
        for (HierarchicalGroup gp : groups) {
            if (gp.getGroupName().equalsIgnoreCase(name))
                return gp;
        }
        return null;
    }
    
    /**
     * Obtains the distance between the classifications of both hierarchies
     * @param h1
     * @param h2
     * @return >0 if the first hierarchy is higher than the second, <0 if the second is higher
     */
    public int getDistance(Hierarchy h1, Hierarchy h2) {
        if (!h1.getHierarchicalGroup().equals(h2.getHierarchicalGroup()))
            throw new Error("Incompatible hierarchical groups");
        return h1.getClassification() - h2.getClassification();
    }

    /**
     * Obtains the social relationship between the given characters.
     * @param charactersList The list of characters to be compared. If only one character is receiver, the result is 0.
     * @return A numeric value representing the distance between the classification of the characters inside the social hierarchy
     */
    public int getSocialRelationship(List<CharacterName> charactersList) {
        if (charactersList.size() < 2)
            return 0;
        CharacterName performer, receiver;
        performer = charactersList.get(0);        
        receiver = charactersList.get(1);
        HierarchicalGroup socialGroup = getSocialGroup();
        Hierarchy perfHierarchy = socialGroup.getHierarchy(performer);
        Hierarchy recHierarchy = socialGroup.getHierarchy(receiver);
        return getDistance(perfHierarchy, recHierarchy);
    }
    
    /**
     * Obtains the gender relationship between the given characters
     * @param charactersList
     * @return A numeric value representing the distance between the classification of the characters inside the gender hierarchy
     */
    public int getGenderRelationship(List<CharacterName> charactersList) {
        if (charactersList.size() < 2)
            return 0;
        CharacterName performer, receiver;
        performer = charactersList.get(0);
        receiver = charactersList.get(1);
        HierarchicalGroup genderGroup = getGenderGroup();
        Hierarchy perfHierarchy = genderGroup.getHierarchy(performer);
        Hierarchy recHierarchy = genderGroup.getHierarchy(receiver);
        return getDistance(perfHierarchy, recHierarchy);
    }
}