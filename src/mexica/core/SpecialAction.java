package mexica.core;

/**
 *
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class SpecialAction extends Action {
    @Override
    public boolean equals(Object obj) {
        boolean ans = false;
        if (obj instanceof SpecialAction) {
            SpecialAction act = ((SpecialAction)obj);
            ans = act.getActionName().equalsIgnoreCase(getActionName());
        }
        return ans;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }
}
