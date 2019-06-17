package mexica.core;

/**
 *
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public enum Position {
    NoWhere, 
    Lake, 
    Mountains,
    Cemetery,
    Castle,
    Village,
    Farmhouse,
    Tavern,
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
            case Village:
            case Tavern:
            case Lake:
            case Cemetery:
            case Mountains:
            case Castle:
            case Farmhouse:
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
        Lake, Mountains, Cemetery, Castle, Village, Farmhouse, Tavern
    };
    
    public static Position[] getSelectablePositions() {
        return selectablePositions;
    }

    public static String getPositionAsString(Position position) {
        switch (position) {
            case Village: return "5";
            case Tavern: return "7";
            case Lake: return "1";
            case Cemetery: return "3";
            case Mountains: return "2";
            case NoWhere: return "0";
            case OtherCharactersPosition: return "b_pos";
            case Castle: return "4";
            case Farmhouse: return "6";
            case UnknownPosition: return "9";
            default: return "";
        }
    }
};