package mexica.story.guidelines;

/**
 * Class to represent the different available tendencies of a tension curve
 * @author Ivan Guerrero
 */
public enum TensionTendency {
    TendencyUp, TendencyDown, SameTendency;

    /**
     * Compares the two given tendency values.<br>
     * If both values are the same, the result is 1.
     * If one of them is SameTendency, the result is 0.
     * If both are different, the result is -1.
     * @param tend1
     * @param tend2
     * @return 
     */
    public static int evaluate(TensionTendency tend1, TensionTendency tend2) {
        int res;
        if (tend1 == tend2)
            res = 1;
        else if (tend1 == SameTendency || tend2 == SameTendency)
            res = 0;
        else
            res = -1;
        return res;
    }
}
