package slant;

import java.util.*;
import mexica.core.*;

/**
 * Class decorator to add Slant information to a Mexica class
 * @author Ivan Guerrero
 */
public class MexicaAction extends Action {
    /** Mexica action */
    private Action action;
    /** Slant actions associated to a mexica action */
    private List<SlantAction> slantActions;
    
    public MexicaAction(Action action) {
        this.action = action;
        slantActions = new ArrayList<>();
    }
    
    @Override
    public String getActionName() {
        return getAction().getActionName();
    }

    @Override
    public void setActionName(String actionName) {
        getAction().setActionName(actionName);
    }

    /**
     * @return All the preconditions, emotions and tensions
     */
    @Override
    public List<Precondition> getPreconditions() {
        return getAction().getPreconditions();
    }
    
    /**
     * @param type The type of preconditions to obtain: Emotions or Tensions
     * @return the preconditions, emotions or tensions
     */
    @Override
    public List<Precondition> getPreconditions(ConditionType type) {
        return getAction().getPreconditions();
    }
    
    @Override
    public void addPrecondition(Precondition pre) {
        getAction().addPrecondition(pre);
    }

    /**
     * @return the posconditions
     */
    @Override
    public List<Poscondition> getPosconditions() {
        return getAction().getPosconditions();
    }
    
    @Override
    public void addPoscondition(Poscondition pos) {
        getAction().addPoscondition(pos);
    }
    
    @Override
    public List<Tension> getTensions() {
        return getAction().getTensions();
    }
    
    @Override
    public void addTension(Tension ten) {
        getAction().addTension(ten);
    }

    /**
     * @return the texts
     */
    @Override
    public List<String> getTexts() {
        return getAction().getTexts();
    }
    
    @Override
    public void addText(String text) {
        getAction().addText(text);
    }

    /**
     * @return the noCharacters
     */
    @Override
    public int getNoCharacters() {
        return getAction().getNoCharacters();
    }
    
    @Override
    public List<String> getCharacters() {
        return getAction().getCharacters();
    }

    /**
     * @param noCharacters the noCharacters to set
     */
    @Override
    public void setNoCharacters(int noCharacters) {
        getAction().setNoCharacters(noCharacters);
    }
    
    @Override
    public void setCharacters(String... characters) {
        getAction().setCharacters(characters);
    }
    
    @Override
    public int getNoTimesEmployed() {
        return getAction().getNoTimesEmployed();
    }
    
    @Override
    public void setNoTimesEmployed(int timesEmployed) {
        getAction().setNoTimesEmployed(timesEmployed);
    }
    
    @Override
    public boolean isSecret() {
        return getAction().isSecret();
    }
    
    @Override
    public void setSecret(boolean secret) {
        getAction().setSecret(secret);
    }
    
    @Override
    public String toString() {
        return getAction().toString();
    }

    /**
     * @return the action
     */
    public Action getAction() {
        return action;
    }

    /**
     * @return the slantAction
     */
    public List<SlantAction> getSlantActions() {
        List<SlantAction> list = new ArrayList<>();
        for (SlantAction a : slantActions) {
            list.add(a);
        }
        return list;
    }

    /**
     * @param slantAction the slantAction to set
     */
    public void addSlantAction(SlantAction slantAction) {
        slantActions.add(slantAction);
    }
}
