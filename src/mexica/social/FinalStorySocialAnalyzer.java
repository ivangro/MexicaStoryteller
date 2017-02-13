package mexica.social;

import java.util.*;
import mexica.CharacterName;
import mexica.MexicaRepository;
import mexica.story.ActionInstantiated;
import mexica.story.SpecialActionStore;
import mexica.story.Story;

/**
 * Analyzes if a story breaks social norms and inserts social texts after the actions
 * @author Ivan Guerrero
 */
public class FinalStorySocialAnalyzer {
    private Story story;
    SpecialActionStore specialActions;
    
    public FinalStorySocialAnalyzer(Story story) {
        this.story = story;
        specialActions = MexicaRepository.getInstance().getSpecialActions();
    }
    
    public List<ActionInstantiated> analyze() {
        List<ActionInstantiated> actions = new ArrayList<>();
        
        for (ActionInstantiated action : story.getActions()) {
            actions.add(action);
            if (action.breaksSocialNorm()) {
                ActionInstantiated socialAction = retrieveSocialAction(action);
                if (socialAction != null)
                    actions.add(socialAction);
            }
        }
        
        return actions;
    }

    private ActionInstantiated retrieveSocialAction(ActionInstantiated action) {
        ActionInstantiated socialAction;
        SocialAction specialAction = null;
        //SocialAction specialAction = specialActions.findSocialAction(action.getSocialData().getSocialAction());
        if (action.getSocialData().getSocialAction() != null)
            specialAction = action.getSocialData().getSocialAction();
        //If no social action was defined, employ the default social action
        if (specialAction == null) {
            specialAction = specialActions.findSocialAction("Social_Complain");
        }
        socialAction = new ActionInstantiated(specialAction, action.getCharactersList().toArray(new CharacterName[0]));
        return socialAction;
    }
}
