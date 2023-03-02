package chat;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;

//receiver can just accept incoming connection from the server, work on it, return back in loop, accept the next one.

public class Receiver extends Thread
{
    static ServerSocket receiverSocket = null;
    static String userName = null;

    // Constructor
    public Receiver()
    {
        try
        {
            receiverSocket = new ServerSocket(0);
            System.out.println("{RECEIVER} Socket created, listening on port " + receiverSocket.getLocalPort());
            
            ChatClient.myNodeInfo.setPort(receiverSocket.getLocalPort());
        }
        catch (IOException ex)
        {
            // Log failure to create socket
            System.out.println("{RECEIVER} Failure to create socket" + ex);  
        }

        // Display listening on message, port
        System.out.println("{RECEIVER} " + ChatClient.myNodeInfo.getName() + " listening on " + ChatClient.myNodeInfo.getAddress() + ":" + receiverSocket.getLocalPort());
    }

    @Override
    public void run()
    {
        // Run server loop
        while(true)
        {
            try
            {
                (new ReceiverWorker(receiverSocket.accept())).start();
            }
            catch (IOException e)
            {
                // Log failure to create Socket Thread Object "error accepting client"
                System.err.println("{RECEIVER} Error accepting client" + e);
            }
        }
    }    
}
