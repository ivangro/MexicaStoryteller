package mexica.core;

import java.util.*;

/**
 * Available emotions in Mexica
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class EmotionType {
    /** Available emotions */
    private static List<EmotionType> emotions;
    /** Basic emotions */
    private static EmotionType Social, InLove, Any;
    /** Numeric value of an emotion */
    int value;
    /** Textual representation of an emotion */
    String textualRepresentation;
    /** Text employed inside the actions' library */
    String text;
    /** Name of the emotion */
    String name;
    
    static {
        emotions = new ArrayList<>();
    }
    
    public static void addEmotion(String name, int value, String text, String textualRepresentation, boolean isSocial, boolean isInLove, boolean isAny) {
        EmotionType emotion = new EmotionType(name, value, text, textualRepresentation);
        emotions.add(emotion);
        if (isSocial)
            Social = emotion;
        if (isInLove)
            InLove = emotion;
        if (isAny)
            Any = emotion;
    }
    
    EmotionType(String name, int value, String text, String textualRepresentation) {
        this.name = name;
        this.value = value;
        this.text = text;
        this.textualRepresentation = textualRepresentation;
    }
    
    @Override
    public String toString() {
        return name;
    }
 
    public static EmotionType[] values() {
        return emotions.toArray(new EmotionType[0]);
    }
    
    /**
     * Obtains the emotion type employed to represent social emotions ocurring due to the rupture of a social norm
     * @return 
     */
    public static EmotionType getSocialEmotion() {
        return Social;
    }
    
    /**
     * Obtains the emotion type employed to represent any non-social emotion
     * @return 
     */
    public static EmotionType getAnyEmotion() {
        return Any;
    }
    
    /**
     * Determines if the given emotion type is employed to represent a loving emotion.<br>
     * This emotion is employed to trigger the love competition tension.
     * @param type The type to compare against the love type
     * @return TRUE if the given type is employed to represent the loving emotion
     */
    public static boolean isLoveCompetition(EmotionType type) {
        return type == InLove;
    }
    
    public static boolean isSocialEmotion(EmotionType type) {
        return type == Social;
    }
    
    public static int getEmotionValue(EmotionType type) {
        return type.value;
    }
    
    public static String getEmotionAsString(EmotionType type) {
        return type.text;
    }
    
    public static EmotionType getEmotionFromValue(int value) {
        for (EmotionType emotion : values()) {
            if (emotion.value == value)
                return emotion;
        }
        return getAnyEmotion();
    }
    
    public static String translateEmotion(EmotionType emotion) {
        return emotion.textualRepresentation;
    }
}