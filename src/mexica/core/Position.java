package mexica.core;

/**
 *
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public enum Position {
    NoWhere, 
    Lake, 
    Mountains, 
    Market, 
    Palace, 
    City, 
    Temple, 
    Jail, 
    OtherCharactersPosition,
    UnknownPosition, 
    NotDefined;
    
    /**
     * Determines if the given position can be employed for a character
     * @param p
     * @return 
     */
    public static boolean isValidPosition(Position p) {
        switch (p) {
            case City:
            case Jail:
            case Lake:
            case Market:
            case Mountains:
            case Palace:
            case Temple:
            case UnknownPosition:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * List of positions selectable in a story
     */
    private static final Position[] selectablePositions = new Position[] {
        Lake, Mountains, Market, Palace, City, Temple, Jail
    };
    
    public static Position[] getSelectablePositions() {
        return selectablePositions;
    }

    public static String getPositionAsString(Position position) {
        switch (position) {
            case City: return "5";
            case Jail: return "7";
            case Lake: return "1";
            case Market: return "3";
            case Mountains: return "2";
            case NoWhere: return "0";
            case OtherCharactersPosition: return "b_pos";
            case Palace: return "4";
            case Temple: return "6";
            case UnknownPosition: return "9";
            default: return "";
        }
    }
};