package utils;

import java.io.File;
import java.util.StringTokenizer;



/**
 * Class [FileUtils]
 * <p>
 * Utilities for handling files, directories, pathes and so on ...
 *
 * @author Prof. Dr.-Ing. Wolf-Dieter Otte
 * @version Feb. 2000
 */
public class FileUtils{


/** 
 * This method translates paths and packet names into corresponding platform dependent paths:
 * e.g. a path "/foo/boo" in the UNIX world would result in a path 
 * "\foo\boo" in the twisted Windoofs pseudo world. The "wrongChar" would be '\'.
 */
	public static String getProperPathString(String pathString, char wrongChar){
		
		StringBuffer pathStringBuffer = new StringBuffer(pathString);
		
		int index  = 0;
		int offset = -1;
		while((index = pathString.indexOf(wrongChar, offset + 1)) != -1){
			pathStringBuffer.setCharAt(index, File.separatorChar);	
			offset = index;
		}
		//System.out.println("getPath(\"" + pathString + "\") --> " + pathStringBuffer);
		return pathStringBuffer.toString();
	}
	
	
/** 
 * This method reads the value of the CLASSPATH-variable
 * and splits it into an array of Strings each representing one path..
 */
	public static String[] getClassPathes(){
		String[] classPathes = new String[1];
		String classPath = System.getProperty("java.class.path");
		System.err.println("Classpathes: " + classPath);

		StringTokenizer tokenizer = new StringTokenizer(classPath, File.pathSeparator);

		int count = 0;
		String[] oldClassPathes;
		String token;
		while(tokenizer.hasMoreTokens()){
			token = tokenizer.nextToken();
			if(token.endsWith(File.separator))
				token = token.substring(0, token.length()-1);
			classPathes[count] = token;
			oldClassPathes = classPathes;
			classPathes = new String[++count + 1];
			System.arraycopy(oldClassPathes, 0, classPathes, 0, count);
		}
		// shrink by one
		oldClassPathes = classPathes;
		classPathes = new String[count];
		System.arraycopy(oldClassPathes, 0, classPathes, 0, count);
		
		return classPathes;
	}
}