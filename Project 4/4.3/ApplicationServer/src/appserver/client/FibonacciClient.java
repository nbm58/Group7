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
 *
 * @author Tyler M.
 */
public class FibonacciClient extends Thread implements MessageTypes
{
    String host = null;
    int port;
    int fibonacciNumber;

    Properties properties;

    public FibonacciClient(String serverPropertiesFile, int fibonacciNumber) {
        try
        {
            properties = new PropertyHandler(serverPropertiesFile);
            host = properties.getProperty("HOST");
            System.out.println("[FibonacciClient.FibonacciClient] Host: " + host);
            port = Integer.parseInt(properties.getProperty("PORT"));
            System.out.println("[FibonacciClient.FibonacciClient] Port: " + port);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        
        this.fibonacciNumber = fibonacciNumber;
    }
    
    public void run() {
        try
        { 
            // connect to application server
            Socket server = new Socket(host, port);
            
            // hard-coded string of class, aka tool name ... plus one argument
            String classString = "appserver.job.impl.Fibonacci";
            
            // create job and job request message
            Job job = new Job(classString, fibonacciNumber);
            Message message = new Message(JOB_REQUEST, job);
            
            // sending job out to the application server in a message
            ObjectOutputStream writeToNet = new ObjectOutputStream(server.getOutputStream());
            writeToNet.writeObject(message);
            
            // reading result back in from application server
            // for simplicity, the result is not encapsulated in a message
            ObjectInputStream readFromNet = new ObjectInputStream(server.getInputStream());
            Integer result = (Integer) readFromNet.readObject();
            System.out.println("Fibonacci of " + fibonacciNumber + ": " + result);
            
            // close connection to application server
            server.close();
        }
        catch (Exception ex)
        {
            System.err.println("[FibonacciClient] Error occurred");
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args)
    {
        for (int i = 1; i <= 48; i++)
        {
            (new FibonacciClient("../../config/Server.properties", i)).run();
        }
    }
}
