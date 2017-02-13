package mexica.engagement.parser;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mexica.CharacterName;
import mexica.MexicaParameters;
import mexica.MexicaRepository;
import mexica.core.*;
import mexica.story.ActionInstantiated;

/**
 * Class to obtain atoms from the previous stories file
 * @author Ivan Guerrero
 */
@Deprecated
public class PreviousStoriesParser {
    public static final String COMMENT = "(\\s*;.*)?";
    public static final String NEW_STORY = "\\s*START\\s*(\\w+)?\\s*";

    /** 
     * P: performer 
     * R: receiver
     * N: none
     * B: both
     * H: Hierarchical relation
     * G: Gender relation
     */
    /** SN|SE-[PRBNHG]"[" contextOwner* "]"-textID */
    /** G2-   G3      -    G4              -G6*/
    public static final String SOCIAL_NORM = "(\\s+(SN|SE)-([PRBNHG])\\[((\\w+\\s*)+)\\]-(\\d+))?";
    /** performer actionName receiver */
    public static final String ACTION = "\\s*(\\w+)\\s+(\\w+)\\s+(\\w+)" + SOCIAL_NORM + "(\\s*;.*)?";
    /** performer actionName */
    public static final String SIMPLE_ACTION = "\\s*(\\w+)\\s+(\\w+)" + SOCIAL_NORM + "(\\s*;.*)?";
    /** performer Realised ACTION | SIMPLE_ACTION */
    public static final String REALIZED_ACTION = "\\s*(\\w+)\\s+(Realised)";
    public static final String END_OF_FILE = "\\s*END\\s*";
    
    public static Pattern pComment, pNewStory, pAction, pSimpleAction, pRealizedSimpleAction, pRealizedAction, pEndOfFile;
    
    private List<PreviousStory> stories;
    private PreviousStory story;
    private ActionStore actionStore;
    private Scanner scanner;
    
    static {
        pComment = Pattern.compile(COMMENT);
        pNewStory = Pattern.compile(NEW_STORY);
        pAction = Pattern.compile(ACTION);
        pSimpleAction = Pattern.compile(SIMPLE_ACTION);
        pRealizedAction = Pattern.compile(REALIZED_ACTION + ACTION);
        pRealizedSimpleAction = Pattern.compile(REALIZED_ACTION + SIMPLE_ACTION);
        pEndOfFile = Pattern.compile(END_OF_FILE);
    }
    
    /**
     * Employed to parse a given text
     * @param actionStore 
     */
    public PreviousStoriesParser(ActionStore actionStore) {
        stories = new ArrayList<>();
        this.actionStore = actionStore;
    }
    
    /**
     * Employed to parse the given file
     * @param actionStore
     * @param path 
     */
    public PreviousStoriesParser(ActionStore actionStore, String path) {
        stories = new ArrayList<>();
        this.actionStore = actionStore;
        Logger.getGlobal().log(Level.INFO, "Retrieving previous stories from {0}", getClass().getClassLoader().getResource(path));
        try {
            //scanner = new Scanner(new File(getClass().getClassLoader().getResource(path).toURI()));
            //scanner = new Scanner(getClass().getClassLoader().getResourceAsStream(path));
            scanner = new Scanner(new java.io.File(path));
        } catch (Exception ex) {
            Logger.getGlobal().log(Level.SEVERE, "Error parsing previous stories {0}", ex.getMessage());
        }
    }

    public void parseFile() {
        try {
            while (scanner.hasNextLine()) {
                parseLine(scanner.nextLine());
            }
        } catch (Error er) {
            Logger.getGlobal().log(Level.FINE, er.getMessage());
        }
    }
    
    public void parseText(String text) {
        story = new PreviousStory();
        String[] lines = text.split("\n");
        try {
            for (String line : lines) {
                parseLine(line);
            }
        } catch (Error er) {
            Logger.getGlobal().log(Level.FINE, er.getMessage());
        }
        stories.add(story);
    }
    
