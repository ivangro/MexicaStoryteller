package mexica.action;

import java.awt.event.ActionEvent;
import java.util.logging.*;
import javax.swing.AbstractAction;
import mexica.MexicaParameters;
import mexica.story.Story;

/**
 * Class to perform a comple Engagement-Reflection cycle
 * @author Ivan Guerrero
 */
public class ERStepAction extends AbstractAction {
    private Story story;
    private EngagementAction engagement;
    private ReflectionAction reflection;
    private boolean impasseDetected;
    
    public ERStepAction(Story story) {
        this.story = story;
        engagement = new EngagementAction(story);
        reflection = new ReflectionAction(story);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (MexicaParameters.INITIAL_STATE) {
            engagementStep(ae);
            reflectionStep(ae);
        }
        else {
            reflectionStep(ae);
            engagementStep(ae);
        }
        
        if (story.getGuidelines().canTerminate()) {
            throw new Error("End of story");
        }
    }

    private void engagementStep(ActionEvent ae) {
        Logger.getGlobal().log(Level.INFO, "ENGAGEMENT for story {0}", story.getActions());
        try {
            for (int i=0; i<MexicaParameters.MAX_ENGAGEMENT_ACTIONS; i++) {
                Logger.getGlobal().log(Level.FINE, "ENGAGEMENT STARTS {0}", i);
                engagement.actionPerformed(ae);
            }
            setImpasseDetected(false);
        } catch (Error e) {
            Logger.getGlobal().log(Level.INFO, "Impasse reached: {0}", e.getMessage());
            setImpasseDetected(true);
            story.setImpasseDetected(true);
        }
    }

    private void reflectionStep(ActionEvent ae) {
        Logger.getGlobal().log(Level.INFO, "REFLECTION for story {0}", story.getActions());
        try {
            int count = 0;
            reflection.setImpasseDetected(isImpasseDetected());
            Logger.getGlobal().log(Level.INFO, "REFLECTION STARTS in story {0}", story.getActions());
            while ((isImpasseDetected() || story.hasMissingConditions()) && (count < MexicaParameters.MAX_REFLECTION_ACTIONS)) {
                Logger.getGlobal().log(Level.FINE, "REFLECTION STARTS {0}", count);
                reflection.actionPerformed(ae);
                setImpasseDetected(reflection.getImpasseDetected());
                count++;
            }
        } catch (Error e) {
            Logger.getGlobal().log(Level.WARNING, "Reflection error: {0}", e.getMessage());
        }
        reflection.performFinalAnalysis();
    }
    
    public ReflectionAction getReflectionAction() {
        return reflection;
    }
    
    public EngagementAction getEngagementAction() {
        return engagement;
    }

    /**
     * @return the impasseDetected
     */
    public boolean isImpasseDetected() {
        return impasseDetected;
    }

    /**
     * @param impasseDetected the impasseDetected to set
     */
    public void setImpasseDetected(boolean impasseDetected) {
        this.impasseDetected = impasseDetected;
    }
}