package mexica.story.guidelines;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.MexicaParameters;

/**
 * Class to read the number of social norms in a story
 * @author Ivan Guerrero
 */
public class SocialNormsIO {
    public void resetFileContent() {
        FileWriter writer = null;
        try {
            String path = MexicaParameters.SOCIAL_NORMS_FILE;
            //path = getClass().getClassLoader().getResource(path).getFile();
            Logger.getGlobal().log(Level.FINE, "Reseting social norms vector in {0}", path);
            writer = new FileWriter(path, false);
            writer.write("");
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getGlobal().log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public List<StoryNorms> read() {
        List<StoryNorms> storyNorms = new ArrayList<>();
        try {
            String path = MexicaParameters.SOCIAL_NORMS_FILE;
            Scanner scanner = new Scanner(new java.io.File(path));
            //Scanner scanner = new Scanner(getClass().getClassLoader().getResource(path).openStream());
            StoryNorms storyNorm;
            while (scanner.hasNext()) {
                int storyID = Integer.parseInt(scanner.nextLine());
                String[] socialNorms = scanner.nextLine().split(" ");
                storyNorm = new StoryNorms();
                storyNorm.setStoryID(storyID);
                for (int i=0; i<socialNorms.length; i++) {
                    storyNorm.addSocialNorms(i, Integer.parseInt(socialNorms[i]));
                }
                storyNorms.add(storyNorm);
            }
        } catch (Exception ex) {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
        }
        return storyNorms;
    }
    
    public void append(StoryNorms storyNorms) throws IOException {
        String path = MexicaParameters.SOCIAL_NORMS_FILE;
        //path = getClass().getClassLoader().getResource(path).getFile();
        Logger.getGlobal().log(Level.FINE, "Adding social norms to {0}", path);
        try (FileWriter writer = new FileWriter(path, true)) {
            writer.append(storyNorms.getStoryID() + "\n");
            for (Integer i : storyNorms.getSocialNorms()) {
                writer.append(i + " ");
            }
            writer.append("\n");
        }
    }
}
