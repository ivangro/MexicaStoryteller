package mexica.social;

/**
 * Determines the current social status of an action
 * @author Ivan Guerrero
 */
public enum SocialStatus {
    breakSocialNorm,                /** When the action breaks with the general principle of social norms */
    actionJustified,                /** When the action breaks with the general principle of social norms, 
                                      * but the action performer has already received a similar action against */
    exceptionToGeneralPrinciple,    /** When the action's context is an exception to the general principle */
    specialSocialNorm,              /** When the action's context is represents a non identified social norm breakage */
    specialActionJustified,         /** When the action breaks with the general principle of social norms,
                                      * but the action performer has a positive link against the receiver */
    multipleResults,                /** When an action breaks a social norm for some characters and is justified for others */
    regularAction;                  /** When the action doesn't break any social norm */
    
    /**
     * Validates if the given status represents a social norm broken
     * @param status
     * @return 
     */
    public static boolean breaksSocialNorm(SocialStatus status) {
        switch (status) {
            case actionJustified: return false;
            case breakSocialNorm: return true;
            case exceptionToGeneralPrinciple: return false;
            case multipleResults: return true;
            case regularAction: return false;
            case specialActionJustified: return false;
            case specialSocialNorm: return true;
        }
        return false;
    }

    public static SocialStatus parse(String status) {
        switch (status) {
            case "Special": return specialSocialNorm;
            case "Exception": return exceptionToGeneralPrinciple;
        }
        return null;
    }
}
