package mexica.core;

/**
 *
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class Poscondition extends Condition {
    /**
     * Converts a numeric position into an element of the enum Position and 
     * establishes as the position to reach when the poscondition is triggered.<br>
     * Note: If the position is set to other character's position,
     * the attribute character's position is enabled and the characterB is set to 'b'
     * @param pos numeric value for position <br>
     * NoWhere-0, Lake-1, Mountains-2, Cemetry-3, Castle-4, Village-5, Farmhouse-6, Tavern-7, {b_Pos}, UnknowknPos-9
     */
    public void setPosition(String pos) {
        if (pos.equals("0"))
            position = Position.NoWhere;
        else if (pos.equals("1")) 
            position = Position.Lake;
        else if (pos.equals("2")) 
            position = Position.Mountains;
        else if (pos.equals("3")) 
            position = Position.Cemetry;
        else if (pos.equals("4")) 
            position = Position.Castle;
        else if (pos.equals("5")) 
            position = Position.Village;
        else if (pos.equals("6")) 
            position = Position.Farmhouse;
        else if (pos.equals("7")) 
            position = Position.Tavern;
        else if (pos.equals("9")) 
            position = Position.UnknownPosition;
        else {
            position = Position.OtherCharactersPosition;
            charactersPosition = pos;
            setCharacterB("b");
        }
    }

    public String toFileString() {
        String res = "";
        switch(type) {
            case Emotion:
                res += "E " + characterA + " " + ((characterB != null) ? characterB : "") + 
                       " " + getIntensityAsString() + " " + EmotionType.getEmotionAsString(emotion);
                break;
            case Position:
                res += "P " + characterA + " " + Position.getPositionAsString(position);
                break;
        }
        
        return res.trim();
    }
}
