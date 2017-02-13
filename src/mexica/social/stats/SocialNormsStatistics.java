package mexica.social.stats;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.MexicaParameters;
import mexica.api.MexicaSocialAPI;
import mexica.core.ActionStore;
import mexica.parser.PreviousStories;
import mexica.engagement.parser.PreviousStory;
import mexica.parser.ParseException;
import mexica.reflection.ActionsParser;
import mexica.story.*;

/**
 * Class to perform statistical analysis for the social data in the previous stories
 * @author Ivan Guerrero
 */
public class SocialNormsStatistics {
    private List<SocialNormsResult> results;
    
    public SocialNormsStatistics() {
        results = new ArrayList<>();
    }
    
    public SocialNormsAnalysis analyzePreviousStories(List<PreviousStory> stories) {
        SocialNormsAnalysis analysis = new SocialNormsAnalysis();
        for (int i=0; i<stories.size(); i++) {
            Logger.getGlobal().log(Level.SEVERE, "Analyzing story: {0}", i);
            List<ActionInstantiated> storyActions = MexicaSocialAPI.performSocialAnalysis(stories.get(i).getActions());
            SocialNormsResult result = new SocialNormsResult(i);
            for (ActionInstantiated action : storyActions) {
                switch (action.getSocialStatus()) {
                    case actionJustified:
                        result.addJustifiedAction(action);
                        break;
                    case breakSocialNorm:
                        result.addSocialNormAction(action);
                        break;
                }
            }
            Logger.getGlobal().log(Level.SEVERE, "Result: {0}", result);
            results.add(result);
        }
        analysis.analyzeResults(results);
        return analysis;
    }
    
    public static void main(String[] args) throws FileNotFoundException, URISyntaxException, MalformedURLException, ParseException {
        //WARNING: When the tension is going to be analyzed, deactivate the story exceptions 
        Logger.getGlobal().setLevel(Level.SEVERE);
        MexicaParameters.ENABLE_STORY_EXCEPTIONS = false;
        ActionsParser actionsParser = new ActionsParser();
        actionsParser.initParse();
        ActionStore actions = actionsParser.getActions();
        boolean illogicalActions = MexicaParameters.ENABLE_ILLOGICAL_ACTIONS;
        MexicaParameters.ENABLE_ILLOGICAL_ACTIONS = true;
        PreviousStories parser = new PreviousStories(actions, MexicaParameters.PREV_STORIES_FILE);
        parser.Start();
        MexicaParameters.ENABLE_ILLOGICAL_ACTIONS = illogicalActions;
        List<PreviousStory> stories = parser.getStories();
        SocialNormsStatistics stats = new SocialNormsStatistics();
        SocialNormsAnalysis analysis = stats.analyzePreviousStories(stories);
        System.out.println(analysis);
    }
}
