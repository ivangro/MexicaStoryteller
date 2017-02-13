package mexica.core;

import mexica.MexicaParameters;
import mexica.social.ConditionStatus;
import mexica.tools.EmotionDictionary;

/**
 * Represents a condition inside an action
 * characterA is the first character inside the condition
 * characterB is the second character (if exists) inside the condition
 * characterC is the third character (if exists) inside the condition
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class Condition implements Cloneable {
    protected int intensity;
    protected EmotionType emotion;
    protected TensionType tension;
    protected Position position;
    protected String characterA, characterB, characterC;
    protected String charactersPosition;
    protected boolean presenceConditioned;
    protected ConditionType type;
    protected ConditionStatus status;
    
    public Condition() {
        status = ConditionStatus.NotDefined;
    }
        
    public void setEmotionType(int emotionType) {
        emotion = EmotionType.getEmotionFromValue(emotionType);
    }
    
    public void setEmotionType(EmotionType emotionType) {
        this.emotion = emotionType;
    }
    
    public void setEmotionType(String emotionStr) {
        int emotionType = -1;
        try {
            emotionType = Integer.parseInt(emotionStr);
        } catch (Exception ex){}
        setEmotionType(emotionType);
    }
    
    public int analyzeNumber(String amount) {
        int number;
        if (amount.equals("%"))
            number = MexicaParameters.PROPORTIONAL_INTENSITY;
        else
            number = (amount.startsWith("+")) ? Integer.parseInt(amount.substring(1)) : Integer.parseInt(amount);
        return number;
    }
    
    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }
    
    public EmotionType getEmotion() {
        return emotion;
    }
    
    public int getIntensity() {
        return intensity;
    }
    
    public String getIntensityAsString() {
        return (intensity == MexicaParameters.PROPORTIONAL_INTENSITY) ? "%" : (intensity > 0) ? "+" + intensity : intensity + "";
    }
    
    /**
     * Obtains the first character in the condition
     * @return 
     */
    public String getCharacterA() {
        return characterA;
    }
    
    /**
     * Obtains the second character in the condition
     * @return 
     */
    public String getCharacterB() {
        return characterB;
    }
    
    public String getCharacterC() {
        return characterC;
    }
    
    public void setCharacterA(String character) {
        characterA = character;
    }
    
    public void setCharacterB(String character) {
        characterB = character;
    }
    
    public void setCharacterC(String character) {
        characterC = character;
    }
    
    public ConditionType getConditionType() {
        return type;
    }
    
    public void setConditionType(ConditionType type) {
        this.type = type;
    }
    
    /**
     * Converts text representation of tension into one of the available tension types
     * @param tensionType Text representation of a tension
     */
    public void setTensionType(String tensionType) {
        tension = TensionType.fromAbbreviation(tensionType);
        if (tension == null)
            throw new Error("Unknown tension: " + tensionType);
    }
    
    public void setTensionType(TensionType type) {
        this.tension = type;
    }
    
    public void setPosition(Position position) {
        this.position = position;
    }
    
    public TensionType getTension() {
        return tension;
    }
    
    public Position getPosition() {
        return position;
    }
    
    /**
     * Employed when a character requires to move to the position of another character
     * @return 
     */
    public String getCharactersPosition() {
        return charactersPosition;
    }
    
    public void setPresenceConditioned(String isPresenceConditioned) {
        presenceConditioned = (isPresenceConditioned != null && isPresenceConditioned.matches("\\s*\\+\\s*"));
    }
    
    public void setPresenceConditioned(boolean presenceConditioned) {
        this.presenceConditioned = presenceConditioned;
    }
    
    /**
     * When the + sign is inside a tension, it means that is presenceConditioned
     * @return TRUE if it's presence conditioned
     */
    public boolean isPresenceConditionedTension() {
        return presenceConditioned;
    }
        
    @Override
    public String toString() {
        EmotionDictionary dic = EmotionDictionary.getInstance();
        String cond = "";
        switch (type) {
            case Emotion:
                cond = characterA + " " + dic.translate(emotion) + 
                        " (" + getIntensityAsString() + ")     " + 
                        ((characterB != null) ? " to " + characterB : "");
                break;
            case Tension:
                cond = characterA + " " + dic.translate(tension) + "     " +
                        ((characterB != null) ? " by " + characterB : "") + 
                        ((isPresenceConditionedTension()) ? " (Loc)" : "");
                break;
            case Position:
                String pos = (charactersPosition != null) ? characterB + " position" : position.toString();
                cond = "Move " + characterA + " to " + pos;
                break;
        }
        return cond;
    }
    
    /**
     * Textual representation of the condition including its status
     * @return [+-]? condition
     */
    public String toStringWithStatus() {
        return ConditionStatus.obtainAbbreviation(status) + toString();
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        Condition c = new Condition();
        c.intensity = intensity;
        c.emotion = emotion;
        c.tension = tension;
        c.position = position;
        c.characterA = characterA;
        c.characterB = characterB;
        c.characterC = characterC;
        c.charactersPosition = charactersPosition;
        c.presenceConditioned = presenceConditioned;
        c.type = type;
        c.status = status;
        return c;
    }

    /**
     * @return the status
     */
    public ConditionStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(ConditionStatus status) {
        this.status = status;
    }
}