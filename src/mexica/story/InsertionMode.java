package mexica.story;

/**
 * Insertion mechanisms utilised to incorporate actions to a story
 * @author ivang_000
 */
public enum InsertionMode {
    Engagement, Reflection, Impasse, InitialAction;
    
    public static String getAbbreviation(InsertionMode mode) {
        switch (mode) {
            case Engagement: return "E";
            case Reflection: return "R";
            case Impasse: return "I";
            case InitialAction: return "IA";
            default: return "";
        }
    }
    
    public static InsertionMode getInsertionMode(String abbreviation) {
        switch (abbreviation) {
            case "R": return Reflection;
            case "E": return Engagement;
            case "I": return Impasse;
            case "IA": return InitialAction;
            default: return null;
        }
    }
}
