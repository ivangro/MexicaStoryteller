package mexica;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.core.*;
import mexica.engagement.AtomConectivityGraph;
import mexica.engagement.AtomsStore;
import mexica.engagement.parser.AtomParser;
import mexica.engagement.parser.PreviousStory;
import mexica.groups.HierarchyStore;
import mexica.parser.ParseException;
import mexica.parser.PreviousStories;
import mexica.reflection.ActionsParser;
import mexica.social.parser.SocialActionsParser;
import mexica.story.SpecialActionStore;
import mexica.story.SpecialActionsParser;
import mexica.story.analyzer.FinalStoryAnalyzer;
import mexica.story.serendipity.TextParser;
import slant.MexicaFileConfiguration;

/**
 * Class to serve as repository of all the generic elements that can be employed in a story
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class MexicaRepository {
    private static MexicaRepository instance = new MexicaRepository();
    
    private ActionStore actions;
    private AtomsStore atoms, socialAtoms;
    private SpecialActionStore specialActions;
    private TextParser textParser;
    private FinalStoryAnalyzer storyAnalyzer;
    private PreviousStories previousStoriesParser;
    private HierarchyStore hierarchyStore;
    private RelationsStore relationsStore;
    private AtomConectivityGraph connectivityGraph;
    
    private MexicaRepository() {}
        
    public static MexicaRepository getInstance() {
        return instance;
    }
    
    /**
     * @return the actions
     */
    public ActionStore getActions() {
        return actions;
    }
    
    public SpecialActionStore getSpecialActions() {
        return specialActions;
    }
    
    public AtomsStore getAtoms() {
        return atoms;
    }
    
    public AtomsStore getSocialAtoms() {
        return socialAtoms;
    }
    
    public TextParser getTextParser() {
        return textParser;
    }
    
    public FinalStoryAnalyzer getStoryAnalyzer() {
        return storyAnalyzer;
    }
    
    public List<PreviousStory> getPreviousStories() {
        return previousStoriesParser.getStories();
    }
    
    public HierarchyStore getHierarchyStore() {
        return hierarchyStore;
    }
    
    public RelationsStore getRelations() {
        return relationsStore;
    }
    
    public AtomConectivityGraph getConnectivityGraph() {
        return connectivityGraph;
    }

    /**
     * IMPORTANT: Call this method to retrieve all the information
     */
    public void loadData() {
        try {
            MexicaFileConfiguration config = new MexicaFileConfiguration();
            config.loadConfiguration();
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
        }
        
        RelationsLoader relationsLoader = new RelationsLoader();
        relationsLoader.loadRelations(MexicaParameters.RELATIONS_FILE);
        relationsStore = relationsLoader.getRelationsStore();
        
        ActionsParser actionsParser = new ActionsParser();
        actionsParser.initParse();
        actions = actionsParser.getActions();
        
        SpecialActionsParser specialActionsParser = new SpecialActionsParser();
        specialActionsParser.initParse();
        specialActions = specialActionsParser.getActions();
        
        try {
            SocialActionsParser socialParser = new SocialActionsParser(MexicaParameters.SOCIAL_ACTIONS_FILE);
            socialParser.start();
            specialActions.setSocialActions(socialParser.getSocialActions());
            actions.setSpecialActionStore(specialActions);
        } catch (IOException | mexica.social.parser.ParseException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Social actions file error: {0}", ex.getMessage());
            System.err.append("Social actions file error: " + ex.getMessage());
        }
                
        textParser = new TextParser(actions);
        textParser.generateThesaurus();
        
        //Reads the atoms
        AtomParser atomsParser = new AtomParser();
        atomsParser.initParse(MexicaParameters.ATOMS_FILE);
        atoms = atomsParser.getAtoms();
        //Reads the social atoms
        atomsParser.initParse(MexicaParameters.SOCIAL_ATOMS_FILE);
        socialAtoms = atomsParser.getAtoms();
        
        storyAnalyzer = new FinalStoryAnalyzer();
        storyAnalyzer.setSpecialActions(specialActions.getActionList());
        
        //Reads the previous stories
        boolean ilogicalActions = MexicaParameters.ENABLE_ILLOGICAL_ACTIONS;
        try {
            MexicaParameters.ENABLE_ILLOGICAL_ACTIONS = true;
            previousStoriesParser = new PreviousStories(actions, MexicaParameters.PREV_STORIES_FILE);
            previousStoriesParser.Start();
            //Calculates the values for novelty analysis
            //previousStoriesParser.countActionsEmployed();
            //actions.calculateNoveltyValues();
        } catch (FileNotFoundException | ParseException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Previous stories file error: {0}", ex.getMessage());
            System.err.append("Previous stories file error: " + ex.getMessage());
        } finally {
            MexicaParameters.ENABLE_ILLOGICAL_ACTIONS = ilogicalActions;
        }
        
        hierarchyStore = new HierarchyStore(MexicaParameters.HIERARCHIES_FILE);
        hierarchyStore.loadData();
        
        connectivityGraph = new AtomConectivityGraph();
        connectivityGraph.calculateConnectivityGraph(atoms);
    }
}