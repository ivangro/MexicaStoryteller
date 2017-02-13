package slant.parser;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mexica.MexicaParameters;
import mexica.core.*;
import slant.*;

/**
 * Class to parse the contents of the actions file of Mexica with Slant information
 * <SLANT> ::= <SLANT_ACTION>*
 * <SLANT_ACTION> ::= "NOT"? <ACTION> <AGENT> (<DIRECT> <INDIRECT>*)?
 * <ACTION> ::= string
 * <DIRECT> ::= <AGENT> | <THING> | <NONE>
 * <INDIRECT> ::= <AGENT> | <THING>
 * <AGENT> ::= "A" | "B" | "a" | "b"
 * <THING> ::= string
 * <NONE> ::= "_"
 * @author Ivan Guerrero
 */
public class SlantActionParser {
    // MEXICA ACTIONS
    public static final String ACTION_COMMENT = "(\\s*\\*\\*\\*.+\\*\\*\\*)?";
    public static final String ACTION_COMMENT_2 = "(\\s*;.*)?";
    public static final String ACTION_START = "\\s*ACT.*";
    /** actionName noCharacters */
    public static final String ACTION_DESCRIPTION = "\\s*(\\w+)\\s+(\\d).*";
    public static final String PRECONDITION_START = "\\s*PRE.*";
    /** E character character intensity (typeOfIntensity | *) */
    public static final String PRECONDITION_EMOTION = "\\s*E\\s+(\\w+)\\s+(\\w+)\\s+([+-]\\d)\\s+([\\d\\*]).*";
    /** T tension character (character|* +?)? */
    public static final String PRECONDITION_TENSION = "\\s*T\\s+(\\w+)\\s+(\\w+)\\s+(([\\w+\\*])\\s+(\\+)?)?.*";
    public static final String POSCONDITION_START = "\\s*POS.*";
    public static final String TENSION_START = "\\s*TEN.*";
    /** P character (numberOfPosition | b_pos) */
    public static final String POSCONDITION_POSITION = "\\s*P\\s+(\\w+)\\s+((\\d)|(b_pos)).*";
    /** E (character | linkedCharacter) character (intensity | %) typeOfIntensity */
    public static final String POSCONDITION_EMOTION = "\\s*E\\s+(\\w+)\\s+(\\w+)\\s+(([+-]\\d)|(\\%))\\s+(\\d).*";
    /** T tension character (character +?)? */
    public static final String POSCONDITION_TENSION = "\\s*T\\s+(\\w+)\\s+(\\w+)(\\s+(\\w+)\\s*(\\+)?)?.*";
    public static final String TEXT_START = "\\s*TEXT.*";
    public static final String TEXT = ".*";
    public static final String END_OF_FILE = "\\s*END.*";
    // SLANT ACTIONS
    public static final String SLANT_START = "\\s*SLANT.*";
    /** <SLANT_ACTION> ::= "NOT"? <ACTION> <AGENT> <DIRECT> <INDIRECT>* */
                                              //   NOT        ACTION    AGENT DIRECT   INDIRECT
    public static final String SLANT_ACTION = "\\s*(NOT)?\\s*(\\w+)\\s+(\\w)(\\s+\\w+)?((\\s+\\w+)?(\\s+\\w+)?)\\s*(;.*)?";
    
    public static Pattern pComment, pComment2, pActionStart, pActionDesc, pPrecStart, pPrecEmotion, pPrecTension,
            pPosStart, pTensionStart, pPosPosition, pPosTension, pPosEmotion, pTextStart, pText, pEOF, 
            pSlantStart, pSlantAction;

    private enum ActionState {Default, Action, Precondition, Poscondition, Tension, Text, Slant};
    
    private ActionState state = ActionState.Default;
    
    static {
        pComment = Pattern.compile(ACTION_COMMENT);
        pComment2 = Pattern.compile(ACTION_COMMENT_2);
        pActionStart = Pattern.compile(ACTION_START);
        pActionDesc = Pattern.compile(ACTION_DESCRIPTION);
        pPrecStart = Pattern.compile(PRECONDITION_START);
        pPrecEmotion = Pattern.compile(PRECONDITION_EMOTION);
        pPrecTension = Pattern.compile(PRECONDITION_TENSION);
        pPosStart = Pattern.compile(POSCONDITION_START);
        pTensionStart = Pattern.compile(TENSION_START);
        pPosPosition = Pattern.compile(POSCONDITION_POSITION);
        pPosTension = Pattern.compile(POSCONDITION_TENSION);
        pPosEmotion = Pattern.compile(POSCONDITION_EMOTION);
        pTextStart = Pattern.compile(TEXT_START);
        pText = Pattern.compile(TEXT);
        pEOF = Pattern.compile(END_OF_FILE);
        pSlantStart = Pattern.compile(SLANT_START);
        pSlantAction = Pattern.compile(SLANT_ACTION);
    }
    
    private MexicaAction currentAction;
    private ActionStore actions;
    private Scanner scanner;
    
    public SlantActionParser() {
    }
    
