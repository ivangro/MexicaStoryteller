package mexica.story;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.core.*;
import mexica.social.SocialAction;

/**
 * The class stores all the available special actions for the program
 * The actions are retrieved from a text file
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class SpecialActionStore {
    private List<SpecialAction> actions;
    private List<SocialAction> socialActions;
    
    public SpecialActionStore() {
        actions = new ArrayList<>();
        socialActions = new ArrayList<>();
    }
    
    public void addAction(SpecialAction action) {
        action.setNewAction(false);
        actions.add(action);
    }
    
    public void addAction(SocialAction action) {
        action.setNewAction(false);
        socialActions.add(action);
    }
    
    /**
     * Obtains a list with the special actions
     * @return 
     */
    public List<SpecialAction> getActionList() {
        return actions;
    }
    
    /**
     * Obtains a list with the social actions
     * @return 
     */
    public List<SocialAction> getSocialActions() {
        return socialActions;
    }
    
    public void setSocialActions(List<SocialAction> actions) {
        this.socialActions = actions;
    }

    public SocialAction findSocialAction(String socialAction) {
        SocialAction action = new SocialAction();
        action.setActionName(socialAction);
        int index = socialActions.indexOf(action);
        if (index >= 0) {
            return socialActions.get(index);
        }
        else
            return null;
    }
}