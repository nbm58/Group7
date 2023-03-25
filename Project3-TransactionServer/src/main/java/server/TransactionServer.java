package server;

import java.util.*;
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

import utils.*;
import account.*;
import transaction.*;

public class TransactionServer implements Runnable
{
    public static AccountManager accountManager;
    public static TransactionManager transactionManager;
    private boolean keepgoing = true;
    ServerSocket clientConnection;
    
    public TransactionServer(String propertiesFile)
    {
        accountManager = new AccountManager();
        transactionManager = new TransactionManager();
        
        Properties properties = null;
        try
        {
            properties = new PropertyHandler(propertiesFile);
        }
        catch (IOException ex)
        {
            System.err.println("Could not open properties file" + ex);
            System.exit(1);
        }
        
        try
        {
            clientConnection = new ServerSocket(0);
            System.out.println("[TS] Socket created, listening on port " + clientConnection.getLocalPort());
        }
        catch (IOException ex)
        {
            // Log failure to create socket
            System.out.println("[TS] Failure to create socket: " + ex);  
        }
    }
    
    void openTransaction(int transactionID)
    {
        
    }
    
    void closeTransaction(int transactionID)
    {
        
    }
    
    @Override
    public void run()
    {
        System.out.println("[TC] Run");
        
        // run server loop
        while (keepgoing)
        {
            try
            {
                TransactionManager.runTransaction(clientConnection.accept());
            }
            catch (IOException e)
            {
                // Log failure to create Socket Thread Object "error accepting client"
                System.err.println("{RECEIVER} Error accepting client" + e);
            }
        }
    }
    
    public static void main(String[] args)
    {
        String propertiesFile = null;
        try
        {
            propertiesFile = args[0];
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            propertiesFile = "TransactionClientDefaults.properties";
        }
        (new TransactionServer(propertiesFile)).run();
    }
}
