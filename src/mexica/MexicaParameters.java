package mexica;

/**
 * Class wrapping the constants employed by Mexica Libre
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class MexicaParameters {
    /** Parameter used to determine which knowledge base to utilise during debugging */
    public static String MEXICA_KB_ID = "/mexica";
    public static String MEXICA_PATH = "src/";
    public static String RELATIONS_FILE;
    public static String ACTIONS_FILE;
    public static String PREV_STORIES_FILE;
    public static String ATOMS_FILE;
    public static String STORY_ATOM_RELATIONS_FILE;
    public static String TENSIONS_FILE;
    public static String SPECIAL_ACTIONS_FILE;
    public static String SOCIAL_ACTIONS_FILE;
    
    public static String HELP_FILE;
    public static String BACKGROUND_FILE;
    
    public static String SOCIAL_NORMS_FILE;
    public static String SOCIAL_ATOMS_FILE;
    public static String HIERARCHIES_FILE;
    
    public static String SLANT_ACTIONS_FILE;
    public static String SLANT_ELEMENTS_FILE;    
    
    /** Determines if the E-R cycle start in engagement (if set to true) or reflection (if set to false) */
    public static boolean INITIAL_STATE = true;
    /** Value for the proportional intensity */
    public static int PROPORTIONAL_INTENSITY = 100;
    /** Minimum percentage for an atom to be enabled inside an engagement cycle */
    public static int ACAS_CONSTANT = 50;
    public static int ACAS_II_CONSTANT = 50;
    public static int ACAS_III_CONSTANT = 50;
    public static boolean ENABLE_ACAS_II = true;
    public static boolean ENABLE_ACAS_III = true;
    /** Minimum percentage for a context to be considered equivalent to a social atom and break a social norm */
    public static double SOCIAL_ACAS_CONSTANT = 70;
    /** Value for a tension in a story */
    public static int TENSION_VALUE = 10;
    /** Maximum number of actions to be selected on an engagement cycle */
    public static int MAX_ENGAGEMENT_ACTIONS = 3;
    /** Enable the insertion of position actions during engagement */
    public static boolean ENABLE_POSITION_ACTIONS = false;
    /** Maximum number of actions to be added during reflection cycle */
    public static int MAX_REFLECTION_ACTIONS = 20;
    /** maximum number of action in a story */
    public static int MAX_ACTIONS_IN_STORY = 20;
    /** Maximum number of impasses in a story */
    public static int MAX_IMPASSES = 3;
    
    /** Mode 1: ER1, Mode 2: ER2 */
    public static int CHARACTER_INSTANTIATION_MODE = 2;
    /** Enables/Disables the instantiation of an action with random characters */
    public static boolean PARTIAL_CHARACTER_INSTANTIATION = true;
    
    /** Maximum number of times an atom can be employed in an engagement step */
    public static int MAX_ATOM_USAGES_IN_STEP = 1;
    /** Maximum number of times an action instantiated can be employed in a story */
    public static int MAX_ACTION_USAGES_IN_STORY = 1;
    /** Maximum number of times an action can be employed in a story */
    public static int MAX_SIMPLE_ACTION_USAGES_IN_STORY = 3;
    /** (De)activates all the exceptions thrown during the story analysis phase */
    public static boolean ENABLE_STORY_EXCEPTIONS = true;
    /** (De)activates the option to include illogical actions to the story */
    public static boolean ENABLE_ILLOGICAL_ACTIONS = true;
    /** (De)activates the social analysis of a story */
    public static boolean ENABLE_SOCIAL_ANALYSIS = true;
    /** Determines the number of actions to look ahead to determine the tension tendency */
    public static int TENSION_OFFSET = 3;
    /** Determines the number of actions to look ahead to determine the social norms tendency */
    public static int SOCIAL_NORMS_OFFSET = 3;
    /** (De)activates the exceptional social norms */
    public static boolean ENABLE_SPECIAL_SOCIAL_NORMS = true;
    /** (De)activates the character validation step inside the social analysis */
    public static boolean ENABLE_SOCIAL_CHARACTER_ANALYSIS = true;
    /** Determines the characters that will be banned during the character instantiation process */
    public static ForbiddenCharacters FORBIDDEN_CHARACTERS = ForbiddenCharacters.Active;
    
    /** Novelty percentages */
    public static double HIGH_NOVELTY_PERCENTAGE = 75;
    public static double MEDIUM_NOVELTY_PERCENTAGE = 50;
    public static double STRICT_NOVELTY_PERCENTAGE = 15;
    public static double LOW_NOVELTY_PERCENTAGE = 0;
    /** Percentage to be combined with the average use of actions in a previous story to obtain the novelty values */
    public static double NOVELTY_PERCENTAGE = 50;
    /** Values to be imployed during the novelty evaluation */
    public static int LOW_NOVELTY = 0;
    public static int STRICT_NOVELTY = 0;
    public static int MEDIUM_NOVELTY = 0;
    public static int HIGH_NOVELTY = 0;
    /** Determines if the system will work together with Slant */
    public static boolean ENABLE_SLANT_MODE = false;
    /** Enables / Disables the non representative context filter */
    public static boolean INCLUDE_NON_REPRESENTATIVE_CONTEXTS = false;
    /** Enables / Disables the graphical notification elements */
    public static boolean DISPLAY_GRAPHICAL_NOTIFICATIONS = false;
    /** Enables / Disables the log info */
    public static boolean ENABLE_LOG_INFO = true;
    /** Enables / disables the Mexica impro collaborative generation */
    public static boolean ENABLE_MEXICA_IMPRO = false;

    public static void setMainPath(String path) {
        MEXICA_PATH = path;
        RELATIONS_FILE = MEXICA_PATH + "relations.xml";
        ACTIONS_FILE = MEXICA_PATH + "actions.txt";
        PREV_STORIES_FILE = MEXICA_PATH + "previousStories.txt";
        ATOMS_FILE = MEXICA_PATH + "atoms.txt";
        STORY_ATOM_RELATIONS_FILE = MEXICA_PATH + "storyAtomRelations.txt";
        TENSIONS_FILE = MEXICA_PATH + "tensions.txt";
        SPECIAL_ACTIONS_FILE = MEXICA_PATH + "specialActions.txt";
        SOCIAL_ACTIONS_FILE = MEXICA_PATH + "socialActions.txt";
    
        HELP_FILE = MEXICA_PATH + "helpText.html";
        BACKGROUND_FILE = MEXICA_PATH + "mexica.jpg";
    
        SOCIAL_NORMS_FILE = MEXICA_PATH + "socialNorms.txt";
        SOCIAL_ATOMS_FILE = MEXICA_PATH + "socialAtoms.txt";
        HIERARCHIES_FILE = MEXICA_PATH + "socialHierarchy.hcy";
    
        SLANT_ACTIONS_FILE = MEXICA_PATH + "slantActions.txt";
        SLANT_ELEMENTS_FILE = MEXICA_PATH + "slantElements.xml";    
    }
}