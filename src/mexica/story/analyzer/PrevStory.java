package mexica.story.analyzer;

import java.util.*;
import mexica.CharacterName;
import mexica.core.Action;
import mexica.story.ActionInstantiated;
import mexica.story.ConditionInstantiated;

/**
 *
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class PrevStory {
    private List<PrevStoryAction> actions;
    private List<PrevStoryAvatar> avatars;
    private int currentActionNumber;
    
    public PrevStory() {
        actions = new ArrayList<>();
        avatars = new ArrayList<>();
        currentActionNumber = 0;
    }
        
    public void addAction(int actionNumber, Action name, String... avatars) {
        currentActionNumber = actionNumber;
        PrevStoryAction action = new PrevStoryAction(name, avatars);
        getActions().add(action);
        addPerformer(action.getAvatars().get(0), false);
        for (int i=1; i<action.getAvatars().size(); i++) {
            addReceiver(action.getAvatars().get(i), false);
        }
        
        ActionInstantiated instance = new ActionInstantiated(name, action.getAvatars().toArray(new CharacterName[0]));
        for (ConditionInstantiated cond : instance.getTensions()) {
            addPerformer(cond.getCharacterA(), true);
            addReceiver(cond.getCharacterB(), true);
        }
    }

    private void addPerformer(CharacterName characterName, boolean isTension) {
        if (!characterName.equals(CharacterName.Not_Defined)) {
            PrevStoryAvatar avatar = new PrevStoryAvatar(characterName);
            if (getAvatars().contains(avatar)) {
                int index = getAvatars().indexOf(avatar);
                avatar = getAvatars().get(index);
                if (isTension)
                    avatar.addTensionsPerformed();
                else
                    avatar.addActionsPerformed();
                avatar.setLastActionNumber(currentActionNumber);
                getAvatars().set(index, avatar);
            }
            else {
                avatar = new PrevStoryAvatar(characterName);
                if (isTension)
                    avatar.addTensionsPerformed();
                else
                    avatar.addActionsPerformed();
                avatar.setLastActionNumber(currentActionNumber);
                getAvatars().add(avatar);
            }
        }
    }

    private void addReceiver(CharacterName characterName, boolean isTension) {
        if (!characterName.equals(CharacterName.Not_Defined)) {
            PrevStoryAvatar avatar = new PrevStoryAvatar(characterName);
            if (getAvatars().contains(avatar)) {
                int index = getAvatars().indexOf(avatar);
                avatar = getAvatars().get(index);
                if (isTension)
                    avatar.addTensionsReceived();
                else
                    avatar.addActionsReceived();
                avatar.setLastActionNumber(currentActionNumber);
                getAvatars().set(index, avatar);
            }
            else {
                avatar = new PrevStoryAvatar(characterName);
                if (isTension)
                    avatar.addTensionsReceived();
                else
                    avatar.addActionsReceived();
                avatar.setLastActionNumber(currentActionNumber);
                getAvatars().add(avatar);
            }
        }
    }

    /**
     * @return the actions
     */
    public List<PrevStoryAction> getActions() {
        return actions;
    }

    /**
     * @return the avatars
     */
    public List<PrevStoryAvatar> getAvatars() {
        return avatars;
    }
}
