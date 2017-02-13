package mexica.story.guidelines;

/**
 * Result of comparing the current story with a previous story
 * @author Ivan Guerrero
 */
public class StoryNovelty {
    /** Number of similar pairs of actions in the current story found in a previous story */
    private int similarPairs;
    /** Total number of similar pairs of actions in the previous story */
    private int totalPairs;
    /** ID of the previous story being analyzed */
    private int storyID;
    
    public void addSimilarPair() {
        similarPairs++;
    }

    public void setTotalPairs(int totalPairs) {
        this.totalPairs = totalPairs;
    }

    void setStoryID(int storyID) {
        this.storyID = storyID;
    }

    /**
     * Obtains the novelty percentage of the previous story with the given storyID
     * @return  similarPairs / totalPairs
     */
    public double getNovelty() {
        return similarPairs * 100.0 / totalPairs;
    }

    public int getStoryID() {
        return storyID;
    }
}
