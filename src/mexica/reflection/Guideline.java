package mexica.reflection;

/**
 * Available guidelines triggered in reflection mode to affect the engagement action selection process
 * @author Ivan Guerrero
 */
public enum Guideline {
    tendencyUp,         /** The tension should increase, all actions that decrease the tension are removed in engagement */
    tendencyDown,       /** The tension should decrease, all actions that increase the tension are removed in engagement */
    tendencyNeutral,    /** There's no guideline for the tension */
    endStory,           /** Sugests to end the current story */
    breakSocialNorm,    /** Establishes that the selected actions must break a social norm */
    notBreakSocialNorm, /** Establishes that the selected actions must not break a social norm */
    strictNovelty,      /** Every action with its number of times employed less than the STRICT_NOVELTY value is banned */
    highNovelty,        /** Every action with its number of times employed less than the HIGH_NOVELTY value is banned */
    mediumNovelty,      /** Every action with its number of times employed less than the MEDIUM_NOVELTY value is banned */
    lowNovelty,         /** No action is banned due to the novelty criteria */
    normsUp,            /** The number of social norms broken should increase */
    normsDown,          /** The number of social norms broken should stop increasing */
    normsNeutral;       /** There's no guideline for the social norms */
}
