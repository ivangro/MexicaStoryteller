package mexica.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import mexica.CharacterName;
import mexica.MexicaParameters;
import mexica.MexicaRepository;
import mexica.core.Action;
import mexica.core.ActionStore;
import mexica.core.CompositeAction;
import mexica.reflection.Guideline;
import mexica.reflection.ImpasseAnalyzer;
import mexica.reflection.ReflectionCharacterInstantiator;
import mexica.story.*;
import mexica.story.analyzer.EndOfStoryAnalyzer;
import mexica.story.filter.StoryFilterException;
import mexica.story.guidelines.NoveltyAnalyzer;
import mexica.story.guidelines.SocialNormAnalyzer;
import mexica.story.guidelines.TensionCurveAnalyzer;
import mexica.story.log.StoryLog;
import mexica.tools.*;

/**
 * Class to perform a reflection cycle
 * @author Ivan Guerrero
 */
public class ReflectionAction extends AbstractAction{
    private Story story;
    private Random random;
    private TensionCurveAnalyzer tensionAnalyzer;
    private SocialNormAnalyzer socialAnalyzer;
    private NoveltyAnalyzer noveltyAnalyzer;
    private EndOfStoryAnalyzer eosAnalyzer;
    private ImpasseAnalyzer impasseAnalyzer;
    private boolean impasseDetected;
    private boolean illogicalActions;
    
    public ReflectionAction(Story story) {
        random = new Random();
        tensionAnalyzer = new TensionCurveAnalyzer();
        socialAnalyzer = SocialNormAnalyzer.getInstance();
        noveltyAnalyzer = new NoveltyAnalyzer();
        eosAnalyzer = new EndOfStoryAnalyzer();
        impasseAnalyzer = new ImpasseAnalyzer();
        this.story = story;
        illogicalActions = MexicaParameters.ENABLE_ILLOGICAL_ACTIONS;
        MexicaParameters.ENABLE_ILLOGICAL_ACTIONS = true;
    }

    /**
     * Obtains the list of missing conditions and retrieves an action to fulfill each condition
     * @param ae 
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        MexicaParameters.ENABLE_ILLOGICAL_ACTIONS = true;
        if (impasseDetected) {
            if (!attempToBreakImpasse()) {
                story.getGuidelines().addGuideline(Guideline.endStory, "Impasse");
            }
            else
                impasseDetected = false;
        }
        
        if (story.hasMissingConditions()) {
            boolean actionAdded;
            //Obtain one missing condition and satisfy it
            ConditionInstantiated cond = story.getMissingConditions().get(0);
            //Obtains a list of actions employed in the story that satisfies the condition
            List<ActionInstantiated> instanceList = obtainActionsFromStory(cond);
            actionAdded = selectCompositeAction(instanceList, cond);
            
            if (!actionAdded) {
                //Obtains a list of actions to satisfy the condition
                List<Action> actionList = obtainActionsForMissingCondition(cond);
                //Apply one of the actions to the story
                if (!actionList.isEmpty())
                    selectActionForCondition(actionList, cond);
                else
                    Logger.getGlobal().log(Level.WARNING, "No actions for condition {0} with guidelines {1}", new Object[] {cond, story.getGuidelines()});
            }
        }
        //Reset the state of the illogical actions
        MexicaParameters.ENABLE_ILLOGICAL_ACTIONS = illogicalActions;
        
        if (story.getGuidelines().canTerminate()) {
            StoryLog.addMessage(story, "End of story, unbreakeable impasse");
            throw new Error("Unbreakable impasse reached.\nEnd of story suggested in reflection mode");
        }
    }
    
    /**
     * Runs all the methods to try to break an impasse in the current story
     * @return True if the impasse was broken
     */
    public boolean attempToBreakImpasse() {
        return impasseAnalyzer.analyzeStory(story);
    }

    /**
     * Adds the given action to the story
     * @param action
     * @param cond
     * @throws InvalidCharacterLocationsException
     * @throws DeadAvatarException 
     */
    private boolean applyActionToStory(Action action, ConditionInstantiated cond) throws InvalidCharacterException, DeadAvatarException, StoryFilterException {
        ActionInstantiated newAction;
        ActionInstantiated instance = ReflectionCharacterInstantiator.instantiateCharacter(story, action, cond);
        List<CharacterName> characters = instance.getCharactersList();
        if (story.getElementsEmployed().isActionAvailable(instance)) {
            if (MexicaParameters.ENABLE_SOCIAL_ANALYSIS && !story.getGuidelines().satisfiesSocialGuidelines(cond.getAction(), instance))
                throw new StoryFilterException("Action banned by social guidelines: " + instance);
            
            if (!story.getGuidelines().satisfiesTensionGuidelines(action) || !story.getGuidelines().satisfiesNoveltyGuidelines(action))
                return false;
            
            if (action.getNoCharacters() == 2)
                newAction = story.addAction(cond.getAction(), action, characters.get(0), characters.get(1));
            else
                newAction = story.addAction(cond.getAction(), action, characters.get(0));
            
            Logger.getGlobal().log(Level.INFO, "Action added {0} to fulfill condition {1} of action {2}", new Object[] {newAction, cond, cond.getAction()});
            StoryLog.addMessage(story, "(R) Action added: " + newAction);
            return true;
        }
        else
            return false;
    }
    
