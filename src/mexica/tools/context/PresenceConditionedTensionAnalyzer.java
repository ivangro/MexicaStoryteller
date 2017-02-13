package mexica.tools.context;

import java.util.*;
import mexica.core.Position;
import mexica.story.Avatar;
import mexica.story.AvatarContext;
import mexica.story.ConditionInstantiated;
import mexica.story.Story;

/**
 * Class that updates the contexts when 
 * characters change their position by analyzing tensions which can only
 * be active when the characters are on the same location
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class PresenceConditionedTensionAnalyzer implements IAnalyzer {
    private Story story;
    
    public PresenceConditionedTensionAnalyzer(Story story) {
        this.story = story;
    }
    
    @Override
    public void analyze(AvatarContext context) {
        Position pos = story.getAvatarFactory().getAvatar(context.getOwner()).getCurrentPosition();
        List<ConditionInstantiated> forRemoval = new ArrayList<>();

        for (ConditionInstantiated cond : context.getFacts()) {
            switch (cond.getType()) {
                case Tension:
                    if (cond.getCondition().isPresenceConditionedTension()) {
                        Avatar charA = story.getAvatarFactory().getAvatar(cond.getCharacterA());
                        Position posA = charA.getCurrentPosition();
                        Avatar charB = story.getAvatarFactory().getAvatar(cond.getCharacterB());
                        Position posB = charB.getCurrentPosition();
                        //If one character is dead, remove the condition
                        //If the characters are in different locations and the context owner is in one of the position, remove the condition
                        if (((!charA.isAlive() || !charB.isAlive()) || (posA != posB)) && ((pos == posA) || (pos == posB)))
                            forRemoval.add(cond);
                    }
                    break;
            }
        }

        for (ConditionInstantiated cond : forRemoval) {
            context.addTensionToList(cond, true);
        }
    }
    
}
