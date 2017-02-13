package mexica.core;

import java.util.*;
import mexica.CharacterName;
import mexica.MexicaParameters;
import mexica.story.ConditionInstantiated;
import mexica.story.SpecialActionStore;
import mexica.tools.ComparisonResult;
import mexica.tools.ConditionComparer;

/**
 * The class stores all the available actions for the program
 * The actions are retrieved from a text file
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class ActionStore {
    private List<Action> actions;
    private SpecialActionStore specialActionsStore;
    /** Any action that has as poscondition to follow a character */
    private List<Action> changePositionActions;
    
    public ActionStore() {
        actions = new ArrayList<>();
        changePositionActions = new ArrayList<>();
    }
    
    /**
     * Obtains a reference to the special actions' store, which contains the available social actions.
     * @return 
     */
    public SpecialActionStore getSpecialActionsStore() {
        return specialActionsStore;
    }
    
    public void setSpecialActionStore(SpecialActionStore store) {
        this.specialActionsStore = store;
    }
    
    public void addAction(Action currentAction) {
        currentAction.setNewAction(false);
        actions.add(currentAction);
    }
    public Action getAction(String actionName) {
        return getAction(actionName, false);
    }
    /**
     * Looks for an action with the given action name.<br> 
     * If no regular action exists with that name, looks for a social action instead.
     * @param actionName
     * @return 
     */
    public Action getAction(String actionName, boolean includeRealised) {
        Action act = new Action();
        act.setNewAction(true);
        act.setActionName(actionName);
        int index = actions.indexOf(act);
        if (index >= 0)
            act = actions.get(index);
        else if (includeRealised && actionName.equalsIgnoreCase(getRealisedAction().getActionName())) {
            act = getRealisedAction();
        }
        else {
            act = specialActionsStore.findSocialAction(actionName);
        }
        return act;
    }
    
    public Action getChangePositionAction() {
        if (changePositionActions.isEmpty()) {
            Action act = new Action();
            act.setActionName("Looked_for_and_found");
            int index = actions.indexOf(act);
            changePositionActions.add(actions.get(index));
            
            act = new Action();
            act.setActionName("Found_by_accident");
            index = actions.indexOf(act);
            changePositionActions.add(actions.get(index));
            
            act = new Action();
            act.setActionName("Followed");
            index = actions.indexOf(act);
            changePositionActions.add(actions.get(index));
        }
        
        return changePositionActions.get(0);
    }
    
    /**
     * Returns true if the given action is one of the actions utilised to look for other character
     * @param action the action to be analized
     * @return TRUE if the action looks for the location of another character
     */
    public boolean isChangePositionAction(Action action) {
        return changePositionActions.contains(action);
    }
    
    public Action getRealisedAction() {
        Action act = new Action();
        act.setActionName("Realised");
        act.setCharacters("a");
        act.setNoCharacters(1);
        return act;
    }
    
    /**
     * Obtains a list with all the actions that alter the current position of the action performer
     * @return A list of actions that change the position of the action performer
     */
    public List<Action> getChangePositionActions() {
        List<Action> changePositionActions = new ArrayList<>();
        for (Action action : actions) {
            for (Poscondition cond : action.posconditions) {
                if (cond.getConditionType() == ConditionType.Position) {
                    changePositionActions.add(action);
                    break;
                }
            }
        }
        
        return changePositionActions;
    }
    
    /**
     * Obtains a list with all the actions that kill the action performer
     * @return A list of actions that kill the action performer
     */
    public List<Action> getKillingActions() {
        List<Action> killingActions = new ArrayList<>();
        for (Action action : actions) {
            for (Tension tension : action.tensions) {
                if (tension.tension == TensionType.ActorDead) {
                    killingActions.add(action);
                    break;
                }
            }
        }
        
        return killingActions;
    }
    
    public List<Action> getActionList() {
        return actions;
    }
    
    /**
     * Obtains a list of actions with the requested condition inside its posconditions
     * @param condition The requested condition
     * @return A list of actions that can fulfill the condition with its posconditions
     */
    public List<Action> getActionList(Condition condition) {
        List<Action> list = new ArrayList<>();
        ConditionInstantiated c1 = new ConditionInstantiated(condition, CharacterName.Any_Character, CharacterName.Any_Character);
        
        for (Action act : actions) {
            if (condition != null) {
                if (condition.getConditionType().equals(ConditionType.Emotion)) {
                    for (Condition cond : act.getPosconditions()) {
                        ConditionInstantiated c2 = new ConditionInstantiated(cond, CharacterName.Any_Character, CharacterName.Any_Character);
                        ComparisonResult res = ConditionComparer.compare(c2, c1);
                        if (res.isEqualOrEquivalent(false, true, false)) {
                            list.add(act);
                            break;
                        }
                    }
                }
                else if (condition.getConditionType().equals(ConditionType.Tension)) {
                    for (Condition cond : act.getTensions()) {
                        ConditionInstantiated c2 = new ConditionInstantiated(cond, CharacterName.Any_Character, CharacterName.Any_Character);
                        ComparisonResult res = ConditionComparer.compare(c2, c1);
                        if (res.isEqualOrEquivalent(false, false, false)) {
                            list.add(act);
                            break;
                        }
                    }
                }
            }
            else {
                list.add(act);
            }
        }
        return list;
    }
    
    public void calculateNoveltyValues() {
        //Determine the average number of times an action has been used in previous stories
        double averageUseOfActions = 0;
        int totalActions = 0;
        for (Action action : actions) {
            if (action.getNoTimesEmployed() > 0) {
                averageUseOfActions += action.getNoTimesEmployed();
                totalActions++;
            }
        }
        averageUseOfActions /= totalActions;
        
        //Determine values for low, medium and strict novelty constants
        double mediumNovelty, strictNovelty;
        strictNovelty = MexicaParameters.STRICT_NOVELTY_PERCENTAGE * averageUseOfActions / MexicaParameters.NOVELTY_PERCENTAGE;
        mediumNovelty = MexicaParameters.MEDIUM_NOVELTY_PERCENTAGE * averageUseOfActions / MexicaParameters.NOVELTY_PERCENTAGE;
        
        MexicaParameters.LOW_NOVELTY = 0;
        MexicaParameters.STRICT_NOVELTY = (int)Math.ceil(strictNovelty);
        MexicaParameters.MEDIUM_NOVELTY = (int)Math.ceil(mediumNovelty);
        MexicaParameters.HIGH_NOVELTY = (int)Math.ceil(averageUseOfActions);
    }
}