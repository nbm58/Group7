package web;

import appserver.client.PlusOneClient;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;
import utils.PropertyHandler;

/**
 * Class [SimpleWebServer]
 *
 * A simple web server, which only implements the GET-method.
 *
 * @author Prof. Dr.-Ing. Wolf-Dieter Otte
 */
public class SimpleWebServer extends GenericServer {

    static String documentRoot;
    static String indexfile = "index.html";

    /**
     * The constructor
     */
    public SimpleWebServer(String webServerPropertiesFile) {
        super(webServerPropertiesFile);

        try {
            // init static variables with properties read 
            Properties properties;
            properties = new PropertyHandler(webServerPropertiesFile);
            documentRoot = properties.getProperty("DOC_ROOT");

        } catch (Exception e) {
            System.err.println("Properties file " + webServerPropertiesFile + " not found, exiting ...");
            System.exit(1);
        }
    }

    /**
     * The method <code>processConnection()</code> implements the functionality
     * of the web server.
     */
    protected void processConnection(Socket socket) {
        (new SocketThread(socket)).start();
    }

    /**
     * Inner Class [SocketThread] This thread processes a client (web browser)
     * request. In the meantime the web server can accept other clients.
     */
    class SocketThread extends Thread {

        Socket socket = null;

        BufferedReader readFromNet = null;
        PrintStream writeToNet = null;

        String inputLine;
        String httpMethod;
        StringTokenizer tokenizer;
        String fileString;
        String version;
        String contentType;
        File fileToServe;

        /**
         * The Constructor
         */
        SocketThread(Socket socket) {
            this.socket = socket;
        }

        /**
         * The method <code>run()</code> is the core of the server
         */
        public void run() {
            try {
                writeToNet = new PrintStream(socket.getOutputStream());
                readFromNet = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                inputLine = readFromNet.readLine();
                tokenizer = new StringTokenizer(inputLine);
                httpMethod = tokenizer.nextToken();

                if (httpMethod.equals("GET")) {
                    fileString = tokenizer.nextToken();

                    if (fileString.endsWith("/")) {
                        fileString += indexfile;
                    }
                    contentType = guessContentTypeFromName(fileString);

                    if (tokenizer.hasMoreTokens()) {
                        version = tokenizer.nextToken();
                    }

                    // Skip the rest
                    while ((inputLine = readFromNet.readLine()) != null) {
                        if (inputLine.trim().equals("")) {
                            break;
                        }
                    }

                    try {
                        System.err.println("File to serve: " + "\"" + fileString + "\"");
                        fileToServe = new File(SimpleWebServer.documentRoot, fileString);
                        FileInputStream fis = new FileInputStream(fileToServe);
                        byte[] theData = new byte[(int) fileToServe.length()];

                        fis.read(theData);
                        fis.close();

                        if (version.startsWith("HTTP/")) {
                            // Send a MIME header
                            writeToNet.print("HTTP/1.0 200 OK\r\n");
                            writeToNet.print("Date: " + new Date() + "\r\n");
                            writeToNet.print("Server: WebServer Version Feb 2000\r\n");
                            writeToNet.print("Content-length: " + theData.length + "\r\n");
                            writeToNet.print("Content-type: " + contentType + "\r\n\r\n");
                        }

                        // Send the file
                        writeToNet.write(theData);
                        writeToNet.close();
                        System.err.println("File: " + fileToServe + " sent\n");

                    } catch (IOException e) {
                        // Cannot find the file
                        if (version.startsWith("HTTP/")) {
                            // send a MIME header
                            writeToNet.print("HTTP/1.0 404 File Not Found\r\n");
                            writeToNet.print("Date: " + new Date() + "\r\n");
                            writeToNet.print("Server: MyWebServer Version Feb 2000\r\n");
                            writeToNet.print("Content-type: text/html" + "\r\n\r\n");
                        }
                        writeToNet.println("<HTML><HEAD><TITLE>File Not Found</TITLE></HEAD>");
                        writeToNet.println("<BODY><H1>HTTP Error 404: File Not Found</H1></BODY></HTML>");
                        writeToNet.close();
                        System.err.println("File: " + fileToServe + " not found\n");
                    }
                } else {
                    // Method doesn't equal "GET"
                    if (version.startsWith("HTTP/")) {
                        // send a MIME header
                        writeToNet.print("HTTP/1.0 501 Not Implemented\r\n");
                        writeToNet.print("Date: " + new Date() + "\r\n");
                        writeToNet.print("Server: MyWebServer Version Feb 2000\r\n");
                        writeToNet.print("Content-type: text/html" + "\r\n\r\n");
                    }

                    writeToNet.println("<HTML><HEAD><TITLE>Not Implemented</TITLE></HEAD>");
                    writeToNet.println("<BODY><H1>HTTP Error 501: Not Implemented</H1></BODY></HTML>");
                    writeToNet.close();

                    System.err.println("Method: " + httpMethod + " is not supported\n");

                }
            } catch (IOException e) {
            }

            try {
                socket.close();
            } catch (IOException e) {
            }
        }

        /**
         * The method <code>guessContentTypeFromName()</code> returns the
         * MIME-type of a file, which is guessed from the file's extention.
         */
        public String guessContentTypeFromName(String name) {
            if (name.endsWith(".html") || name.endsWith(".htm")) {
                return "text/html";
            } else if (name.endsWith(".txt") || name.endsWith(".java")) {
                return "text/plain";
            } else if (name.endsWith(".gif")) {
                return "image/gif";
            } else if (name.endsWith(".class")) {
                return "application/octet-stream";
            } else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                return "image/jpeg";
            } else {
                return "text/plain";
            }
        }
    }
    
    
    public static void main(String[] args) {

        //(new Thread(new SimpleWebServer(args[0]))).start();
        
        SimpleWebServer webServer = null;
        if(args.length == 1) {
            webServer = new SimpleWebServer(args[0]);
        } else {
            webServer = new SimpleWebServer("../../config/WebServer.properties");
        }
        
        webServer.run();
    }
}
