package mexica.engagement;

/**
 * Class to store the conectivity information of an atom
 * @author Ivan Guerrero
 */
public class AtomConectivity {
    /** Determines the type of node according to the number of connections with similar atoms */
    public enum ConnectivityClass {UNDEFINED, ISOLATED, REGULAR, FOCAL};
    /** The number of similar nodes */
    private int connectivityCount;
    private ConnectivityClass connectivityClass;
    
    public AtomConectivity() {
        connectivityClass = ConnectivityClass.UNDEFINED;
    }
    
    /**
     * @return the connectivityCount
     */
    public int getConnectivityCount() {
        return connectivityCount;
    }

    /**
     * @param connectivityCount the connectivityCount to set
     */
    public void setConnectivityCount(int count) {
        this.connectivityCount = count;
        if (count < 3)
            connectivityClass = ConnectivityClass.ISOLATED;
        else if (count < 5)
            connectivityClass = ConnectivityClass.REGULAR;
        else
            connectivityClass = ConnectivityClass.FOCAL;
    }

    /**
     * @return the connectivityClass
     */
    public ConnectivityClass getConnectivityClass() {
        return connectivityClass;
    }

    /**
     * @param connectivityClass the connectivityClass to set
     */
    public void setConnectivityClass(ConnectivityClass connectivityClass) {
        this.connectivityClass = connectivityClass;
    }    
}
