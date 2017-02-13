package mexica.story.analyzer;

import mexica.CharacterName;

/**
 *
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class PrevStoryAvatar {
    private CharacterName name;
    private int actionsPerformed, actionsReceived, tensionsPerformed, tensionsReceived, lastActionNumber;
    
    public PrevStoryAvatar(CharacterName name) {
        this.name = name;
        actionsPerformed = 0;
        actionsReceived = 0;
        tensionsPerformed = 0;
        tensionsReceived = 0;
    }

    /**
     * @return the name
     */
    public CharacterName getName() {
        return name;
    }

    /**
     * @return the actionsPerformed
     */
    public int getActionsPerformed() {
        return actionsPerformed;
    }

    public void addActionsPerformed() {
        actionsPerformed++;
    }

    /**
     * @return the actionsReceived
     */
    public int getActionsReceived() {
        return actionsReceived;
    }

    public void addActionsReceived() {
        actionsReceived++;
    }

    /**
     * @return the tensionsPerformed
     */
    public int getTensionsPerformed() {
        return tensionsPerformed;
    }

    public void addTensionsPerformed() {
        this.tensionsPerformed++;
    }

    /**
     * @return the tensionsReceived
     */
    public int getTensionsReceived() {
        return tensionsReceived;
    }

    public void addTensionsReceived() {
        this.tensionsReceived++;
    }

    /**
     * @return the totalActions
     */
    public int getTotalActions() {
        return actionsPerformed + actionsReceived;
    }

    /**
     * @return the totalTensions
     */
    public int getTotalTensions() {
        return tensionsPerformed + tensionsReceived;
    }

    @Override
    public boolean equals(Object obj) {
        boolean res = obj instanceof PrevStoryAvatar;
        if (res) {
            PrevStoryAvatar av = (PrevStoryAvatar)obj;
            res = av.getName().equals(this.name);
        }
        else {
            res = obj instanceof CharacterName;
            if (res) {
                res = name.equals((CharacterName)obj);
            }
        }
        return res;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    /**
     * @return the lastActionNumber
     */
    public int getLastActionNumber() {
        return lastActionNumber;
    }

    /**
     * @param lastActionNumber the lastActionNumber to set
     */
    public void setLastActionNumber(int lastActionNumber) {
        this.lastActionNumber = lastActionNumber;
    }
}
