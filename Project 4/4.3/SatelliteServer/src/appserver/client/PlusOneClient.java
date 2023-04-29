package appserver.client;

import appserver.comm.Message;
import appserver.comm.MessageTypes;
import appserver.job.Job;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;

import utils.PropertyHandler;

/**
 * Class [PlusOneClient] A primitive POC client that uses the PlusOne tool
 * 
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public class PlusOneClient implements MessageTypes{
    
    String host = null;
    int port;

    Properties properties;

    public PlusOneClient(String serverPropertiesFile) {
        try {
            properties = new PropertyHandler(serverPropertiesFile);
            host = properties.getProperty("HOST");
            System.out.println("[PlusOneClient.PlusOneClient] Host: " + host);
            port = Integer.parseInt(properties.getProperty("PORT"));
            System.out.println("[PlusOneClient.PlusOneClient] Port: " + port);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void run() {
        try { 
            // connect to application server
            Socket server = new Socket(host, port);
            
            // hard-coded string of class, aka tool name ... plus one argument
            String classString = "appserver.job.impl.PlusOne";
            Integer number = new Integer(42);
            
            // create job and job request message
            Job job = new Job(classString, number);
            Message message = new Message(JOB_REQUEST, job);
            
            // sending job out to the application server in a message
            ObjectOutputStream writeToNet = new ObjectOutputStream(server.getOutputStream());
            writeToNet.writeObject(message);
            
            // reading result back in from application server
            // for simplicity, the result is not encapsulated in a message
            ObjectInputStream readFromNet = new ObjectInputStream(server.getInputStream());
            Integer result = (Integer) readFromNet.readObject();
            System.out.println("RESULT: " + result);
        } catch (Exception ex) {
            System.err.println("[PlusOneClient.run] Error occurred");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        
        PlusOneClient client = null;
        if(args.length == 1) {
            client = new PlusOneClient(args[0]);
        } else {
            client = new PlusOneClient("../../config/Server.properties");
        }
        client.run();
    }  
}
