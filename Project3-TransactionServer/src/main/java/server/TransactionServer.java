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
    static ServerSocket clientConnection = null;
    
    String myIP;
    int myPort;
    int numberOfAccounts;
    int startingBalance;
    
    public TransactionServer(String serverPropertiesFile)
    {
        transactionManager = new TransactionManager();
        
        Properties serverProperties = null;
        try
        {
            serverProperties = new PropertyHandler(serverPropertiesFile);
        }
        catch (IOException ex)
        {
            System.err.println("[TS] Could not open server properties file" + ex);
            System.exit(1);
        }
        
        // get number of accounts
        numberOfAccounts = 0;
        try
        {
            numberOfAccounts = Integer.parseInt(serverProperties.getProperty("NUMBER_OF_ACCOUNTS"));
        }
        catch (NumberFormatException ex)
        {
            System.err.println("[TS] Error getting number of accounts: " + ex);
            System.exit(1);
        }
        
        // get starting balance
        startingBalance = 0;
        try
        {
            startingBalance = Integer.parseInt(serverProperties.getProperty("STARTING_BALANCE"));
        }
        catch (NumberFormatException ex)
        {
            System.err.println("[TS] Error getting starting balance: " + ex);
            System.exit(1);
        }
        
        accountManager = new AccountManager(numberOfAccounts, startingBalance);
        
        // get my ip
        myIP = serverProperties.getProperty("SERVER_IP");
        if (myIP == null)
        {
            System.err.println("[TC] Error getting server IP: ");
            System.exit(1);
        }
        
        // get my port
        myPort = 0;
        try
        {
            myPort = Integer.parseInt(serverProperties.getProperty("SERVER_PORT"));
        }
        catch (NumberFormatException ex)
        {
            System.err.println("[TC] Error getting server port: " + ex);
            System.exit(1);
        }
        
        try
        {
            clientConnection = new ServerSocket(myPort);
            System.out.println("[TS] Socket created, listening on port " + myPort);
        }
        catch (IOException ex)
        {
            // Log failure to create socket
            System.out.println("[TS] Failure to create socket: " + ex);
        }
        
        System.out.println("[TS] Listening on " + myIP + ":" + myPort);
    }
    
    @Override
    public void run()
    {
        System.out.println("[TS] Run");
        
        // run server loop
        while (keepgoing)
        {
            try
            {
                transactionManager.runTransaction(clientConnection.accept());
            }
            catch (IOException e)
            {
                // Log failure to create Socket Thread Object "error accepting client"
                System.err.println("[TS] Error accepting client" + e);
            }
        }
    }
    
    public static void main(String[] args)
    {
        String serverPropertiesFile = null;
        try
        {
            serverPropertiesFile = args[0];
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            serverPropertiesFile = "TransactionServer.properties";
        }
        (new TransactionServer(serverPropertiesFile)).run();
    }
}
