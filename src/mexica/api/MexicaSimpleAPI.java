package mexica.api;

import java.util.List;
import mexica.MexicaRepository;
import mexica.core.Action;
import mexica.core.Position;
import mexica.tools.PositionFactory;

/**
 * Class providing basic functionality for the simple GUI
 * This class can be used only for testing purposes
 * @author Ivan
 */
public class MexicaSimpleAPI {
    private static MexicaSimpleAPI INSTANCE = new MexicaSimpleAPI();
    private MexicaRepository mexica;
    private MexicaSimpleAPI() {
        mexica = MexicaRepository.getInstance();
    }
    
    public static MexicaSimpleAPI getInstance() {
        return INSTANCE;
    }
    
    public List<Action> getActionList() {
        return mexica.getActions().getActionList();
    }
    
    public Action findAction(String actionName) {
        Action action = null;
        List<Action> actions = getActionList();
        for (Action a : actions) {
            if (a.getActionName().equalsIgnoreCase(actionName))
                return a;
        }
        return action;
    }
            
    
    public MexicaRepository getRepository() {
        return mexica;
    }
        
    public List<Position> getAvailablePositions() {
        return PositionFactory.getInstance().getAvailablePositions();
    }
}
