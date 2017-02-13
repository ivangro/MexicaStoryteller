package mexica.core;

import java.util.*;

/**
 * Storage for all of the available emotions in Mexica
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class RelationsStore {
    /** Available emotions */
    private List<EmotionType> emotions;
    /** Basic emotions */
    private EmotionType Social, InLove, Any;
    
    public RelationsStore() {
        emotions = new ArrayList<>();
    }
    
    void addEmotion(String name, int value, String text, String textualRepresentation, boolean isSocial, boolean isInLove, boolean isAny) {
        EmotionType emotion = new EmotionType(name, value, text, textualRepresentation);
        emotions.add(emotion);
        if (isSocial)
            Social = emotion;
        if (isInLove)
            InLove = emotion;
        if (isAny)
            Any = emotion;
    }
     
    public EmotionType[] values() {
        return emotions.toArray(new EmotionType[0]);
    }
    
    /**
     * Obtains the emotion type employed to represent social emotions ocurring due to the rupture of a social norm
     * @return 
     */
    public EmotionType getSocialEmotion() {
        return Social;
    }
    
    /**
     * Obtains the emotion type employed to represent any non-social emotion
     * @return 
     */
    public EmotionType getAnyEmotion() {
        return Any;
    }
    
    /**
     * Determines if the given emotion type is employed to represent a loving emotion.<br>
     * This emotion is employed to trigger the love competition tension.
     * @param type The type to compare against the love type
     * @return TRUE if the given type is employed to represent the loving emotion
     */
    public boolean isLoveCompetition(EmotionType type) {
        return type == InLove;
    }
    
    public int getEmotionValue(EmotionType type) {
        return type.value;
    }
    
    public String getEmotionAsString(EmotionType type) {
        return type.text;
    }
    
    public EmotionType getEmotionFromValue(int value) {
        for (EmotionType emotion : values()) {
            if (emotion.value == value)
                return emotion;
        }
        return getAnyEmotion();
    }
    
    public String translateEmotion(EmotionType emotion) {
        return emotion.textualRepresentation;
    }
}