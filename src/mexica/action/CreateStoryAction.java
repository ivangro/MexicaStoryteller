package mexica.action;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import mexica.MexicaParameters;
import mexica.story.Story;

/**
 * Action to create a new story employing the E-R creativity model
 * @author Ivan Guerrero
 */
public class CreateStoryAction extends AbstractAction {
    private Story story;
    private ERStepAction stepAction;
    
    public CreateStoryAction(Story story) {
        this.story = story;
        stepAction = new ERStepAction(story);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            while (true) {
                stepAction.actionPerformed(ae);
            }
        } catch (Error e) {
            Logger.getGlobal().log(Level.FINE, "Story finished");
            if (MexicaParameters.DISPLAY_GRAPHICAL_NOTIFICATIONS)
                JOptionPane.showMessageDialog(null, "Story finished");
        }
    }
}
