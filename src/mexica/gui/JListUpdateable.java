package mexica.gui;

import java.util.List;
import javax.swing.JList;
import mexica.story.*;


/**
 *
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class JListUpdateable extends JList implements IStoryContextListener {
    private Story story;
    
    public JListUpdateable() {}
    
    public JListUpdateable(Story story) {
        this.story = story;
    }
    
    @Override
    public void notifyListener(ListType type) {
        switch (type) {
            case Conditions:
                List<ConditionInstantiated> missingConditions =  story.getMissingConditions();
                this.setListData(missingConditions.toArray());
                break;
            case Characters:
                List<Avatar> characters = story.getCharacters();
                this.setListData(characters.toArray());
                break;
            case Actions:
                List<ActionInstantiated> actions =  story.getActions();
                this.setListData(actions.toArray());
                break;
            case Story:
                List<TextInstantiated> texts = story.getStory();
                this.setListData(texts.toArray());
                break;
        }
    }   
}