package mexica.social.stats;

import java.util.*;
import mexica.CharacterName;
import mexica.story.ActionInstantiated;

/**
 * Class to store the results of performing a social analysis to a given story
 * @author Ivan Guerrero
 */
public class SocialNormsResult {
    /** Number of actions that broke a social norm in a story */
    private List<ActionInstantiated> socialNormActions;
    /** Number of characters that broke a social norm in a story */ 
    private Set<CharacterName> performerSocialCharacters, receiverSocialCharacters;
    /** Number of actions that justifiedly broke a social norm in a story */
    private List<ActionInstantiated> justifiedActions;
    /** Story identifier */
    private int storyID;
    
    public SocialNormsResult(int storyID) {
        this.storyID = storyID;
        performerSocialCharacters = EnumSet.noneOf(CharacterName.class);
        receiverSocialCharacters = EnumSet.noneOf(CharacterName.class);
        socialNormActions = new ArrayList<>();
        justifiedActions = new ArrayList<>();
    }
    
    public void addSocialNormAction(ActionInstantiated action) {
        socialNormActions.add(action);
        performerSocialCharacters.add(action.getCharactersList().get(0));
        if (action.getCharactersList().size() == 2)
            receiverSocialCharacters.add(action.getCharactersList().get(1));
    }
    
    public void addJustifiedAction(ActionInstantiated action) {
        justifiedActions.add(action);
    }
    
    public int getSocialNormActions() {
        return socialNormActions.size();
    }
    
    public int getJustifiedActions() {
        return justifiedActions.size();
    }
    
    public int getPerformerSocialCharacters() {
        return performerSocialCharacters.size();
    }
    
    public int getReceiverSocialCharacters() {
        return receiverSocialCharacters.size();
    }

    @Override
    public String toString() {
        return "StoryID: " + storyID + 
               "\nSocial norms broken: " + socialNormActions.size() + 
               "\nJustified actions: " + justifiedActions.size() + 
               "\nSocial performer: " + performerSocialCharacters.size() +
               "\nSocial receivers: " + receiverSocialCharacters.size();
    }
}
