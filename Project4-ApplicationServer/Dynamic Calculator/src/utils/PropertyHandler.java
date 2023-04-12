package utils;

import java.util.*;
import java.io.*;

import utils.FileUtils;

/**
 * Class [PropertyHandler]
 * <p>
 * Auxiliary class for reading configuration data from
 * a configuration file.
 * 
 * @author Prof. Dr.-Ing. Wolf-Dieter Otte
 * @version Feb. 2000
 */
public class PropertyHandler extends Properties{
	
	File propertyFile = null;


/**
 * Constructor
 */
	public PropertyHandler(Properties defaultProperties, String propertyFileString)
				throws FileNotFoundException, IOException{

		super(defaultProperties);
		
		propertyFile = getPropertyFile(propertyFileString);
		
		InputStream is = new BufferedInputStream(new FileInputStream(propertyFile));
		this.load(is);
		is.close();
	}



/**
 * Constructor
 */
	public PropertyHandler(String propertyFileString)
				throws FileNotFoundException, IOException{
		this(null, propertyFileString);
	}
	


/**
 * This important method reads the properties.
 * Had been overridden from the superclass to make debugging possible.
 * For doing that uncomment the two comment lines (dirty but quick).
 */
	public String getProperty(String key){
		// System.out.print("PropertyHandler.getProperty(\"" + key + "\")");
		String value = super.getProperty(key);
		// System.out.println(" --> " + value);
				
		return value;
	}


/**
 * Here the search for a valid property file is done ...
 */
	private File getPropertyFile(String propertyFileString)
				throws FileNotFoundException, IOException{
 

 		// ... in the current directory
 		if ((propertyFile = new File(propertyFileString)).exists())
 			return propertyFile;

 		// ... in the directory, where the programm was started
 		String dirString = System.getProperty("user.dir");
 		String completeString = dirString + File.separator + propertyFileString;
 		if ((propertyFile = new File(completeString)).exists())
 			return propertyFile;

 		// ... in the HOME-directory of the user
 		dirString = System.getProperty("user.home");
 		completeString = dirString + File.separator + propertyFileString;
 		if ((propertyFile = new File(completeString)).exists())
 			return propertyFile;
 			
		// ... the directory, where Java itself keeps its property files
		dirString = System.getProperty("java.home") + File.separator + "lib";
 		completeString = dirString + File.separator + propertyFileString;
 		if ((propertyFile = new File(completeString)).exists())
 			return propertyFile;

		// ... in all pathes specified in the CLASSPATH-variable
		String[] classPathes = FileUtils.getClassPathes();
		for(int i=0; i<classPathes.length; i++){
			completeString = classPathes[i] + File.separator + propertyFileString;
			if ((propertyFile = new File(completeString)).exists())
 				return propertyFile;
		}

		throw new FileNotFoundException("configuration file \"" + propertyFileString + "\" not found!");
	}


/**
 * Method to reread properties. Not implemented.
 */
	private void updatePropertyData(){
	}
}