
package utils;

import java.io.File;
import java.util.StringTokenizer;



/**
 * Klasse [FileUtils]
 * <p>
 * Utilities um Files, Directories, Pfade usw.
 *
 * @author Prof. Dr.-Ing. Wolf-Dieter Otte
 * @version Feb. 2000
 */
public class FileUtils{


/** 
 * Diese Methode setzt Pfadbezeichnungen bzw. Packagebezeichnungen in 
 * plattformabhaengig korrekte Pfadbezeichnungen um. So wuede z.B.
 * eine in der Unixwelt uebliche Pfadbezeichnung "/foo/boo" fuer Windows
 * umgesetzt werden in "\foo\boo" (der "wrongChar" waere hier mit '/' anzugeben).
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
 * Diese Methode liest den CLASSPATH-String ein und spaltet ihn
 * in ein String-Feld einzelner Pfade auf.
 */
	public static String[] getClassPathes(){
		String[] classPathes = new String[1];
		String classPath = System.getProperty("java.class.path");
		//System.err.println("Klassenpfade: " + classPath);

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
			//System.err.println("piep!");
			System.arraycopy(oldClassPathes, 0, classPathes, 0, count);
		}
		// Schrumpfen um eins
		oldClassPathes = classPathes;
		classPathes = new String[count];
		//System.err.println("piep!");
		System.arraycopy(oldClassPathes, 0, classPathes, 0, count);
		
		return classPathes;
	}
}