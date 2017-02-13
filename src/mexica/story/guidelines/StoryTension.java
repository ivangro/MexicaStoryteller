package mexica.story.guidelines;

import java.util.*;

/**
 * Class to store the tension values of a story
 * @author Ivan Guerrero
 */
public class StoryTension {
    private List<Integer> tensions;
    private List<TensionTendency> tendencies;
    private int storyID;
    
    public StoryTension() {
        tensions = new ArrayList<>();
        tendencies = new ArrayList<>();
    }
    
    public void addTension(int year, int tension) {
        tensions.add(year, tension);
        addTendency(year, tension);
    }
    
    public int getTension(int year) {
        return tensions.get(year);
    }
    
    private void addTendency(int year, int tension) {
        if (year > 0) {
            int prevTension = tensions.get(year-1);
            if ((tension - prevTension) > 0)
                tendencies.add(TensionTendency.TendencyUp);
            else if ((tension - prevTension) < 0)
                tendencies.add(TensionTendency.TendencyDown);
            else
                tendencies.add(TensionTendency.SameTendency);
        }
    }
    
    public List<Integer> getTensions() {
        List<Integer> tensions = new ArrayList<>();
        for (Integer i : this.tensions) {
            tensions.add(i);
        }
        return tensions;
    }
    
    public List<TensionTendency> getTendencies() {
        List<TensionTendency> tendencies = new ArrayList<>();
        for (TensionTendency tt : this.tendencies) {
            tendencies.add(tt);
        }
        return tendencies;
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
    
    @Override
    public String toString() {
        return storyID + "\nTensions[" + tensions + "]\nTendencies[" + tendencies + "]";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StoryTension) {
            StoryTension t = (StoryTension)obj;
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