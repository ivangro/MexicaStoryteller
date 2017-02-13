package mexica.story;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mexica.MexicaParameters;
import mexica.core.SpecialAction;

/**
 * Parser for special actions. No longer employed for social actions.
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class SpecialActionsParser {
    public static final String ACTION_COMMENT = "(\\s*\\*\\*\\*.+\\*\\*\\*)?";
    public static final String ACTION_COMMENT_2 = "(\\s*;.*)?";
    public static final String ACTION_START = "\\s*ACT.*";
    public static final String ACTION_DESCRIPTION = "\\s*(\\w+)\\s+(\\d).*";
    
    public static final String TEXT_START = "\\s*TEXT.*";
    public static final String TEXT = ".*";
    public static final String END_OF_FILE = "\\s*END.*";
    
    private static Pattern pComment, pComment2, pActionStart, pActionDesc, pTextStart, pText, pEOF;
    
    static {
        pComment = Pattern.compile(ACTION_COMMENT);
        pComment2 = Pattern.compile(ACTION_COMMENT_2);
        pActionStart = Pattern.compile(ACTION_START);
        pActionDesc = Pattern.compile(ACTION_DESCRIPTION);
        pTextStart = Pattern.compile(TEXT_START);
        pText = Pattern.compile(TEXT);
        pEOF = Pattern.compile(END_OF_FILE);
    }
    
    private Scanner scanner;
    private SpecialActionStore actions;
    private SpecialAction currentAction;
    
    public SpecialActionsParser(String path) {
        try {
            Logger.getGlobal().log(Level.INFO, "Retrieving special actions from {0}", path);
            scanner = new Scanner(new java.io.File(path));
            actions = new SpecialActionStore();
        } catch (Exception ex) {
            Logger.getGlobal().log(Level.INFO, "Error retrieving special actions from {0}, {1}", 
                                                new Object[] {path, ex.getMessage()});
        }
    }
    
    public SpecialActionsParser() {
        this(MexicaParameters.SPECIAL_ACTIONS_FILE);
    }
    
    public void initParse()
    {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            parseLine(line);
        }
    }
    
    public boolean parseLine(String nextLine) {
        Matcher m;
        
        m = pEOF.matcher(nextLine);
        if (m.matches()) {
            if (currentAction != null)
                actions.addAction(currentAction);
            return true;
        }
        m = pComment.matcher(nextLine);
        if (m.matches()) {
            return true;
        }
        m = pComment2.matcher(nextLine);
        if (m.matches()) {
            return true;
        }
        m = pActionStart.matcher(nextLine);
        if (m.matches()) {
            if (currentAction != null) {
                actions.addAction(currentAction);
            }
            return true;
        }
        m = pActionDesc.matcher(nextLine);
        if (m.matches()) {
            currentAction = new SpecialAction();
            currentAction.setActionName(m.group(1));
            currentAction.setNoCharacters(Integer.parseInt(m.group(2)));
            return true;
        }
        m = pTextStart.matcher(nextLine);
        if (m.matches()) {
            return true;
        }
        m = pText.matcher(nextLine);
        if (m.matches()) {
            currentAction.addText(nextLine);
            return true;
        }
        return false;
    }
    
    public SpecialActionStore getActions() {
        return actions;
    }
    
    public static void main(String[] args) {
        SpecialActionsParser parser = new SpecialActionsParser();
        parser.initParse();
        System.out.println(parser.getActions().getActionList());
    }
}
