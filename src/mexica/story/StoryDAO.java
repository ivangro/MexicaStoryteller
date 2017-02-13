package mexica.story;

import java.util.*;

/**
 *  Class that stores all the data related to a story
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class StoryDAO {
    private List<ActionInstantiated> actions;
    private List<ConditionInstantiated> missingConditions;
    private List<TextInstantiated> storyText;
    private int currentYear = 1;
    private int actionID = 1;
    
    public StoryDAO() {
        actions = new ArrayList<>();
        missingConditions = new ArrayList<>();
        storyText = new ArrayList<>();
    }
    
    public void restart(boolean cleanActions) {
        if (cleanActions) {
            setActions(new ArrayList<ActionInstantiated>());
            actionID = 1;
        }
        
        setMissingConditions(new ArrayList<ConditionInstantiated>());
        setStoryText(new ArrayList<TextInstantiated>());
        setCurrentYear(1);
    }
    
    public List<ActionInstantiated> getActions() {
        return actions;
    }
    
    public void addAction(int index, ActionInstantiated action) {
        action.setActionID(actionID++);
        actions.add(index, action);
    }
    
    public void addAction(ActionInstantiated action) {
        action.setActionID(actionID++);
        actions.add(action);
    }
    
    public List<ConditionInstantiated> getMissingConditions() {
        return missingConditions;
    }
    
    public List<TextInstantiated> getStoryText() {
        return storyText;
    }
    
    /**
     * Obtains the year being analyzed in the story
     */
    public int getCurrentYear() {
        return currentYear;
    }
    
    public void setCurrentYear(int year) {
        this.currentYear = year;
    }

    /**
     * @param actions the actions to set
     */
    public void setActions(List<ActionInstantiated> actions) {
        this.actions = actions;
    }

    /**
     * @param missingConditions the missingConditions to set
     */
    private void setMissingConditions(List<ConditionInstantiated> missingConditions) {
        this.missingConditions = missingConditions;
    }

    /**
     * @param storyText the storyText to set
     */
    public void setStoryText(List<TextInstantiated> storyText) {
        this.storyText = storyText;
    }

    void incrementCurrentYear() {
        currentYear++;
    }

    void addMissingCondition(ConditionInstantiated condition) {
        missingConditions.add(condition);
    }
}
