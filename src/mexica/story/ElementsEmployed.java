package mexica.story;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.MexicaParameters;
import mexica.core.Action;
import mexica.engagement.Atom;

/**
 * Class to store the elements employed during the current story, or the current step
 * @author Ivan Guerrero
 */
public class ElementsEmployed implements IStoryContextListener {
    private Map<Atom, Integer> atomsEmployed;
    private Map<ActionInstantiated, Integer> actionsEmployed;
    private Map<Action, Integer> simpleActionsEmployed;
    private ActionInstantiated lastAction;
    
    public ElementsEmployed() {
        atomsEmployed = new HashMap<>();
        actionsEmployed = new HashMap<>();
        simpleActionsEmployed = new HashMap<>();
    }
    
    public void notifyStartOfStep() {
        //atomsEmployed = new HashMap<>();
    }
    
    public void restart() {
        Logger.getGlobal().log(Level.FINE, "Elements restarted");
        atomsEmployed = new HashMap<>();
        actionsEmployed = new HashMap<>();
        simpleActionsEmployed = new HashMap<>();
        lastAction = null;
    }
    
    /**
     * Determines if the given atom can be employed during the current step
     * @param atom
     * @return 
     */
    public boolean isAtomAvailable(Atom atom) {
        boolean res = true;
        int instances;
        if (atomsEmployed.containsKey(atom)) {
            instances = atomsEmployed.get(atom);
            if (instances >= MexicaParameters.MAX_ATOM_USAGES_IN_STEP)
                res = false;
        }
        return res;
    }
    
    /**
     * Determines if the given action can be employed during the current story
     * @param action
     * @return 
     */
    public boolean isActionAvailable(Action action) {
        boolean res = true;
        int instances;
        
        if (simpleActionsEmployed.containsKey(action)) {
            instances = simpleActionsEmployed.get(action);
            if (instances >= MexicaParameters.MAX_SIMPLE_ACTION_USAGES_IN_STORY)
                res = false;
        }
        //Condition to prevent the use of two consecutive equal actions
        if (lastAction != null && lastAction.getAction().equals(action))
            res = false;
        
        return res;
    }
    
    /**
     * Determines if the given action can be employed during the current story
     * @param action
     * @return 
     */
    public boolean isActionAvailable(ActionInstantiated action) {
        boolean res = true;
        int instances;
        if (actionsEmployed.containsKey(action)) {
            instances = actionsEmployed.get(action);
            //Modified to prevent duplicated actions without considering characters
            if (instances >= MexicaParameters.MAX_ACTION_USAGES_IN_STORY || lastAction.equals(action))
                res = false;
        }
        res &= isActionAvailable(action.getAction());
        return res;
    }
    
    public void addAtom(Atom atom) {
        if (atomsEmployed.containsKey(atom)) {
            int instances = atomsEmployed.get(atom);
            atomsEmployed.put(atom, instances+1);
        }
        else {
            atomsEmployed.put(atom, 1);
        }
        Logger.getGlobal().log(Level.FINE, "Atoms employed: {0}", atomsEmployed);
    }
    
    public void addAction(ActionInstantiated action) {
        if (actionsEmployed.containsKey(action)) {
            int instances = actionsEmployed.get(action);
            actionsEmployed.put(action, instances+1);
        }
        else {
            actionsEmployed.put(action, 1);
        }
        addSimpleAction(action.getAction());
        lastAction = action;
        Logger.getGlobal().log(Level.FINE, "Actions employed: {0}", actionsEmployed);
    }

    @Override
    public void notifyListener(ListType type) {
        if (type == ListType.Actions) {
            notifyStartOfStep();
        }
    }

    private void addSimpleAction(Action action) {
        if (simpleActionsEmployed.containsKey(action)) {
            int instances = simpleActionsEmployed.get(action);
            simpleActionsEmployed.put(action, instances+1);
        }
        else {
            simpleActionsEmployed.put(action, 1);
        }
        Logger.getGlobal().log(Level.FINE, "Simple actions employed: {0}", simpleActionsEmployed);
    }

    public void removeAction(ActionInstantiated action) {
        if (actionsEmployed.containsKey(action)) {
            int instances = actionsEmployed.get(action);
            actionsEmployed.put(action, instances-1);
        }

        removeSimpleAction(action.getAction());
        Logger.getGlobal().log(Level.FINE, "Actions employed: {0}", actionsEmployed);
    }
    
    private void removeSimpleAction(Action action) {
        if (simpleActionsEmployed.containsKey(action)) {
            int instances = simpleActionsEmployed.get(action);
            simpleActionsEmployed.put(action, instances-1);
        }
        Logger.getGlobal().log(Level.FINE, "Simple actions employed: {0}", simpleActionsEmployed);
    }
}