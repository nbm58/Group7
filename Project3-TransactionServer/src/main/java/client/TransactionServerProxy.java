package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import message.*;


public class TransactionServerProxy implements MessageTypes, Runnable
{
    int transactionNumber;
    String serverIP;
    int serverPort;
    Socket serverConnection;
    
    public TransactionServerProxy(int transactionNumber, String serverIP, int serverPort)
    {
        this.transactionNumber = transactionNumber;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }
    
    void openTransaction()
    {
        ObjectOutputStream writeToNet;
        ObjectInputStream readFromNet;

        try
        {
            // Open connection to server
            serverConnection = new Socket(serverIP, serverPort);

            // Open object streams
            writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());
            readFromNet = new ObjectInputStream(serverConnection.getInputStream());

            // Send OPEN_TRANSACTION message
            writeToNet.writeObject(new Message(OPEN_TRANSACTION));
        }
        catch (IOException ex)
        {
            // Log failure to connect
            System.err.println("[TSP] Error connecting to server, creating object streams, or closing connection: " + ex);
        }
    }
    
    void closeTransaction(int transactionID)
    {
        try
        {
            // close connection
            serverConnection.close();
        }
        catch (IOException ex)
        {
            System.err.println("[TSP] Error closing connection to server: " + ex);
        }
    }
    
    @Override
    public void run()
    {
        openTransaction();
    }
}
