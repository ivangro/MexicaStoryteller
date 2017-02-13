package mexica.story.guidelines;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.CharacterName;
import mexica.MexicaParameters;
import mexica.reflection.Guideline;
import mexica.reflection.Guidelines;
import mexica.story.*;
import mexica.story.filter.StoryFilterException;
import mexica.story.log.StoryLog;
import mexica.tools.InvalidCharacterException;

/**
 * Class to analyze the social norm vector of a story
 * @author Ivan Guerrero
 */
public class SocialNormAnalyzer {
    private static SocialNormAnalyzer instance = new SocialNormAnalyzer();
    private List<StoryNorms> stories;
    private SocialNormsIO socialNormsIO;
        
    private SocialNormAnalyzer() {
        socialNormsIO = new SocialNormsIO();
        stories = readPreviousStories();
    }
    
    public static SocialNormAnalyzer getInstance() {
        return instance;
    }
    
    public void updateGuidelines(Story story) {
        StoryNorms socialNormsGuide = obtainSimilarStory(story);
        Guidelines guidelines = story.getGuidelines();
        guidelines.removeSocialNormGuidelines();
        //TEST
        guidelines.addGuideline(Guideline.normsNeutral);
        try {
            int currentYear = story.getCurrentYear() - 2; //To obtain the tension in the same year (1 automatically added, 1 to convert into zero based index)
            int socialNorms = socialNormsGuide.getSocialNorms().get(currentYear);
            int maxIndex = Math.min(currentYear + MexicaParameters.SOCIAL_NORMS_OFFSET, socialNormsGuide.getSocialNorms().size()-1);
            int futureSocialNorms = socialNormsGuide.getSocialNorms().get(maxIndex);
            int diff = futureSocialNorms - socialNorms;
            String socialNormsGuideline;
            //Logger.getGlobal().log(Level.FINE, "Social norms guide from story {0} with diff {1} in year {2}", new Object[]{socialNormsGuide, diff, currentYear});
            if (diff > 0) {
                //guidelines.addGuideline(Guideline.normsUp);
                socialNormsGuideline = "UP";
            }
            else if (diff < 0) {
                //guidelines.addGuideline(Guideline.normsDown);
                socialNormsGuideline = "DOWN";
            }
            else {
                guidelines.addGuideline(Guideline.normsNeutral);
                socialNormsGuideline = "NEUTRAL";
            }
            StoryNorms socialVector = analyzeStory(-1, story.getActions());
            StoryLog.addMessage(story, "Current social vector : " + socialVector);
            StoryLog.addMessage(story, "Social norms is " + socialNormsGuideline + " from story " + socialNormsGuide.toString());
            Logger.getGlobal().log(Level.INFO, "[TEST]Current social vector {0}", socialVector);
            Logger.getGlobal().log(Level.INFO, "[TEST]Social norms are {0} from story {1}", new Object[]{socialNormsGuideline, socialNormsGuide});
        } catch (Exception ex) {
            Logger.getGlobal().log(Level.WARNING, "Error analyzing guidelines: {0}", ex.getMessage());
            guidelines.addGuideline(Guideline.normsNeutral);
        }
    }
    
    public StoryNorms obtainSimilarStory(Story story) {
        StoryNorms similar = null;
        StoryNorms storyNorms = analyzeStory(-1, story.getActions());
        int minDist = Integer.MAX_VALUE;
        
        for (StoryNorms prevStory : stories) {
            int distance = calculateDistance(prevStory, storyNorms);
            if (distance < minDist) {
                minDist = distance;
                similar = prevStory;
            }
        }
        
        return similar;
    }
    
    private List<StoryNorms> readPreviousStories() {
        stories = socialNormsIO.read();
        return stories;
    }
    
    public SocialNormsIO getSocialNormsIO() {
        return socialNormsIO;
    }

    public StoryNorms analyzeStory(int storyID, List<ActionInstantiated> storyActions) {
        StoryNorms storyNorms = new StoryNorms();
        storyNorms.setStoryID(storyID);
        int numberOfSocialNorms = 0;
        
        Story story = new Story();
        
        for (int i=0; i<storyActions.size(); i++) {
            //Obtain the next action in the story
            ActionInstantiated action = storyActions.get(i);
            try {
                //Apply action to story
                action = story.addAction(action.getAction(), action.getCharactersList().toArray(new CharacterName[0]));
            } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) {
                Logger.getGlobal().log(Level.WARNING, "Exception in social norms analysis: {0}", ex.getMessage());
            }
            //Obtain the context of each character and determine the number of social norms broken
            if (action.breaksSocialNorm())
                numberOfSocialNorms++;
            storyNorms.addSocialNorms(i, numberOfSocialNorms);
        }
        return storyNorms;
    }

    private int calculateDistance(StoryNorms prevStory, StoryNorms storyNorms) {
        int distance = 0;
        List<Integer> prevSocialNorms = prevStory.getSocialNorms();
        List<Integer> currentSocialNorms = storyNorms.getSocialNorms();
        int min = Math.min(prevSocialNorms.size(), currentSocialNorms.size());
        for (int i = 0; i<min; i++) {
            distance += Math.abs(prevSocialNorms.get(i) - currentSocialNorms.get(i));
        } 
        return distance;
    }
}