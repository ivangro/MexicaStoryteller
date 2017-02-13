package subgraph;

import java.util.*;

/**
 *
 * @author Ivan Guerrero
 */
public class Mapping<T> {
    private Map<T, T> map, invertedMap;
    
    public Mapping() {
        map = new HashMap<>();
        invertedMap = new HashMap<>();
    }
    
    public boolean addMapping(T source, T target) {
        if (!map.containsKey(source)) {
            map.put(source, target);
            invertedMap.put(target, source);
        }
        else 
            return false;
        return true;
    }

    public T getMapping(T source) {
        return map.get(source);
    }
    
    public T getInvertedMapping(T target) {
        return invertedMap.get(target);
    }

    public Iterable<T> getKeys() {
        return map.keySet();
    }
    
    public Collection<T> getValues() {
        return map.values();
    }
    
    @Override
    public String toString() {
        return map.toString();
    }
}
