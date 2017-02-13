package mexica.impro;

import mexica.engagement.Atom;
import mexica.story.Story;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Class to analyze a partial story and write several factors
 * @author Ivan Guerrero
 */
public class MexicaImproStoryAnalyzer implements IMexicaImproXMLTags, MexicaImproXMLLogTags {

    /**
     * Analyzes a partial story and evaluates its coherence
     * @param story The current story
     * @param doc The root element of the xml file
     */
    public void analyzeStory(Story story, Document doc) {
        try {
            int missingConditions, irrelevantActions, illogicalActions;
            Element storyNode = (Element)doc.getElementsByTagName(MEXICA_STORY).item(0);
            //Read the information about the previous iteration to evaluate communication
            int currentIteration = Integer.parseInt(storyNode.getAttribute(ITERATION_NUMBER));
            Element iterationNode = null;

            Element iterationsNode = (Element)storyNode.getElementsByTagName(LOG_ITERATIONS).item(0);
            NodeList iterationList = iterationsNode.getElementsByTagName(LOG_ITERATION);
            for (int i=0; i<iterationList.getLength(); i++) {
                Element iteration = (Element)iterationList.item(i);
                int id = Integer.parseInt(iteration.getAttribute(LOG_INSTANCE_ID));
                int mexicaID = Integer.parseInt(iteration.getAttribute(LOG_MEXICA_INSTANCE_ID));
                story.getStoryMeta().setMexicaID(id, mexicaID);
                
                if (id == currentIteration) {
                    iterationNode = iteration;
                } else {
                    missingConditions = Integer.parseInt(iteration.getAttribute(MISSING_CONDITIONS));
                    irrelevantActions = Integer.parseInt(iteration.getAttribute(IRRELEVANT_ACTIONS));
                    illogicalActions = Integer.parseInt(iteration.getAttribute(ILLOGICAL_ACTIONS));
                    story.setIllogicalActions(id, illogicalActions);
                    story.setMissingConditions(id, missingConditions);
                    story.setIrrelevantActions(id, irrelevantActions);
                }

                Element atomsList = (Element)iteration.getElementsByTagName(LOG_ATOMS).item(0);
                NodeList atoms = atomsList.getElementsByTagName(LOG_ATOM);
                for (int j=0; j<atoms.getLength(); j++) {
                    Element atom = (Element)atoms.item(j);
                    Atom.ConnectivityClass atomClass = Atom.ConnectivityClass.valueOf(atom.getAttribute(LOG_ATOM_CLASS));
                    int clusterID = Integer.parseInt(atom.getAttribute(LOG_ATOM_CLUSTER));
                    String atomID = atom.getAttribute(LOG_ATOM_ID).replace("Atom ", "").trim();
                    story.getStoryMeta().addAtom(id, atomClass, clusterID, atomID);
                }
            }

            //Add the evaluation of the previous iteration
            if (iterationNode != null) {
                //Evaluate preconditions
                //If the story has missing preconditions, mark missing preconditions
                missingConditions = story.getMissingConditions().size();
                iterationNode.setAttribute(MISSING_CONDITIONS, missingConditions + "");
                
                //Count the number of actions that do not progress the story
                //If there are less actions than those in the original story
                irrelevantActions = story.getIrrelevantActions(story.getIterations());
                iterationNode.setAttribute(IRRELEVANT_ACTIONS, irrelevantActions + "");

                //Evaluate Illogical actions
                illogicalActions = story.getIllogicalActions(story.getIterations());
                iterationNode.setAttribute(ILLOGICAL_ACTIONS, illogicalActions + "");
                
                //TODO: Evaluate Thematic progression
                
                story.setIllogicalActions(currentIteration, illogicalActions);
                story.setMissingConditions(currentIteration, missingConditions);
                story.setIrrelevantActions(currentIteration, irrelevantActions);
            }
        } catch (NullPointerException npe) {}
    }
}
