package mexica.impro;

/**
 * Tags employed to log the iterations of a story
 * @author Ivan Guerrero
 */
public interface MexicaImproXMLLogTags {
    String LOG_ITERATIONS = "iterations";
    String LOG_ITERATION = "iteration";
    String LOG_ATOMS = "atoms";
    String LOG_ATOM = "atom";
    String LOG_ATOM_ID = "id";
    String LOG_ACTION = "action";
    String LOG_INSTANCE_ID = "id";
    String LOG_MEXICA_INSTANCE_ID = "mexicaImproID";
    String LOG_ACTION_INSTANCE = "instance";
    String LOG_ACTION_ORDER = "order";
    String LOG_ATOM_CLASS = "connectivityClass";
    String LOG_ATOM_CLUSTER = "clusterID";
    String LOG_ATOM_CLUSTER_TYPE = "clusterType";
    String MISSING_CONDITIONS = "missingConditions";
    String IRRELEVANT_ACTIONS = "irrelevantActions";
    String ILLOGICAL_ACTIONS = "illogicalActions";
}
