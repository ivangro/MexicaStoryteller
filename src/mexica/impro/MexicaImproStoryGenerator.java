package mexica.impro;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import mexica.CharacterName;
import mexica.core.ActionStore;
import mexica.core.CompositeAction;
import mexica.story.*;
import mexica.story.filter.StoryFilterException;
import mexica.tools.InvalidCharacterException;
import org.w3c.dom.*;

/**
 * Class to write a colaborative story into an XML file.
 * @author Ivan Guerrero
 */
public class MexicaImproStoryGenerator implements IMexicaImproXMLTags, MexicaImproXMLLogTags {
    private ActionStore actionStore;
    private Document dom;
    private List<ActionInstantiated> actions;
    
    public int generateXML(Story story, Document originalDom) {
        dom = originalDom;
        return generateXML(story);
    }
    
    /**
     * Generate an XML file with the contents of the given story
     * @param story The story to be converted into XML format
     * @return The number of actions inside the story
     */
    public int generateXML(Story story) {
        int noActions = 0;
        try {
            if (dom == null) {
                System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                // use factory to get an instance of document builder
                DocumentBuilder db = dbf.newDocumentBuilder();
                // create instance of DOM
                dom = db.newDocument();
            }
            
            //Create or obtain the root element
            NodeList rootList = dom.getElementsByTagName(MEXICA_STORY);
            Element root;
            if (rootList != null && rootList.getLength() > 0) {
                root = (Element)rootList.item(0);
                //Remove all the actions inside the story
                NodeList actionNode = root.getElementsByTagName(MEXICA_ACTIONS);
                for (int i=0; i<actionNode.getLength(); i++) {
                   root.removeChild(actionNode.item(i));
                }
            }
            else {
                root = dom.createElement(MEXICA_STORY);
                dom.appendChild(root);
            }
            
            root.setAttribute(COMPLETE, story.isFinished() + "");
            root.setAttribute(FINISHED_BY, story.getFinishingReason());
            story.addIteration();
            root.setAttribute(ITERATION_NUMBER, story.getIterations() + "");
            root.setAttribute(IMPASSES, story.getImpasses() + "");
            noActions = addActions(dom, root, story);
        } catch (ParserConfigurationException ex) {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
        }
        
        createStoryLog(story);
        
        return noActions;
    }
    
