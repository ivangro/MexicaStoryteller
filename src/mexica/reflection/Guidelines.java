package mexica.reflection;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.CharacterName;
import mexica.api.MexicaSocialAPI;
import mexica.core.*;
import mexica.story.ActionInstantiated;
import mexica.story.DeadAvatarException;
import mexica.story.Story;
import mexica.story.filter.StoryFilterException;
import mexica.tools.InvalidCharacterException;

/**
 * Available guidelines employed during the engagement mode<br>
 * WARNING: Adding a new method to this class implies updating the SlantGuidelines class, which decorates this class.
 * @author Ivan Guerrero
 */
public class Guidelines {
    private Set<Guideline> guidelines;
    private Story story;
    
    public Guidelines() {
        this(null);
    }
    
    public Guidelines(Story story) {
        guidelines = new HashSet<>();
        this.story = story;
    }
    
    public boolean addGuideline(Guideline guideline) {
        Logger.getGlobal().log(Level.FINE, "Guideline added: {0}", guideline);
        if (guideline == Guideline.endStory)
            story.setFinished(true, "UNDEFINED");
        return guidelines.add(guideline);
    }
    
    public boolean addGuideline(Guideline guideline, String reason) {
        Logger.getGlobal().log(Level.FINE, "Guideline added: {0}", guideline);
        if (guideline == Guideline.endStory)
            story.setFinished(true, reason);
        return guidelines.add(guideline);
    }
    
    public boolean removeGuideline(Guideline guideline) {
        Logger.getGlobal().log(Level.FINE, "Guideline removed: {0}", guideline);
        return guidelines.remove(guideline);
    }
    
    public boolean containsGuideline(Guideline guideline) {
        return guidelines.contains(guideline);
    }
    
    /**
     * Determines if the action satisfies the active guidelines
     * @param action Action to be analyzed
     * @return TRUE if the action satisfies the active guidelines
     */
    public boolean satisfiesGuidelines(Action action) {
        boolean result;
        result = (guidelines.isEmpty()) ? true : satisfiesTensionGuidelines(action) && satisfiesNoveltyGuidelines(action);
        return result;
    }
    
    public boolean satisfiesNoveltyGuidelines(Action action) {
        //Novelty analysis
        /*if (containsGuideline(Guideline.lowNovelty)) {
            //Low novelty means no actions are banned
            if (action.getNoTimesEmployed() <= MexicaParameters.LOW_NOVELTY)
                noveltyResult = true;
        }
        else if (containsGuideline(Guideline.strictNovelty)) {
            if (action.getNoTimesEmployed() <= MexicaParameters.STRICT_NOVELTY)
                noveltyResult = true;
        }
        else if (containsGuideline(Guideline.mediumNovelty)) {
            if (action.getNoTimesEmployed() <= MexicaParameters.MEDIUM_NOVELTY)
                noveltyResult = true;
        }
        else if (containsGuideline(Guideline.highNovelty)) {
            if (action.getNoTimesEmployed() <= MexicaParameters.HIGH_NOVELTY)
                noveltyResult = true;
        }*/
        return true;
    }
    
    /**
     * Method employed to validate if an action inserted in engagement mode satisfies the social guidelines
     * @param action The action that will be added to the story
     * @return True if the action follows the social guidelines
     */
    public boolean satisfiesSocialGuidelines(ActionInstantiated action) {
        boolean res = true;
        
        if (guidelines.isEmpty())
            return true;
        
        try {
            Story newStory = MexicaSocialAPI.createStory(story.getActions());
            action = newStory.addAction(action.getAction(), action.getCharactersList().toArray(new CharacterName[0]));
        } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) {
            Logger.getGlobal().log(Level.FINE, "Guidelines: ", ex.getMessage());
        }
        
        if (containsGuideline(Guideline.normsUp)) {
            res = action.breaksSocialNorm();
        }
        else if (containsGuideline(Guideline.normsDown)) {
            res = !action.breaksSocialNorm();
        }
        else if (containsGuideline(Guideline.normsNeutral)) {
            res = true;
        }
        
        return res;
    }
    
    /**
     * Method employed to validate if an action inserted in reflection mode satisfies the social guidelines
     * @param followingAction The action that has the missing condition
     * @param action The action that fulfills the missing condition
     * @return True if the action follows the social guidelines
     */
    public boolean satisfiesSocialGuidelines(ActionInstantiated followingAction, ActionInstantiated action) {
        boolean res = true;
        
        if (guidelines.isEmpty())
            return true;
        
        try {
            Story newStory = MexicaSocialAPI.createStory(story.getActions());
            newStory.setDefaultPosition(story.getDefaultPosition());
            action = newStory.addAction(followingAction, action.getAction(), action.getCharactersList().toArray(new CharacterName[0]));
        } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) {
            Logger.getGlobal().log(Level.FINE, "Guidelines: ", ex.getMessage());
        }
        
        if (containsGuideline(Guideline.normsUp)) {
            res = action.breaksSocialNorm();
        }
        else if (containsGuideline(Guideline.normsDown)) {
            res = !action.breaksSocialNorm();
        }
        else if (containsGuideline(Guideline.normsNeutral)) {
            res = true;
        }
        
        return res;
    }
    
    public boolean satisfiesTensionGuidelines(Action action) {
        boolean tensionResult = false;
        
        if (guidelines.isEmpty())
            return true;
        
        //Tesion analysis
        if (containsGuideline(Guideline.tendencyUp)) {
            tensionResult = true;
            for (Tension t : action.getTensions()) {
                //If the action deactivates a tension, then it is banned
                if (TensionType.isTensionDeactivator(t.getTension())) {
                    tensionResult = false;
                    break;
                }
            }
        }
        else if (containsGuideline(Guideline.tendencyDown)) {
            tensionResult = true;
            for (Tension t : action.getTensions()) {
                //If the action triggers a tension, then it is banned
                if (TensionType.isTensionTrigger(t.getTension())) {
                    tensionResult = false;
                    break;
                }
            }
        }
        else if (containsGuideline(Guideline.tendencyNeutral))
            tensionResult = true;
        return tensionResult;
    }
    
    /**
     * Determines when a story is considered to be finished
     * @return 
     */
    public boolean canTerminate() {
        return guidelines.contains(Guideline.endStory);
    }

    public void removeTensionGuidelines() {
        removeGuideline(Guideline.tendencyDown);
        removeGuideline(Guideline.tendencyUp);
        removeGuideline(Guideline.tendencyNeutral);
    }

    public void removeNoveltyGuidelines() {
        removeGuideline(Guideline.lowNovelty);
        removeGuideline(Guideline.mediumNovelty);
        removeGuideline(Guideline.highNovelty);
        removeGuideline(Guideline.strictNovelty);
    }

    public void removeSocialNormGuidelines() {
        removeGuideline(Guideline.normsDown);
        removeGuideline(Guideline.normsUp);
        removeGuideline(Guideline.normsNeutral);
    }
    
    @Override
    public String toString() {
        return guidelines.toString();
    }
}