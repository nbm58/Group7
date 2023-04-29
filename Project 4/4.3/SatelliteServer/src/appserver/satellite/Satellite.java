package appserver.satellite;

import appserver.job.Job;
import appserver.comm.ConnectivityInfo;
import appserver.job.UnknownToolException;
import appserver.comm.Message;
import static appserver.comm.MessageTypes.JOB_REQUEST;
import static appserver.comm.MessageTypes.REGISTER_SATELLITE;
import appserver.job.Tool;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.PropertyHandler;

/**
 * Class [Satellite] Instances of this class represent computing nodes that execute jobs by
 * calling the callback method of tool a implementation, loading the tool's code dynamically over a network
 * or locally from the cache, if a tool got executed before.
 *
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public class Satellite extends Thread {

    private ConnectivityInfo satelliteInfo = new ConnectivityInfo();
    private ConnectivityInfo serverInfo = new ConnectivityInfo();
    private HTTPClassLoader classLoader = null;
    private Hashtable toolsCache = null;

    public Satellite(String satellitePropertiesFile, String classLoaderPropertiesFile, String serverPropertiesFile) {

        // read this satellite's properties and populate satelliteInfo object,
        // which later on will be sent to the server
        // ...
        PropertyHandler satelliteProperties = null;
        try 
        {
            satelliteProperties = new PropertyHandler(satellitePropertiesFile);
        } 
        catch (IOException e) 
        {
            // log error
            Logger.getLogger(serverPropertiesFile).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
        
        if (satelliteProperties != null)
        {
            satelliteInfo.setPort(Integer.parseInt(satelliteProperties.getProperty("PORT")));
            satelliteInfo.setName(satelliteProperties.getProperty("NAME"));
        }
        
        // read properties of the application server and populate serverInfo object
        // other than satellites, the as doesn't have a human-readable name, so leave it out
        // ...
        PropertyHandler serverProperties = null;
        try 
        {
            serverProperties = new PropertyHandler(serverPropertiesFile);
        }
        catch (IOException ex)
        {
            Logger.getLogger(Satellite.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (satelliteProperties != null)
        {
            serverInfo.setHost(serverProperties.getProperty("HOST"));
            serverInfo.setPort(Integer.parseInt(serverProperties.getProperty("PORT")));
            serverInfo.setName(serverProperties.getProperty("NAME"));
        }
        
        // read properties of the code server and create class loader
        // -------------------
        // ...
        PropertyHandler classLoaderProperties = null;
        String host = null;
        String portString = null;
        try 
        {
            classLoaderProperties = new PropertyHandler(classLoaderPropertiesFile);
        } 
        catch (IOException e) 
        {
            // log error
            Logger.getLogger(serverPropertiesFile).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
     
        if (classLoaderProperties != null)
        {
            host = classLoaderProperties.getProperty("HOST");
            portString = classLoaderProperties.getProperty("PORT");
        }

        if ((host != null) && (portString != null)) {
            try
            {
                classLoader = new HTTPClassLoader(host, Integer.parseInt(portString));
            }
            catch (NumberFormatException nfe)
            {
                System.err.println("Wrong Port Number, using Defaults: " + nfe);
            }
        }
        else
        {
            System.err.println("Configuration data incomplete, using Defaults");
        }

        if (classLoader == null) {
            System.err.println("Could not create HTTPClassLoader, exiting ...");
            System.exit(1);
        }
        
        // create tools cache
        // -------------------
        // ...
        toolsCache = new Hashtable<>();
    }

    @Override
    public void run() {

        // register this satellite with the SatelliteManager on the server
        // ---------------------------------------------------------------
        // ...
        
        
        // create server socket
        // ---------------------------------------------------------------
        // ...
        ServerSocket satelliteSocket = null;
        try 
        {
            satelliteSocket = new ServerSocket(satelliteInfo.getPort());
        } 
        catch (IOException e)
        {
            Logger.getLogger(Satellite.class.getName()).log(Level.SEVERE, null, e);
        }
        
        // start taking job requests in a server loop
        // ---------------------------------------------------------------
        // ...
        while (true)
        {
            try 
            {
                (new SatelliteThread(satelliteSocket.accept(), this)).start();
            }  
            catch (IOException e) 
            {
                Logger.getLogger(Satellite.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    // inner helper class that is instanciated in above server loop and processes single job requests
    private class SatelliteThread extends Thread {

        Satellite satellite = null;
        Socket jobRequest = null;
        ObjectInputStream readFromNet = null;
        ObjectOutputStream writeToNet = null;
        Message message = null;

        SatelliteThread(Socket jobRequest, Satellite satellite) {
            this.jobRequest = jobRequest;
            this.satellite = satellite;
        }

        @Override
        public void run() {
            // setting up object streams
            // ...
            try
            {
                readFromNet = new ObjectInputStream(jobRequest.getInputStream());
                writeToNet = new ObjectOutputStream(jobRequest.getOutputStream());
            }
            catch(IOException  | NullPointerException ex)
            {
                // Log Failure to create object streams.
                System.err.println("Failure to open create object streams");
            }
            
            // reading message
            // ...
            try
            {
                // Read message
                message = (Message)readFromNet.readObject();
            }
            catch (IOException | ClassNotFoundException ex)
            {
                // Log Failure to read message
                System.err.println("Failure to read message" + ex);
                System.exit(1);
            }
            
            switch (message.getType()) {
                case JOB_REQUEST:
                    // processing job request
                    // ...
                    try
                    {
                        // get requested operation from job object
                        String operation = ((Job) message.getContent()).getToolName();
                        
                        // get parameters from job object
                        Object number = ((Job) message.getContent()).getParameters();
                        
                        // get operation object and calculate result then send result back to client
                        writeToNet.writeObject(satellite.getToolObject(operation).go(number));
                    }
                    catch (UnknownToolException | ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e)
                    {
                        System.err.println("Error processiong job request: " + e);
                    }
                    
                    break;

                default:
                    System.err.println("[SatelliteThread.run] Warning: Message type not implemented");
            }
        }
    }

    /**
     * Aux method to get a tool object, given the fully qualified class string
     * If the tool has been used before, it is returned immediately out of the cache,
     * otherwise it is loaded dynamically
     */
    synchronized public Tool getToolObject(String toolClassString) throws UnknownToolException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        Tool toolObject = null;

        if ((toolObject = (Tool) toolsCache.get(toolClassString)) == null)
        {
            System.out.println("\nTool's Class: " + toolClassString);
            if (toolClassString == null) 
            {
                throw new UnknownToolException();
            }

            Class<?> toolClass = classLoader.loadClass(toolClassString);
            try
            {
                toolObject = (Tool) toolClass.getDeclaredConstructor().newInstance();
            }
            catch (InvocationTargetException | NoSuchMethodException ex)
            {
                System.err.println("[Satellite] getToolObject() - InvocationTargetException/NoSuchMethodException: " + ex);
            }
            toolsCache.put(toolClassString, toolObject);
        } 
        else 
        {
            System.out.println("Operation: \"" + toolClassString + "\" already in Cache");
        }
        
        return toolObject;
    }

    public static void main(String[] args)
    {
        // start the satellite
        Satellite satellite = new Satellite(args[0], args[1], args[2]);
        satellite.start();
    }
}
