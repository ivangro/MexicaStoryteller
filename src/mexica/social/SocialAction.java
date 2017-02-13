package mexica.social;

import mexica.core.*;

/**
 * Class to represent a social action in Mexica.
 * @author Ivan Guerrero
 */
public class SocialAction extends Action {
    private SociallyRelevantCharacter socialCharacter;
    private SociallyRelevantRelation socialRelation;
    private SocialPosconditionMode socialPosconditionMode;
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        SocialAction a = new SocialAction();
        a.ID = ID;
        a.actionName = actionName;
        for (Precondition p : preconditions) {
            a.preconditions.add(p);
        }
        for (Poscondition p : posconditions) {
            a.posconditions.add(p);
        }
        for (Tension t : tensions) {
            a.tensions.add(t);
        }
        for (String t : texts) {
            a.texts.add(t);
        }
        a.noCharacters = noCharacters;
        a.timesEmployed = timesEmployed;
        a.secret = secret;
        a.newAction = newAction;
        
        a.socialCharacter = socialCharacter;
        a.socialPosconditionMode = socialPosconditionMode;
        a.socialRelation = socialRelation;
        
        return a;
    }

    /**
     * @return the socialCharacter
     */
    public SociallyRelevantCharacter getSocialCharacter() {
        return socialCharacter;
    }
    
    public void setSocialCharacter(String socialCharacter) {
        setSocialCharacter(SociallyRelevantCharacter.valueOf(socialCharacter));
    }
    
    public void setSocialRelation(String socialRelation) {
        setSocialRelation(SociallyRelevantRelation.valueOf(socialRelation));
    }

    /**
     * @param socialCharacter the socialCharacter to set
     */
    public void setSocialCharacter(SociallyRelevantCharacter socialCharacter) {
        this.socialCharacter = socialCharacter;
    }

    /**
     * @return the socialRelation
     */
    public SociallyRelevantRelation getSocialRelation() {
        return socialRelation;
    }

    /**
     * @param socialRelation the socialRelation to set
     */
    public void setSocialRelation(SociallyRelevantRelation socialRelation) {
        this.socialRelation = socialRelation;
    }
    
    
    @Override
    public void addPrecondition(Precondition precondition) {
        throw new Error("Social actions doesn't have preconditions");
    }
    
    @Override
    public void addTension(Tension tension) {
        if (tensions.size() + posconditions.size() > 1)
            throw new Error("Social actions can only have one tension");
        else
            tensions.add(tension);
    }
    
    @Override
    public void addPoscondition(Poscondition poscondition) {
        if (tensions.size() + posconditions.size() > 1)
            throw new Error("Social actions can only have one tension");
        else
            posconditions.add(poscondition);
    }

    /**
     * @return the socialPosconditionMode
     */
    public SocialPosconditionMode getSocialPosconditionMode() {
        return socialPosconditionMode;
    }

    /**
     * @param socialPosconditionMode the socialPosconditionMode to set
     */
    public void setSocialPosconditionMode(SocialPosconditionMode socialPosconditionMode) {
        this.socialPosconditionMode = socialPosconditionMode;
    }
    
    public void setSocialPosconditionMode(String mode) {
        this.socialPosconditionMode = SocialPosconditionMode.valueOf(mode);
    }
}