package mexica.story;

/**
 * Exception thrown when a dead character is tried to be employed
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class DeadAvatarException extends Exception {
    public DeadAvatarException(String msg) {
        super(msg);
    }
}
