package mexica.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import mexica.story.Story;
import slant.SlantXMLWriter;

/**
 * Action to generate XML output for Slant of a given story
 * @author Ivan Guerrero
 */
public class SlantOutputAction extends AbstractAction {
    private Story story;
    
    public SlantOutputAction(Story story) {
        this.story = story;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        SlantXMLWriter generator = new SlantXMLWriter();
        generator.generateXML(story);
        JOptionPane.showMessageDialog(null, "Story output generated", "Successfully generated", JOptionPane.INFORMATION_MESSAGE);
    }
}
