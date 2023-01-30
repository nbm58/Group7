package chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ReceiverWorker extends Thread
{
    ObjectInputStream readFromNet;
    ObjectOutputStream writeToNet;
    Message message = null;
    public ReceiverWorker(Socket serverConnection)
    {
        try
        {
            readFromNet = new ObjectInputStream(serverConnection.getInputStream());
            writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());
        }
        catch(IOException ex)
        {
            //Log Failure to open object streams.
        }
    }

    @Override
    public void run()
    {
        try {
            //read message
            message = (Message)readFromNet.readObject();
        }
        catch (IOException | ClassNotFoundException ex)
        {
            Logger.getLogger(ReceiverWorker.class.getName()).log(Level.SEVERE, "[ReceiverWorker.run] Error reading message from server", ex);
            
            System.exit(1);
        }
 
        //decide what to do depending on the type of message received.
        switch (message.getType())
        {
            case SHUTDOWN:
                System.out.println("Received shutdown message from server, exiting");

                try
                {
                    serverConnection.close();
                }
                catch (IOException ex)
                {
                    //Log Failure to close connection
                }

                System.exit(0);

                break;

            case NOTE:
                //display message
                System.out.println((String) message.getContent());

                try
                {
                    serverConnection.close();
                }
                catch (IOException ex)
                {
                    Logger.getLogger(ReceiverWorker.class.getName*()).log(Level.SEVERE, "[ReceiverWorker.run] Error closing connection", ex);
                }

            default:
                // cannot occur
        }
    }   
}
