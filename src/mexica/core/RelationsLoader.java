package mexica.core;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * Loads all the available relations
 * @author Ivan Guerrero
 */
public class RelationsLoader {
    private Document doc;
    private Element root;
    private final String ELEMENTS_NODE = "relations";
    private final String EMOTIONS_NODE = "emotions";
    private final String EMOTION_NODE = "emotion";
    private RelationsStore store;
    
    public RelationsLoader() {
    }
    
    public void loadRelations(String path) {
        store = new RelationsStore();
        try {
            InputStream stream = new FileInputStream(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(stream);
            //Obtains the root element
            root = (Element)doc.getElementsByTagName(ELEMENTS_NODE).item(0);
            parseEmotions(root);
            parseTensions(root);
            Logger.getGlobal().log(Level.INFO, "Relations loaded {0}", EmotionType.values().length);
            //Logger.getGlobal().log(Level.INFO, "Relations loaded {0}", store.values().length);
        } catch (SAXException | IOException | ParserConfigurationException | NullPointerException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Error reading relations XML file {0}", ex.getMessage());
            throw new Error("Error reading relations XML file " + ex.getMessage());
        } 
    }

    private void parseEmotions(Element root) {
        Element emotions = (Element)root.getElementsByTagName(EMOTIONS_NODE).item(0);
        NodeList list = emotions.getElementsByTagName(EMOTION_NODE);
        for (int i=0; i<list.getLength(); i++) {
            Node emotion = list.item(i);
            NamedNodeMap atts = emotion.getAttributes();
            String name = atts.getNamedItem("name").getNodeValue();
            String value = atts.getNamedItem("value").getNodeValue();
            String text = atts.getNamedItem("text").getNodeValue();
            String textualRep = atts.getNamedItem("textualRepresentation").getNodeValue();
            Node social = atts.getNamedItem("social");
            Node inLove = atts.getNamedItem("inLove");
            Node any = atts.getNamedItem("any");
            EmotionType.addEmotion(name, Integer.parseInt(value), text, textualRep, social != null, inLove != null, any != null);
            //store.addEmotion(name, Integer.parseInt(value), text, textualRep, social != null, inLove != null, any != null);
        }
    }

    private void parseTensions(Element root) {
        //throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public RelationsStore getRelationsStore() {
        return store;
    }
}