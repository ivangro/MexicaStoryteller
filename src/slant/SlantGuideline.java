package slant;

import mexica.CharacterName;
import mexica.core.TensionType;

/**
 * Class to represent a guideline defined by slant
 * @author Ivan Guerrero
 */
public class SlantGuideline {
    private CharacterName performer;
    private TensionType tension;
    private boolean prohibit;
    
    public SlantGuideline(CharacterName performer, TensionType tension, boolean prohibit) {
        this.performer = performer;
        this.tension = tension;
        this.prohibit = prohibit;
    }

    /**
     * @return the performer
     */
    public CharacterName getPerformer() {
        return performer;
    }

    /**
     * @param performer the performer to set
     */
    public void setPerformer(CharacterName performer) {
        this.performer = performer;
    }

    /**
     * @return the tension
     */
    public TensionType getTension() {
        return tension;
    }

    /**
     * @param tension the tension to set
     */
    public void setTension(TensionType tension) {
        this.tension = tension;
    }
    
    public boolean isProhibit() {
        return prohibit;
    }
    
    @Override
    public String toString() {
        return ((prohibit) ? "-" : "+") + performer.name() + " " + tension.name();
    }
}
