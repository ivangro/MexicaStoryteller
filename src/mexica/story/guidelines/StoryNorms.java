package mexica.story.guidelines;

import java.util.*;

/**
 *
 * @author Ivan Guerrero
 */
public class StoryNorms {
    private int storyID;
    private List<Integer> socialNorms;
    
    public StoryNorms() { 
        socialNorms = new ArrayList<>();
    }

    public void addSocialNorms(int year, int value) {
        socialNorms.add(year, value);
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
    
    public List<Integer> getSocialNorms() {
        return socialNorms;
    }
    
    @Override
    public String toString() {
        return storyID + "\nSocial Norms[" + socialNorms + "]";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StoryNorms) {
            StoryNorms t = (StoryNorms)obj;
            return t.storyID == storyID;
        }
        
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + this.storyID;
        return hash;
    }
}