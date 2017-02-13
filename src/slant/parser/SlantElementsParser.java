package slant.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Ivan Guerrero
 */
public class SlantElementsParser {
    private static SlantElementsParser instance = new SlantElementsParser();
    private Document doc;
    private Element root;
    private final String ELEMENTS_NODE = "slantElements";
    private final String CHARACTER_NODE = "character";
    private final String CHARACTER_LIST_NODE = "characters";
    private final String LOCATION_LIST_NODE = "locations";
    private final String LOCATION_NODE = "location";
    private final String THING_LIST_NODE = "things";
    private final String THING_NODE = "thing";
    
    private List<String> weapons;
    private int weaponIndex;
    
    private SlantElementsParser() {
        weapons = Arrays.asList("weapon", "dagger", "knife", "obsidian_knife", "sharp_stone", "lance", "spear");
        weaponIndex = 0;
    }
    
    public static SlantElementsParser getInstance() {
        return instance;
    }
    
    public void readXml(String path) {
        try {
            InputStream stream = new java.io.FileInputStream(path);
            //InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(stream);
            //Obtains the root element
            root = (Element)doc.getElementsByTagName(ELEMENTS_NODE).item(0);
        } catch (SAXException | IOException | ParserConfigurationException | NullPointerException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Error reading slant XML file {0}", ex.getMessage());
            throw new Error("Error reading slant XML file " + ex.getMessage());
        } 
    }
    
    public String getArticle(String id, SlantElementType type) {
        return getValue(id, "article", type);
    }
    
    public String getNoun(String id, SlantElementType type) {
        String noun;
        switch (type) {
            case Thing:
                noun = getValue(id, "name", type);
                break;
            default:
                noun = getValue(id, "name", type);
        }
        
        return noun;
    }
    
    public String getValue(String id, String attribute, SlantElementType type) {
        String value = null, elemId;
        NodeList list = null;
        Element listNode;
        
        switch (type) {
            case Character:
                listNode = (Element)root.getElementsByTagName(CHARACTER_LIST_NODE).item(0);
                list = listNode.getElementsByTagName(CHARACTER_NODE);
                break;
            case Location:
                listNode = (Element)root.getElementsByTagName(LOCATION_LIST_NODE).item(0);
                list = listNode.getElementsByTagName(LOCATION_NODE);
                break;
            case Thing:
                listNode = (Element)root.getElementsByTagName(THING_LIST_NODE).item(0);
                list = listNode.getElementsByTagName(THING_NODE);
                break;
        }
        
        if (list != null) {
            for (int i=0; i<list.getLength(); i++) {
                Element avatar = (Element)list.item(i);
                elemId = avatar.getAttribute("id");
                if (elemId.equalsIgnoreCase(id)) {
                    value = avatar.getAttribute(attribute);
                    break;
                }
            }
        }
        
        return value;
    }
    
    public String iterateWeapon(String noun) {
        if (noun != null && noun.equalsIgnoreCase("weapon")) {
            noun = weapons.get(weaponIndex % weapons.size());
            weaponIndex++;
        }
        
        return noun;
    }

    public void resetWeaponCounter() {
        weaponIndex = 0;
    }
}
