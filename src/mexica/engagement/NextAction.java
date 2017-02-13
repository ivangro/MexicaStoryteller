package mexica.engagement;

import java.util.List;
import mexica.core.Action;
import mexica.core.ConditionType;
import mexica.core.Poscondition;
import mexica.core.Precondition;
import mexica.core.Tension;

/**
 * Decorator of the action class to allow a different presentation
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class NextAction extends Action implements Cloneable {
    private Action action;
    
    public NextAction(Action action) {
        this.action = action;
    }
    
    public Action getAction() {
        return action;
    }
    
    @Override
    public String toString() {
        String actionTxt, name;
        List<String> characs = action.getCharacters();
        
        name = getActionName().replace("_", " ");
        name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
        
        actionTxt = characs.get(0) + " " + name;
        actionTxt += (characs.size() > 1) ? " " + characs.get(1) : "";
        
        return actionTxt;
    }

    @Override
    public boolean equals(Object obj) {
        return action.equals(obj);
    }

    @Override
    public String getActionName() {
        return action.getActionName();
    }

    @Override
    public int getNoCharacters() {
        return action.getNoCharacters();
    }
    
    @Override
    public List<String> getCharacters() {
        return action.getCharacters();
    }

    @Override
    public List<Poscondition> getPosconditions() {
        return action.getPosconditions();
    }

    @Override
    public List<Precondition> getPreconditions() {
        return action.getPreconditions();
    }

    @Override
    public List<Precondition> getPreconditions(ConditionType type) {
        return action.getPreconditions(type);
    }

    @Override
    public List<Tension> getTensions() {
        return action.getTensions();
    }

    @Override
    public List<String> getTexts() {
        return action.getTexts();
    }

    @Override
    public int hashCode() {
        return action.hashCode();
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
