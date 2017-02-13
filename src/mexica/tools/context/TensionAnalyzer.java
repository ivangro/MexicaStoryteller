package mexica.tools.context;

import java.util.*;
import mexica.core.ConditionType;
import mexica.story.*;

/**
 * Verifies all the regular tension in character context
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class TensionAnalyzer implements IAnalyzer {
    private Story story;
    
    public TensionAnalyzer(Story story) {
        this.story = story;
    }
    
    @Override
    public void analyze(AvatarContext context) {
        List<ConditionInstantiated> forRemoval = new ArrayList<>();
        
        //If one of the characters is dead, remove the tension
        for (ConditionInstantiated cond : context.getFacts()) {
            if (cond.getType().equals(ConditionType.Tension)) {
                Avatar avatarA = story.getAvatarFactory().getAvatar(cond.getCharacterA());
                Avatar avatarB = story.getAvatarFactory().getAvatar(cond.getCharacterB());
                if (!avatarA.isAlive() || !avatarB.isAlive())
                    forRemoval.add(cond);
            }
        }

        /*
        for (ConditionInstantiated cond : forRemoval) {
            context.addTensionToList(cond, true);
        }
         */
    }
    
}
