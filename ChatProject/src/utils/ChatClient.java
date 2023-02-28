package utils;

import java.io.IOException;
import java.util.Properties;


public class ChatClient implements Runnable 
{
    static Receiver receiver = null;
    static Sender sender = null;
    
    public static NodeInfo myNodeInfo = null;
    public static NodeInfo serverNodeInfo = null;
    
    public ChatClient(String propertiesFile)
    {
        //Get properties from properties file
        Properties properties = null;
        try
        {
        properties = new PropertyHandler(propertiesFile);
        }
        catch(IOException ex)
        {
            System.out.println("Failure getting properties");
            System.exit(1); 
        }
                
        //get my receiver port number
        int myPort = 0;
        try
        {
            myPort = Integer.parseInt(properties.getProperty("MY_PORT"));
        }
        catch(NumberFormatException ex)
        {
            System.out.println("Failure getting port");
            System.exit(1);
        }
            
        //Get my name
        String myName = properties.getProperty("MY_NAME");
        if (myName == null)
        {
            System.out.println("Failure getting name");
            System.exit(1);
        }
            
        //create my own node info
        myNodeInfo = new NodeInfo(NetworkUtilities.getMyIP(), myPort, myName);
            
        //get server default port
        int serverPort = 0;
        try
        {
            serverPort = Integer.parseInt(properties.getProperty("SERVER_PORT"));
        }
        catch (NumberFormatException ex)
        {
        	System.out.println("Failed to get server port.");
        }
        
        //Get server default IP
        String serverIP = null;
        serverIP = properties.getProperty("SERVER_IP");
        if (serverIP == null)
        {
            System.out.println("Failed to get server IP.");
        }
        
        //create server default connectivity information
        if(serverPort != 0 && serverIP != null)
        {
            serverNodeInfo = new NodeInfo(serverIP, serverPort);
        }
    }
    
    
    @Override
    public void run() 
    {
        System.out.println("{CLIENT} Run");
        
        //receiver start
        (receiver = new Receiver()).start();
        
        //sender start
        (sender = new Sender()).start();
    }
        
    //main
    public static void main(String[] args)
    {
        String propertiesFile = null;
        try
        {
            propertiesFile = args[0];
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            propertiesFile = "db.properties";
        }
        System.out.println("{CLIENT} Boot");
        (new ChatClient(propertiesFile)).run();
    }
}
