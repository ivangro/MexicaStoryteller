package mexica.story.filter;

/**
 * Exception throwed when an action doesn't fulfille with all the filters
 * @author Ivan Guerrero
 */
public class StoryFilterException extends Exception {
    public StoryFilterException(String msg) {
        super(msg);
    }
    
    public StoryFilterException() {}
}
