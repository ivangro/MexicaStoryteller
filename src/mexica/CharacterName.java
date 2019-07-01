package mexica;

/**
 * Available characters in Mexica
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public enum CharacterName {
    Dracula,
    Nosferatu,
    Count_Von_Krolock,
    Priest,
    Lady,
    Virgin,
    Farmer,
    Hunter,
    Fisherman,
    Trader,
    Vampire_Researcher,
    Host,
    Servant,
    Villager,
    Beauty,
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

    public boolean isVampire(CharacterName name) {
        switch (name) {
            case Dracula:
            case Nosferatu:
            case Count_Von_Krolock:
                return true;

            case Priest:
            case Lady:
            case Virgin:
            case Farmer:
            case Hunter:
            case Fisherman:
            case Trader:
            case Vampire_Researcher:
            case Host:
            case Servant:
            case Villager:
            case Beauty:
            default:
                return false;
        }
    }
    
    public static String getAbbreviation(CharacterName name) {
        switch (name) {
            case Dracula: return "T";
            case Nosferatu: return "N";
            case Count_Von_Krolock:return "CK";
            case Priest:return "Pt";
            case Lady:return "L";
            case Virgin:return "V";
            case Farmer:return "F";
            case Hunter:return "H";
            case Fisherman:return "Fs";
            case Trader:return "Tr";
            case Vampire_Researcher:return "VR";
            case Host:return "HO";
            case Servant:return "S";
            case Villager:return "VI";
            case Beauty: return "B";
            default: return "UNK";
        }
    }
    
    public static CharacterName valueFromAbbreviation(String abbreviation) {
        switch (abbreviation) {
            case "T" : return Dracula;
            case "N": return Nosferatu;
            case "CK": return Count_Von_Krolock;
            case "Pt": return Priest;
            case "L": return Lady;
            case "V": return Virgin;
            case "F": return Farmer;
            case "H": return Hunter;
            case "Fs": return Fisherman;
            case "Tr": return Trader;
            case "VR": return Vampire_Researcher;
            case "HO": return Host;
            case "S": return Servant;
            case "VI": return Villager;
            case "B": return Beauty;
            default: return Not_Defined;
        }
    }
    
    /** Characters that can be selected for an action */
    private static CharacterName[] selectableCharacters = new CharacterName[] {
            Dracula, Nosferatu, Count_Von_Krolock, Priest, Beauty,
            Lady, Virgin, Farmer, Hunter, Fisherman, Trader, Vampire_Researcher, Host, Servant, Villager
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