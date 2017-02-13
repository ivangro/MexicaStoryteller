package mexica.social;

import java.util.List;
import java.util.Map;
import mexica.CharacterName;
import mexica.core.Condition;
import mexica.core.Tension;
import mexica.engagement.Atom;

/**
 * Decorator for an atom to convert it to a social atom
 * @author Ivan Guerrero
 */
public class SocialAtom extends Atom {
    private Atom atom;
    private Map<CharacterName, String> mapping;
    private int socialRelation, genderRelation;
    private boolean isSocialRelation, isGenderRelation;
    private String socialActionName;
    
    public SocialAtom(Atom atom, Map<CharacterName, String> mapping) {
        this.atom = atom;
        this.mapping = mapping;
    }
    
    @Override
    public List<Tension> getTensions() {
        return atom.getTensions();
    }
    
    @Override
    public List<Condition> getEmotions() {
        return atom.getEmotions();
    }
    
//    @Override
//    public int getPercentage() {
//        return atom.getPercentage();
//    }
    
    @Override
    public String getCellID() {
        return atom.getCellID();
    }

    /**
     * @return the socialRelation
     */
    public int getSocialRelation() {
        return socialRelation;
    }

    /**
     * @param socialRelation the hierarchicalRelation to set
     */
    public void setSocialRelation(int socialRelation) {
        this.socialRelation = socialRelation;
        this.isSocialRelation = true;
    }

    /**
     * @return the genderRelation
     */
    public int getGenderRelation() {
        return genderRelation;
    }

    /**
     * @param genderRelation the genderRelation to set
     */
    public void setGenderRelation(int genderRelation) {
        this.genderRelation = genderRelation;
        this.isGenderRelation = true;
    }

    /**
     * @return the isSocialRelation
     */
    public boolean isSocialRelation() {
        return isSocialRelation;
    }

    /**
     * @return the isGenderRelation
     */
    public boolean isGenderRelation() {
        return isGenderRelation;
    }
    
    public Map<CharacterName, String> getMapping() {
        return mapping;
    }

    public Atom getAtom() {
        return atom;
    }

    /**
     * @return the socialAction
     */
    public String getSocialAction() {
        return socialActionName;
    }

    /**
     * @param socialAction the socialAction to set
     */
    public void setSocialAction(String socialAction) {
        this.socialActionName = socialAction.trim();
    }
    
    @Override
    public String toString() {
        return atom.toString();
    }
}