package mexica.gui.utils;

import javax.swing.JComboBox;
import mexica.core.Position;

/**
 * Class to present all the available locations in a combo box
 * @author Ivan Guerrero
 */
public class LocationsList extends JComboBox<Position> {
    public LocationsList() {
        super(Position.getSelectablePositions());
    }
}
