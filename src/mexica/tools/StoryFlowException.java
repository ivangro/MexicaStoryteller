package mexica.tools;

import mexica.story.filter.StoryFilterException;

/**
 * Exception throwed when an added action does not allow the story to flow
 * @author Ivan Guerrero
 */
public class StoryFlowException extends StoryFilterException {
    public StoryFlowException(String msg) {
        super(msg);
    }
    
    public StoryFlowException() {}
}
