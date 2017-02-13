package mexica.tools.context;

import java.util.*;
import mexica.CharacterName;
import mexica.core.ConditionType;
import mexica.core.TensionType;
import mexica.core.Condition;
import mexica.story.AvatarContext;
import mexica.story.ConditionInstantiated;
import mexica.story.Story;

/**
 * Class that analyzes the presence of clashing emotions conditions
 * inside a given context
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class ClashingEmotionsAnalyzer implements IAnalyzer {
    private Story story;
    
    public ClashingEmotionsAnalyzer(Story story) {
        this.story = story;
    }
    @Override
    public void analyze(AvatarContext context) {
        List<CharactersRelationship> posRelations, negRelations;
        posRelations = new ArrayList<>();
        negRelations = new ArrayList<>();
        
        List<ConditionInstantiated> tensions = new ArrayList<>();
        
        for (ConditionInstantiated instance : context.getFacts()) {
            Condition condition = instance.getCondition();
            switch (condition.getConditionType()) {
                case Emotion:
                    if (condition.getIntensity() >= 0)
                        posRelations.add(new CharactersRelationship(instance.getCharacterA(), instance.getCharacterB()));
                    else
                        negRelations.add(new CharactersRelationship(instance.getCharacterA(), instance.getCharacterB()));
                    break;
                case Tension:
                    if (condition.getTension().equals(TensionType.ClashingEmotions)) {
                        tensions.add(instance);
                    }
                    break;
            }
        }
        
        //For every pair of positive/negative relationship between the same characters
        //Add a clashing emotion tension
        for (CharactersRelationship rel : posRelations) {
            if (negRelations.contains(rel))
                context.addTensionToList(createClashingEmotion(rel.char1, rel.char2), false);
        }
        
        //For every clashing emotions' tension inside the context, validate if the conditions still apply
        for (ConditionInstantiated cond : tensions) {
            CharactersRelationship rel = new CharactersRelationship(cond.getCharacterA(), cond.getCharacterB());
            if (!posRelations.contains(rel) || !negRelations.contains(rel)) {
                context.addTensionToList(cond, true);
            }
        }
    }
    
    private ConditionInstantiated createClashingEmotion(CharacterName... names) {
        Condition cond = new Condition();
        cond.setConditionType(ConditionType.Tension);
        cond.setTensionType(TensionType.ClashingEmotions);
        cond.setCharacterA("a");
        cond.setCharacterB("b");
        ConditionInstantiated instance = new ConditionInstantiated(cond, names);
        return instance;
    }
}

/**
 * Represents a relationship between two characters
 * char1 -> char2
 * @author Ivan Guerrero (UNAM, Mexico)
 */
class CharactersRelationship {
    CharacterName char1, char2;
    
    public CharactersRelationship(CharacterName char1, CharacterName char2) {
        this.char1 = char1;
        this.char2 = char2;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean ans = false;
        if (obj instanceof CharactersRelationship) {
            CharactersRelationship rel = (CharactersRelationship)obj;
            ans = rel.char1.equals(char1) && rel.char2.equals(char2);
        }
        return ans;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.char1 != null ? this.char1.hashCode() : 0);
        hash = 17 * hash + (this.char2 != null ? this.char2.hashCode() : 0);
        return hash;
    }
}