package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import message.*;


public class TransactionServerProxy extends Thread implements MessageTypes, Runnable
{
    int transactionNumber;
    Socket serverConnection;
    
    public TransactionServerProxy(int transactionNumber)
    {
        this.transactionNumber = transactionNumber;
    }
    
    @Override
    public void run()
    {
        ObjectOutputStream writeToNet;
        ObjectInputStream readFromNet;

        try
        {
            // Open connection to server
            serverConnection = new Socket(/* serverIP */, /* serverSocket */);

            // Open object streams
            writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());
            readFromNet = new ObjectInputStream(serverConnection.getInputStream());

            // Send OPEN_TRANSACTION message
            writeToNet.writeObject(new Message(OPEN_TRANSACTION));

            // close connection
            serverConnection.close();
        }
        catch (IOException ex)
        {
            // Log failure to connect
            System.err.println("{SENDER} Error connecting to server, creating object streams, or closing connection" + ex);
        }
    }
}
