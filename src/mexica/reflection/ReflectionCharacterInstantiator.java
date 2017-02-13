package mexica.reflection;

import java.util.*;
import mexica.CharacterName;
import mexica.core.Action;
import mexica.story.*;
import mexica.tools.*;

/**
 * Class to instantiate a character
 * @author Ivan Guerrero
 */
public class ReflectionCharacterInstantiator {
    private static Random random = new Random();
    
    /**
     * Instantiate the characters for the action to fulfill the given condition
     * @param action Action to be instantiated
     * @param cond Condition to be fulfilled
     * @return 
     */
    public static ActionInstantiated instantiateCharacter(Story story, Action action, ConditionInstantiated cond) {
        List<CharacterName> availableCharacters = story.getAvatarFactory().getAvailableCharacterNames();
        CharacterName performer = cond.getCharacterA();
        CharacterName receiver = cond.getCharacterB();
        
        if (AvatarFactory.getNonElegibleCharacternames().contains(performer)) {
            List<CharacterName> banned = new ArrayList<>();
            banned.add(receiver);
            performer = selectCharacter(banned, availableCharacters);
        }
        if (AvatarFactory.getNonElegibleCharacternames().contains(receiver)) {
            List<CharacterName> banned = new ArrayList<>();
            banned.add(performer);
            receiver = selectCharacter(banned, availableCharacters);
        }

        ActionInstantiated instance = (action.getNoCharacters() > 1) ? 
                                        new ActionInstantiated(action, performer, receiver):
                                        new ActionInstantiated(action, performer);
        if (containsCondition(instance, cond)) {
            return instance;
        }
        else {
            instance = (action.getNoCharacters() > 1) ? 
                        new ActionInstantiated(action, receiver, performer):
                        new ActionInstantiated(action, receiver);
            return instance;
        }
    }
    
    public static CharacterName selectCharacter(List<CharacterName> banned, List<CharacterName> availableCharacters) {
        CharacterName name;
        do {
            name = availableCharacters.get(random.nextInt(availableCharacters.size()));
        } while (banned.contains(name));
        
        return name;
    }
    
    /**
     * Determines if the given action contains a condition equal to the one given
     * @param instance Action instance
     * @param cond Condition instance
     * @return TRUE if the action contains an equivalent condition
     */
    private static boolean containsCondition(ActionInstantiated instance, ConditionInstantiated cond) {
        switch (cond.getType()) {
            case Emotion:
                for (ConditionInstantiated cond2 : instance.getPosconditions()) {
                    ComparisonResult result = ConditionComparer.compare(cond, cond2);
                    if (result.isEqualOrEquivalent(true, true))
                        return true;
                }
                break;
            case Tension:
                for (ConditionInstantiated cond2 : instance.getTensions()) {
                    ComparisonResult result = ConditionComparer.compare(cond, cond2);
                    if (result.isEqualOrEquivalent(true, false))
                        return true;
                }
                break;
        }
        
        return false;
    }
}
