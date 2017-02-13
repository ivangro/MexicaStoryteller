package mexica.action;

import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import mexica.*;
import mexica.core.ActionStore;
import mexica.engagement.parser.*;
import mexica.parser.*;
import mexica.story.guidelines.*;

/**
 * Action to create atoms, social atoms and tension graphs from the available previous stories
 * @author Ivan Guerrero
 */
public class CreateAtomsAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent ae) {
        boolean illogicalActions = MexicaParameters.ENABLE_ILLOGICAL_ACTIONS;
        ActionStore actionStore = MexicaRepository.getInstance().getActions();
        PreviousStories parser = null;
        try {
            MexicaParameters.ENABLE_ILLOGICAL_ACTIONS = true;
            parser = new PreviousStories(actionStore, MexicaParameters.PREV_STORIES_FILE);
            parser.Start();
        } catch (FileNotFoundException | ParseException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Previous stories file error: {0}", ex.getMessage());
        } 
        //Obtain the previous stories
        List<PreviousStory> stories = parser.getStories();
        //Create the atoms
        AtomGenerator generator = new AtomGenerator();
        generator.generateAtoms(stories);
        //Store atoms
        AtomWriter writer = new AtomWriter();
        writer.saveAtomsInFile(generator.getAtomsStore(), MexicaParameters.ATOMS_FILE);
        writer.saveStoryAtomRelations(generator.getAtomsStore(), MexicaParameters.STORY_ATOM_RELATIONS_FILE);
        //Store social atoms
        writer.saveAtomsInFile(generator.getSocialAtomsStore(), MexicaParameters.SOCIAL_ATOMS_FILE);
        //Create tension graphs
        TensionCurveAnalyzer analyzer = new TensionCurveAnalyzer();
        analyzer.getTensionIO().resetFileContent();
        //Create social graphs
        SocialNormAnalyzer socialAnalyzer = SocialNormAnalyzer.getInstance();
        socialAnalyzer.getSocialNormsIO().resetFileContent();
        
        int i=0;
        for (PreviousStory story : stories) {
            StoryTension tensionStory = analyzer.analyzeStory(i, story.getActions(), story.getInitialLocation());
            try {
                analyzer.getTensionIO().append(tensionStory);
            } catch (IOException ex) {
                Logger.getGlobal().log(Level.SEVERE, null, ex);
            }
            
            StoryNorms socialStory = socialAnalyzer.analyzeStory(i, story.getActions());
            try {
                socialAnalyzer.getSocialNormsIO().append(socialStory);
            } catch (IOException ex) {
                Logger.getGlobal().log(Level.SEVERE, null, ex);
            }
            i++;
        }
        
        //Read the values generated
        MexicaRepository.getInstance().loadData();
        MexicaParameters.ENABLE_ILLOGICAL_ACTIONS = illogicalActions;
        JOptionPane.showMessageDialog(null, "Atoms successfully created", "Atoms created and reloaded", JOptionPane.INFORMATION_MESSAGE);
    }   
}