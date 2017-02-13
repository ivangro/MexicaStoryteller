package mexica.story.analyzer;

import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.MexicaParameters;
import mexica.reflection.Guideline;
import mexica.story.Story;
import mexica.story.log.StoryLog;

/**
 * Determines when a story can be finished
 * @author Ivan Guerrero
 */
public class EndOfStoryAnalyzer {
    private int impasseCount = 0;
    private final int MAX_IMPASSE_COUNT = 2;
    
    /**
     * Determines if the given story can be finished or not.<br>
     * When a story is considered complete the guideline endOfStory is activated
     * @param story 
     * @param impasseDetected
     */
    public void analyzeStory(Story story, boolean impasseDetected) {
        impasseCount = (impasseDetected) ? impasseCount+1 : 0;
        
        //If the number of actions in the current story has reached the limit, end the story
        if (story.getActions().size() >= MexicaParameters.MAX_ACTIONS_IN_STORY) {
            story.getGuidelines().addGuideline(Guideline.endStory, "MaximumActions");
            Logger.getGlobal().log(Level.FINE, "End of story by number of actions: {0}", story.getActions().size());
            StoryLog.addMessage(story, "End of story, max number of actions reached.");
        }
        //When all the characters are dead, end the story
        else if (story.getAvatarFactory().getActiveCharacters(false, story.getCurrentYear()-1).isEmpty()) {
            story.getGuidelines().addGuideline(Guideline.endStory, "DeadCharacters");
            Logger.getGlobal().log(Level.FINE, "End of story by dead characters");
            StoryLog.addMessage(story, "End of story, all characters are dead.");
        }
        //When two consecutive impasse have been detected, end the story
        else if (impasseCount > MAX_IMPASSE_COUNT) {
            story.getGuidelines().addGuideline(Guideline.endStory, "MultipleImpasses");
            Logger.getGlobal().log(Level.FINE, "End of story by multiple impasses: {0}", impasseCount);
            StoryLog.addMessage(story, "End of story, multiple impasses found.");
        }
    }
}