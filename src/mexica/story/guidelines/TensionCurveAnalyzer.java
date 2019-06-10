package mexica.story.guidelines;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.*;
import mexica.CharacterName;
import mexica.core.ActionStore;
import mexica.core.Position;
import mexica.core.TensionType;
import mexica.engagement.parser.PreviousStory;
import mexica.parser.ParseException;
import mexica.parser.PreviousStories;
import mexica.reflection.ActionsParser;
import mexica.reflection.Guideline;
import mexica.reflection.Guidelines;
import mexica.story.*;
import mexica.story.filter.StoryFilterException;
import mexica.story.log.StoryLog;
import mexica.tools.InvalidCharacterException;

/**
 * Class to analyze the tension curves of a story and manage the tension guidelines
 * @author Ivan Guerrero
 */
public class TensionCurveAnalyzer {
    private TensionIO tensionIO;
    private Random random;
    private List<StoryTension> prevStories;
    
    public TensionCurveAnalyzer() {
        tensionIO = new TensionIO();
        random = new Random();
    }

    /**
     * For the guiven story, determines which guidelines will be activated
     * @param story 
     */
    public StoryTension analyzeGuidelines(Story story) {
        StoryTension tensionGuide = obtainSimilarStory(story.getActions(), story.getDefaultPosition());
        Guidelines guidelines = story.getGuidelines();
        guidelines.removeTensionGuidelines();
        //TEST
        //guidelines.addGuideline(Guideline.normsDown);
        try {
            int currentYear = story.getCurrentYear() - 2; //To obtain the tension in the same year (1 automatically added, 1 to convert into zero based index)
            int tension = tensionGuide.getTension(currentYear);
            int maxIndex = Math.min(currentYear + MexicaParameters.TENSION_OFFSET, tensionGuide.getTensions().size()-1);
            int futureTension = tensionGuide.getTension(maxIndex);
            int diff = futureTension - tension;
            String tensionGuideline;
            //Logger.getGlobal().log(Level.INFO, "Tension guide from story {0} with diff {1} in year {2}", new Object[]{tensionGuide, diff, currentYear});
            if (diff > 0) {
                guidelines.addGuideline(Guideline.tendencyUp);
                tensionGuideline = "UP";
            }
            else if (diff < 0) {
                guidelines.addGuideline(Guideline.tendencyDown);
                tensionGuideline = "DOWN";
            }
            else {
                guidelines.addGuideline(Guideline.tendencyNeutral);
                tensionGuideline = "NEUTRAL";
            }
            StoryTension tensionVector = analyzeStory(-1, story.getActions(), story.getDefaultPosition());
            StoryLog.addMessage(story, "Current tension vector is: " + tensionVector);
            StoryLog.addMessage(story, "Tension Guideline is " + tensionGuideline + " from story " + tensionGuide.toString());
            
            //Logger.getGlobal().log(Level.INFO, "Tension vector for year {0} is {1}", new Object[]{currentYear, tensionVector});
            //Logger.getGlobal().log(Level.INFO, "Tension is {0} from story {1}", new Object[]{tensionGuideline, tensionGuide});
            Logger.getGlobal().log(Level.INFO, "Tension guideline is {0} from story {1} in year {2}\nStory tension\t{3}\nGuide tension\t{4}", 
                                    new Object[] {tensionGuideline, tensionGuide.getStoryID(), currentYear, tensionVector.getTensions(), tensionGuide.getTensions()});
        } catch (Exception ex) {
            Logger.getGlobal().log(Level.WARNING, "Error analyzing guidelines: {0}", ex.getMessage());
            guidelines.addGuideline(Guideline.tendencyNeutral);
        }
        
        return tensionGuide;
    }
    
