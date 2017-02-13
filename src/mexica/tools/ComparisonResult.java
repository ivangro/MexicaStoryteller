package mexica.tools;

import mexica.MexicaParameters;
import mexica.core.ConditionType;

/**
 * Class that stores the result of the comparison between two instantiated conditions
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class ComparisonResult {
    private ConditionRelationship conditionComparison, characterComparison;
    private ConditionType type;
    private int intensityDifference;
    private boolean signsEqual;
    
    /**
     * Obtains the result of comparing two conditions
     * @param includeCharacters If the comparison must include character equivalence
     * @param includeIntensity If the intensity of the first condition must be greater or equal to the second
     * @return TRUE if the second condition is equal or equivalent to the first
     */
    public boolean isEqualOrEquivalent(boolean includeCharacters, boolean includeIntensity) {
        return isEqualOrEquivalent(includeCharacters, includeIntensity, true);
    }
    
    /**
     * Obtains the result of comparing two conditions
     * @param includeCharacters If the comparison must include character equivalence
     * @param includeIntensity If the intensity of the first condition must be greater or equal to the second
     * @param includeProportionalIntensity If TRUE when the intensity of one of the conditions is proportional
     *  the intensity comparison returns true
     * @return TRUE if the second condition is equal or equivalent to the first
     */
    public boolean isEqualOrEquivalent(boolean includeCharacters, boolean includeIntensity, boolean includeProportionalIntensity) {
        boolean res = false;
        if (type != null) {
            switch(type) {
                case Tension:
                    res = conditionComparison.equals(ConditionRelationship.Equal);
                    break;
                case Emotion:
                    res = conditionComparison.equals(ConditionRelationship.Equal) ||
                    conditionComparison.equals(ConditionRelationship.Equivalent);
                    break;
                case Position:
                    res = conditionComparison.equals(ConditionRelationship.Equal);
                    break;
            }
            
            if (includeCharacters) {
                res &= (characterComparison.equals(ConditionRelationship.EqualCharacters) ||
                    characterComparison.equals(ConditionRelationship.EquivalentCharacters));
            }
            if (includeIntensity) {
                res &= ((getIntensityDifference() >= 0 && getIntensityDifference() != MexicaParameters.PROPORTIONAL_INTENSITY) ||
                        (includeProportionalIntensity && getIntensityDifference() == MexicaParameters.PROPORTIONAL_INTENSITY));
            }            
        }
        
        return res;
    }
    
    /**
     * Obtains the result of comparing two tensions
     * @param includeCharacters If the comparison must include character equivalence
     * @return TRUE If the first tension removes the second
     */
    public boolean isDefeatable(boolean includeCharacters) {
        boolean res = false;
        if (type != null) {
            if (type.equals(ConditionType.Tension))
                res = conditionComparison.equals(ConditionRelationship.Defeatable);

            if (includeCharacters) {
                res &= (characterComparison.equals(ConditionRelationship.EqualCharacters) ||
                    characterComparison.equals(ConditionRelationship.EquivalentCharacters));
            }
        }
        
        return res;
    }
    
    /**
     * @return the conditionComparison
     */
    public ConditionRelationship getConditionComparison() {
        return conditionComparison;
    }

    /**
     * @param conditionComparison the conditionComparison to set
     */
    public void setConditionComparison(ConditionRelationship conditionComparison) {
        this.conditionComparison = conditionComparison;
    }

    /**
     * @return the characterComparison
     */
    public ConditionRelationship getCharacterComparison() {
        return characterComparison;
    }

    /**
     * @param characterComparison the characterComparison to set
     */
    public void setCharacterComparison(ConditionRelationship characterComparison) {
        this.characterComparison = characterComparison;
    }

    /**
     * @return the type
     */
    public ConditionType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(ConditionType type) {
        this.type = type;
    }

    /**
     * @return the intensityDifference The difference between both intensities
     * If the difference is zero, both intensities were of the same intensity
     * If the value is equal to MexicaParameters.PROPORTIONAL_INTENSITY, one of the intensities was proportional
     * If the difference is positive, the intensity of the first condition is greater than the second
     */
    public int getIntensityDifference() {
        return intensityDifference;
    }

    /**
     * @param intensityDifference the intensityDifference to set
     */
    public void setIntensityDifference(int intensityDifference) {
        this.intensityDifference = intensityDifference;
    }
    
    public void areSignsEqual(boolean equal) {
        signsEqual = equal;
    }
    
    /**
     * @return TRUE if the signs ob both intensities are equal
     */
    public boolean areSignsEqual() {
        return signsEqual;
    }
    
}