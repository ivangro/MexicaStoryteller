package mexica.social;

/**
 * Defines if a condition has been inserted, removed or remained unchanged
 * @author Ivan Guerrero
 */
public enum ConditionStatus {
    Inserted, Removed, Remained, NotDefined;
    
    /**
     * Obtains the status for a condition from the text read from the atoms file
     * @param abbreviation
     * @return
     */
    public static ConditionStatus fromAbbreviation(String abbreviation) {
        switch (abbreviation) {
            case "+": return Inserted;
            case "-": return Removed;
            default: return Remained;
        }
    }
    
    public static String obtainAbbreviation(ConditionStatus status) {
        switch (status) {
            case Inserted: return "+";
            case Removed: return "-";
            default: return "";
        }
    }
}
