package mexica.story.guidelines;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.MexicaParameters;

/**
 * Class to read/write StoryTensions in/from a file
 * @author Ivan Guerrero
 */
public class TensionIO {
    /**
     * Resets the file content of the tension file
     */
    public void resetFileContent() {
        FileWriter writer = null;
        try {
            String path = MexicaParameters.TENSIONS_FILE;
            //path = getClass().getClassLoader().getResource(path).getFile();
            Logger.getGlobal().log(Level.FINE, "Reseting tensions in {0}", path);
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
    /**
     * Appends the given story tensions to the tensions file
     * @param storyTension
     * @throws IOException 
     */
    public void append(StoryTension storyTension) throws IOException {
        String path = MexicaParameters.TENSIONS_FILE;
        //path = getClass().getClassLoader().getResource(path).getFile();
        Logger.getGlobal().log(Level.FINE, "Adding tensions to {0}", path);
        try (FileWriter writer = new FileWriter(path, true)) {
            writer.append(storyTension.getStoryID() + "\n");
            for (Integer i : storyTension.getTensions()) {
                writer.append(i + " ");
            }
            writer.append("\n");
        }
    }
    
    /**
     * Reads all the available tensions from the tensions file
     * @return
     * @throws IOException 
     */
    public List<StoryTension> read() throws IOException {
        List<StoryTension> storyTensions = new ArrayList<>();
        String path = MexicaParameters.TENSIONS_FILE;
        Scanner scanner = new Scanner(new java.io.File(path));
        //Scanner scanner = new Scanner(TensionIO.class.getClassLoader().getResource(path).openStream());
        StoryTension storyTension;
        while (scanner.hasNext()) {
            int storyID = Integer.parseInt(scanner.nextLine());
            String[] tensions = scanner.nextLine().split(" ");
            storyTension = new StoryTension();
            storyTension.setStoryID(storyID);
            for (int i=0; i<tensions.length; i++) {
                storyTension.addTension(i, Integer.parseInt(tensions[i]));
            }
            storyTensions.add(storyTension);
        }
        
        return storyTensions;
    }
}