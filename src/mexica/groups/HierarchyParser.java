package mexica.groups;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mexica.MexicaParameters;

/**
 * Class to parse the file with hierarchical relations between characters
 * A file with social groups has the following format:
 *  Its extension is .hcy (From hierarchical group)
 *  FILE = <hierarchicalGroups>
 *  hierarchicalGroups = <hierarchicalGroup>, <hierarchicalGroups> | NIL
 *  hierarchicalGroup = <groupName>, <hierarchies>
 *  groupName = String
 *  hierarchies = <hierarchy>, <hierarchies> | NIL
 *  hierarchy = <hierarchyName>, <ranking>, '(' <characters> ')'
 *  hierarchyName = String
 *  ranking = integer
 *  characters = <character>','?, <characters> | NIL
 *  character = MexicaCharacter
 * @author Ivan Guerrero
 */
public class HierarchyParser {
    /** hierarchyName */
    public static final String HIERARCHY_NAME = "\\s*(\\w+).*";
    /** groupName classification (character*) */
    public static final String GROUP_ELEMS = "\\s*(\\w+)\\s+(\\d+)\\s+\\((.*)\\).*";
    
    public static final String EOF = "\\s*END\\s*.*";
    
    public static Pattern pHierarchy, pGroup, pEOF;
    private Scanner scanner;
    private List<HierarchicalGroup> groups;
    private HierarchicalGroup currentGroup;
    
    static {
        pHierarchy = Pattern.compile(HIERARCHY_NAME);
        pGroup = Pattern.compile(GROUP_ELEMS);
        pEOF = Pattern.compile(EOF);
    }
    
    public HierarchyParser(String path) {
        Logger.getGlobal().log(Level.INFO, "Retrieving hierarchies from {0}", path);
        try {
            scanner = new Scanner(new java.io.File(path));
            //scanner = new Scanner(getClass().getClassLoader().getResourceAsStream(path));
        } catch (Exception ex) {
            Logger.getGlobal().log(Level.SEVERE, "Error parsing hierarchies: {0}", ex.getMessage());
        }
        groups = new ArrayList<>();
    }
    
    public void parseFile() {
        try {
            while (scanner.hasNextLine()) {
                parseLine(scanner.nextLine());
            }
        } catch (Error er) {
            Logger.getGlobal().log(Level.FINE, er.getMessage());
        }
    }
    
    public boolean parseLine(String nextLine) {
        Matcher m;
        
        m = pGroup.matcher(nextLine);
        if (m.matches()) {
            Hierarchy hierarchy = new Hierarchy();
            hierarchy.setHierarchyName(m.group(1));
            hierarchy.setClassification(Integer.parseInt(m.group(2)));
            hierarchy.parseCharacters(m.group(3));
            hierarchy.setHierarchicalGroup(currentGroup);
            currentGroup.addHierarchy(hierarchy);
            return true;
        }
        
        m = pEOF.matcher(nextLine);
        if (m.matches()) {
            if (currentGroup != null) {
                groups.add(currentGroup);
            }
            throw new Error("End of file reached");
        }
        
        m = pHierarchy.matcher(nextLine);
        if (m.matches()) {
            if (currentGroup != null) {
                groups.add(currentGroup);
            }
            currentGroup = new HierarchicalGroup();
            currentGroup.setGroupName(m.group(1));
            return true;
        }
        
        return false;
    }
    
    public List<HierarchicalGroup> getGroups() {
        return groups;
    }
    
    public static void main(String[] args) {
        Logger.getGlobal().setLevel(Level.INFO);
        HierarchyParser parser = new HierarchyParser(MexicaParameters.HIERARCHIES_FILE);
        parser.parseFile();
        System.out.println(parser.getGroups());
    }
}