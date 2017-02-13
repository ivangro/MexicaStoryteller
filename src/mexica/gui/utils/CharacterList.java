package mexica.gui.utils;

import javax.swing.JList;
import mexica.CharacterName;

/**
 * Class to present in a GUI all the selectable characters to instantiate an action
 * @author Ivan Guerrero
 */
public class CharacterList extends JList<CharacterName> {
    public CharacterList() {
        super(CharacterName.getSelectableCharacters());
    }
}
