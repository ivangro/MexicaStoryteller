package mexica.core;

/**
 *
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class Tension extends Condition {

    public String toFileString() {
        String res = "";
        switch(type) {
            case Tension:
                res += "T " + TensionType.getAbbreviation(tension) + " " + characterA + " " + ((characterB != null) ? characterB : "") + 
                       ((isPresenceConditionedTension()) ? " +" : "");
                break;
        }
        
        return res.trim();
    }
}
