package slant.parser;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import mexica.MexicaParameters;
import mexica.MexicaRepository;
import mexica.core.*;
import slant.MexicaAction;
import slant.SlantAction;

/**
 *
 * @author Ivan Guerrero
 */
public class SlantActionViewer {
    public static void main(String[] args) {
        Set<SlantAction> slantActions = new TreeSet<>();
        
        try {
            SlantActionParser parser = new SlantActionParser();
            parser.initParse(MexicaParameters.SLANT_ACTIONS_FILE);
            ActionStore actions = parser.getActions();
            lookupMissingActions(actions);
            for (Action act : actions.getActionList()) {
                MexicaAction action = (MexicaAction)act;
                for (SlantAction slant : action.getSlantActions()) {
                    slantActions.add(slant);
                }
            }
            
            System.out.println("<slantActions>");
            for (SlantAction action : slantActions) {
                String indirects = action.getIndirects().toString();
                indirects = indirects.replaceAll("\\[", "");
                indirects = indirects.replaceAll("\\]", "");
                
                String str = "<action name=\"" + action.getActionName() + "\"\t" + 
                                      "agent=\"" + action.getAgent().toLowerCase() + "\"\t" + 
                                      "direct=\"" + action.getDirect().toLowerCase() + "\"\t" + 
                                      "indirect=\"" + indirects + "\" />";
                System.out.println(str);
            }
            System.out.println("</slantActions>");
        } catch (Exception ex) {
            Logger.getLogger(SlantActionViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void lookupMissingActions(ActionStore slantStore) {
        Logger.getGlobal().setLevel(Level.ALL);
        MexicaRepository.getInstance().loadData();
        ActionStore mexicaStore = MexicaRepository.getInstance().getActions();
        
        List<Action> slantActions = slantStore.getActionList();
        
        for (Action action : mexicaStore.getActionList()) {
            if (!slantActions.contains(new MexicaAction(action)))
                System.out.println("Missing action in slant file: " + action);
        }
    }
}
