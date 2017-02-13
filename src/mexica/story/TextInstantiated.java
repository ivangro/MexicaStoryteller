package mexica.story;

import java.util.*;
import mexica.CharacterName;

/**
 * Represents a text with its character variables associated to names
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class TextInstantiated implements Cloneable {
    private String text;
    private List<CharacterName> charactersList;
    
    public TextInstantiated(String text, CharacterName... characters) {
        charactersList = new ArrayList<>(Arrays.asList(characters));
        setText(text);
    }
    
    private String parseCharacters(String text) {
        text = text.replaceAll("\\@A", charactersList.get(0).toString().replace("_", " "));
        if (charactersList.size() > 1)
            text = text.replaceAll("\\@B", charactersList.get(1).toString().replace("_", " "));
        if (charactersList.size() > 2)
            text = text.replaceAll("\\@C", charactersList.get(2).toString().replace("_", " "));
        
        return text;
    }
    
    /**
     * Stores the given text and updates the character variables with the corresponding names
     * @param text Text with variables
     */
    public final void setText(String text) {
        this.text = (text != null) ?  parseCharacters(text) : "";
    }
    
    public String getText() {
        return text;
    }
    
    @Override
    public String toString() {
        return text;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
