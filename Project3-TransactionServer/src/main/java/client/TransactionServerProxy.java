package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import message.*;


//From the client perspective, an object of this class IS the transaction.
//TSP "api" is considered Read/Write/Open/Close
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
    
    // OPEN_TRANSACTION, returns a transactionID.
    // creating a transactionserverproxy "object", that represents the
    // transaction
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
        
        System.out.println("[TS] OPEN received, returning transactionID");

        return transactionID;
    }
    
    //Should return either TRANSACTION_COMMITTED or TRANSACTION_ABORTED
    public int closeTransaction(int transactionID)
    {
     int returnStatus = TRANSACTION_COMMITTED;

        try
        {
         writeToNet.writeObject(new(Message(CLOSE_TRANSACTION));
         returnStatus = (Integer) readFromNet.readObject();
         readFromNet.close();
         writeToNet.close();
         serverConnection.close();
        }
        catch (IOException ex)
        {
         System.err.println("[TSP] Error closing connection to server: " + ex);
         ex.printStackTrace();
        }

        return returnStatus;
    }


    /*
     * Read is translated into a message and broadcast over a network
     * returns the response
     */
    public void read(int accountNumber) //throws TransactionAbortedException
    {
     Message message = new Message(READ_REQUEST, accountNumber);

     try
     {
      writeToNet.writeObject(message);
      message = (Message) readFromNet.readObject();
     }
     catch(Exception ex)
     {
      System.out.println("TransactionServerProxy.read] Error occured");
      ex.printStackTrace();  
     }

     if(message.getType() == READ_REQUEST_RESPONSE)
     {
        return (Integer) message.getContent();
     }
     else
     {
        System.out.println("[TSP] Transaction Aborted Exception Thrown"); 
      //throw new TransactionAbortedException();      
     }
    }

    public void write(int accountNumber, int amount) //throws TransactionAbortedException
    {
     Object[] content = new Object[]{accountNumber, amount};
     Message message = new Message(WRITE_REQUEST,content);

     try
     {
      writeToNet.writeObject(message);
      message = (Message) readFromNet.readObject();
     }
     catch (IOException | ClassNotFoundException ex)
     {
      System.out.println("[TransactionServerProxy.write] Error occured: IOException | ClassNotFoundException");
      ex.printStackTrace();
      System.err.print("\n\n");
     }

     if(message.getType() == TRANSACTION_ABORTED)
     {
       System.out.println("[TSP] Transaction Aborted Exception Thrown"); 
      //throw new TransactionAbortedException();
     }

    }
    
    @Override
    public void run()
    {
        openTransaction();
    }
}
