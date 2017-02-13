package mexica.reflection;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.CharacterName;
import mexica.MexicaRepository;
import mexica.core.Action;
import mexica.engagement.parser.PreviousStory;
import mexica.story.*;
import mexica.story.filter.StoryFilterException;
import mexica.story.log.StoryLog;
import mexica.tools.InvalidCharacterException;


/**
 * Class to select an action from a previous story to break an impasse in the current story
 * @author Ivan Guerrero
 */
public class ImpasseAnalyzer {
    private Random random;
    
    public ImpasseAnalyzer() {
        random = new Random();
    }
    
    /**
     * Analyzes the current story, picks the last action and looks for a suitable next action in the previous stories
     * @param currentStory 
     */
    public boolean analyzeStory(Story currentStory) {
        //Obtains the action that generated an impasse
        List<ActionInstantiated> actions = currentStory.getActions();
        if (actions.isEmpty())
            return false;
        ActionInstantiated lastAction = actions.get(actions.size()-1);
        Map<String, CharacterName> mapping = new HashMap<>();
        List<CharacterName> characters = lastAction.getCharactersList();
        mapping.put("A", characters.get(0));
        if (characters.size() == 2)
            mapping.put("B", characters.get(1));
        
        //Looks for action candidates to break the impasse
        List<ActionPair> candidates = new ArrayList<>();
        
        List<PreviousStory> previousStories = MexicaRepository.getInstance().getPreviousStories();
        for (PreviousStory story : previousStories) {
            List<ActionInstantiated> storyActions = story.getActions();
            for (int i=0; i<storyActions.size()-1; i++) {
                ActionInstantiated act = storyActions.get(i);
                ActionInstantiated nextAct = storyActions.get(i+1);
                //remove composite actions
                if (act.getAction().equals(lastAction.getAction()) && !nextAct.getAction().getActionName().equalsIgnoreCase("realised"))
                    candidates.add(new ActionPair(act, nextAct));
            }
        }
        
        //Look for the actions that breaks the impasse
        List<ActionInstantiated> fullyInstantiated, partiallyInstantiated;
        fullyInstantiated = new ArrayList<>();
        partiallyInstantiated = new ArrayList<>();
        
        for (ActionPair a : candidates) {
            String[] map = a.getNextActionMapping();
            CharacterName performer, receiver;
            if (mapping.containsKey(map[0]) && (map.length == 1 || mapping.containsKey(map[1]))) {
                performer = mapping.get(map[0]);
                if (map.length == 2) {
                    receiver = mapping.get(map[1]);
                    fullyInstantiated.add(new ActionInstantiated(a.getNextAction(), performer, receiver));
                }
                else {
                    fullyInstantiated.add(new ActionInstantiated(a.getNextAction(), performer));
                }
            }
            else if (mapping.containsKey(map[0]) || map.length == 1 || mapping.containsKey(map[1])) {
                performer = (mapping.containsKey(map[0])) ? mapping.get(map[0]) : 
                                instantiateCharacter(currentStory, lastAction.getCharactersList(), mapping);
                receiver = (map.length == 2 && mapping.containsKey(map[1])) ? mapping.get(map[1]) : 
                                instantiateCharacter(currentStory, lastAction.getCharactersList(), mapping);
                if (map.length == 2)
                    partiallyInstantiated.add(new ActionInstantiated(a.getNextAction(), performer, receiver));
                else
                    partiallyInstantiated.add(new ActionInstantiated(a.getNextAction(), performer));
            }
        }
        
        //Try to add a fully instantiated action to the story
        if (!fullyInstantiated.isEmpty()) {
            int offset = random.nextInt(fullyInstantiated.size());
            for (int i=0; i<fullyInstantiated.size(); i++) {
                ActionInstantiated a = fullyInstantiated.get((i + offset) % fullyInstantiated.size());
                try {
                    ActionInstantiated addAction = currentStory.addAction(a.getAction(), a.getCharactersList().toArray(new CharacterName[0]));
                    addAction.setInsertionMode(InsertionMode.Impasse);
                    Logger.getGlobal().log(Level.INFO, "Impasse broken with FI action {0}", addAction);
                    StoryLog.addMessage(currentStory, "Impasse broken with FI action " + addAction);
                    return true;
                } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) {}
            }
        }
        //If there were not available fully instantiated actions, try to add a partially instantiated action
        if (!partiallyInstantiated.isEmpty()) {
            int offset = random.nextInt(partiallyInstantiated.size());
            for (int i=0; i<partiallyInstantiated.size(); i++) {
                ActionInstantiated a = partiallyInstantiated.get((i + offset) % partiallyInstantiated.size());
                try {
                    ActionInstantiated addAction = currentStory.addAction(a.getAction(), a.getCharactersList().toArray(new CharacterName[0]));
                    addAction.setInsertionMode(InsertionMode.Impasse);
                    Logger.getGlobal().log(Level.INFO, "Impasse broken with PI action {0}", addAction);
                    StoryLog.addMessage(currentStory, "Impasse broken with PI action " + addAction);
                    return true;
                } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) {}
            }
        }
        //Impasse couldn't be broken
        return false;
    }
            
    /**
     * Obtains a new character. First obtains a character employed in the story and not employed in the last action.
     * If this process fails, obtains a random character not employed in the story.
     * @param story
     * @param banned The banned characters
     * @return 
     */
    private CharacterName instantiateCharacter(Story story, List<CharacterName> banned, Map<String, CharacterName> mapped) {
        CharacterName name = CharacterName.Not_Defined;
        List<Avatar> activeAvatars = story.getAvatarFactory().getActiveCharacters();
        List<CharacterName> activeCharacters = new ArrayList<>();
        for (Avatar a : activeAvatars) {
            activeCharacters.add(a.getName());
        }
        
        if (!banned.containsAll(activeCharacters)) {
            do {
                name = activeCharacters.get(random.nextInt(activeCharacters.size()));
            } while (banned.contains(name) || mapped.containsValue(name));
        }
        else {
            List<CharacterName> availableCharacters = story.getAvatarFactory().getAvailableCharacterNames();
            do {
                name = availableCharacters.get(random.nextInt(availableCharacters.size()));
            } while (banned.contains(name) || mapped.containsValue(name));
        }
        return name;
    }
}
class ActionPair {
    private ActionInstantiated nextAction;
    private Map<CharacterName, String> mapping;
    private char letter;
    
    ActionPair(ActionInstantiated action, ActionInstantiated nextAction) {
        this.nextAction = nextAction;
        mapping = new HashMap<>();
        letter = 'A';
        mapCharacters(action);
        mapCharacters(nextAction);
    }

    private void mapCharacters(ActionInstantiated action) {
        List<CharacterName> characters = action.getCharactersList();
        if (!mapping.containsKey(characters.get(0))) {
            mapping.put(characters.get(0), String.valueOf(letter));
            letter = (char)(letter + 1);
        }
        if (characters.size() == 2 && !mapping.containsKey(characters.get(1))) {
            mapping.put(characters.get(1), String.valueOf(letter));
            letter = (char)(letter + 1);
        }
    }
    
    String[] getNextActionMapping() {
        String[] characters = new String[nextAction.getCharactersList().size()];
        characters[0] = mapping.get(nextAction.getCharactersList().get(0));
        if (nextAction.getAction().getNoCharacters() == 2)
            characters[1] = mapping.get(nextAction.getCharactersList().get(1));
        
        return characters;
    }
    
    Action getNextAction() {
        return nextAction.getAction();
    }
}