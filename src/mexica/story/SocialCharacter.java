package mexica.story;

/**
 * Represent the different available types of character selections for a social norm
 * @author Ivan Guerrero
 */
@Deprecated
public enum SocialCharacter {
    performer,
    receiver,
    both,
    none,
    hierarchical,
    gender,
    undefined;
    
    public static SocialCharacter parse(String socialCharacter, String relation) {
        SocialCharacter result = undefined;
        
        switch (socialCharacter) {
            case "Performer": result = performer; break;
            case "Receiver": result = receiver; break;
            case "Both": result = both; break;
            case "NoCharacter": result = none; break;
        }
        
        switch (relation) {
            case "Social": result = hierarchical; break;
            case "Gender": result = gender; break;
            //case "NoRelation": result = undefined; break;
        }
        
        return result;
    }

    @Deprecated
    public static SocialCharacter parseAbbreviation(String socialCharacter) {
        switch (socialCharacter) {
            case "P":
                return performer;
            case "R":
                return receiver;
            case "B":
                return both;
            case "N":
                return none;
            case "H":
                return hierarchical;
            case "G":
                return gender;
            default:
                return undefined;
        }
    }
}
