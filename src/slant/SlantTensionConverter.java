package slant;

import mexica.core.TensionType;

/**
 * Class to convert Slant tensions to Mexica tensions
 * @author Ivan Guerrero
 */
public class SlantTensionConverter {
    /**
     * Converts a Slant tension into a Mexica tension
     * @param tension
     * @return 
     */
    public static TensionType convert(SlantTension tension) {
        switch (tension) {
            case actorDead:
                return TensionType.ActorDead;
            case mortallyWounded:
                return TensionType.HealthAtRisk;
            case undefined:
                return null;
        }
        
        return null;
    }
}
