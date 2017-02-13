package mexica.engagement;

import java.util.*;

/**
 * The class stores all the available atoms for the program
 * The actions are retrieved from a text file
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class AtomsStore {
    private List<Cell> cells;
    
    public AtomsStore() {
        cells = new ArrayList<>();
    }
    
    public List<Cell> getCells() {
        return cells;
    }
    
    /**
     * Obtains the number of atoms in the store
     * @return 
     */
    public int getAtomCount() {
        int atoms = 0;
        for (Cell c : cells) {
            atoms += c.getAtoms().size();
        }
        return atoms;
    }
    
    /**
     * Looks for the cell with the given number of parameters, if doesn't exist, creates it, adds it to the list and returns it
     * @param emotions Number of emotions of the atoms inside the cell
     * @param tensions Number of tensions of the atoms inside the cell
     * @return Cell with the atoms fulfilling the given values
     */
    public Cell getCell(int emotions, int tensions) {
        for (Cell cell : cells) {
            if (cell.getEmotionCount() == emotions && cell.getTensionCount() == tensions)
                return cell;
        }
        Cell c = new Cell(tensions, emotions);
        cells.add(c);
        return c;
    }

    /**
     * Adds the given atom to its correspondent cell and assigns the atom's ID according to the cell's size
     * @param atom The atom to be stored
     */
    public void addAtom(Atom atom) {
        int emotions, tensions;
        emotions = atom.getEmotions().size();
        tensions = atom.getTensions().size();
        Cell cell = getCell(emotions, tensions);
        boolean atomFound = false;
        for (Atom a : cell.getAtoms()) {
            if (AtomComparer.areEqual(atom, a)) {
                if (!atom.getNextActions().isEmpty()) {
                    a.getNextActions().add(atom.getNextActions().get(0));
                    a.addAllStoryIDs(atom.getStoryIDs());
                    atomFound = true;
                    break;
                }
            }
        }
        if (!atomFound && emotions+tensions > 0) {
            atom.setCellID(cell.getCellID());
            atom.setID(cell.getAtoms().size());
            cell.addAtom(atom);
        }
    }
}