    /**
     * Analyzes the given story to obtain its tensional representations
     * @param story
     * @param initialPosition The initial position of the characters in the story. If null, the default position is the city
     * @return 
     */
    public StoryTension analyzeStory(int storyID, List<ActionInstantiated> storyActions, Position initialPosition) {
        StoryTension storyTension = new StoryTension();
        storyTension.setStoryID(storyID);
        
        Story story = new Story();
        if (initialPosition == null)
            initialPosition = Position.Village;
        
        story.setDefaultPosition(initialPosition);

        for (int i=0; i<storyActions.size(); i++) {
            //Obtain the next action in the story
            ActionInstantiated action = storyActions.get(i);
            try {
                //Apply action to story
                story.addAction(action.getAction(), action.getCharactersList().toArray(new CharacterName[0]));
            } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) {
                Logger.getGlobal().log(Level.WARNING, "Exception in tension analysis: {0}", ex.getMessage());
            }
            //Obtain the context of each character and determine the number of active tensions
            int numberOfTensions = calculateNumberOfTensions(story, i+1);
            storyTension.addTension(i, numberOfTensions);
        }
        return storyTension;
    }

    /**
     * Obtains the most similar story from all the previous stories compared against the given story
     * @param storyActions Actions of the story to be analyzed
     * @return The most similar story
     */
    public StoryTension obtainSimilarStory(List<ActionInstantiated> storyActions, Position initialPosition) {
        StoryTension story, storyTension = null;
        story = analyzeStory(-1, storyActions, initialPosition);
        //Logger.getGlobal().log(Level.INFO, "Tension analysis for current story: {0}", story);
        List<StoryTension> qualitative, quantitative;
        //Obtain the quantitative and qualitative analysis
        qualitative = qualitativeAnalysis(story);
        quantitative = quantitativeAnalysis(story);
        
        //Obtain the stories selected in both analysis
        List<StoryTension> intersection = new ArrayList<>();
        for (StoryTension st : qualitative) {
            if (quantitative.contains(st))
                intersection.add(st);
        }
        
        //Randomly pick a story
        if (!intersection.isEmpty())
            storyTension = intersection.get(random.nextInt(intersection.size()));
        else {
            List<StoryTension> list;
            if (quantitative.isEmpty())
                list = qualitative;
            else if (qualitative.isEmpty())
                list = quantitative;
            else
                list = (random.nextInt(2) == 1) ? quantitative : qualitative;
            
            if (!list.isEmpty()) {
                storyTension = list.get(random.nextInt(list.size()));
            }
        }
            
        return storyTension;
    }
    
    /**
     * Performs a qualitative analysis of the given story compared against the previous stories.
     * @param story
     * @return A list with the most similar stories
     */
    public List<StoryTension> qualitativeAnalysis(StoryTension story) {
        List<StoryTension> result = new ArrayList<>();
        List<Integer> prevStoryEval = new ArrayList<>();
        
        if (prevStories == null)
            prevStories = readPreviousStories();
        List<TensionTendency> tendencies = story.getTendencies();
        //Compare the tendency curve for the given story against each previous story
        for (StoryTension st : prevStories) {
            List<TensionTendency> prevTendencies = st.getTendencies();
            int actions = Math.min(tendencies.size(), prevTendencies.size());
            int eval = 0;
            for (int i=0; i<actions; i++) {
                eval += TensionTendency.evaluate(tendencies.get(i), prevTendencies.get(i));
            }
            prevStoryEval.add(eval);
        }
        
        //Select the maximal evaluations
        int maxValue = Integer.MIN_VALUE;
        for (int i=0; i<prevStoryEval.size(); i++) {
            if (prevStoryEval.get(i) > maxValue) {
                maxValue = prevStoryEval.get(i);
            }
        }
        for (int i=0; i<prevStoryEval.size(); i++) {
            if (prevStoryEval.get(i) == maxValue)
                result.add(prevStories.get(i));
        }
        return result;
    }
    
    /**
     * Performs a quantitative analysis of the given story compared against the previous stories.
     * @param story
     * @return A list with the most similar stories
     */
    public List<StoryTension> quantitativeAnalysis(StoryTension story) {
        List<StoryTension> result = new ArrayList<>();
        List<Integer> prevStoryEval = new ArrayList<>();
        
        if (prevStories == null)
            prevStories = readPreviousStories();
        List<Integer> tensions = story.getTensions();
        
        //Compare the tendency curve for the given story against each previous story
        for (StoryTension st : prevStories) {
            List<Integer> prevTensions = st.getTensions();
            int actions = Math.min(tensions.size(), prevTensions.size());
            int eval = 0;
            for (int i=0; i<actions; i++) {
                eval += Math.abs(tensions.get(i) - prevTensions.get(i));
            }
            prevStoryEval.add(eval);
        }
        
        //Select the minimal evaluations
        int minValue = Integer.MAX_VALUE;
        for (int i=0; i<prevStoryEval.size(); i++) {
            if (prevStoryEval.get(i) < minValue) {
                minValue = prevStoryEval.get(i);
            }
        }
        for (int i=0; i<prevStoryEval.size(); i++) {
            if (prevStoryEval.get(i) == minValue)
                result.add(prevStories.get(i));
        }
        
        return result;
    }
    
    public TensionIO getTensionIO() {
        return tensionIO;
    }
    
    /**
     * Obtains all the story tensions available in the previous stories file
     * @return 
     */
    public List<StoryTension> readPreviousStories() {
        List<StoryTension> stories = new ArrayList<>();
        try {
            stories = tensionIO.read();
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
        }
        
        return stories;
    }
    
    public static int calculateNumberOfTensions(Story story, int year) {
        return calculateNumberOfTensions(story, year, false);
    }
    
    /**
     * Obtains the number of active tensions at the given year inside the given story
     * @param story The story to analyze
     * @param year The year to consider as reference
     * @param unsolvedOnly TRUE if only unsolved (all except Ad) tensions are considered.<br>
     * FALSE to include all the tensions
     * @return The number of active tensions at the given year
     */
    public static int calculateNumberOfTensions(Story story, int year, boolean unsolvedOnly) {
        int numberOfTensions = 0;
        List<Avatar> characters = story.getAvatarFactory().getActiveCharacters(true, year);
        for (Avatar avatar : characters) {
            if (avatar.wasAlive(year)) {
                Set<ConditionInstantiated> tensions = obtainTensions(avatar, year, unsolvedOnly);
                numberOfTensions += tensions.size();
            }
            else if (avatar.wasBorn(year) && !unsolvedOnly) {
                //Count the dead character tension
                numberOfTensions++;
            }
        }
        
        return numberOfTensions;
    }
    
    public static Set<ConditionInstantiated> obtainTensions(Avatar avatar, int year) {
        return obtainTensions(avatar, year, false);
    }
    
    /**
     * Obtains all the known tensions by the given avatar at the given year
     * @param avatar The character to be analyzed
     * @param year The year where the tensions are active
     * @param unsolvedOnly TRUE if only unsolved (all except Ad) tensions are considered.<br>
     * FALSE to include all the tensions
     * @return The active tensions at the given year known by the given avatar
     */
    public static Set<ConditionInstantiated> obtainTensions(Avatar avatar, int year, boolean unsolvedOnly) {
        Set<ConditionInstantiated> result = new HashSet<>();
        
        Set<ConditionInstantiated> tensions = avatar.getContext().getAvatarTensions().getTensions(year);
        for (ConditionInstantiated cond : tensions) {
            if (cond.getCharacterA() == avatar.getName()) {
                if (!(unsolvedOnly && cond.getCondition().getTension() == TensionType.ActorDead))
                    result.add(cond);
            }
        }
        return result;
    }
        
    /**
     * Run it to create the tensions' file from the previous stories file
     * When run locally, the story file is read from the src folder and the tension results are written in
     * the build/classes/src folder
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException, ParseException, FileNotFoundException, URISyntaxException {
        //WARNING: When the tension is going to be analyzed, deactivate the story exceptions 
        MexicaParameters.ENABLE_STORY_EXCEPTIONS = false;
        ActionsParser actionsParser = new ActionsParser();
        actionsParser.initParse();
        ActionStore actions = actionsParser.getActions();
        TensionCurveAnalyzer analyzer = new TensionCurveAnalyzer();
        
        boolean illogicalActions = MexicaParameters.ENABLE_ILLOGICAL_ACTIONS;
        MexicaParameters.ENABLE_ILLOGICAL_ACTIONS = true;
        PreviousStories parser = new PreviousStories(actions, MexicaParameters.PREV_STORIES_FILE);
        parser.Start();
        MexicaParameters.ENABLE_ILLOGICAL_ACTIONS = illogicalActions;
        
        List<PreviousStory> stories = parser.getStories();
        
        int i=0;
        for (PreviousStory story : stories) {
            StoryTension analyzeStory = analyzer.analyzeStory(i, story.getActions(), story.getInitialLocation());
            analyzer.getTensionIO().append(analyzeStory);
            i++;
        }
        
        MexicaParameters.ENABLE_STORY_EXCEPTIONS = true;
    }
}