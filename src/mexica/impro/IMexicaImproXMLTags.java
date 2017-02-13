package mexica.impro;

/**
 *
 * @author Ivan Guerrero
 */
public interface IMexicaImproXMLTags {
    String MEXICA_STORY = "mexicaStory";
    String ACTION_ID = "actionID";
    String ACTION_NAME = "name";
    String ACTION_PERFORMER = "performer";
    String ACTION_RECEIVER = "receiver";
    String DEFAULT_LOCATION = "defaultLocation";
    String FINISHED_BY = "finishedBy";
    String COMPLETE = "complete";
    String MEXICA_ACTION = "action";
    String MEXICA_ACTIONS = "actions";
    String COMPOSITE_ACTION = "composite";
    String MEXICA_INSTANCE_ID = "mexicaImproID";
    /** Determines if the action was added during engagement or reflection */
    String INSERTION_MODE = "mode";
    /** Identifies the number of iteration where the action was generated */
    String ITERATION_NUMBER = "iteration";
    /** Defines the generation order of the action inside the story */
    String ACTION_NUMBER = "order";
    /** Stores the number of impasses detected in the story */
    String IMPASSES = "impasses";
}
