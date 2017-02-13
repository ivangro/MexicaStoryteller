package mexica.core;

import mexica.story.ActionInstantiated;
import mexica.story.TextInstantiated;
import mexica.tools.AvatarFactory;

/**
 * Special action type to represent a composed action (realized, was told)
 * with a contained action (i.e. A realized B action C)
 * The preconditions of the action are omitted
 * The posconditions of the action are obtained from the contained action
 *  If a poscondition has a Lb character, is replaced for the character A if A is friend of C
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class CompositeAction extends Action {
    private Action composedAction;
    private ActionInstantiated contained;
    private AvatarFactory avatarFactory;
    
    /**
     * Creates a new instance of a composite action
     * WARNING: Do not use this constructor for the story elements
     * @param composedAction
     * @param contained 
     */
    public CompositeAction(Action composedAction, ActionInstantiated contained) {
        this(composedAction, contained, null);
    }
    
    public CompositeAction(Action composedAction, ActionInstantiated contained, AvatarFactory avatarFactory) {
        this.composedAction = composedAction;
        this.contained = contained;
        this.avatarFactory = avatarFactory;
        super.setActionName("Realised");

        String composedActionName = composedAction.getActionName();
        for (TextInstantiated text : contained.getTexts()) {
            String txt = text.getText();
            addText("@A " + composedActionName + " " + txt);
        }
    }
    
    /**
     * Returns the action discovered by the character of the composed action
     * @return The action discovered
     */
    public ActionInstantiated getContainedAction() {
        return contained;
    }

    /**
     * A composite action is always secret
     * @return TRUE
     */
    @Override
    public boolean isSecret() {
        return true;
    }
    
    /**
     * @return the avatarFactory
     */
    public AvatarFactory getAvatarFactory() {
        return avatarFactory;
    }
    
    public void setAvatarFactory(AvatarFactory factory) {
        this.avatarFactory = factory;
    }
    
    @Override
    public String toString() {
        String name = composedAction.toString() + " " + contained.toString();
        return name;
    }
    
    @Override
    public String toStringWithoutFormat() {
        return composedAction.toString() + " " + contained.toStringWithoutFormat();
    }
}
