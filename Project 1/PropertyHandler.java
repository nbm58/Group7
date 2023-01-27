package utils;

import java.util.*;
import java.io.*;


/**
 * class [PropertyHandler]
 *
 * This is a utility class reading properties data contained in some file
 */
public class PropertyHandler extends Properties {

    File propertyFile = null;

    /**
     * Constructor
     * 
     * @param propertyFileString File name containing properties on a relative path
     * 
     * @throws java.io.FileNotFoundException
     */
    public PropertyHandler(String propertyFileString) throws FileNotFoundException, IOException {

        propertyFile = getPropertyFile(propertyFileString);

        InputStream is = new BufferedInputStream(new FileInputStream(propertyFile));
        this.load(is);
        is.close();
    }

    
    /**
     * Important method reading the properties.
     */
    @Override
    public String getProperty(String key) {
        String value = super.getProperty(key);

        return value;
    }

    
    /**
     * Private method, looking for a valid properties file in different directories
     */
    private File getPropertyFile(String propertyFileString)
            throws FileNotFoundException, IOException {

        // ... in the current directory
        if ((propertyFile = new File(propertyFileString)).exists()) {    
            //System.out.println(new File(".").getAbsolutePath());
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

        throw new FileNotFoundException("[PropertyHandler.PropertyHandler] Configuration file \"" + propertyFileString + "\" not found!");
    }
}
