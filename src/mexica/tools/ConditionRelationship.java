package mexica.tools;

/**
 * Different kinds of result after a comparisson between conditions
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public enum ConditionRelationship {
    /**
     * When both conditions are equal
     */
    Equal,
    /**
     * When both conditions are equivalent (i.e. any -> love)
     */
    Equivalent,
    /**
     * For tensions only, when they are defeatable (i.e. prisoner vs. prisoner free)
     */
    Defeatable,
    /**
     * When the conditions are different in type
     */
    Different,
    /**
     * When no comparison has been made yet
     */
    NotDefined,
    /**
     * When the employed characters or variables are equal
     */
    EqualCharacters,
    /**
     * When the employed characters or variables are equivalent (i.e. any vs. A || any vs. Dracula)
     */
    EquivalentCharacters
};