    /**
     * Determines if there are still missing conditions inside the given story
     * @return TRUE if there are still missing conditions 
     */
    public boolean hasMissingConditions() {
        return story.getMissingConditions().size() > 0;
    }

    /**
     * @param impasseDetected the impasseDetected to set
     */
    public void setImpasseDetected(boolean impasseDetected) {
        this.impasseDetected = impasseDetected;
    }

    /**
     * Obtain available actions to fulfill a missing condition
     * @param cond The missing condition to satisfy
     * @return A list of actions to satisfy the given condition
     */
    private List<Action> obtainActionsForMissingCondition(ConditionInstantiated cond) {
        ActionStore actions = MexicaRepository.getInstance().getActions();
        List<Action> actionList = actions.getActionList(cond.getCondition());
        return actionList;
    }

    /**
     * From the available actions that satisfy the condition, randomly selects one and tries to apply it to the story
     * @param actionList Actions to fulfill the condition
     * @param cond Missing condition to be satisfied
     */
    private boolean selectActionForCondition(List<Action> actionList, ConditionInstantiated cond) {
        //Validate previously employed actions in the story
        boolean actionAdded = false;
        int offset, size;
        size = actionList.size();
        offset = random.nextInt(size);
        for (int i=0; i<size && !actionAdded; i++) {
            Action action = actionList.get((i+offset) % size);
            //Instantiate the action validating appropriate character instantiation and
            //add the action to the story
            try {
                actionAdded = applyActionToStory(action, cond);
            } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) {
                Logger.getGlobal().log(Level.SEVERE, "Action invalidated: {0}, {1}", new Object[] {action, ex.getMessage()});
                StoryLog.addMessage(story, "(R) Action invalidated: " + action + ", " + ex.getMessage());
            }
        }
        
        return actionAdded;
    }

    /**
     * Analyze the tension and social graphs, and manage guidelines for the next engagement phase.<br>
     * Determine if the story can be finished after this step.
     */
    public void performFinalAnalysis() {
        MexicaParameters.ENABLE_ILLOGICAL_ACTIONS = true;
        
        tensionAnalyzer.analyzeGuidelines(story);
        if (MexicaParameters.ENABLE_SOCIAL_ANALYSIS)
            socialAnalyzer.updateGuidelines(story);
        //noveltyAnalyzer.analyzeGuidelines(story);
        eosAnalyzer.analyzeStory(story, impasseDetected);
        
        MexicaParameters.ENABLE_ILLOGICAL_ACTIONS = illogicalActions;
    }

    public boolean getImpasseDetected() {
        return impasseDetected;
    }

    /**
     * Looks for actions previously employed in the story that added the condition
     * @param cond A missing condition
     * @return A list of actions employed in the current story that contain the given condition
     */
    private List<ActionInstantiated> obtainActionsFromStory(ConditionInstantiated cond) {
        List<ActionInstantiated> actions = new ArrayList<>();
        ActionInstantiated action = cond.getAction();
        //Obtains the action index
        int index = story.getActions().indexOf(action);
        
        for (Avatar avatar : story.getAvatarFactory().getActiveCharacters()) {
            for (ConditionInstantiated c : avatar.getContext().getHistoricalFacts()) {
                ComparisonResult res = ConditionComparer.compare(cond, c);
                if (res.isEqualOrEquivalent(true, true, true)) {
                    action = c.getAction();
                    if (action != null && story.getActions().indexOf(action) < index) {
                        actions.add(action);
                    }
                }
            }
        }
        return actions;
    }

    private boolean selectCompositeAction(List<ActionInstantiated> instanceList, ConditionInstantiated cond) {
        boolean actionAdded = false;
        if (!instanceList.isEmpty()) {
            Action realised = new Action();
            realised.setActionName("Realised");
            realised.setNoCharacters(1);
            ActionInstantiated instance = ActionInstantiated.createActionInstantiated(instanceList.get(0));
            Action composite = new CompositeAction(realised, instance, story.getAvatarFactory());
            try {
                instance = story.addAction(cond.getAction(), composite, cond.getAction().getCharactersList().get(0));
                StoryLog.addMessage(story, "(R) Action added: " + instance);
                actionAdded = true;
            } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) {
                Logger.getGlobal().log(Level.WARNING, "Invalid action {0}", composite);
                actionAdded = false;
            }
        }
        return actionAdded;
    }
}