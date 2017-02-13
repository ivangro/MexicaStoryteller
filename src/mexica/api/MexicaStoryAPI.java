package mexica.api;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import mexica.CharacterName;
import mexica.MexicaRepository;
import mexica.action.CreateStoryAction;
import mexica.action.ERStepAction;
import mexica.story.ActionInstantiated;
import mexica.story.DeadAvatarException;
import mexica.story.Story;
import mexica.story.filter.StoryFilterException;
import mexica.story.log.StoryLog;
import mexica.tools.InvalidCharacterException;
import slant.MexicaAPI;

/**
 *
 * @author Ivan Guerrero
 */
public class MexicaStoryAPI {
    public MexicaStoryAPI() {
        MexicaRepository.getInstance().loadData();
    }
    
    public Story createStory(ActionInstantiated initialAction) {
        Story story = new Story();
        story.setDefaultPosition(MexicaAPI.obtainRandomPosition());
        boolean validAction = false;
        do {
            try {
                story.addAction(initialAction.getAction(), initialAction.getCharactersList().toArray(new CharacterName[0]));
                validAction = true;
            } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) {
                Logger.getGlobal().log(Level.SEVERE, "Error adding initial action {0}", ex.getMessage());
                initialAction = MexicaAPI.obtainRandomAction(story);
            }
        } while (!validAction);
        
        StoryLog.addMessage(story, "Initial action: " + initialAction);
        StoryLog.addMessage(story, "Default location: " + story.getDefaultPosition().name());
        CreateStoryAction action = new CreateStoryAction(story);
        
        action.actionPerformed(null);
        return story;
    }
    
    public Story createStory() {
        Story story = new Story();
        //mexica.core.Action act = MexicaRepository.getInstance().getActions().getAction("admired_and_respected");
        //ActionInstantiated action = new ActionInstantiated(act, CharacterName.Hunter, CharacterName.Princess);
        ActionInstantiated action = MexicaAPI.obtainRandomAction(story);
        story = createStory(action);
        return story;
    }
    
    public Story performERStep() {
        Story story = new Story();
        ActionInstantiated action = MexicaAPI.obtainRandomAction(story);
        story = performERStep(action);
        return story;
    }
    
    public Story performERStep(ActionInstantiated initialAction) {
        Story story = new Story();
        boolean validAction = false;
        do {
            try {
                story.addAction(initialAction.getAction(), initialAction.getCharactersList().toArray(new CharacterName[0]));
                validAction = true;
            } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) {
                Logger.getGlobal().log(Level.SEVERE, "Error adding initial action {0}", ex.getMessage());
                initialAction = MexicaAPI.obtainRandomAction(story);
            } 
        } while (!validAction);
        
        ERStepAction action = new ERStepAction(story);
        try {
            action.actionPerformed(null);
        } catch (Error er) {
            Logger.getGlobal().log(Level.SEVERE, "Error during the generacion process: {0}", er.getMessage());
        }
        return story;
    }
}