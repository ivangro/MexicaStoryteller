package slant;

/**
 * Available tensions in Slant
 * @author Ivan Guerrero
 */
public enum SlantTension {
    actorDead,
    mortallyWounded, 
    undefined;
    
    public static SlantTension valueFromXML(String tension) {
        switch (tension) {
            case "actor dead":
                return actorDead;
            case "mortally-wounded":
                return mortallyWounded;
            default:
                return undefined;
        }
    }
}
