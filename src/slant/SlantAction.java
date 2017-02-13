package slant;

import java.util.*;

/**
 * Class to represent an action in Slant
 * @author Ivan Guerrero
 */
public class SlantAction implements Comparable<SlantAction> {
    private String actionName;
    private String agent;
    private String direct;
    private List<String> indirects;
    private boolean negated;
    
    public SlantAction() {
        actionName = "";
        agent = "";
        direct = "";
        indirects = new ArrayList<>();
        negated = false;
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
     * @return the agent
     */
    public String getAgent() {
        return agent;
    }

    /**
     * @param agent the agent to set
     */
    public void setAgent(String agent) {
        this.agent = agent;
    }

    /**
     * @return the direct
     */
    public String getDirect() {
        return direct;
    }

    /**
     * @param direct the direct to set
     */
    public void setDirect(String direct) {
        if (direct.equals("_"))
            this.direct = "";
        else
            this.direct = direct;
    }

    /**
     * @return the indirect
     */
    public List<String> getIndirects() {
        List<String> list = new ArrayList<>();
        list.addAll(indirects);
        return list;
    }

    /**
     * @param indirect the indirect to set
     */
    public void setIndirect(String indirect) {
        String trim = indirect.trim();
        String[] split = trim.split(" ");
        indirects = Arrays.asList(split);
    }

    /**
     * @return the negated
     */
    public boolean isNegated() {
        return negated;
    }

    /**
     * @param negated the negated to set
     */
    public void setNegated(boolean negated) {
        this.negated = negated;
    }
    
    @Override
    public String toString() {
        return ((isNegated()) ? "NOT" : "") + actionName + " " + agent + " " + direct + " " + indirects;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SlantAction) {
            return ((SlantAction)obj).actionName.equalsIgnoreCase(actionName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.actionName);
        return hash;
    }

    @Override
    public int compareTo(SlantAction t) {
        return actionName.compareTo(t.actionName);
    }
}
