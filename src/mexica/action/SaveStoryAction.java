package mexica.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import mexica.story.*;

/**
 *
 * @author Ivan Guerrero
 */
public class SaveStoryAction extends AbstractAction {
    private Story story;
    
    public SaveStoryAction(Story story) {
        this.story = story;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        JFileChooser fileChooser = new JFileChooser(".");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Story files", "txt"));
        int result = fileChooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                file.createNewFile();
                FileWriter writer = new FileWriter(file);
                writer.write("START\n");
                for (ActionInstantiated action : story.getActions()) {
                    writer.write(action.toStringWithoutFormat() + "\n");
                }
                writer.write("END\n");
                writer.close();
                JOptionPane.showMessageDialog(null, "File has been saved", 
                                                "Saving successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(null, "Error saving the file. Please try again.\n" + ioe.getMessage(), 
                                                "Error saving", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
