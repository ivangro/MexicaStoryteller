package mexica.tools.context;

import java.util.*;
import mexica.CharacterName;
import mexica.core.ConditionType;
import mexica.core.EmotionType;
import mexica.core.TensionType;
import mexica.core.Condition;
import mexica.story.AvatarContext;
import mexica.story.ConditionInstantiated;
import mexica.story.Story;

/**
 * Class that updates the contexts when 
 * a love competition tension is triggered
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class LoveCompetitionAnalyzer implements IAnalyzer {
    private Story story;
    
    public LoveCompetitionAnalyzer(Story story) {
        this.story = story;
    }
    
    @Override
    public void analyze(AvatarContext context) {
        List<ConditionInstantiated> inLove = new ArrayList<>();
        List<ConditionInstantiated> loveCompetitions = new ArrayList<>();
        List<LoveRelationship> activeRelations = new ArrayList<>();
        
        //Obtain all the amorous love relations and all the love competitions
        for (ConditionInstantiated cond : context.getFacts()) {
            switch (cond.getType()) {
                case Emotion: 
                    EmotionType emotionType = cond.getCondition().getEmotion();
                    if (EmotionType.isLoveCompetition(emotionType) && cond.getCondition().getIntensity() > 0) {
                       //(emotionType.equals(EmotionType.InLove) && cond.getCondition().getIntensity() > 0) {
                        inLove.add(cond);
                    }
                    break;
                case Tension:
                    TensionType tensionType = cond.getCondition().getTension();
                    if (tensionType.equals(TensionType.LoveCompetition))
                        loveCompetitions.add(cond);
                    break;
            }
        }
        
        //Add a new love competition for every pair of characters in love of the same character
        for (int i=0; i<inLove.size(); i++) {
            CharacterName name = inLove.get(i).getCharacterB();
            for (int j=i+1; j<inLove.size(); j++) {
                CharacterName name2 = inLove.get(j).getCharacterB();
                if (name.equals(name2) && story.getAvatarFactory().getAvatar(name).isAlive()) {
                    LoveRelationship rel = new LoveRelationship(inLove.get(i).getCharacterA(), inLove.get(j).getCharacterA());
                    activeRelations.add(rel);
                    ConditionInstantiated ins = createLoveCompetition(rel.char1, rel.char2);
                    context.addTensionToList(ins, false);
                }
            }
        }
        
        //Remove old love competitions without the requested emotions
        for (ConditionInstantiated cond : loveCompetitions) {
            LoveRelationship rel = new LoveRelationship(cond.getCharacterA(), cond.getCharacterB());
            if (!activeRelations.contains(rel))
                context.addTensionToList(cond, true);
        }
    }

    private ConditionInstantiated createLoveCompetition(CharacterName... names) {
        Condition c = new Condition();
        c.setCharacterA("a");
        c.setCharacterB("b");
        c.setConditionType(ConditionType.Tension);
        c.setTensionType(TensionType.LoveCompetition);
        ConditionInstantiated instance = new ConditionInstantiated(c, names);
        
        return instance;
    }    
}

class LoveRelationship {
    CharacterName char1, char2;
    
    public LoveRelationship(CharacterName char1, CharacterName char2) {
        this.char1 = char1;
        this.char2 = char2;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean ans = false;
        if (obj instanceof LoveRelationship) {
            LoveRelationship rel = (LoveRelationship)obj;
            ans = (rel.char1.equals(char1) && rel.char2.equals(char2)) || 
                  (rel.char1.equals(char2) && rel.char2.equals(char1));
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