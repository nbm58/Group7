package chat;

import java.util.Random;

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
        Random random = new Random();
        int max = 999999;
        int randomInteger = random.nextInt(max);
        String randIntString = Integer.toString(randomInteger);
        
        int myPort = 0;
        String myName = "User" + randIntString;
        String myIP = NetworkUtilities.getMyIP();
        
        // create my own node info
        myNodeInfo = new NodeInfo(myIP, myPort, myName);
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