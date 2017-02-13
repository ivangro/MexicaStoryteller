package mexica.tools.context;

import mexica.core.Condition;
import java.util.*;
import mexica.CharacterName;
import mexica.story.ConditionInstantiated;

/**
 * Class that provides different tools to manage avatar contexts
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class ContextTools {
    private Map<CharacterName, String> mapping;
    private Map<String,String> variableMapping;
    private char nextVariable;
    
    public ContextTools() {
        mapping = new EnumMap<CharacterName, String>(CharacterName.class);
        nextVariable = 'a';
    }
    
    /**
     * Variabilizes a context
     * @param conditions Conditions that define a context
     * @return The conditions with variables instead of character names
     */
    public List<Condition> variabilize(List<ConditionInstantiated> conditions) {
        List<Condition> conds = new ArrayList<Condition>();
        
        for (ConditionInstantiated instance : conditions) {
            Condition cond = new Condition();
            cond.setConditionType(instance.getType());
            switch (cond.getConditionType()) {
                case Emotion:
                    cond.setEmotionType(instance.getCondition().getEmotion());
                    cond.setIntensity(instance.getCondition().getIntensity());
                    break;
                case Tension:
                    cond.setTensionType(instance.getCondition().getTension());
                    break;
            }
            cond.setCharacterA(getVariable(instance.getCharacterA()));
            cond.setCharacterB(getVariable(instance.getCharacterB()));
            conds.add(cond);
        }
        
        return conds;
    }
    
    /**
     * Instantiates the given conditions with the character mapping obtained when the variabilization action
     * was performed
     * NOTE: This action cannot be performed if the variabilize method was not executed first
     * @param conditions Conditions to be instantiated
     * @return The set of instantiated conditions
     */
    public List<ConditionInstantiated> instantiate(List<Condition> conditions) {
        List<ConditionInstantiated> conds = new ArrayList<ConditionInstantiated>();
        variableMapping = new HashMap<String,String>();
        nextVariable = 'a';
        
        //Obtains a variable mapping with the same format for the new conditions
        for (Condition cond : conditions) {
            addVariableMapping(cond.getCharacterA());
            addVariableMapping(cond.getCharacterB());
        }
        
        //TODO: Compares the mappings and obtains a list of missing characters
            
        /*ConditionInstantiated condIns = new ConditionInstantiated(cond);
        conds.add(condIns);*/
        
        return conds;
    }
    
    private String getVariable(CharacterName name) {
        String variable = "";
        if (mapping.containsKey(name))
            variable = mapping.get(name);
        else {
            variable = nextVariable+"";
            mapping.put(name, variable);
            nextVariable = (char)(nextVariable + 1);
        }
            
        return variable;
    }
    
    private void addVariableMapping(String variable) {
        if (variable != null && !variableMapping.containsKey(variable)) {
            variableMapping.put(variable, nextVariable+"");
            nextVariable = (char)(nextVariable + 1);
        }
    }
    
}