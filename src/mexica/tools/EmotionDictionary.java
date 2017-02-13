package mexica.tools;

import mexica.core.EmotionType;
import mexica.core.TensionType;

/**
 * Translates an emotion to a textual representation
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class EmotionDictionary {
    private static EmotionDictionary instance = new EmotionDictionary();
    
    private EmotionDictionary() {}
    
    public static EmotionDictionary getInstance() {
        return instance;
    }
    
    public String translate(EmotionType type) {
        return EmotionType.translateEmotion(type);
    }
    
    public String translate(TensionType type) {
        String text = type.toString();
        text = text.replaceAll("([A-Z])", " $1");
        text = text.substring(1).toLowerCase();
        text = text.substring(0, 1).toUpperCase() + text.substring(1);
        return text;
    }
    
    public static void main(String[] args) {
        String tmp = EmotionDictionary.getInstance().translate(EmotionType.getEmotionFromValue(1));
        System.out.println(tmp);
        tmp = EmotionDictionary.getInstance().translate(TensionType.ActorDead);
        System.out.println(tmp);
    }
}
