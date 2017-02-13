package mexica.story.guidelines;

import java.util.List;
import mexica.MexicaParameters;
import mexica.MexicaRepository;
import mexica.engagement.parser.PreviousStory;
import mexica.reflection.Guideline;
import mexica.reflection.Guidelines;
import mexica.story.*;

/**
 * Class to analyze the novelty of a story and manage the novelty guidelines
 * @author Ivan Guerrero
 */
public class NoveltyAnalyzer {
    private Story story;
    
    public NoveltyAnalyzer() {}
    
    /**
     * If the novelty value is 100, the created story is an exact copy of a previous story
     *  thus high novelty is requested, only new actions can be employed
     * If the novelty value is 0, the created story is totally new
     *  thus low novelty is requested, any action can be employed
     * @param story 
     */
    public void analyzeGuidelines(Story story) {
        this.story = story;
        StoryNovelty similarStory = obtainSimilarStory();
        Guidelines guidelines = story.getGuidelines();
        guidelines.removeNoveltyGuidelines();
        
        if (similarStory.getNovelty() >= MexicaParameters.HIGH_NOVELTY_PERCENTAGE)
            guidelines.addGuideline(Guideline.highNovelty);
        else if (similarStory.getNovelty() >= MexicaParameters.MEDIUM_NOVELTY_PERCENTAGE)
            guidelines.addGuideline(Guideline.mediumNovelty);
        else if (similarStory.getNovelty() >= MexicaParameters.STRICT_NOVELTY_PERCENTAGE)
            guidelines.addGuideline(Guideline.strictNovelty);
        else if (similarStory.getNovelty() >= MexicaParameters.LOW_NOVELTY_PERCENTAGE)
            guidelines.addGuideline(Guideline.lowNovelty);
    }
    
    /**
     * Obtains the most similar previous story comparing against the current story
     * @return 
     */
    public StoryNovelty obtainSimilarStory() {
        StoryNovelty novelty = null;
        List<PreviousStory> previousStories = MexicaRepository.getInstance().getPreviousStories();
        double maxNovelty = 0;
        
        int i=0;
        for (PreviousStory prevStory : previousStories) {
            StoryNovelty analysis = analyzeStory(i++, prevStory.getActions());
            if (analysis.getNovelty() > maxNovelty) {
                maxNovelty = analysis.getNovelty();
                novelty = analysis;
            }
        }
        
        return novelty;
    }
    
    /**
     * Analyzes the number of action sequences of the current story are repeated in the given story
     * @param storyID ID of the story
     * @param previousStory Actions of the story
     * @return A novelty study of the given story compared against the current story
     */
    public StoryNovelty analyzeStory(int storyID, List<ActionInstantiated> previousStory) {
        StoryNovelty novelty = new StoryNovelty();
        List<ActionInstantiated> actions = story.getActions();
        int storySize = actions.size() - 1;
        novelty.setTotalPairs(previousStory.size()-1);
        novelty.setStoryID(storyID);
        
        for (int i=0; i<storySize; i++) {
            ActionInstantiated action = actions.get(i);
            ActionInstantiated nextAction = actions.get(i+1);
            for (int j=0; j<previousStory.size()-1; j++) {
                ActionInstantiated psAction = previousStory.get(j);
                ActionInstantiated psNextAction = previousStory.get(j+1);
                if (action.getAction().equals(psAction.getAction()) &&
                    nextAction.getAction().equals(psNextAction.getAction()))
                    novelty.addSimilarPair();
            }
        }
        
        return novelty;
    }
}