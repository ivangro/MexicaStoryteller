package mexica.story;

/**
 *
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public interface IStoryContextListener {
    public void notifyListener(ListType type);
    
    public enum ListType {Facts, Characters, Conditions, Story, Actions, Tensions, StoryUpdated};
}
