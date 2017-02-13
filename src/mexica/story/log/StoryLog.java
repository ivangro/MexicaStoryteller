package mexica.story.log;

import java.io.*;
import java.util.*;
import mexica.social.SocialStatus;
import mexica.story.*;
import mexica.story.guidelines.TensionCurveAnalyzer;

/**
 * Class to store the story log
 * @author Ivan Guerrero
 */
public class StoryLog {
    private static Map<Story, List<String>> storyLog;
    
    static {
        storyLog = new HashMap<>();
    }
    
    /**
     * Adds the given message to the story
     * @param story Story
     * @param message Message to be added to the story log
     */
    public static void addMessage(Story story, String message) {
        if (storyLog.containsKey(story)) {
            storyLog.get(story).add(message);
        }
        else {
            List<String> list = new ArrayList<>();
            list.add(message);
            storyLog.put(story, list);
        }
    }
    
    /**
     * Obtains the log messages linked to a story
     * @param story Story
     * @return A list of log messages
     */
    public static List<String> getLog(Story story) {
        return storyLog.get(story);
    }
    
    public static void saveToFile(Story story, File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        List<String> log = StoryLog.getLog(story);
        writer.write(story + "\n");
        for (ActionInstantiated action : story.getActions()) {
            writer.write(action.toString() + "\n");
        }
        writer.write("Story log\n");
        for (String msg : log) {
            writer.write(msg + "\n");
        }

        writer.write("Tension curve\n");
        for (int i=1; i<story.getCurrentYear(); i++) {
            writer.write(i + " " + TensionCurveAnalyzer.calculateNumberOfTensions(story, i) + "\n");
        }
        
        writer.write("Multiple results analysis: \n");
        for (ActionInstantiated action : story.getActions()) {
            if (action.getSocialStatus().equals(SocialStatus.multipleResults)) {
                SocialData socialData = action.getSocialData();
                writer.write(action + " BSN: " + socialData.getContexts()+ "/JA: " + socialData.getJustifiedContexts() + "\n");
            }
        }
        
        writer.close();
    }
}