    private int addActions(Document dom, Element mexicaStory, Story story) {
        Story newStory = new Story();
        newStory.setDefaultPosition(story.getDefaultPosition());
        mexicaStory.setAttribute(DEFAULT_LOCATION, story.getDefaultPosition().name());
        Element mexicaActions = dom.createElement(MEXICA_ACTIONS);
        mexicaStory.appendChild(mexicaActions);
        
        int i=1;
        for (ActionInstantiated instance : story.getActions()) {
            List<CharacterName> characters = instance.getCharactersList();
            try {
                newStory.addAction(instance.getAction(), characters.toArray(new CharacterName[0]));
                
                //Mexica action
                Element mexicaActionElem = dom.createElement(MEXICA_ACTION);
                mexicaActionElem.setAttribute(ACTION_ID, i + "");
                mexicaActionElem.setAttribute(ACTION_NAME, instance.getAction().getActionName());
                mexicaActionElem.setAttribute(ACTION_PERFORMER, characters.get(0).name());
                mexicaActionElem.setAttribute(INSERTION_MODE, InsertionMode.getAbbreviation(instance.getInsertionMode()));
                mexicaActionElem.setAttribute(ITERATION_NUMBER, (instance.getIterationNumber() >= 0) ? 
                                              instance.getIterationNumber() + "" : story.getIterations() + "");
                mexicaActionElem.setAttribute(ACTION_NUMBER, instance.getActionID() + "");
                if (characters.size() == 2)
                    mexicaActionElem.setAttribute(ACTION_RECEIVER, characters.get(1).name());
                //Detect complosite actions 
                if (instance.getAction() instanceof CompositeAction) {
                    mexicaActionElem.setAttribute(COMPOSITE_ACTION, findCompositeActionIndex(instance, story) + "");
                }
                if (!instance.isCollaborativeAction())
                    mexicaActionElem.setAttribute(MEXICA_INSTANCE_ID, MexicaImproAPI.MEXICA_INSTANCE_ID + "");
                else
                    mexicaActionElem.setAttribute(MEXICA_INSTANCE_ID, instance.getMexicaImproID() + "");
                mexicaActions.appendChild(mexicaActionElem);
                
                i++;
            } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) { 
                Logger.getGlobal().log(Level.WARNING, "Error during XML story generation {0}", ex);
            } catch (NullPointerException ex) {
                Logger.getGlobal().log(Level.WARNING, "Action doesn't exists {0}", instance);
            }
        }
        return i-1;
    }
    
    public Document getDocument() {
        return dom;
    }
    
    public void sendToStdOutput() {
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            //tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            // send DOM to file
            tr.transform(new DOMSource(dom), new StreamResult(System.out));
        } catch (TransformerException ex) {
            Logger.getGlobal().log(Level.SEVERE, ex.getMessage());
        }
    }
    
    public void sendToFile(File file) {
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            //tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            // send DOM to file
            tr.transform(new DOMSource(dom), new StreamResult(file));
            
        } catch (TransformerException ex) {
            Logger.getGlobal().log(Level.SEVERE, ex.getMessage());
        }
    }

    /**
     * @param actionStore the actionStore to set
     */
    public void setActionStore(ActionStore actionStore) {
        this.actionStore = actionStore;
    }

    private int findCompositeActionIndex(ActionInstantiated instance, Story story) {
        CompositeAction composite = (CompositeAction)instance.getAction();
        ActionInstantiated containedAction = composite.getContainedAction();
        actions = story.getActions();
        for (int i=0; i<actions.size(); i++) {
            if (actions.get(i).equals(containedAction))
                return (i+1); //Since the actionID begins in 1
        }
        System.err.println("<!-- Action " + instance + " couldn't be found -->");
        System.err.println("<!-- Contained action " + containedAction + " -->");
        return -1;
    }

    private void createStoryLog(Story story) {
        NodeList rootList = dom.getElementsByTagName(MEXICA_STORY);
        if (rootList != null && rootList.getLength() > 0) {
            Element root;
            root = (Element)rootList.item(0);
            
            //Create the root for the log information
            NodeList iterationsList = root.getElementsByTagName(LOG_ITERATIONS);
            Element rootIterations;
            if (iterationsList != null && iterationsList.getLength() > 0)
                rootIterations = (Element)iterationsList.item(0);
            else {
                rootIterations = dom.createElement(LOG_ITERATIONS);
                root.appendChild(rootIterations);
            }
            
            //Add to the log the information of the new iteration
            Element rootInstance = dom.createElement(LOG_ITERATION);
            rootInstance.setAttribute(LOG_INSTANCE_ID, story.getIterations() + "");
            rootInstance.setAttribute(LOG_MEXICA_INSTANCE_ID, MexicaImproAPI.MEXICA_INSTANCE_ID + "");
            rootIterations.appendChild(rootInstance);
            Element rootAtoms = dom.createElement(LOG_ATOMS);
            rootInstance.appendChild(rootAtoms);
            List<ActionInstantiated> instances = story.getStoryLog().getActions();
            List<AtomActionPair> atoms = story.getStoryLog().getAtoms();
            for (int i=0; i<atoms.size(); i++) {
                AtomActionPair pair = atoms.get(i);
                Element atomElem = dom.createElement(LOG_ATOM);
                rootAtoms.appendChild(atomElem);
                atomElem.setAttribute(LOG_ATOM_ID, pair.getAtom().getAtomLabel());
                atomElem.setAttribute(LOG_ACTION, pair.getAction().toString());
                atomElem.setAttribute(LOG_ACTION_INSTANCE, instances.get(i).toString());
                atomElem.setAttribute(LOG_ACTION_ORDER, instances.get(i).getActionID() + "");
                atomElem.setAttribute(LOG_ATOM_CLASS, pair.getAtom().getNeighborClass().name() + "");
                atomElem.setAttribute(LOG_ATOM_CLUSTER, pair.getAtom().getClusterID() + "");
            }
        }
    }
}