package mexica.context;

import java.util.Objects;
import mexica.CharacterName;
import subgraph.INode;

/**
 *
 * @author Ivan Guerrero
 */
public class Element implements INode {
    private String label;
    
    public Element(String label) {
        if (label == null)
            label = "UNK";
        this.label = label;
    }
    
    public Element(CharacterName name) {
        label = CharacterName.getAbbreviation(name);
    }
    
    @Override
    public String toString() {
        return label;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Element) {
            Element e = (Element)obj;
            return e.label.equalsIgnoreCase(label);
        }
        
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.label);
        return hash;
    }

    @Override
    public String getID() {
        return label;
    }
}
