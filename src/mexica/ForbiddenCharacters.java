package mexica;

/**
 * Determines the characters that will be banned during the character instantiation process
 * @author Ivan Guerrero
 */
public enum ForbiddenCharacters {
    Active, /** All context characters are banned */
    HalfActive, /** The context owner is banned */ 
    Inactive; /** No characters are banned */
}
