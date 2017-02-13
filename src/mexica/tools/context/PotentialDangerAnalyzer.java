package mexica.tools.context;

import java.util.*;
import mexica.CharacterName;
import mexica.core.Condition;
import mexica.core.ConditionType;
import mexica.core.EmotionType;
import mexica.core.TensionType;
import mexica.story.*;
import mexica.tools.ComparisonResult;
import mexica.tools.ConditionComparer;

/**
 * Class that analyzes the presence of potential danger conditions
 * inside a given context
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class PotentialDangerAnalyzer implements IAnalyzer {
    private Story story;
    
    public PotentialDangerAnalyzer(Story story) {
        this.story = story;
    }
    
    @Override
    public void analyze(AvatarContext context) {
        List<ConditionInstantiated> existingPD = new ArrayList<>();
        List<ConditionInstantiated> PDRelations = new ArrayList<>();
        
        List<ConditionInstantiated> facts = context.getFacts();
        
        for (ConditionInstantiated cond : facts) {
            switch (cond.getType()) {
                case Emotion:
                    if (cond.getCondition().getIntensity() == -3 && 
                            validateCharacters(cond.getCharacterA(), cond.getCharacterB()))
                        PDRelations.add(cond);
                    break;
                case Tension:
                    if (cond.getCondition().getTension().equals(TensionType.PotencialDanger)) {
                        existingPD.add(cond);
                    }
                    break;
            }
        }
        
        //Adds a new PD tension for every appropriate condition
        for (ConditionInstantiated cond : PDRelations) {
            context.addTensionToList(createPotentialDangerCondition(cond.getCharacterB(), cond.getCharacterA()), false);
        }
        
        //Removes the existing PD that no longer fulfill the conditions
        for (ConditionInstantiated cond1 : existingPD) {
            ConditionInstantiated negEmotion = createNegativeEmotion(cond1.getCharacterB(), cond1.getCharacterA());
            //If the characters are not valid, remove the condition
            boolean removeCondition = !validateCharacters(cond1.getCharacterA(), cond1.getCharacterB());
            
            //If the characters are valid, but already exists the tension, remove it
            if (!removeCondition) {
                for (ConditionInstantiated cond2 : PDRelations) {
                    ComparisonResult res = ConditionComparer.compare(negEmotion, cond2);
                    if (res.isEqualOrEquivalent(true, true)) {
                        removeCondition = false;
                        break;
                    }
                }
            }
            
            if (removeCondition)
                context.addTensionToList(cond1, true);
        }
    }

    private ConditionInstantiated createPotentialDangerCondition(CharacterName... characterNames) {
        Condition cond = new Condition();
        cond.setConditionType(ConditionType.Tension);
        cond.setCharacterA("a");
        cond.setCharacterB("b");
        cond.setTensionType(TensionType.PotencialDanger);
        ConditionInstantiated instance = new ConditionInstantiated(cond, characterNames);
        
        return instance;
    }
    
    private ConditionInstantiated createNegativeEmotion(CharacterName... characterNames) {
        Condition cond = new Condition();
        cond.setConditionType(ConditionType.Emotion);
        cond.setCharacterA("a");
        cond.setCharacterB("b");
        cond.setEmotionType(EmotionType.getAnyEmotion());
        cond.setIntensity(-3);
        ConditionInstantiated instance = new ConditionInstantiated(cond, characterNames);
        
        return instance;
    }
    
    /**
     * Validates that the characters are on the same location and both also alive
     * @param names
     * @return 
     */
    private boolean validateCharacters(CharacterName name1, CharacterName name2) {
        boolean valid;
        Avatar avatarA = story.getAvatarFactory().getAvatar(name1);
        Avatar avatarB = story.getAvatarFactory().getAvatar(name2);
        valid = !avatarA.equals(avatarB) && avatarA.isAlive() && avatarB.isAlive();
        valid = valid && avatarA.getCurrentPosition().equals(avatarB.getCurrentPosition());
        return valid;
    }
}