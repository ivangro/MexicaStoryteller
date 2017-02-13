package mexica.tools;

/**
 * Exception invoked when two characters in different positions try to perform
 * an action together
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class InvalidCharacterException extends Exception {
    public InvalidCharacterException(String msg) {
        super(msg);
    }
    
    public InvalidCharacterException() {}
}
