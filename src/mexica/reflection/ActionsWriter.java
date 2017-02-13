package mexica.reflection;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import mexica.core.*;

/**
 * Class to store in the actions' file the curent action store with the pertinent updates.
 * @author Ivan Guerrero
 */
public class ActionsWriter {
    private String path;
    
    public ActionsWriter(String path) {
        this.path = path;
    }
    
    public void write(List<Action> actions) throws IOException {
        FileWriter writer;
        Logger.getGlobal().log(Level.INFO, "Saving actions in {0}", path);
        writer = new FileWriter(path, false);
        Object[] myActions = actions.toArray();
        storeActions(myActions, writer);
        writer.close();
    }
    
    /**
     * Stores the given actions into the current actions' file
     * @param actions
     * @throws java.io.IOException
     */
    public void write(DefaultListModel<Action> actions) throws IOException {
        FileWriter writer;
        Logger.getGlobal().log(Level.FINE, "Saving actions in {0}", path);
        writer = new FileWriter(path, false);
        Object[] myActions = actions.toArray();
        storeActions(myActions, writer);
        writer.close();
    }

    /**
     * Determines if the first action name previously alphabetically ordered than the second action name
     * @param a
     * @param a2
     * @return 
     */
    private boolean isGreater(Action a, Action a2) {
        String name1, name2;
        name1 = a.getActionName().toLowerCase();
        name2 = a2.getActionName().toLowerCase();
        int min = Math.min(name1.length(), name2.length());
        for (int i=0; i<min; i++) {
            if (name1.charAt(i) > name2.charAt(i))
                return true;
            else if (name1.charAt(i) < name2.charAt(i))
                return false;
        }
        
        return false;
    }

    private void storeActions(Object[] myActions, FileWriter writer) throws IOException {
        //Orders alphabetically the actions by action name
        for (int i=0; i<myActions.length; i++) {
            for (int j=i+1; j<myActions.length; j++) {
                Action a = (Action)myActions[i];
                Action a2 = (Action)myActions[j];
                if (isGreater(a, a2)) {
                    Action tmp = a;
                    myActions[i] = myActions[j];
                    myActions[j] = tmp;
                }
            }
        }
        
        //Stores the values inside the file
        for (Object myAction : myActions) {
            Action a = (Action)myAction;
            try {
                writer.append(a.toFileString());
            } catch (Exception ex) {
                writer.append("ERROR with action " + a + "\n");
            }
        }
        writer.append("END");
    }
}
