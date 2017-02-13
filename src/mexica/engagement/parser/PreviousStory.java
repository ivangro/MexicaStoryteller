package mexica.engagement.parser;

import java.util.*;
import mexica.core.Position;
import mexica.story.ActionInstantiated;

/**
 * Class containing the information of a previous story
 * @author Ivan Guerrero
 */
public class PreviousStory {
    private List<ActionInstantiated> actions;
    private Position initialLocation;
    private String name;
    
    public PreviousStory() {
        actions = new ArrayList<>();
    }
    
    public void addAction(ActionInstantiated action) {
        actions.add(action);
    }
    
    public void setInitialLocation(Position initialLocation) {
        this.initialLocation = initialLocation;
    }
    
    public List<ActionInstantiated> getActions() {
        return actions;
    }
    
    public Position getInitialLocation() {
        return initialLocation;
    }

    public void setInitialLocation(String locationStr) {
        try {
            initialLocation = Position.valueOf(locationStr);
        } catch (Exception ex) {
            initialLocation = Position.City;
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
}