package chat;

import java.io.IOException;

import utils.PropertyHandler;
import java.util.Properties;

import java.util.ArrayList;

import utils.*;

public class ChatClient implements Runnable
{
    static Receiver receiver = null;
    static Sender sender = null;
    
    public static NodeInfo myNodeInfo = null;
    
    public static ArrayList<NodeInfo> participants = new ArrayList<NodeInfo>();
    
    public ChatClient(String propertiesFile)
    {
        int myPort = 0;

        // get properties from properties file
        Properties properties = null;
        try
        {
            properties = new PropertyHandler(propertiesFile);
        }
        catch (IOException ex)
        {
            System.err.println("Failed to get properties " + ex);
            System.exit(1);
        }

        // get my name
        String myName = properties.getProperty("MY_NAME");
        if (myName == null)
        {
            System.out.println("Failure getting name");
            System.exit(1);
        }
            
        // create my own node info
        myNodeInfo = new NodeInfo(NetworkUtilities.getMyIP(), myPort, myName);
    }
    
    @Override
    public void run() 
    {
        System.out.println("{CLIENT} Run");

        // receiver start
        (receiver = new Receiver()).start();

        // sender start
        (sender = new Sender()).start();
    }
    
    // main
    public static void main(String[] args)
    {
        String propertiesFile = null;
        try
        {
            propertiesFile = args[0];
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            propertiesFile = "ChatNodeDefaults.properties";
        }
        System.out.println("{CLIENT} Boot");
        (new ChatClient(propertiesFile)).run();
    }
}