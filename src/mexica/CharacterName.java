package mexica;

/**
 * Available characters in Mexica
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public enum CharacterName {
    Tlatoani, 
    Prince,
    Princess,
    Eagle_Knight,
    Jaguar_Knight,
    Priest,
    Lady,
    Virgin,
    Farmer,
    Hunter,
    Fisherman,
    Trader,
    Warrior,
    Artist,
    Slave,
    Enemy,
    Enemy_Priest,
    Beauty,
    Chaman,
    Not_Defined,
    Linked_Characters_A,
    Linked_Characters_B,
    Any_Character;
    
    public static CharacterName valueOfIgnoreCase(String nameStr) {
        for (CharacterName name : CharacterName.values()) {
            if (name.toString().toLowerCase().equals(nameStr.toLowerCase()))
                return name;
        }
        return Not_Defined;
    }
    
    public static CharacterName valueOfName(String nameStr) {
        nameStr = nameStr.replaceAll(" ", "_");
        for (CharacterName name : CharacterName.values()) {
            if (name.toString().equalsIgnoreCase(nameStr))
                return name;
        }
        return Not_Defined;
    }
    
    public static String getAbbreviation(CharacterName name) {
        switch (name) {
            case Tlatoani: return "T";
            case Prince: return "P";
            case Princess: return "Ps";
            case Eagle_Knight: return "EK";
            case Jaguar_Knight:return "JK";
            case Priest:return "Pt";
            case Lady:return "L";
            case Virgin:return "V";
            case Farmer:return "F";
            case Hunter:return "H";
            case Fisherman:return "Fs";
            case Trader:return "Tr";
            case Warrior:return "W";
            case Artist:return "A";
            case Slave:return "S";
            case Enemy:return "E";
            case Enemy_Priest: return "EP";
            case Beauty: return "B";
            case Chaman: return "C";
            default: return "UNK";
        }
    }
    
    public static CharacterName valueFromAbbreviation(String abbreviation) {
        switch (abbreviation) {
            case "T" : return Tlatoani;
            case "P": return Prince;
            case "Ps": return Princess;
            case "EK": return Eagle_Knight;
            case "JK": return Jaguar_Knight;
            case "Pt": return Priest;
            case "L": return Lady;
            case "V": return Virgin;
            case "F": return Farmer;
            case "H": return Hunter;
            case "Fs": return Fisherman;
            case "Tr": return Trader;
            case "W": return Warrior;
            case "A": return Artist;
            case "S": return Slave;
            case "E": return Enemy;
            case "EP": return Enemy_Priest;
            case "B": return Beauty;
            case "C": return Chaman;
            default: return Not_Defined;
        }
    }
    
    /** Characters that can be selected for an action */
    private static CharacterName[] selectableCharacters = new CharacterName[] {
            Tlatoani, Prince, Princess, Eagle_Knight, Jaguar_Knight, Priest, Beauty, Chaman,
            Lady, Virgin, Farmer, Hunter, Fisherman, Trader, Warrior, Artist, Slave, Enemy, Enemy_Priest
        };
    
    /**
     * Obtains a list with the characters selectable to instantiate an action
     * @return 
     */
    public static CharacterName[] getSelectableCharacters() {
        return selectableCharacters;
    }
    
    /**
     * Determines if the given character is selectable to instantiate an action
     * @param character The character to be analyzed
     * @return True if the character is selectable
     */
    public static boolean isSelectableCharacter(CharacterName character) {
        for (CharacterName charac : selectableCharacters) {
            if (charac == character)
                return true;
        }
        return false;
    }
}