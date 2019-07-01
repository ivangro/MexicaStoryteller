package mexica.tools.context;

import java.util.*;
import mexica.CharacterName;
import mexica.core.Position;
import mexica.core.TensionType;
import mexica.story.*;


/**
 * Class that updates the contexts when 
 * a dead character tension is triggered
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class CharacterDeadAnalyzer implements IAnalyzer {
    private Story story;
    
    public CharacterDeadAnalyzer(Story story) {
        this.story = story;
    }
    
    @Override
    public void analyze(AvatarContext context) {
        List<CharacterName> deads = new ArrayList<>();
        List<CharacterName> vampires = new ArrayList<>();
        
        for (ConditionInstantiated instance : context.getFacts()) {
            switch (instance.getType()) {
                case Tension:
                    if (instance.getCondition().getTension().equals(TensionType.ActorDead)) {
                        deads.add(instance.getCharacterA());
                    }
                    if (instance.getCondition().getTension().equals(TensionType.ActorVampire)) {
                        vampires.add(instance.getCharacterA());
                    }
                    break;
            }
        }
        
        //For every dead character, update his/her status and 
        //remove all the conditions in the context where the dead character is the performer
        for (CharacterName name : deads) {
            Avatar avatar = story.getAvatarFactory().getAvatar(name);
            Position ownersPosition = story.getAvatarFactory().getAvatar(context.getOwner()).getCurrentPosition();
            Position deadPosition = avatar.getCurrentPosition();
            if (ownersPosition.equals(deadPosition))
                removeConditions(context, name);

            if (avatar.isAlive()) {
                avatar.kill(story.getCurrentYear());
            }
        }

        for (CharacterName name : vampires) {
            Avatar avatar = story.getAvatarFactory().getAvatar(name);

            if (!avatar.isVampire()) {
                avatar.becomeVampire();
            }
        }
    }
    
    /**
     * Removes all the conditions in the context where the deadCharacter is the source
     * @param context Context to analyze
     * @param deadCharacter Character which conditions will be removed
     */
    private void removeConditions(AvatarContext context, CharacterName deadCharacter) {
        List<ConditionInstantiated> forRemoval = new ArrayList<>();
        
        for (ConditionInstantiated cond : context.getFacts()) {
            if (cond.getCharacterA().equals(deadCharacter)) {
                TensionType type = cond.getCondition().getTension();
                if (type == null || !type.equals(TensionType.ActorDead))
                    forRemoval.add(cond);
            }
        }
        
        for (ConditionInstantiated cond : forRemoval) {
            switch (cond.getType()) {
                case Tension:
                    context.addTensionToList(cond, true);
                    break;
                case Emotion:
                    context.removeEmotionFromList(cond);
            }
        }
    }
}