    public boolean parseLine(String nextLine) {
        Matcher m;
        CharacterName realized, performer, receiver;
        Action action, realizedAction;
        ActionInstantiated instance, realizedInstance;
        String[] socialValues;
        
        m = pComment.matcher(nextLine);
        if (m.matches())
            return true;
        
        m = pNewStory.matcher(nextLine);
        if (m.matches()) {
            if (story != null && !story.getActions().isEmpty())
                stories.add(story);
            story = new PreviousStory();
            story.setInitialLocation(m.group(1));
            return true;
        }
        
        m = pRealizedSimpleAction.matcher(nextLine);
        if (m.matches()) {
            realized = CharacterName.valueOfIgnoreCase(m.group(1));
            realizedAction = new Action();
            realizedAction.setActionName(m.group(2));
            realizedAction.setNoCharacters(1);
            performer = CharacterName.valueOfIgnoreCase(m.group(3));
            action = actionStore.getAction(m.group(4));
            socialValues = parseSocialNorm(m, 4);
            instance = new ActionInstantiated(action, performer);
            action = new CompositeAction(realizedAction, instance);
            realizedInstance = new ActionInstantiated(action, realized, performer);
            //realizedInstance.setSocialData(socialValues);
            story.addAction(realizedInstance);
            return true;
        }
        
        m = pRealizedAction.matcher(nextLine);
        if (m.matches()) {
            realized = CharacterName.valueOfIgnoreCase(m.group(1));
            realizedAction = new Action();
            realizedAction.setActionName(m.group(2));
            realizedAction.setNoCharacters(1);
            performer = CharacterName.valueOfIgnoreCase(m.group(3));
            action = actionStore.getAction(m.group(4));
            receiver = CharacterName.valueOfIgnoreCase(m.group(5));
            socialValues = parseSocialNorm(m, 5);
            instance = new ActionInstantiated(action, performer, receiver);
            action = new CompositeAction(realizedAction, instance);
            realizedInstance = new ActionInstantiated(action, realized, performer, receiver);
            //realizedInstance.setSocialData(socialValues);
            story.addAction(realizedInstance);
            return true;
        }
        
        m = pSimpleAction.matcher(nextLine);
        if (m.matches()) {
            performer = CharacterName.valueOfIgnoreCase(m.group(1));
            action = actionStore.getAction(m.group(2));
            socialValues = parseSocialNorm(m, 2);
            if (action != null) {
                instance = new ActionInstantiated(action, performer);
                //instance.setSocialData(socialValues);
                story.addAction(instance);
                return true;
            }
            return false;
        }
        
        m = pAction.matcher(nextLine);
        if (m.matches()) {
            performer = CharacterName.valueOfIgnoreCase(m.group(1));
            action = actionStore.getAction(m.group(2));
            receiver = CharacterName.valueOfIgnoreCase(m.group(3));
            socialValues = parseSocialNorm(m, 3);
            if (action != null) {
                instance = new ActionInstantiated(action, performer, receiver);
                //instance.setSocialData(socialValues);
                story.addAction(instance);
                return true;
            }
            return false;
        }
        
        m = pEndOfFile.matcher(nextLine);
        if (m.matches()) {
            if (story != null && !story.getActions().isEmpty())
                stories.add(story);
            throw new Error("Previous stories parsing stage finished");
        }
        
        return false;
    }
    
    public List<PreviousStory> getStories() {
        return stories;
    }
    
    /**
     * Parses the social norm from the given matcher beginning in the given offset
     * @param m
     * @param offset 
     * @return 
     */
    private String[] parseSocialNorm(Matcher m, int offset) {
        String socialStatus = m.group(2 + offset);
        String socialCharacter = m.group(3 + offset);
        String contexts = m.group(4 + offset);
        String textID = m.group(6 + offset);
        String[] socialNorm = new String[]{socialStatus, socialCharacter, contexts, textID};
        return socialNorm;
    }
    
    /**
     * Counts the number of times each action in a previous story has been employed
     */
    public void countActionsEmployed() {
        for (PreviousStory s : stories) {
            for (ActionInstantiated instance : s.getActions()) {
                Action action = actionStore.getAction(instance.getAction().getActionName());
                action.setNoTimesEmployed(action.getNoTimesEmployed() + 1);
            }
        }
    }
        
    /**
     * Run this file to generate the atom's file from the previous stories' file
     * When run locally, the prev stories file is taken from the src foder.<br>
     * The results are stored in the <default package> folder.<br>
     * @param args 
     */
    public static void main(String[] args) {
        Logger.getGlobal().setLevel(Level.SEVERE);
        //In previous stories illogical actions can be employed
        MexicaParameters.ENABLE_ILLOGICAL_ACTIONS = true;
        MexicaRepository.getInstance().loadData();
        ActionStore actionStore = MexicaRepository.getInstance().getActions();
        PreviousStoriesParser parser = new PreviousStoriesParser(actionStore, MexicaParameters.PREV_STORIES_FILE);
        parser.parseFile();
        
//        for (PreviousStory story : parser.getStories()) {
//            System.out.println("New story");
//            for (ActionInstantiated action : story.getActions()) {
//                System.out.println(action);
//            }
//            System.out.println();
//        }
        
        AtomGenerator generator = new AtomGenerator();
        generator.generateAtoms(parser.getStories());
        AtomWriter writer = new AtomWriter();
        writer.saveAtomsInFile(generator.getAtomsStore(), MexicaParameters.ATOMS_FILE);
        writer.saveAtomsInFile(generator.getSocialAtomsStore(), MexicaParameters.SOCIAL_ATOMS_FILE);
    }
}