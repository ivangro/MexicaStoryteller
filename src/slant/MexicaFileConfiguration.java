package slant;

import java.io.*;
import mexica.MexicaParameters;

/**
 * Establishes the folder of the configuration files to properly open from JAR files
 * @author Ivan Guerrero
 */
public class MexicaFileConfiguration {
    
    public void loadConfiguration() throws IOException {
        String name = this.getClass().getName().replace('.', '/');
        String s = this.getClass().getResource("/" + name + ".class").toString();
        if (s.startsWith("jar:")) {
            s = s.replace('/', File.separatorChar);
            s = s.substring(0, s.indexOf(".jar")+4);
            if (System.getProperty("os.name").indexOf("win") >= 0)
                s = s.substring(s.lastIndexOf(':')-1);//In windows -1
            else
                s = s.substring(s.lastIndexOf(':')+1);//In linux +1
            s = s.substring(0, s.indexOf("MexicaStoryteller.jar"));
            MexicaParameters.setMainPath(s + "src/");
        }
        else {
            File f = new File(".");
            String path = f.getCanonicalPath().replaceAll("\\\\", "/");
            MexicaParameters.setMainPath(path + MexicaParameters.MEXICA_KB_ID + "/src/");
        }
    }
}