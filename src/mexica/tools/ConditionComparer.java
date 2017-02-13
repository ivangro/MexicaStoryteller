package mexica.tools;

import mexica.CharacterName;
import mexica.MexicaParameters;
import mexica.core.Condition;
import mexica.core.ConditionType;
import mexica.core.EmotionType;
import mexica.core.TensionType;
import mexica.story.ConditionInstantiated;

/**
 * Class for comparing two conditions
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class ConditionComparer {
    
    /**
     * Compares both options and establishes how similar they are
     * NOTE: This comparison is not simetric
     * @param cond1 The condition that will be removed if the second is equivalent
     * @param cond2 Second condition
     */
    public static ComparisonResult compare(ConditionInstantiated cond1, ConditionInstantiated cond2) {
        ConditionRelationship rel = ConditionRelationship.Different;
        ComparisonResult result = new ComparisonResult();
        Condition c1, c2;
        c1 = cond1.getCondition();
        c2 = cond2.getCondition();
                    
        if (cond1.getType().equals(cond2.getType())) {
            switch (cond1.getType()) {
                case Emotion:
                    rel = compareEmotions(c1, c2);
                    int intensityDiff = compareIntensities(c1.getIntensity(), c2.getIntensity());
                    result.areSignsEqual(c1.getIntensity() * c2.getIntensity() > 0);
                    result.setIntensityDifference(intensityDiff);
                    break;
                case Tension:
                    rel = compareTensions(c1, c2);
                    break;
                case Position:
                    break;
            }
        
            ConditionRelationship relCharacters = areCompatibleCharacters(cond1, cond2, rel);

            result.setType(cond1.getType());
            result.setCharacterComparison(relCharacters);
            result.setConditionComparison(rel);
        }
        
        return result;
    }

    public static ConditionRelationship compareEmotions(Condition cond1, Condition cond2) {
        return compareEmotions(cond1, cond2, false).getConditionComparison();
    }

    public static ConditionRelationship compareTensions(Condition cond1, Condition cond2) {
        return compareTensions(cond1, cond2, false).getConditionComparison();
    }
    
    /**
     * Compares the two given conditions to determine if the tensions involved are the same
     * @param cond1
     * @param cond2
     * @param compareCharacterVariables If TRUE the variables for characters a and b are also compared.
     * @return 
     */
    public static ComparisonResult compareTensions(Condition cond1, Condition cond2, boolean compareCharacterVariables) {
        ComparisonResult res = new ComparisonResult();
        res.setType(ConditionType.Tension);
        
        ConditionRelationship rel = ConditionRelationship.Different;
        if (cond1.getTension().equals(cond2.getTension()))
            rel = ConditionRelationship.Equal;
        else if (areCompatibleTensions(cond1.getTension(), cond2.getTension()))
            rel = ConditionRelationship.Defeatable;
        res.setConditionComparison(rel);
        
        if (compareCharacterVariables) {
            res.setCharacterComparison(areCompatibleVariables(cond1, cond2));
        }
        return res;
    }
    
    /**
     * Compares the two given conditions to determine if the emotions involved are the same
     * @param cond1
     * @param cond2
     * @param compareCharacterVariables If TRUE the variables for characters a and b are also compared.
     * @return 
     */
    public static ComparisonResult compareEmotions(Condition cond1, Condition cond2, boolean compareCharacterVariables) {
        ComparisonResult res = new ComparisonResult();
        res.setType(ConditionType.Emotion);
        
        ConditionRelationship rel = ConditionRelationship.Different;
        EmotionType emotion1 = cond1.getEmotion();
        EmotionType emotion2 = cond2.getEmotion();
        
        if (emotion1.equals(emotion2))
            rel = ConditionRelationship.Equal;
        else if ((emotion1.equals(EmotionType.getAnyEmotion()) || emotion2.equals(EmotionType.getAnyEmotion())) && 
                (emotion1 != EmotionType.getSocialEmotion() && emotion2 != EmotionType.getSocialEmotion())) {
            rel = ConditionRelationship.Equivalent;
        }
        int intensityDiff = compareIntensities(cond1.getIntensity(), cond2.getIntensity());
        res.areSignsEqual(cond1.getIntensity() * cond2.getIntensity() > 0);
        res.setIntensityDifference(intensityDiff);
        res.setConditionComparison(rel);
        
        if (compareCharacterVariables) {
            res.setCharacterComparison(areCompatibleVariables(cond1, cond2));
        }
        return res;
    }
    
    /**
     * Validates when the second tension type invalidates the existence of the first
     * @param type1 Current tension
     * @param type2 New tension that invalidates the first
     * @return True if type2 invalidates type1
     */
    private static boolean areCompatibleTensions(TensionType type1, TensionType type2) {
        boolean ans = false;
        if (type1 != null && type2 != null) {
            ans = (type1.equals(TensionType.PrisonerFree) && type2.equals(TensionType.Prisoner));
            ans |= (type1.equals(TensionType.HealthNormal) && type2.equals(TensionType.HealthAtRisk));
            ans |= (type1.equals(TensionType.LifeNormal) && type2.equals(TensionType.LifeAtRisk));
            if (MexicaParameters.ENABLE_SOCIAL_ANALYSIS) {
                ans |= (type1.equals(TensionType.SocialBurdenNormal) && type2.equals(TensionType.SocialBurden));
                ans |= (type1.equals(TensionType.SocialDisobedienceNormal) && type2.equals(TensionType.SocialDisobedience));
                ans |= (type1.equals(TensionType.SocialThreatNormal) && type2.equals(TensionType.SocialThreat));
            }
        }
        return ans;
    }
    
    /**
     * Validates that the characters of the conditions are equivalent or equal
     * @param cond1 First condition
     * @param cond2 Second condition
     * @return Relation between characters 
     *  (EqualCharacters if they are equal)
     *  (EquivalentCharacters if one of the first and one of the second is "any character"
     */
    private static ConditionRelationship areCompatibleCharacters(ConditionInstantiated cond1, ConditionInstantiated cond2) {
        ConditionRelationship rel = ConditionRelationship.Different;
        CharacterName name11 =  cond1.getCharacterA();
        CharacterName name12 =  cond1.getCharacterB();
        CharacterName name21 =  cond2.getCharacterA();
        CharacterName name22 =  cond2.getCharacterB();
        
        if (name11.equals(name21) && name21.equals(name22))
            rel = ConditionRelationship.EqualCharacters;
        else if (areEquivalentCharacters(name11, name21) && areEquivalentCharacters(name12, name22))
            rel = ConditionRelationship.EquivalentCharacters;

        return rel;
    }
    
    /**
     * Validates that the characters of the conditions are equivalent or equal
     * @param cond1 First condition
     * @param cond2 Second condition
     * @param relation If relation is defeatable then only the first character is required
     * @return Relation between characters 
     *  (EqualCharacters if they are equal)
     *  (EquivalentCharacters if one of the first and one of the second is "any character"
     */
    private static ConditionRelationship areCompatibleCharacters(ConditionInstantiated cond1, ConditionInstantiated cond2, ConditionRelationship relation) {
        ConditionRelationship rel = ConditionRelationship.Different;
        
        if (relation.equals(ConditionRelationship.Defeatable)) {
            CharacterName name11 =  cond1.getCharacterA();
            CharacterName name21 =  cond2.getCharacterA();
            if (name11.equals(name21))
                rel = ConditionRelationship.EqualCharacters;
            else if (areEquivalentCharacters(name11, name21))
                rel = ConditionRelationship.EquivalentCharacters;
        }
        else
            rel = areCompatibleCharacters(cond1, cond2);
        
        return rel;
    }
    
    private static ConditionRelationship areCompatibleVariables(Condition cond1, Condition cond2) {
        ConditionRelationship res;
        if ((cond1.getCharacterA().equalsIgnoreCase(cond2.getCharacterA())) && 
            ((cond1.getCharacterB() == null && cond2.getCharacterB() == null) ||
             (cond1.getCharacterB() != null && cond2.getCharacterB() != null && cond1.getCharacterB().equalsIgnoreCase(cond2.getCharacterB())))) {
            res = ConditionRelationship.EqualCharacters;
        }
        else {
            res = ConditionRelationship.Different;
        }
    
        return res;
    }
    
    /**
     * Validates if the given character names are equivalent
     * If the first character is any or is not defined, then the characters are equivalent
     * If the second character is any, then the characters are equivalent
     * If the characters are equal are also equivalent
     * @param name1 First name
     * @param name2 Second name
     * @return Relation between the character names
     */
    private static boolean areEquivalentCharacters(CharacterName name1, CharacterName name2) {
        return (name1.equals(CharacterName.Any_Character) || 
                name1.equals(CharacterName.Not_Defined) || 
                name2.equals(CharacterName.Any_Character) || 
                name2.equals(CharacterName.Not_Defined) || //TODO: Determine if adds an error
                name1.equals(name2));
    }
    
    /**
     * If one of the intensities if PROPORTIONAL, the difference is PROPORTIONAL
     * If they have the same sign, the difference is positive if the first is greater
     * If they have different sign, the difference is negative always
     * @param intensity1
     * @param intensity2
     * @return The difference between the intensities
     */
    private static int compareIntensities(int intensity1, int intensity2) {
        int diff = 0;
        if (intensity1 == MexicaParameters.PROPORTIONAL_INTENSITY ||
            intensity2 == MexicaParameters.PROPORTIONAL_INTENSITY)
                diff = MexicaParameters.PROPORTIONAL_INTENSITY;
        else if (intensity1 * intensity2 >= 0) {
            diff = Math.abs(intensity1) - Math.abs(intensity2);
        }
        else {
            diff = - Math.abs(intensity1 - intensity2);
        }
        
        return diff;
    }

    /**
     * Compares the two given conditions to determine if the positions involved are the same
     * @param cond1
     * @param cond2
     * @param compareCharacterVariables If TRUE the variables for characters a and b are also compared.
     * @return 
     */
    public static ComparisonResult comparePositions(Condition cond1, Condition cond2, boolean compareCharacterVariables) {
        ComparisonResult res = new ComparisonResult();
        res.setType(ConditionType.Position);
        if (cond1.getPosition() == cond2.getPosition())
            res.setConditionComparison(ConditionRelationship.Equal);
        else
            res.setConditionComparison(ConditionRelationship.Different);
        
        if (compareCharacterVariables)
            res.setCharacterComparison(areCompatibleVariables(cond1, cond2));
        
        return res;
    }

    /**
     * Compares two conditions extracted from social actions
     * @param cond1
     * @param cond2
     * @return 
     */
    public static ComparisonResult compareSocialPoscondition(Condition cond1, Condition cond2) {
        //Compare if their types are equal
        //TODO: Compare if their status are equal
        //For emotions: Compare their intensities and emotion types
        //For tensions: Compare their type
        ConditionRelationship rel = ConditionRelationship.Different;
        ComparisonResult result = new ComparisonResult();
                    
        if (cond1.getConditionType() == cond2.getConditionType()) {
            switch (cond1.getConditionType()) {
                case Emotion:
                    rel = compareEmotions(cond1, cond2);
                    int intensityDiff = compareIntensities(cond1.getIntensity(), cond2.getIntensity());
                    result.areSignsEqual(cond1.getIntensity() * cond2.getIntensity() > 0);
                    result.setIntensityDifference(intensityDiff);
                    break;
                case Tension:
                    rel = compareTensions(cond1, cond2);
                    break;
                case Position:
                    break;
            }
        
            result.setType(cond1.getConditionType());
            result.setConditionComparison(rel);
        }
        
        return result;
    }
}