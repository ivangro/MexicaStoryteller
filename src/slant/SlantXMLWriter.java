package slant;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import mexica.*;
import mexica.CharacterName;
import mexica.core.*;
import mexica.story.*;
import mexica.story.filter.StoryFilterException;
import mexica.story.guidelines.TensionCurveAnalyzer;
import mexica.tools.InvalidCharacterException;
import org.w3c.dom.*;
import slant.parser.SlantActionParser;
import slant.parser.SlantElementType;
import slant.parser.SlantElementsParser;

/**
 * Class to generate the XML output for Slant of a Mexica story
 * @author Ivan Guerrero
 */
public class SlantXMLWriter implements SlantXML {
    private ActionStore actionStore;
    private Document dom;
    private SlantElementsParser elementsParser;
    
    public SlantXMLWriter() {
        try {
            SlantActionParser slantActionParser = new SlantActionParser();
            slantActionParser.initParse(MexicaParameters.SLANT_ACTIONS_FILE);
            elementsParser = SlantElementsParser.getInstance();
            elementsParser.readXml(MexicaParameters.SLANT_ELEMENTS_FILE);
            actionStore = slantActionParser.getActions();
        } catch (Exception ex) {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
        }
    }
    
    public void generateXML(Story story, Document originalDom) {
        NodeList slant = originalDom.getElementsByTagName("slantstory");
        if (slant != null && slant.getLength() > 0) {
            Element slantStory = (Element)slant.item(0);
            //Remove the previous elements
            NodeList existents = slantStory.getElementsByTagName("existents");
            NodeList actions = slantStory.getElementsByTagName("actions");
            NodeList mexica = slantStory.getElementsByTagName(MEXICA_STORY);
            for (int i=0; i<existents.getLength(); i++) {
                slantStory.removeChild(existents.item(i));
            }
            for (int i=0; i<actions.getLength(); i++) {
                slantStory.removeChild(actions.item(i));
            }
            for (int i=0; i<mexica.getLength(); i++) {
                slantStory.removeChild(mexica.item(i));
            }
        }
        dom = originalDom;
        generateXML(story);
    }
    
    public int generateXML(Story story) {
        int slantActions = 0;
        try {
            if (dom == null) {
                System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                // use factory to get an instance of document builder
                DocumentBuilder db = dbf.newDocumentBuilder();
                // create instance of DOM
                dom = db.newDocument();
            }
            
            //Create/obtain the root element
            NodeList rootList = dom.getElementsByTagName("slantstory");
            Element root;
            if (rootList != null && rootList.getLength() > 0)
                root = (Element)rootList.item(0);
            else {
                root = dom.createElement("slantstory");
                dom.appendChild(root);
            }
            
            //Add the existent elements in the story
            Element existents = dom.createElement("existents");
            existents.setAttribute("complete", story.isFinished() + "");
            addExistents(dom, existents, story);
            root.appendChild(existents);
            
            //Add the story actions
            Element actions = dom.createElement("actions");
            actions.setAttribute("complete", story.isFinished() + "");
            actions.setAttribute("finishingReason", story.getFinishingReason());
            Element mexicaStory = dom.createElement(MEXICA_STORY);
            mexicaStory.setAttribute(STORY_ID, "1");
            slantActions = addActions(dom, actions, mexicaStory, story);
            root.appendChild(actions);
            root.appendChild(mexicaStory);
            
            //saveToFile(dom);
        } catch (ParserConfigurationException ex) {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
        }
        
        return slantActions;
    }
    
