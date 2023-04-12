package web;

import java.io.*;
import java.net.*;
import java.util.Properties;
import utils.PropertyHandler;

/**
 * Class [GenericServer]
 * <p>
 * An abstract template-class for socket servers.. Concrete servers have to be
 * derived from this class. To do this, the method
 * <code>processConnection()</code>. has to be overridden
 *
 * @author Prof. Dr.-Ing. Wolf-Dieter Otte
 * @version Feb. 2000
 */
public abstract class GenericServer implements Runnable {

    static ServerSocket serverSocket;
    static int port;

    protected Socket socket;

    /**
     * The Constructor
     */
    public GenericServer(String serverPropertiesFile) {
        try {
            // init static variables with properties read 
            Properties properties;
            properties = new PropertyHandler(serverPropertiesFile);
            port = Integer.parseInt(properties.getProperty("PORT"));
        } catch (Exception e) {
            System.err.println("[GenericServer.GenericServer] Properties file " + serverPropertiesFile + " not found, exiting ...");
            System.exit(1);
        }

        this.port = port;
    }

    /**
     * The method <code>run()</code> implements the interface
     * <code>Runnable</code>
     */
    public void run() {
        try {
            serverSocket = new ServerSocket(port);

            while (true) {
                System.out.println("[GenericServer.run] Waiting for connections on Port #" + port);
                socket = serverSocket.accept();
                System.out.println("[GenericServer.run] A connection to a client is established!");
                processConnection(socket);
            }

        } catch (IOException ioe) {
            System.err.println("IOException" + ioe.getMessage());
            ioe.printStackTrace();
        }
    }

    /**
     * The method <code>processConnection()</code> contains the "Intelligence"
     * of servers, i.e. his application specific functionality. This method has
     * to be overridden by concrete servers.
     */
    protected abstract void processConnection(Socket socket);
}
