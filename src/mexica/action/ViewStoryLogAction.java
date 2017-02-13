package mexica.action;

import java.awt.event.ActionEvent;
import javax.swing.*;
import mexica.gui.utils.StoryLogViewer;
import mexica.story.Story;

/**
 * Class to store in a file the story log
 * @author Ivan Guerrero
 */
public class ViewStoryLogAction extends AbstractAction {
    private Story story;
    private JFrame parent;
    public ViewStoryLogAction(JFrame parent, Story story) {
        this.parent = parent;
        this.story = story;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        StoryLogViewer viewer = new StoryLogViewer(parent, false, story);
        viewer.setVisible(true);
    }
}
