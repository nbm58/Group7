package appserver.server;

import appserver.comm.Message;
import static appserver.comm.MessageTypes.JOB_REQUEST;
import static appserver.comm.MessageTypes.REGISTER_SATELLITE;
import appserver.comm.ConnectivityInfo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import utils.PropertyHandler;

/**
 *
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public class Server {

    // Singleton objects - there is only one of them. For simplicity, this is not enforced though ...
    static SatelliteManager satelliteManager = null;
    static LoadManager loadManager = null;
    static ServerSocket serverSocket = null;

    public Server(String serverPropertiesFile) {

        // create satellite manager and load manager
        satelliteManager = new SatelliteManager();
        loadManager = new LoadManager();
        
        // read server properties and create server socket
        PropertyHandler serverProperties = null;
        try 
        {
            serverProperties = new PropertyHandler(serverPropertiesFile);
        } 
        catch (IOException e) 
        {
            // log error
            Logger.getLogger(serverPropertiesFile).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
        
        if (serverProperties != null)
        {
            int port = Integer.parseInt(serverProperties.getProperty("PORT"));
            String name = serverProperties.getProperty("NAME");
            
            try 
            {
                serverSocket = new ServerSocket(port);
            } 
            catch (IOException e)
            {
                Logger.getLogger(name).log(Level.SEVERE, null, e);
            }
        }
    }

    public void run() {
        // serve clients in server loop ...
        // when a request comes in, a ServerThread object is spawned
        System.out.println("[Application Server] Run");
        while (true)
        {
            try 
            {
                (new ServerThread(serverSocket.accept())).start();
            }  
            catch (IOException e) 
            {
                System.err.println("[Application Server] Error accepting client connection: " + e);
            }
        }
    }

    // objects of this helper class communicate with satellites or clients
    private class ServerThread extends Thread {

        Socket client = null;
        ObjectInputStream readFromNet = null;
        ObjectOutputStream writeToNet = null;
        Message message = null;

        private ServerThread(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            // set up object streams and read message
            try
            {
                readFromNet = new ObjectInputStream(client.getInputStream());
                writeToNet = new ObjectOutputStream(client.getOutputStream());
            }
            catch(IOException  | NullPointerException ex)
            {
                // Log Failure to create object streams.
                System.err.println("Failure to open create object streams: " + ex);
            }
            
            // reading message
            try
            {
                // Read message
                message = (Message)readFromNet.readObject();
            }
            catch (IOException | ClassNotFoundException ex)
            {
                // Log Failure to read message
                System.err.println("Failure to read message: " + ex);
                System.exit(1);
            }

            
            // process message
            switch (message.getType()) {
                case REGISTER_SATELLITE:
                    // read satellite info
                    ConnectivityInfo satelliteInfo = (ConnectivityInfo) message.getContent();
                    System.out.println("Received registration request from satellite " + satelliteInfo.getName());
                    
                    // register satellite
                    synchronized (Server.satelliteManager) {
                        satelliteManager.registerSatellite(satelliteInfo);
                        System.out.println("Satellite registered to Satellite Manager.");
                    }

                    // add satellite to loadManager
                    synchronized (Server.loadManager) {
                        loadManager.satelliteAdded(satelliteInfo.getName());
                        System.out.println("Satellite added to Load Manager.");
                    }

                    break;

                case JOB_REQUEST:
                    System.err.println("\n[ServerThread.run] Received job request");

                    String satelliteName = null;
                    String satelliteHost = null;
                    int satellitePort = 0;
                    synchronized (Server.loadManager) {
                        // get next satellite from load manager
                        try
                        {
                            satelliteName = loadManager.nextSatellite();
                        }
                        catch (Exception e)
                        {
                            System.err.println("[Application Server] Error getting next satellite: " + e);
                            System.exit(1);
                        }
                        
                        // get connectivity info for next satellite from satellite manager
                        ConnectivityInfo nextSatelliteInfo = satelliteManager.getSatelliteForName(satelliteName);
                        satelliteHost = nextSatelliteInfo.getHost();
                        satellitePort = nextSatelliteInfo.getPort();
                    }
                    
                    // delegate job to next satellite and send result to client
                    System.out.println("Job request being sent to satellite: " + satelliteName);
                    try
                    {
                        // connect to satellite
                        Socket satellite = new Socket(satelliteHost, satellitePort);
                        
                        // open object streams
                        ObjectOutputStream writeToNet2 = new ObjectOutputStream(satellite.getOutputStream());
                        ObjectInputStream readFromNet2 = new ObjectInputStream(satellite.getInputStream());
                        
                        // forward message (as is) to satellite
                        writeToNet2.writeObject(message);
                        
                        // receive result from satellite and
                        Integer result = (Integer) readFromNet2.readObject();
                        System.out.println("Result received from satellite: " + result);
                        System.out.println("Result being sent to client...");
                        
                        // write result back to client
                        writeToNet.writeObject(result);
                        
                        // close connection to satellite
                        satellite.close();
                        
                        // close connection to client
                        client.close();
                    }
                    catch (Exception ex)
                    {
                        System.err.println("[Application Server] Error connecting to satellite: ");
                        ex.printStackTrace();
                    }

                    break;

                default:
                    System.err.println("[ServerThread.run] Warning: Message type not implemented");
            }
        }
    }

    // main()
    public static void main(String[] args) {
        // start the application server
        Server server = null;
        if(args.length == 1) {
            server = new Server(args[0]);
        } else {
            server = new Server("../../config/Server.properties");
        }
        server.run();
    }
}
