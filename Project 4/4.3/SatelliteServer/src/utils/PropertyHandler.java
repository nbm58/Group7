package utils;

import java.util.*;
import java.io.*;

import utils.FileUtils;

/**
 * class [PropertyHandler]
 * <p>
 * This is an auxiliary class to read properties data contained in some file
 *
 * @author Prof. Dr.-Ing. Wolf-Dieter Otte
 * @version Feb. 2000
 */
public class PropertyHandler extends Properties {

    File propertyFile = null;

    /**
     * constructor
     */
    public PropertyHandler(Properties defaultProperties, String propertyFileString) throws FileNotFoundException, IOException {

        super(defaultProperties);
        // System.out.println("[PropertyHandler.PropertyHandler] Property handler called on " + propertyFileString);

        propertyFile = getPropertyFile(propertyFileString);

        InputStream is = new BufferedInputStream(new FileInputStream(propertyFile));
        this.load(is);
        is.close();
    }

    /**
     * constructor
     */
    public PropertyHandler(String propertyFileString)
            throws FileNotFoundException, IOException {
        this(null, propertyFileString);
    }

    /**
     * This is the important method reading the properties.
     */
    public String getProperty(String key) {
        // System.out.print("PropertyHandler.getProperty(\"" + key + "\")");
        String value = super.getProperty(key);
		// System.out.println(" --> " + value);

        return value;
    }

    /**
     * Looks for a valid properties file ...
     */
    private File getPropertyFile(String propertyFileString)
            throws FileNotFoundException, IOException {

        // ... in the current directory
        if ((propertyFile = new File(propertyFileString)).exists()) {
            return propertyFile;
        }

        // ... in the directory, where the program was started
        String dirString = System.getProperty("user.dir");
        String completeString = dirString + File.separator + propertyFileString;
        if ((propertyFile = new File(completeString)).exists()) {
            return propertyFile;
        }

        // ... in Home-directory of the user
        dirString = System.getProperty("user.home");
        completeString = dirString + File.separator + propertyFileString;
        if ((propertyFile = new File(completeString)).exists()) {
            return propertyFile;
        }

        // ... in the directory where Java keeps its own property files
        dirString = System.getProperty("java.home") + File.separator + "lib";
        completeString = dirString + File.separator + propertyFileString;
        if ((propertyFile = new File(completeString)).exists()) {
            return propertyFile;
        }

        // ... in all directories specified by the CLASSPATH
        String[] classPathes = FileUtils.getClassPathes();
        for (int i = 0; i < classPathes.length; i++) {
            completeString = classPathes[i] + File.separator + propertyFileString;
            if ((propertyFile = new File(completeString)).exists()) {
                return propertyFile;
            }
        }

        throw new FileNotFoundException("[PropertyHandler.PropertyHandler]Configuration file \"" + propertyFileString + "\" not found!");
    }
}
