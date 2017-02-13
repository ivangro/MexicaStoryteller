package mexica.story.analyzer;

import java.util.*;
import mexica.CharacterName;
import mexica.core.ConditionType;
import mexica.story.*;

/**
 * Class for storing the tensions in a character's context at every moment in the story
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class AvatarTensions implements Cloneable {
    private CharacterName avatar;
    private Map<Integer, Set<ConditionInstantiated>> tensions;
    
    public AvatarTensions(CharacterName avatar) {
        this.avatar = avatar;
        tensions = new HashMap<>();
    }
    
    public void addTension(int year, ConditionInstantiated condition) {
        if (condition.getType().equals(ConditionType.Tension)) {
            Set<ConditionInstantiated> list;
            list = tensions.get(year);
            if (list == null)
                list = new HashSet<>();
            
            list.add(condition);
            tensions.put(year, list);
        }
    }
    
    public Set<ConditionInstantiated> getTensions(int year) {
        if (tensions.containsKey(year))
            return tensions.get(year);
        else
            return new HashSet<>();
    }
    
    public CharacterName getAvatar() {
        return avatar;
    }
    
    public int getNumberOfTensions(int year) {
        Set<ConditionInstantiated> data = getTensions(year);
        return data.size();
    }
    
    @Override
    public Object clone() {
        AvatarTensions a = new AvatarTensions(avatar);
        for (Integer key : tensions.keySet()) {
            Set<ConditionInstantiated> conds = tensions.get(key);
            for (ConditionInstantiated c : conds) {
                a.addTension(key, (ConditionInstantiated)c.clone());
            }
        }
        
        return a;
    }
}
