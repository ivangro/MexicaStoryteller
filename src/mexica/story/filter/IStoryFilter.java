package mexica.story.filter;

import mexica.story.*;

/**
 * Interface to be implemented by any class that adds a filter to the story actions
 * @author Ivan Guerrero
 */
public interface IStoryFilter {
    public boolean analyzeStory(Story story, ActionInstantiated action) throws StoryFilterException;
}
