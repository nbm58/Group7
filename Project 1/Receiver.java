package chat;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;

import java.util.logging.Level;
import java.util.logging.Logger;


public class Receiver extends Thread
{
    //client-side
    //receives messages from the chat server
    //init receiver ServerSocket
    static ServerSocket receiverSocket = null;
    //init client username
    static String userName = null;

    //Constructor
    public Receiver()
    {
        try
        {
            receiverSocket = new ServerSocket(ChatClient.myNodeInfo.getPort());
            System.out.println("[Receiver.Receiver] reciever socket created, listening on port " + ChatClient.myNodeInfo.getPort());
        }
        catch (IOException ex)
        {
            Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, "Creating receiver socket failed, ex");
        }
        
        //display listening on message, port
        System.out.println(ChatClient.myNodeInfo.getName() + " listening on " + ChatClient.myNodeInfo.getAddress() + ":" +
            ChatClient.myNodeInfo.getPort());
    }

    @Override
    public void run()
    {
        //run server loop
        while(true)
        {
            try
            {
                (new ReceiverWorker(receiverSocket.accept())).start();
            }
            catch (IOException e)
            {
                System.err.println("[Receiver.run] Warning: Error accepting client");
            }
        }
    }    
}
