package mexica.core;

import java.io.Serializable;
import java.util.*;

/**
 * Class representing an available action inside the system
 * Contains a list with its preconditions, posconditions, tensions and
 * available texts.
 * Also stores its name and number of characters
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class Action implements Cloneable, Serializable {
    /** TODO: ID employed to diferentiate between two actions with the same name */
    protected int ID;
    protected String actionName;
    protected List<Precondition> preconditions;
    /** Posconditions related to positions and emotions */
    protected List<Poscondition> posconditions;
    /** Tensions employed as posconditions */
    protected List<Tension> tensions;
    protected List<String> texts;
    protected List<String> characters;
    protected int noCharacters;
    /** Number of times this action has been employed in previous stories */
    protected int timesEmployed;
    /** If an action is secret, only the performer and receiver characters are aware of posconditions */
    protected boolean secret;
    /** If the action is obtained from a story but is not inside the actions' repository, is considered a new action */
    protected boolean newAction;
    
    public Action() {
        characters = new ArrayList(Arrays.asList("a", "b", "c"));
        preconditions = new ArrayList<>();
        posconditions = new ArrayList<>();
        tensions = new ArrayList<>();
        texts = new ArrayList<>();
    }
    
    public void setID(int ID) {
        this.ID = ID;
    }
    
    /**
     * @return the actionName
     */
    public String getActionName() {
        return actionName;
    }

    /**
     * @param actionName the actionName to set
     */
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    /**
     * @return All the preconditions, emotions and tensions
     */
    public List<Precondition> getPreconditions() {
        return preconditions;
    }
    
    /**
     * @param type The type of preconditions to obtain: Emotions or Tensions
     * @return the preconditions, emotions or tensions
     */
    public List<Precondition> getPreconditions(ConditionType type) {
        List<Precondition> list = new ArrayList<>();
        
        for (Precondition prec : preconditions) {
            if (prec.getConditionType().equals(type))
                list.add(prec);
        }
        
        return list;
    }
    
    public void addPrecondition(Precondition pre) {
        preconditions.add(pre);
    }

    /**
     * @return the posconditions
     */
    public List<Poscondition> getPosconditions() {
        return posconditions;
    }
    
    public void addPoscondition(Poscondition pos) {
        posconditions.add(pos);
    }
    
    public List<Tension> getTensions() {
        return tensions;
    }
    
    public void addTension(Tension ten) {
        tensions.add(ten);
    }

    /**
     * @return the texts
     */
    public List<String> getTexts() {
        return texts;
    }
    
    public void addText(String text) {
        if (text.length() > 0)
            texts.add(text);
    }

    /**
     * @return the noCharacters
     */
    public int getNoCharacters() {
        return noCharacters;
    }
    
    public List<String> getCharacters() {
        return characters;
    }

    /**
     * @param noCharacters the noCharacters to set
     */
    public void setNoCharacters(int noCharacters) {
        this.noCharacters = noCharacters;
        characters = characters.subList(0, noCharacters);
    }
    
    public void setCharacters(String... characters) {
        this.characters.clear();
        for (String charac : characters) {
            if (charac != null && charac.length() > 0)
                this.characters.add(charac);
        }
        this.noCharacters = this.characters.size();
    }
    
    public int getNoTimesEmployed() {
        return timesEmployed;
    }
    
    public void setNoTimesEmployed(int timesEmployed) {
        this.timesEmployed = timesEmployed;
    }
    
    public boolean isSecret() {
        return secret;
    }
    
    public void setSecret(boolean secret) {
        this.secret = secret;
    }
    
    public boolean isNewAction() {
        return newAction;
    }
    
    public void setNewAction(boolean newAction) {
        this.newAction = newAction;
    }
    
    @Override
    public String toString() {
        String name = actionName.replace("_", " ");
        name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
        return name;
    }
    
    public String toStringWithoutFormat() {
        return actionName;
    }
        
    /**
     * To actions are equal if its action name is equal
     * @param obj
     * @return TRUE if both actions have the same name
     */
    @Override
    public boolean equals(Object obj) {
        boolean ans = false;
        if (obj instanceof Action) {
            Action act = ((Action)obj);
            ans = act.getActionName().equalsIgnoreCase(getActionName());
        }
        return ans;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.actionName != null ? this.actionName.hashCode() : 0);
        hash = 79 * hash + this.noCharacters;
        return hash;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        //TODO: Clone the conditions
        Action a = new Action();
        a.ID = ID;
        a.actionName = actionName;
        for (Precondition p : preconditions) {
            a.preconditions.add(p);
        }
        for (Poscondition p : posconditions) {
            a.posconditions.add(p);
//            Condition c = (Condition)p.clone();
//            if (c instanceof Poscondition)
//                a.posconditions.add((Poscondition)c);
//            else {
//                Poscondition pc = new Poscondition();
//                pc.intensity = c.intensity;
//                pc.emotion = c.emotion;
//                pc.tension = c.tension;
//                pc.position = c.position;
//                pc.characterA = c.characterA;
//                pc.characterB = c.characterB;
//                pc.characterC = c.characterC;
//                pc.charactersPosition = c.charactersPosition;
//                pc.presenceConditioned = c.presenceConditioned;
//                pc.type = c.type;
//                pc.status = c.status;
//                a.posconditions.add(pc);
//            }
        }
        for (Tension t : tensions) {
            a.tensions.add(t); //(Tension)t.clone());
        }
        for (String t : texts) {
            a.texts.add(t);
        }
        a.noCharacters = noCharacters;
        a.timesEmployed = timesEmployed;
        a.secret = secret;
        a.newAction = newAction;
        
        return a;
    }

    /**
     * Converts an action to a String to be written in the actions' file
     * @return A string formatted to be written in the actions' file
     */
    public String toFileString() {
        String format = "ACT\n" + actionName + " " + noCharacters + "\n";
        if (!preconditions.isEmpty())
            format += "PRE\n";
        for (Precondition p : preconditions) {
            format += p.toFileString() + "\n";
        }
        if (!posconditions.isEmpty())
            format += "POS\n";
        for (Poscondition p : posconditions) {
            format += p.toFileString() + "\n";
        }
        if (!tensions.isEmpty())
            format += "TEN\n";
        for (Tension t : tensions) {
            format += t.toFileString() + "\n";
        }
        format += "TEXT\n";
        for (String s : texts) {
            format += s + "\n";
        }
        if (texts.isEmpty())
            format += "PENDING TEXT\n";
        format += "\n";
        return format;
    }
}
