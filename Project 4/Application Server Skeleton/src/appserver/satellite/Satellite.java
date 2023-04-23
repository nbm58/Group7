package appserver.satellite;

import appserver.job.Job;
import appserver.comm.ConnectivityInfo;
import appserver.job.UnknownToolException;
import appserver.comm.Message;
import static appserver.comm.MessageTypes.JOB_REQUEST;
import static appserver.comm.MessageTypes.REGISTER_SATELLITE;
import appserver.job.Tool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.PropertyHandler;

/**
 * Class [Satellite] Instances of this class represent computing nodes that
 * execute jobs by
 * calling the callback method of tool a implementation, loading the tool's code
 * dynamically over a network
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
        try {
            PropertyHandler satelliteProperties = new PropertyHandler(satellitePropertiesFile);
        } catch (IOException e) {
            // log error
            Logger.getLogger(serverPropertiesFile).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }

        // populate satelliteInfo object
        try {
            satelliteInfo.setHost(Integer.parseInt(satellitePropertiesFile.getProperty("HOST")));
            satelliteInfo.setPort(Integer.parseInt(satellitePropertiesFile.getProperty("PORT")));
            satelliteInfo.setName(satellitePropertiesFile.getProperty("NAME"));
        } catch (UnknownHostException ex) {
            Logger.getLogger(Satellite.class.getName()).log(Level.SEVERE, null, ex);
        }

        // read properties of the application server and populate serverInfo object
        // other than satellites, the as doesn't have a human-readable name, so leave it
        // out
        // ...
        try {
            serverInfo.setHost(serverPropertiesFile.getProperty("HOST"));
            serverInfo.setPort(serverPropertiesFile.getProperty("PORT"));
            // serverInfo.setName(serverPropertiesFile.getProperty("NAME"));
        } catch (UnknownHostException ex) {
            Logger.getLogger(Satellite.class.getName()).log(Level.SEVERE, null, ex);
        }

        // read properties of the code server and create class loader
        // -------------------
        // ...
        try {
            classLoader = new HTTPClassLoader(classLoaderPropertiesFile);
        } catch (IOException e) // TODO: not sure if this is the correct exception. change if needed
        {
            Logger.getLogger(classLoaderPropertiesFile).log(Level.SEVERE, null, e);
        }

        // create tools cache
        // -------------------
        // ...
        toolsCache = new Hashtable<>();

    }

    @Override
    public void run() 
    {
     // register this satellite with the SatelliteManager on the server
     // ---------------------------------------------------------------
     // ...

     /* TODO: not needed? From Assignment:
        Also, you won't be able to register with the application server,
        as it is not there. So ignore this part of the skeleton code, i.e. lines
        62-64. 
     */

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
     boolean keepgoing = true;
     while (keepgoing) 
     {
     // take job requests in a server loop
     Socket satelliteRequests = null;
     try 
     {
      satelliteRequests = satelliteSocket.accept();
     }  
     catch (IOException e) 
     {
      Logger.getLogger(Satellite.class.getName()).log(Level.SEVERE, null, e);
     }
     // create new thread to process job request
     SatelliteThread satelliteThread = new SatelliteThread(satelliteRequests, this);
     satelliteThread.start();
     }
    }

    // inner helper class that is instanciated in above server loop and processes
    // single job requests
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
             writeToNet = new ObjectOutputStream(jobRequest.getOutputStream());
             readFromNet = new ObjectInputStream(jobRequest.getInputStream());
             message = (Message) readFromNet.readObject();

            }
            catch(IOException | ClassNotFoundException e)
            {
                Logger.getLogger(Satellite.class.getName()).log(Level.SEVERE, null, e);
            }


            // reading message
            // ...

            switch (message.getType()) {
                case JOB_REQUEST:
                    // processing job request
                    // ...

                    //print message received
                    System.out.println("[SatelliteThread.run] Received job request message");
                    S
                    

                    break;

                default:
                    System.err.println("[SatelliteThread.run] Warning: Message type not implemented");
            }
        }
    }

    /**
     * Aux method to get a tool object, given the fully qualified class string
     * If the tool has been used before, it is returned immediately out of the
     * cache,
     * otherwise it is loaded dynamically
     */
    public Tool getToolObject(String toolClassString)
            throws UnknownToolException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        Tool toolObject = null;

        // ...

        return toolObject;
    }

    public static void main(String[] args) {
        // start the satellite
        Satellite satellite = new Satellite(args[0], args[1], args[2]);
        satellite.run();
    }
}
