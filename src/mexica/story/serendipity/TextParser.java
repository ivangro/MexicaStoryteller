package mexica.story.serendipity;

import java.util.*;
import mexica.core.*;

/**
 *
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class TextParser {
    private ActionStore store;
    private Map<Action, Set<String>> thesaurus;
            
    public TextParser(ActionStore store) {
        this.store = store;
        thesaurus = new HashMap<>();
    }
    
    public void generateThesaurus() {
        for (Action act : store.getActionList()) {
            Set<String> set = new HashSet<>();
            for (String text : act.getTexts()) {
                for (String word : text.split(" ")) {
                    if (!word.startsWith("@"))
                        set.add(word);
                }
            }
            thesaurus.put(act, set);
        }
    }
    
    public Action generateRandomAction(int noWords, List<Action> availableActions) {
        String[] list = new String[noWords];
        Action act = null;
        do {
            for (int i=0; i<noWords; i++) {
                ArrayList<Action> actionsList = new ArrayList(thesaurus.keySet());
                int actionIndex = (int)(Math.random() * actionsList.size());
                Action action = actionsList.get(actionIndex);
                ArrayList<String> wordsList = new ArrayList(thesaurus.get(action));
                int wordIndex = (int)(Math.random() * wordsList.size());
                list[i] = wordsList.get(wordIndex);
                if (list[i].startsWith("@"))
                    i--;
            }
            act = generateRandomAction(list, availableActions);
        } while (act == null);
        //System.out.println("Selected words: " + Arrays.toString(list)); 
        return act;
    }
    
    public Action generateRandomAction(String[] words, List<Action> availableActions) {
        List<Action> actions = new ArrayList<>();
        
        for (Action act : thesaurus.keySet()) {
            Set<String> set = thesaurus.get(act);
            boolean res = true;
            for (String word : words) {
                res &= set.contains(word);
            }
            if (res && availableActions.contains(act))
                actions.add(act);
        }
        if (actions.size() > 0)
            return actions.get((int)(Math.random() * actions.size()));
        else 
            return null;
    }
}