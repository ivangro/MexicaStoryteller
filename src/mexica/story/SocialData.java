package mexica.story;

import java.util.*;
import mexica.CharacterName;
import mexica.social.SocialAction;
import mexica.social.SocialStatus;

/**
 * Class to represent the social data of an action instantiated
 * @author Ivan Guerrero
 */
public class SocialData {
    private SocialStatus socialStatus = SocialStatus.regularAction;
    private SocialAction socialAction;
    /** Represents the contexts where the social norm was detected to be broken */
    private Set<CharacterName> brokenContexts, justifiedContexts;
        
    public SocialData() {
        brokenContexts = EnumSet.noneOf(CharacterName.class);
        justifiedContexts = EnumSet.noneOf(CharacterName.class);
        socialStatus = SocialStatus.regularAction;
    }

    /**
     * @return the socialStatus
     */
    public SocialStatus getSocialStatus() {
        return socialStatus;
    }

    /**
     * If the social data is associated to a regular action, the given social status is set.<br>
     * If the current social status breaks a social norm, and the given social status not, the social status is set to multiple results.<br>
     * Otherwise, the current social status remains.
     * @param socialStatus the socialStatus to set
     */
    public void setSocialStatus(SocialStatus socialStatus) {
        if (this.socialStatus == SocialStatus.regularAction)
            this.socialStatus = socialStatus;
        else if (SocialStatus.breaksSocialNorm(this.socialStatus) ^ SocialStatus.breaksSocialNorm(socialStatus)) {
            if (this.socialStatus != SocialStatus.specialSocialNorm)
                this.socialStatus = SocialStatus.multipleResults;
        }
    }
    
    public void resetSocialStatus() {
        this.socialStatus = SocialStatus.regularAction;
    }
    
    public final void setSocialStatus(String socialText) {
        switch (socialText) {
            case "SN":
                socialStatus = SocialStatus.specialSocialNorm;
                break;
            case "SE":
                socialStatus = SocialStatus.exceptionToGeneralPrinciple;
                break;
            default:
                socialStatus = SocialStatus.regularAction;
                break;
        }
    }
    
    /**
     * Obtains a list of characters who detected that the action broke a social norm
     * @return the contexts
     */
    public Set<CharacterName> getContexts() {
        return brokenContexts;
    }
    
    /**
     * Obtains a list of characters who detected that the action was justified
     * @return 
     */
    public Set<CharacterName> getJustifiedContexts() {
        return justifiedContexts;
    }

    /**
     * @param context The character that will be added to the contexts set
     */
    public void addContext(CharacterName context, boolean breaksSocialNorm) {
        if (breaksSocialNorm)
            brokenContexts.add(context);
        else
            justifiedContexts.add(context);
    }

    public boolean breaksSocialNorm() {
        return (socialStatus == SocialStatus.breakSocialNorm || socialStatus == SocialStatus.specialSocialNorm);
    }
    
    @Override
    public String toString() {
        String text;
        
        switch (socialStatus) {
            case exceptionToGeneralPrinciple:
                text = "EGP-";
                break;
            case specialSocialNorm:
                text = "SSN-";
                break;
            case breakSocialNorm:
                text = "BSN-";
                break;
            case actionJustified:
                text = "JA-";
                break;
            case specialActionJustified:
                text = "SJA-";
                break;
            case multipleResults:
                text = "MR-";
                break;
            default:
                text = "";
        }
        return text;
    }

    /**
     * @return the socialAction
     */
    public SocialAction getSocialAction() {
        return socialAction;
    }

    /**
     * @param socialAction the socialAction to set
     */
    public void setSocialAction(SocialAction action) {
        this.socialAction = action;
        switch (socialAction.getSocialPosconditionMode()) {
            case insert:
                setSocialStatus(SocialStatus.specialSocialNorm);
                //getContexts().add(action.getCharactersList().get(0));
                break;
            case justify:
                setSocialStatus(SocialStatus.specialActionJustified);
                //getJustifiedContexts().add(action.getCharactersList().get(0));
                break;
            //TODO: What to do for the remove mode
        }
    }
}