package slant;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.CharacterName;
import mexica.MexicaParameters;
import mexica.MexicaRepository;
import mexica.action.ERStepAction;
import mexica.core.Action;
import mexica.core.Position;
import mexica.story.ActionInstantiated;
import mexica.story.DeadAvatarException;
import mexica.story.Story;
import mexica.story.filter.StoryFilterException;
import mexica.tools.InvalidCharacterException;

/**
 * Class to present the Slant requested funcionality from Mexica
 * @author Ivan Guerrero
 */
public class MexicaAPI {
    /**
     * Based on the story available in the given file, perform a ER cycle step
     * @param path 
     */
    public static int performERCycle(String path) {
        //Initialize mexica
        MexicaParameters.ENABLE_SLANT_MODE = true;
        MexicaRepository.getInstance().loadData();
        //Reads the given file
        Story story;
        SlantXMLReader reader = null;
        try {
            reader = new SlantXMLReader();
            story = reader.readXML(path);
        } catch (Error er) {
            story = new Story();
            Position startingPosition = obtainRandomPosition();
            story.setDefaultPosition(startingPosition);
            
            //Sets the initial action
            boolean initialActionAdded = false;
            do {
                ActionInstantiated action = obtainRandomAction(story);
                try {
                    story.addAction(action.getAction(), action.getCharactersList().toArray(new CharacterName[0]));
                    initialActionAdded = true;
                } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) {}
            } while (!initialActionAdded);
        }
        //Performs a new step
        try {
            ERStepAction step = new ERStepAction(story);
            step.actionPerformed(null);
        } catch (Error er) {
            Logger.getGlobal().log(Level.SEVERE, "Status: {0}", er.getMessage());
        }
        //Writes the generated story in Slant format
        int slantActions = 1;
        SlantXMLWriter writer = new SlantXMLWriter();
        if (reader.getDocument() != null) 
            writer.generateXML(story, reader.getDocument());
        else
            slantActions = writer.generateXML(story);
        
        if (slantActions > 0) {
            writer.sendToStdOutput();
        }
        //writer.saveToFile(path);
        return slantActions;
    }
 
    /**
     * Method to obtain a random action
     * @param story
     * @return 
     */
    public static ActionInstantiated obtainRandomAction(Story story) {
        return obtainRandomAction(story, true);
    }
    
    /**
     * Method to obtain a random action
     * @param story 
     * @param enableLocationActions If true, the actions that only change the location of a character are banned
     * @return 
     */
    public static ActionInstantiated obtainRandomAction(Story story, boolean enableLocationActions) {
        CharacterName performer, receiver;
        ActionInstantiated action;
        Random random = new Random();
        Action act;
        
        List<Action> actions = MexicaRepository.getInstance().getActions().getActionList();
        List<Action> locationActions = MexicaRepository.getInstance().getActions().getChangePositionActions();

        do {
            act = actions.get(random.nextInt(actions.size()));
        } while (!enableLocationActions && locationActions.contains(act));
        
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
    
    public static void main(String[] args) {
        if (args.length == 1) {
            Logger.getGlobal().setLevel(Level.INFO);
            int slantActions;
            do {
                slantActions = performERCycle(args[0]);
            } while (slantActions <= 0);
        }
        else
            System.err.println("Usage: MexicaAPI XMLStoryFile");
    }    
}