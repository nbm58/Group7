package chat;

import java.io.IOException;

import utils.PropertyHandler;
import java.util.Properties;

import java.util.logging.Level;
import java.util.logging.Logger;
import utils.NetworkUtilities;


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
        catch (IOException ex)
        {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, "Could not open properties file", ex);
            System.exit(1);
        }
                
        //get my receiver port number
        int myPort = 0;
        try
        {
            myPort = Integer.parseInt(properties.getProperty("MY_PORT"));
        }
        catch (NumberFormatException ex)
        {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, "Could not read receiver port", ex);
            System.exit(1);
        }
            
        //Get my name
        String myName = properties.getProperty("MY_NAME");
        if (myName == null)
        {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, "Could not read my name");
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
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, "Could not read server port", ex);
        }
        
        //Get server default IP
        String serverIP = null;
        serverIP = properties.getProperty("SERVER_IP");
        if(serverIP == null)
        {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, "Could not read server IP");
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
            propertiesFile = "config/ChatNodeDefault.properties";
        }
        
        (new ChatClient(propertiesFile)).run();
    }
    
}