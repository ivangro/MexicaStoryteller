package mexica.tools;

import java.util.*;
import mexica.core.*;

/**
 * Class to manage the available locations in a story
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class PositionFactory {
    private static PositionFactory instance = new PositionFactory();
    
    private List<Position> availablePositions;
    
    private PositionFactory() {
        availablePositions = new ArrayList<>(Arrays.asList(Position.values()));
        availablePositions.remove(Position.NotDefined);
        availablePositions.remove(Position.OtherCharactersPosition);
        availablePositions.remove(Position.NoWhere);
        availablePositions.remove(Position.UnknownPosition);
    }
    
    public static PositionFactory getInstance() {
        return instance;
    }
    
    public List<Position> getAvailablePositions() {
        return availablePositions;
    }
    
    public Position getPosition(String name) {
        return Position.valueOf(name);
    }
    
    /**
     * Returns the element in the given position, according to the available positions list
     */
    public Position getPosition(int index) {
        return availablePositions.get(index);
    }
}
