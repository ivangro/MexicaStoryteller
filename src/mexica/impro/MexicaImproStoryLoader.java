package mexica.impro;

import java.io.File;
import java.io.FileFilter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.story.Story;

/**
 * Class to load all the available stories inside a given directory
 * @author Ivan Guerrero
 */
public class MexicaImproStoryLoader {
    private FileFilter filter;
    
    public MexicaImproStoryLoader() {
        filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".xml");
            }
        };
    }
    
    public List<Story> loadStories(List<File> files) {
        List<Story> stories = new ArrayList<>();
        MexicaImproStoryReader reader = new MexicaImproStoryReader();
        int i = 0;
        for (File f : files) {
            try {
                Story story = reader.readXML(f);
                story.setStoryName(f.getName());
                story.setStoryID(i++);
                stories.add(story);
                Logger.getGlobal().log(Level.INFO, "Story {0} loaded", f);
            } catch (Exception e) {
                Logger.getGlobal().log(Level.WARNING, "Story {0} couldn't be loaded: {1}", new Object[]{f, e});
            }
        }
        
        return stories;
    }
    
    public List<Story> loadStories(File directory) {
        File[] files = directory.listFiles(filter);
        return loadStories(Arrays.asList(files));
    }
}