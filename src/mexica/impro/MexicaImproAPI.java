package mexica.impro;

import java.io.*;
import java.util.List;
import java.util.Random;
import java.util.logging.*;
import mexica.CharacterName;
import mexica.MexicaParameters;
import mexica.MexicaRepository;
import mexica.action.ERStepAction;
import mexica.core.Action;
import mexica.core.Position;
import mexica.reflection.Guideline;
import mexica.story.ActionInstantiated;
import mexica.story.DeadAvatarException;
import mexica.story.Story;
import mexica.story.filter.StoryFilterException;
import mexica.tools.InvalidCharacterException;

/**
 *
 * @author Ivan Guerrero
 */
public class MexicaImproAPI {
    public static int MEXICA_INSTANCE_ID;
    
    public static int performERCycle(String path) {
        int actions;
        boolean newStory = true;
        //Initialize Mexica
        MexicaParameters.ENABLE_MEXICA_IMPRO = true;
        MexicaRepository.getInstance().loadData();
        
        //Reads the given file
        Story story;
        MexicaImproStoryReader reader = new MexicaImproStoryReader();
        try {
            story = reader.readXML(path);
            newStory = false;
        } catch (Error er) {
            story = new Story();
            Position startingPosition = obtainRandomPosition();
            story.setDefaultPosition(startingPosition);
        }
        
        //Sets the initial action
        boolean initialActionAdded = !story.getActions().isEmpty();
        ActionInstantiated initialAction = null;
        
        while (!initialActionAdded) {
            initialAction = obtainRandomAction(story, false, false);
            try {
                story.addAction(initialAction.getAction(), initialAction.getCharactersList().toArray(new CharacterName[0]));
                initialActionAdded = true;
            } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) {}
        }
        
        //Performs a new step
        try {
            
            ERStepAction step = new ERStepAction(story);
            //if the story is not new, create its guidelines
            if (!newStory) {
                step.getReflectionAction().performFinalAnalysis();
            }
            else {
                story.getGuidelines().addGuideline(Guideline.tendencyNeutral);
                if (MexicaParameters.ENABLE_SOCIAL_ANALYSIS)
                    story.getGuidelines().addGuideline(Guideline.normsNeutral);
            }
            step.actionPerformed(null);
        } catch (Error er) {
            Logger.getGlobal().log(Level.SEVERE, "Status: {0}", er.getMessage());
        }
        
        MexicaImproStoryGenerator writer = new MexicaImproStoryGenerator();
        writer.setActionStore(MexicaRepository.getInstance().getActions());
        
        if (reader.getDocument() != null) 
            actions = writer.generateXML(story, reader.getDocument());
        else
            actions = writer.generateXML(story);
        
        if (actions > 0) {
            writer.sendToStdOutput();
        }
        
        if (story.isFinished()) {
            File finish = new File("finished.xml");
            try {
                finish.createNewFile();
            } catch (IOException ex) {
            }
        }
        
        return actions;
    }
    
    /**
     * Method to obtain a random action
     * @param story
     * @return 
     */
    public static ActionInstantiated obtainRandomAction(Story story) {
        return obtainRandomAction(story, true, true);
    }
    
    /**
     * Method to obtain a random action
     * @param story 
     * @param enableLocationActions If true, the actions that only change the location of a character are enabled
     * @param enableKillingActions If true, the actions that kill a character are enabled
     * @return 
     */
    public static ActionInstantiated obtainRandomAction(Story story, boolean enableLocationActions, boolean enableKillingActions) {
        CharacterName performer, receiver;
        ActionInstantiated action;
        Random random = new Random();
        Action act;
        
        List<Action> actions = MexicaRepository.getInstance().getActions().getActionList();
        List<Action> locationActions = MexicaRepository.getInstance().getActions().getChangePositionActions();
        List<Action> killingActions = MexicaRepository.getInstance().getActions().getKillingActions();
        
        //act = MexicaRepository.getInstance().getActions().getAction("rescued");
        do {
            act = actions.get(random.nextInt(actions.size()));
        } while ((!enableLocationActions && locationActions.contains(act)) || 
                (!enableKillingActions && killingActions.contains(act)));
        
        List<CharacterName> availableCharacters = story.getAvatarFactory().getAvailableCharacterNames();
        performer = availableCharacters.get(random.nextInt(availableCharacters.size()));
        do {
            receiver = availableCharacters.get(random.nextInt(availableCharacters.size()));
        } while (receiver == performer);
        
        action = (act.getNoCharacters() == 2) ? new ActionInstantiated(act, performer, receiver) : new ActionInstantiated(act, performer);
        
        return action;
    }
    
    /**
     * Obtains a random valid position to start a story
     * @return A valid position
     */
    public static Position obtainRandomPosition() {
        Position[] values = Position.values();
        Position startingPosition;
        do {
            startingPosition = values[new Random().nextInt(values.length)];
        } while(!Position.isValidPosition(startingPosition));
        return startingPosition;
    }
    
    public static void main(String[] args) throws IOException {
        if (args.length == 2) {
            MEXICA_INSTANCE_ID = Integer.parseInt(args[1]);
            LogManager.getLogManager().reset();
            Logger.getGlobal().setLevel(Level.INFO);
            FileHandler handler = new FileHandler("mexica.log", true);
            SimpleFormatter formatter = new SimpleFormatter();
            handler.setFormatter(formatter);
            
            Logger.getGlobal().addHandler(handler);
            int actions;
            do {
                actions = performERCycle(args[0]);
            } while (actions <= 0);
        }
        else
            System.err.println("Usage: MexicaImproAPI XMLStoryFile MEXICA_INSTANCE_ID");
    }
}