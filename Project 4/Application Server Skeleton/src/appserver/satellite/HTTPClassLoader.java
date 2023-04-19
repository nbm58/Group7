package appserver.satellite;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Class [HTTPClassLoader] Instances of this class can load class files from web servers
 *
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public class HTTPClassLoader extends ClassLoader {

    String host;
    int port;
    String classRootDir;

    /**
     * Common Constructor
     */
    public HTTPClassLoader(String host, int port) {
        super();
        this.host = host;
        this.port = port;
    }

    /**
     * Constructor for test purposes on "well-known" port
     */
    public HTTPClassLoader() {
        this("localhost", 23657);
    }

    /**
     * Overrides corresponding method in superclass <code>ClassLoader</code>
     */
    public Class findClass(String className) throws ClassNotFoundException {
        byte[] classData = loadClassData(className);

        Class classObject = defineClass(className, classData, 0, classData.length);
        System.out.println("Class object \"" + className + "\" created");
        return classObject;
    }

    /**
     * Auxiliary method for <code>findClass()</code>. Provides for loading of
     * the bytes of a class file from a web server
     */
    private byte[] loadClassData(String className) throws ClassNotFoundException {

        DataInputStream readFromNet = null;
        PrintStream writeToNet = null;

        // aux fields for processing HTTP-header
        byte[] protocolHeaderLine = new byte[256];
        String inputLine;

        // aux fields for reading bytes of class file
        byte[] classData = null;
        int bytesRead = 0;
        int offset = 0;
        int blockSize = 128;

        try {
            Socket classDataSocket = new Socket(host, port);

            readFromNet = new DataInputStream(classDataSocket.getInputStream());
            writeToNet = new PrintStream(classDataSocket.getOutputStream());

            // process resource string
            String classPath = className.replace('.', '/') + ".class";

            String stringToNet = "GET " + classPath + " HTTP/1.0 \r\n\r\n";
            System.err.println("\nRequest to Server: \n\"GET " + classPath + " HTTP/1.0\"");

            //  ... requesting class file ...
            writeToNet.print(stringToNet);
            writeToNet.flush();

	    // **************************************************************************************************
            // process header information ...
            System.err.println("\nServer responds:");
            int i = -1;
            while ((protocolHeaderLine[++i] = readFromNet.readByte()) != (byte) '\n') {
                ; // empty loop
            }
            i--;
            inputLine = new String(protocolHeaderLine, 0, i);
            System.err.println(inputLine);

            StringTokenizer tokenizer = new StringTokenizer(inputLine);
            tokenizer.nextToken();
            String returnCode = tokenizer.nextToken();

            if (returnCode.equals("200")) {
                // skip the rest
                while (true) {
                    i = -1;
                    while ((protocolHeaderLine[++i] = readFromNet.readByte()) != (byte) '\n') {
                        ; // empty loop
                    }
                    i--;
                    inputLine = new String(protocolHeaderLine, 0, i);
                    System.err.println(inputLine);

                    // look for empty line, there is the beginning of the class file data
                    if (inputLine.trim().equals("")) {
                        break; // ... this is the beginning
                    }
                }
            } else {
                throw new ClassNotFoundException("Class file not found");
            }

	    // **************************************************************************************************
            // read the bytes of the class file ...
            classData = new byte[blockSize];

            while (true) {
                bytesRead = readFromNet.read(classData, offset, blockSize);

				// End-of-Stream reached
                // is EOS reached immediately, the file is empty or not there
                if (bytesRead == -1) {
                    // EOS or no data
                    break;
                }

                offset += bytesRead;

                // enlarge field, if necessary
                if (offset + blockSize >= classData.length) {
                    byte[] temp = new byte[classData.length * 2];
                    System.arraycopy(classData, 0, temp, 0, offset);
                    classData = temp;
                }
            }

            // cut field to proper size
            if (offset < classData.length) {
                byte[] temp = new byte[offset];
                System.arraycopy(classData, 0, temp, 0, offset);
                classData = temp;
            }
        } catch (IOException ioe) {
            throw new ClassNotFoundException(ioe.toString());
        }

        // class file data there?
        if (classData.length == 0) {
            throw new ClassNotFoundException("No class file present or class file empty");
        }

        System.err.println("Bytes Class \"" + className + "\" loaded");

        return classData;
    }
}