    public void initParse(String filePath) throws Exception {
        //The file is read from the src folder
        Logger.getGlobal().log(Level.FINE, "Retrieving slant actions from {0}", getClass().getClassLoader().getResource(filePath));
        scanner = new Scanner(new java.io.File(filePath));
        //scanner = new Scanner(getClass().getClassLoader().getResource(filePath).openStream());
        actions = new ActionStore();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            parseLine(line);
        }
    }
    
    public ActionStore getActions() {
        return actions;
    }
    
    public boolean parseLine(String nextLine) {
        Matcher m;
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
            state = ActionState.Action;
            return true;
        }
        m = pActionDesc.matcher(nextLine);
        if (m.matches()) {
            currentAction = new MexicaAction(new Action());
            currentAction.setActionName(m.group(1));
            currentAction.setNoCharacters(Integer.parseInt(m.group(2)));
            return true;
        }
        m = pPrecStart.matcher(nextLine);
        if (m.matches()) {
            state = ActionState.Precondition;
            return true;
        }
        m = pPosStart.matcher(nextLine);
        if (m.matches()) {
            state = ActionState.Poscondition;
            return true;
        }
        m = pTensionStart.matcher(nextLine);
        if (m.matches()) {
            state = ActionState.Poscondition;
            return true;
        }
        m = pTextStart.matcher(nextLine);
        if (m.matches()) {
            state = ActionState.Text;
            return true;
        }
        m = pSlantStart.matcher(nextLine);
        if (m.matches()) {
            state = ActionState.Slant;
            return true;
        }
        m = pSlantAction.matcher(nextLine);
        if (m.matches() && state == ActionState.Slant) {
            SlantAction slantAction = new SlantAction();
            slantAction.setNegated(m.group(1) != null);
            slantAction.setActionName(m.group(2));
            slantAction.setAgent(m.group(3));
            if (m.group(4) != null)
                slantAction.setDirect(m.group(4).trim());
            if (m.group(5) != null && m.group(5).length() > 0)
                slantAction.setIndirect(m.group(5).trim());
            
            currentAction.addSlantAction(slantAction);
            return true;
        }
        m = pEOF.matcher(nextLine);
        if (m.matches()) {
            if (currentAction != null)
                actions.addAction(currentAction);
            return true;
        }
        switch (state) {
            case Action:
                break;
            case Poscondition:
                return parsePoscondition(nextLine);
            case Precondition:
                return parsePrecondition(nextLine);
            case Text:
                m = pText.matcher(nextLine);
                if (m.matches()) {
                    currentAction.addText(nextLine);
                    return true;
                }
        }
        return false;
    }
    
    private boolean parsePrecondition(String nextLine) {
        Matcher m = pPrecEmotion.matcher(nextLine);
        if (m.matches()) {
            Precondition prec = new Precondition();
            prec.setCharacterA(m.group(1));
            prec.setCharacterB(m.group(2));
            prec.setIntensity(prec.analyzeNumber(m.group(3)));
            prec.setEmotionType(m.group(4));
            prec.setConditionType(ConditionType.Emotion);
            currentAction.addPrecondition(prec);
            return true;
        }
        m = pPrecTension.matcher(nextLine);
        if (m.matches()) {
            Precondition prec = new Precondition();
            prec.setTensionType(m.group(1));
            prec.setCharacterA(m.group(2));
            prec.setCharacterB(m.group(4));
            prec.setPresenceConditioned(m.group(5));
            prec.setConditionType(ConditionType.Tension);
            currentAction.addPrecondition(prec);
            return true;
        }
        return false;
    }
    
    private boolean parsePoscondition(String nextLine) {
        Matcher m = pPosPosition.matcher(nextLine);
        if (m.matches()) {
            Poscondition pos = new Poscondition();
            pos.setCharacterA(m.group(1));
            pos.setPosition(m.group(2));
            pos.setConditionType(ConditionType.Position);
            currentAction.addPoscondition(pos);
            return true;
        }
        m = pPosEmotion.matcher(nextLine);
        if (m.matches()) {
            Poscondition pos = new Poscondition();
            pos.setCharacterA(m.group(1));
            pos.setCharacterB(m.group(2));
            pos.setIntensity(pos.analyzeNumber(m.group(3)));
            pos.setEmotionType(m.group(6));
            pos.setConditionType(ConditionType.Emotion);
            currentAction.addPoscondition(pos);
            return true;
        }
        m = pPosTension.matcher(nextLine);
        if (m.matches()) {
            Tension ten = new Tension();
            ten.setTensionType(m.group(1));
            ten.setCharacterA(m.group(2));
            ten.setCharacterB(m.group(4));
            ten.setPresenceConditioned(m.group(5));
            ten.setConditionType(ConditionType.Tension);
            currentAction.addTension(ten);
            return true;
        }
        return false;
    }
    
    /**
     * Parses the slant actions file from the src folder
     * @param args 
     */
    public static void main(String[] args) {
        try {
            SlantActionParser parser = new SlantActionParser();
            parser.initParse(MexicaParameters.SLANT_ACTIONS_FILE);
        } catch (Exception ex) {
            Logger.getLogger(SlantActionParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}