    public void saveToFile(String path) {
        File selectedFile = null;
        try {
            if (path == null) {
                URL url = getClass().getClassLoader().getResource(".");
                JFileChooser fileChooser = new JFileChooser((url != null) ? new File(url.toURI()) : null);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files", "xml");
                fileChooser.setFileFilter(filter);
                int returnVal = fileChooser.showSaveDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                }
            } 
            else {
                selectedFile = new File(path);
            }
            
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            //tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            // send DOM to file
            //String file = getClass().getClassLoader().getResource(path).getFile();
            tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(selectedFile)));
        } catch (URISyntaxException | FileNotFoundException | TransformerException ex) {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
        }
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

    private void addExistents(Document dom, Element existents, Story story) {
        Set<Position> visitedPositions = EnumSet.noneOf(Position.class);
        Set<CharacterName> avatarsEmployed = EnumSet.noneOf(CharacterName.class);
        Map<String, Position> thingsEmployed = new HashMap<>();
        
        //Adds the default position
        visitedPositions.add(story.getDefaultPosition());
        Story newStory = new Story();
        newStory.setDefaultPosition(story.getDefaultPosition());
        for (ActionInstantiated instance : story.getActions()) {
            try {
                List<CharacterName> characters = instance.getCharactersList();
                newStory.addAction(instance.getAction(), characters.toArray(new CharacterName[0]));
                //Obtains the action location
                Position actionLocation = newStory.getAvatarFactory().getAvatar(characters.get(0)).getCurrentPosition();
                //Adds the characters employed
                avatarsEmployed.addAll(characters);
                //Adds the character locations to the list of visited positions
                for (CharacterName avatar : characters) {
                    visitedPositions.add(newStory.getAvatarFactory().getAvatar(avatar).getCurrentPosition());
                }
                //Adds the items employed in the action to the list
                Logger.getGlobal().log(Level.INFO, "Looking for action: {0}", instance);
                MexicaAction action = (MexicaAction)actionStore.getAction(instance.getAction().getActionName());
                for (SlantAction a : action.getSlantActions()) {
                    thingsEmployed.put(elementsParser.iterateWeapon(a.getDirect()), actionLocation);
                    for (String item : a.getIndirects()) {
                        thingsEmployed.put(elementsParser.iterateWeapon(item), actionLocation);
                    }
                }
            } catch (ClassCastException | InvalidCharacterException | DeadAvatarException | StoryFilterException ex) { 
                Logger.getGlobal().log(Level.SEVERE, "Error during XML story generation {0} {1}", 
                                                      new Object[]{ex, instance});
            }
        }
        
        //Adds the visited positions to the existences
        for (Position p : visitedPositions) {
            if (Position.isValidPosition(p)) {
                Element location = dom.createElement("location");
                location.setAttribute("name", elementsParser.getNoun(p.name(), SlantElementType.Location));
                location.setAttribute("article", elementsParser.getArticle(p.name(), SlantElementType.Location));
                existents.appendChild(location);
            }
        }
        //Adds the employed characters to the existences
        for (CharacterName cn : avatarsEmployed) {
            Element character = dom.createElement("character");
            character.setAttribute("name", elementsParser.getNoun(cn.name(), SlantElementType.Character));
            character.setAttribute("article", elementsParser.getArticle(cn.name(), SlantElementType.Character));
            //Adds the location of the character
            Avatar avatar = newStory.getAvatarFactory().getAvatar(cn);
            Position initialPosition = avatar.getBirthPosition();
            //TODO: Validate that characters always have an initial position
            character.setAttribute("location", elementsParser.getNoun(initialPosition.name(), SlantElementType.Location));
            //Adds the gender of the character
            String gender = MexicaRepository.getInstance().getHierarchyStore().getGenderGroup().getHierarchy(cn).getHierarchyName();
            character.setAttribute("gender", gender.toLowerCase());
            existents.appendChild(character);
        }
        //Adds the employed items to the existences
        for (String item : thingsEmployed.keySet()) {
            Element thing = dom.createElement("thing");
            String thingName = elementsParser.getNoun(item, SlantElementType.Thing);
            if (thingName != null && thingName.length() > 0) {
                thing.setAttribute("name", thingName);
                thing.setAttribute("article", elementsParser.getArticle(item, SlantElementType.Thing));
                Position location = thingsEmployed.get(item);
                thing.setAttribute("location", elementsParser.getNoun(location.name(), SlantElementType.Location));
                existents.appendChild(thing);
            }
        }
        elementsParser.resetWeaponCounter();
    }

    private int addActions(Document dom, Element actions, Element mexicaStory, Story story) {
        int slantActions = 0;
        boolean locationSet;
        Story newStory = new Story();
        newStory.setDefaultPosition(story.getDefaultPosition());
        mexicaStory.setAttribute(DEFAULT_LOCATION, story.getDefaultPosition().name());
        Element mexicaActions = dom.createElement(MEXICA_ACTIONS);
        mexicaStory.appendChild(mexicaActions);
        
        int i=1, j;
        for (ActionInstantiated instance : story.getActions()) {
            List<CharacterName> characters = instance.getCharactersList();
            try {
                newStory.addAction(instance.getAction(), characters.toArray(new CharacterName[0]));
                MexicaAction action = (MexicaAction)actionStore.getAction(instance.getAction().getActionName());
                
                //Mexica action
                Element mexicaActionElem = dom.createElement(MEXICA_ACTION);
                mexicaActionElem.setAttribute(ACTION_ID, i + "");
                mexicaActionElem.setAttribute(ACTION_NAME, action.getActionName());
                mexicaActionElem.setAttribute(ACTION_PERFORMER, characters.get(0).name());
                if (characters.size() == 2)
                    mexicaActionElem.setAttribute(ACTION_RECEIVER, characters.get(1).name());
                mexicaActions.appendChild(mexicaActionElem);
                
                //Slant action
                j = 0;
                for (SlantAction slantAction : action.getSlantActions()) {
                    locationSet = false;
                    Element actionElem = dom.createElement("action");
                    actionElem.setAttribute("verb", slantAction.getActionName().replaceAll("_", " "));
                    if (slantAction.isNegated())
                        actionElem.setAttribute("negated", slantAction.isNegated() + "");
                    if (slantAction.getAgent().equalsIgnoreCase("A"))
                        actionElem.setAttribute("agent", elementsParser.getNoun(characters.get(0).name(), SlantElementType.Character));
                    else if (slantAction.getAgent().equalsIgnoreCase("B") && characters.size() == 2)
                        actionElem.setAttribute("agent", elementsParser.getNoun(characters.get(1).name(), SlantElementType.Character));
                    
                    //Adds the direct element
                    if (slantAction.getDirect() != null && slantAction.getDirect().length() > 0)
                    switch (slantAction.getDirect().toLowerCase()) {
                        case "a":
                            actionElem.setAttribute("direct", elementsParser.getNoun(characters.get(0).name(), SlantElementType.Character));
                            break;
                        case "b":
                            actionElem.setAttribute("direct", elementsParser.getNoun(characters.get(1).name(), SlantElementType.Character));
                            break;
                        default:    //Corresponds to a thing or a location
                            String location = convertToLocation(slantAction.getDirect(), characters, newStory);
                            if (location != null && location.length() > 0) {
                                actionElem.setAttribute("direct", elementsParser.getNoun(location, SlantElementType.Location));
                                actionElem.setAttribute("location", elementsParser.getNoun(location, SlantElementType.Location));
                                locationSet = true;
                            }
                            else {
                                String thing = elementsParser.iterateWeapon(slantAction.getDirect());
                                actionElem.setAttribute("direct", elementsParser.getNoun(thing, SlantElementType.Thing));
                            }
                            break;
                    }
                    //Add the indirect elements
                    for (String indirect : slantAction.getIndirects()) {
                        String characterName = convertVariableToCharacter(indirect, characters);
                        String value;
                        if (characterName != null)
                            value = elementsParser.getNoun(characterName, SlantElementType.Character);
                        else {
                            String thing = elementsParser.iterateWeapon(indirect);
                            value = elementsParser.getNoun(thing, SlantElementType.Thing);
                        }
                        if (value == null) {
                            String location = convertToLocation(indirect, characters, newStory);
                            if (location != null) {
                                //actionElem.setAttribute("indirect", elementsParser.getNoun(location, SlantElementType.Location));
                                actionElem.setAttribute("location", elementsParser.getNoun(location, SlantElementType.Location));
                                locationSet = true;
                            }
                        }
                        
                        if (value != null && value.length() > 0)
                            actionElem.setAttribute("indirect", value);
                    }
                    
                    Avatar performer = newStory.getAvatarFactory().getAvatar(characters.get(0));
                    if (!locationSet)
                        actionElem.setAttribute("location", elementsParser.getNoun(performer.getCurrentPosition().name(), SlantElementType.Location));
                    int tensions = TensionCurveAnalyzer.calculateNumberOfTensions(newStory, i);
                    actionElem.setAttribute("tension", (tensions * MexicaParameters.TENSION_VALUE) + "");
                    actionElem.setAttribute("id", (i * 10 + j) + "");
                    actions.appendChild(actionElem);
                    j++;
                    slantActions++;
                }
                i++;
            } catch (InvalidCharacterException | DeadAvatarException | StoryFilterException ex) { 
                Logger.getGlobal().log(Level.WARNING, "Error during XML story generation {0}", ex);
            } catch (ClassCastException ex) {
                Logger.getGlobal().log(Level.WARNING, "Action doesn't exists for Slant {0}", instance);
            }
        }
        
        return slantActions;
    }
    
    public Document getDocument() {
        return dom;
    }

    private String convertVariableToCharacter(String indirect, List<CharacterName> characters) {
        String characterName = null;
        switch (indirect.toLowerCase()) {
            case "a":
                characterName = characters.get(0).name();
                break;
            case "b":
                characterName = characters.get(1).name();
                break;
        }
        
        return characterName;
    }

    private String convertToLocation(String indirect, List<CharacterName> characters, Story story) {
        String location = null;
        if (indirect.endsWith("location")) {
            String[] split = indirect.split("_");
            //Obtain the character
            CharacterName name = null;
            switch (split[0].toLowerCase()) {
            case "a":
                name = characters.get(0);
                break;
            case "b":
                name = characters.get(1);
                break;
            }
            //Obtain the location of the character
            Avatar avatar = story.getAvatarFactory().getAvatar(name);
            switch (split[1].toLowerCase()) {
                case "location":
                    location = avatar.getCurrentPosition().name();
                    break;
                case "prevlocation":
                    if (avatar.getPreviousPosition() != Position.NotDefined)
                        location = avatar.getPreviousPosition().name();
                    else
                        location = story.getDefaultPosition().name();
                    break;
            }
        }
        
        return location;
    }
}