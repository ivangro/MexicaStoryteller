package mexica.core;

/**
 *
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class Precondition extends Condition {

    public String toFileString() {
        String res = "";
        switch(type) {
            case Emotion:
                res += "E " + characterA + " " + ((characterB != null) ? characterB : "") + 
                       " " + getIntensityAsString() + " " + EmotionType.getEmotionAsString(emotion);
                break;
            case Tension:
                res += "T " + TensionType.getAbbreviation(tension) + " " + characterA + " " + ((characterB != null) ? characterB : "");
                break;
        }
        
        return res.trim();
    }
}
