package chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import utils.*;

public class ReceiverWorker extends Thread implements MessageTypes
{
    Socket serverConnection = null;
    ObjectInputStream readFromNet;
    ObjectOutputStream writeToNet;
    Message message = null;


    public ReceiverWorker(Socket serverConnection)
    {
        System.out.println("{RECEIVER} Server Message Incoming!");
        this.serverConnection = serverConnection;
        try
        {
            readFromNet = new ObjectInputStream(serverConnection.getInputStream());
            writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());
        }
        catch(IOException  | NullPointerException ex)
        {
            // Log Failure to open object streams.
            System.err.println("Failure to open socket / object streams");
        }
    }

    @Override
    public void run()
    {
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

        // Decide what to do depending on the type of message received.
        switch(message.getType())
        {
            case SHUTDOWN:
                System.out.println("Shutdown request received, exiting");
                
                try
                {
                    serverConnection.close();
                }
                catch (IOException ex)
                {
                    // Do nothing, we are closing
                }
                
                System.exit(0);
                
                break;

                
            case NOTE:
                // Display note
                System.out.println((String) message.getContent());
                try {
                    serverConnection.close();
                }
                catch (IOException ex)
                {
                    // Do nothing, we are closing
                }
                
                break;
                
            default:
                // cannot occur
        }
    }
}
