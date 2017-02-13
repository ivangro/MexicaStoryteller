package mexica.story;

import java.util.*;
import mexica.CharacterName;
import mexica.core.*;
import mexica.tools.AvatarFactory;
import mexica.tools.EmotionDictionary;

/**
 *
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class ConditionInstantiated implements Cloneable {
    private Condition condition;
    private List<CharacterName> charactersList;
    private ActionInstantiated action;
    
    /**
     * Instantiates the given condition
     * @param condition Condition to be instantiated
     * @param characters Ordered characters (index 0 is the first selected character)
     */
    public ConditionInstantiated(Condition condition, CharacterName... characters) {
        this.condition = condition;
        charactersList = new ArrayList<>(Arrays.asList(characters));
    }
    
    /**
     * If the condition performs a chenge in the position of the characters, 
     * this is the new position
     * @return The new position for the action's character
     */
    public Position getPosition() {
        return condition.getPosition();
    }
    
    /**
     * Obtains the first character of the condition
     * @return 
     */
    public CharacterName getCharacterA() {
        String charac = condition.getCharacterA();
        return getCharacter(charac);
    }
    
    /**
     * Obtains the second character of the condition
     * @return 
     */
    public CharacterName getCharacterB() {
        String charac = condition.getCharacterB();
        return getCharacter(charac);
    }
    
    /**
     * Obtains the third character of the condition
     * @return 
     */
    public CharacterName getCharacterC() {
        String charac = condition.getCharacterC();
        return getCharacter(charac);
    }
    
    public ConditionType getType() {
        return condition.getConditionType();
    }
    
    private CharacterName getCharacter(String characterVariable) {
        CharacterName name = CharacterName.Not_Defined;
        if (characterVariable != null) {
            characterVariable = characterVariable.toLowerCase().trim();
            if (characterVariable.equals("a"))
                name = charactersList.get(0);
            else if (characterVariable.equals("b") && charactersList.size() > 1)
                name = charactersList.get(1);
            else if (characterVariable.equals("c") && charactersList.size() > 2)
                name = charactersList.get(2);
            else if (characterVariable.equals("la"))
                name = CharacterName.Linked_Characters_A;
            else if (characterVariable.equals("lb"))
                name = CharacterName.Linked_Characters_B;
            else if (characterVariable.equals("*"))
                name = CharacterName.Any_Character;
            else 
                name = CharacterName.Not_Defined;
        }
        return name;
    }
    
    public Condition getCondition() {
        return condition;
    }
    
    public ActionInstantiated getAction() {
        return action;
    }
    
    /**
     * @param action the action to set
     */
    public void setAction(ActionInstantiated action) {
        this.action = action;
    }
    
    @Override
    public String toString() {
        EmotionDictionary dic = EmotionDictionary.getInstance();
        String cond = "";
        
        boolean isElegible = (getCharacterB() != null) && 
                             !AvatarFactory.getNonElegibleCharacternames().contains(getCharacterB());
        switch (condition.getConditionType()) {
            case Emotion:
                cond = getCharacterA() + " " + dic.translate(condition.getEmotion()) + 
                        " (" + condition.getIntensityAsString() + ") " +
                        ((isElegible) ? " to " + getCharacterB() : "");
                break;
            case Tension:
                cond = getCharacterA() + " " + dic.translate(condition.getTension()) + " " +
                        ((isElegible) ? " by " + getCharacterB() : "");
                break;
            case Position:
                /*Avatar avatar = AvatarFactory.getInstance().getAvatar(getCharacterB());
                String pos = (condition.getCharactersPosition() != null) ? avatar.getCurrentPosition() + 
                        " position" : condition.getPosition().toString();
                cond = "Move " + getCharacterA() + " to " + pos;
                */
                //TODO: Validate code
                String pos = (condition.getCharactersPosition() != null) ? action.getNewCharacterPosition() +
                        " position" : condition.getPosition().toString();
                cond = "Move " + getCharacterA() + " to " + pos;
                break;
        }
        return cond;
    }
    
    @Override
    public Object clone() {
        ConditionInstantiated c = new ConditionInstantiated(condition, charactersList.toArray(new CharacterName[0]));
        c.setAction(action);
        return c;
    }
}