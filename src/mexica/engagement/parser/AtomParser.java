package mexica.engagement.parser;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mexica.MexicaParameters;
import mexica.core.Condition;
import mexica.core.Tension;
import mexica.engagement.Atom;
import mexica.engagement.AtomsStore;
import mexica.engagement.Cell;
import mexica.social.ConditionStatus;
import mexica.social.SocialAtom;

/**
 * Class to parse the atoms file in Mexica's format
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class AtomParser {
    public static final String ATOM_COMMENT = "(\\s*\\*\\*\\*.+\\*\\*\\*)?";
    public static final String ATOM_CELL_HEADER = "\\s*\\*Tension:(\\d+)\\s+EmoLin:(\\d+)\\s*.*";
    /**
     * "**Atom" "["storyID (,storyID)*"]" socialActionName
     */
    public static final String ATOM_DEFINITION = "\\s*\\*{2}\\s*Atom\\s*(\\[\\d+(,\\s*\\d+)*\\])(\\s*(\\w+))?.*";
    /**
     * FeaTen=> AD->number  Lr->number  Hr->number  Pd->number  Pr->number  Ce->number  Lc->number
     */
    public static final String ATOM_TENSION_COUNT = "\\s*FeaTen=\\>\\s+AD-\\>(\\d+)\\s+Lr-\\>(\\d+)\\s+Hr-\\>(\\d+)\\s+Pd-\\>(\\d+)\\s+Pr-\\>(\\d+)\\s+Ce-\\>(\\d+)\\s+Lc-\\>(\\d+).*";
    /**
     * FeaEmoLin=>   Type1->number    Type2->number    Type3->number    Type4->number  
     */
    public static final String ATOM_EMOTION_COUNT = "\\s*FeaEmoLin=\\>\\s+Type1-\\>(\\d+)\\s+Type2-\\>(\\d+)\\s+Type3-\\>(\\d+)\\s+Type4-\\>(\\d+).*";
    public static final String ATOM_TENSION_HEADER = "\\s*Tension=\\>.*";
    public static final String ATOM_EMOTION_HEADER = "\\s*EmoLin=\\>.*";
    public static final String SOCIAL_ATOM_RELATION_HEADER = "\\s*Relation=\\>.*";
    /**
     * [+-]?character([+-]?intensity,emotionType)(:character)?
     */
    public static final String ATOM_EMOTION = "\\s*([+-])?(\\w+)\\(([+-]?\\d),(\\d)\\)(:\\s*(\\w+))?.*";
    /**
     * [+-]?tensionType(character):character?
     */
    public static final String ATOM_TENSION = "\\s*([+-])?(\\w+)\\((\\w+)\\)(:\\s*(\\w+))?.*";
    /**
     * LasAct=> character actionName character?
     */
    public static final String ATOM_LAST_ACTION = "\\s*LasAct=\\>\\s*(\\w+)\\s+(\\w+)(\\s+(\\w+))?.*";
    /**
     * NexAct=> character actionName character?
     */
    public static final String ATOM_NEXT_ACTION = "\\s*NexAct=\\>\\s*(\\w+)\\s+(\\w+)(\\s+(\\w+))?.*";
    /**
     * Rel=> relationType intensity
     */
    public static final String SOCIAL_ATOM_RELATION = "\\s*Rel=\\>\\s*(\\w+)\\s+([+-]?\\d+).*";
    
    private static Pattern pComment, pCellHeader, pDefinition, pTensionCount, pEmotionCount, pTensionHeader, pEmotionHeader,
            pEmotion, pTension, pLastAction, pNextAction;
    /** Social patterns */
    private static Pattern pRelationHeader, pRelation;
    
    static {
        pComment = Pattern.compile(ATOM_COMMENT);
        pCellHeader = Pattern.compile(ATOM_CELL_HEADER);
        pDefinition = Pattern.compile(ATOM_DEFINITION);
        pTensionCount = Pattern.compile(ATOM_TENSION_COUNT);
        pEmotionCount = Pattern.compile(ATOM_EMOTION_COUNT);
        pTensionHeader = Pattern.compile(ATOM_TENSION_HEADER);
        pEmotionHeader = Pattern.compile(ATOM_EMOTION_HEADER);
        pEmotion = Pattern.compile(ATOM_EMOTION);
        pTension = Pattern.compile(ATOM_TENSION);
        pLastAction = Pattern.compile(ATOM_LAST_ACTION);
        pNextAction = Pattern.compile(ATOM_NEXT_ACTION);
        
        pRelationHeader = Pattern.compile(SOCIAL_ATOM_RELATION_HEADER);
        pRelation = Pattern.compile(SOCIAL_ATOM_RELATION);
    }
    
    private Cell currentCell;
    private Atom currentAtom;
    private Scanner scanner;
    private AtomsStore atoms;
    
    public boolean parseLine(String nextLine) {
        Matcher m;
        m = pComment.matcher(nextLine);
        if (m.matches())
            return true;
        m = pCellHeader.matcher(nextLine);
        if (m.matches())
        {
            if (currentAtom != null)
                currentCell.addAtom(currentAtom);
            if (currentCell != null && currentCell.getAtoms().size() > 0) {
                atoms.getCells().add(currentCell);
                currentAtom = null;
            }
            currentCell = new Cell(m.group(1), m.group(2));
            return true;
        }
        m = pDefinition.matcher(nextLine);
        if (m.matches())
        {
            if (currentAtom != null)
                currentCell.addAtom(currentAtom);
            currentAtom = new Atom(currentCell.getAtoms().size());
            currentAtom.setCellID(currentCell.getCellID());
            currentAtom.addAllStoryIDs(parseStoryIDs(m.group(1)));
            if (m.group(3) != null) {
                currentAtom = new SocialAtom(currentAtom, null);
                ((SocialAtom)currentAtom).setSocialAction(m.group(3));
            }
            return true;
        }
        m = pTensionCount.matcher(nextLine);
        if (m.matches())
        {
            currentAtom.addTensionCounters(m.group(1),m.group(2),m.group(3),m.group(4),m.group(5),m.group(6),m.group(7));
            return true;
        }
        m = pEmotionCount.matcher(nextLine);
        if (m.matches())
        {
            currentAtom.addEmotionCounters(m.group(1),m.group(2),m.group(3),m.group(4));
            return true;
        }
        m = pTensionHeader.matcher(nextLine);
        if (m.matches())
            return true;
        m = pEmotionHeader.matcher(nextLine);
        if (m.matches())
            return true;
        m = pRelationHeader.matcher(nextLine);
        if (m.matches())
            return true;
        m = pEmotion.matcher(nextLine);
        if (m.matches())
        {
            currentAtom.addEmotion(m.group(2),m.group(3),m.group(4),m.group(6));
            if (m.group(1) != null) {
                List<Condition> emotions = currentAtom.getEmotions();
                emotions.get(emotions.size()-1).setStatus(ConditionStatus.fromAbbreviation(m.group(1)));
            }
            return true;
        }
        m = pTension.matcher(nextLine);
        if (m.matches())
        {
            currentAtom.addTension(m.group(2),m.group(3),m.group(5));
            if (m.group(1) != null) {
                List<Tension> tensions = currentAtom.getTensions();
                tensions.get(tensions.size()-1).setStatus(ConditionStatus.fromAbbreviation(m.group(1)));
            }
            return true;
        }
        m = pLastAction.matcher(nextLine);
        if (m.matches())
        {
            currentAtom.addPreviousAction(m.group(1),m.group(2),m.group(4));
            return true;
        }
        m = pNextAction.matcher(nextLine);
        if (m.matches())
        {
            currentAtom.addNextAction(m.group(1),m.group(2),m.group(4));
            return true;
        }
        m = pRelation.matcher(nextLine);
        if (m.matches()) {
            //currentAtom = new SocialAtom(currentAtom, null);
            String relationType = m.group(1);
            int intensity = Integer.parseInt(m.group(2));
            switch (relationType) {
                case "social":
                    ((SocialAtom)currentAtom).setSocialRelation(intensity);
                    break;
                case "gender":
                    ((SocialAtom)currentAtom).setGenderRelation(intensity);
                    break;
            }
            return true;
        }
        return false;
    }

    public AtomParser() { }
    
    public void initParse(String path) {
        try {
            //Logger.getGlobal().log(Level.INFO, "Atoms read from {0}", getClass().getClassLoader().getResource(path));
            Logger.getGlobal().log(Level.INFO, "Atoms read from {0}", path);
            scanner = new Scanner(new java.io.File(path));
            //scanner = new Scanner(getClass().getClassLoader().getResourceAsStream(path));
            atoms = new AtomsStore();
            currentCell = null;
            currentAtom = null;
            
            while (scanner.hasNextLine()) {
                String nextLine = scanner.nextLine();
                parseLine(nextLine);
            }
            if (currentAtom != null)
                currentCell.addAtom(currentAtom);
            if (currentCell != null && currentCell.getAtoms().size() > 0)
                atoms.getCells().add(currentCell);
        } catch (Exception ex) {
            Logger.getGlobal().log(Level.WARNING, "Error reading atoms from {0}", path);
        }
    }
    
    public AtomsStore getAtoms() {
        return atoms;
    }
    
    public static void main(String[] args)
    {
        AtomParser parser = new AtomParser();
        //For regular atoms
        parser.initParse(MexicaParameters.ATOMS_FILE);
        //For social atoms
        parser.initParse(MexicaParameters.SOCIAL_ATOMS_FILE);
    }

    private List<Integer> parseStoryIDs(String listIDs) throws NumberFormatException {
        List<Integer> storyIDs = new ArrayList<>();
        listIDs = listIDs.substring(1, listIDs.length()-1);
        String[] ids = listIDs.split(",");
        for (String id : ids) {
            storyIDs.add(Integer.parseInt(id.trim()));
        }
        return storyIDs;
    }
}