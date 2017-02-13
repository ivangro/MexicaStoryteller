package mexica.story;

import java.util.*;

/**
 *
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class StoryContextListener {
    private List<IStoryContextListener> factListeners, charListeners, conditionListeners, 
            storyListeners, actionListeners, tensionListeners, storyUpdatedListeners;
    
    public StoryContextListener() {
        factListeners = new ArrayList<>();
        charListeners = new ArrayList<>();
        conditionListeners = new ArrayList<>();
        storyListeners = new ArrayList<>();
        actionListeners = new ArrayList<>();
        tensionListeners = new ArrayList<>();
        storyUpdatedListeners = new ArrayList<>();
    }
    
    public void addStoryContextListener(IStoryContextListener listener, IStoryContextListener.ListType type) {
        switch (type) {
            case Characters:
                charListeners.add(listener);
                break;
            case Conditions:
                conditionListeners.add(listener);
                break;
            case Facts:
                factListeners.add(listener);
                break;
            case Story:
                storyListeners.add(listener);
                break;
            case Actions:
                actionListeners.add(listener);
                break;
            case Tensions:
                tensionListeners.add(listener);
                break;
            case StoryUpdated:
                storyUpdatedListeners.add(listener);
                break;
        }
    }
    
    public void notifyListeners(IStoryContextListener.ListType type) {
        List<IStoryContextListener> list = null;
        switch (type) {
            case Characters:
                list = charListeners;
                break;
            case Conditions:
                list = conditionListeners;
                break;
            case Facts:
                list = factListeners;
                break;
            case Story:
                list = storyListeners;
                break;
            case Actions:
                list = actionListeners;
                break;
            case Tensions:
                list = tensionListeners;
                break;
            case StoryUpdated:
                list = storyUpdatedListeners;
        }

        for (IStoryContextListener listener : list) {
            listener.notifyListener(type);
        }
    }
}
