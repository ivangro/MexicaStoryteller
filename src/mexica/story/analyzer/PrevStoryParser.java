package mexica.story.analyzer;

import java.util.regex.*;
import mexica.core.Action;
import mexica.core.ActionStore;
import mexica.reflection.ActionsParser;

/**
 *
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class PrevStoryParser {
    private static PrevStoryParser instance = new PrevStoryParser();
    private Pattern linePattern, lineRealized;
    private PrevStory prevStory;
    private ActionStore store;
    
    private PrevStoryParser() {
        linePattern = Pattern.compile("(\\w+)\\s+(\\w+)\\s*(\\w+)?");
        lineRealized = Pattern.compile("(\\w+)\\s+(Realised)\\s+(\\w+)\\s+(\\w+)\\s*(\\w+)?");
        ActionsParser parser = new ActionsParser();
        parser.initParse();
        store = parser.getActions();
    }
    
    public static PrevStoryParser getInstance() {
        return instance;
    }
    
    public void parse(String text) {
        prevStory = new PrevStory();
        Matcher m = null;
        String name, character;
        Action action = null;
        int actionNumber = 0;
        
        String[] lines = text.split("\n");
        for (String line : lines) {
            line = line.trim();
            m = linePattern.matcher(line);
            if (m.matches()) {
                name = m.group(2);
                character = m.group(1);
                action = store.getAction(name);
                prevStory.addAction(actionNumber, action, character, m.group(3));
            }
            else {
                m = lineRealized.matcher(line);
                if (m.matches()) {
                    name = m.group(2);
                    character = m.group(1);
                    action = store.getAction(name);
                    prevStory.addAction(actionNumber, action, character);
                }
            }
            actionNumber++;
        }
    }
    
    public static void main(String[] args) {
        String text = "Farmer killed Prince "; //Virgin\nHunter Cured Virgin\n";
        PrevStoryParser.getInstance().parse(text);
    }
    
    public PrevStory getPrevStory() {
        return prevStory;
    }
}
