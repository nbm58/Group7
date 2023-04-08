package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import message.*;


public class TransactionServerProxy implements MessageTypes, Runnable
{
    int transactionNumber;
    Integer transactionID;
    String serverIP;
    int serverPort;
    Socket serverConnection;
    ObjectOutputStream writeToNet;
    ObjectInputStream readFromNet;
    
    public TransactionServerProxy(int transactionNumber, String serverIP, int serverPort)
    {
        this.transactionNumber = transactionNumber;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }
    
    //opens a transaction, OPEN_TRANSACTION returns a transactionID.
    public int openTransaction()
    {

        try
        {
            // Open connection to server
            serverConnection = new Socket(serverIP, serverPort);

            // Open object streams
            writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());
            readFromNet = new ObjectInputStream(serverConnection.getInputStream());

        }
        catch (IOException ex)
        {
            // Log failure to connect
            System.err.println("[TSP] Error connecting to server, creating object streams, or closing connection: " + ex);
        }

        try
        {
         // Send OPEN_TRANSACTION message
         writeToNet.writeObject(new Message(OPEN_TRANSACTION)); 
         transactionID = (Integer) readFromNet.readObject();

        }
        catch(IOException | ClassNotFoundException | NullPointerException ex)
        {
         System.out.println("[TransactionServerProxy.openTranaction] Error when writing/reading messages");
         ex.printStackTrace();   
        }

        return transactionID;
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
