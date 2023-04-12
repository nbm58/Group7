package client;

import utils.*;
import java.util.*;
import java.io.*;

import message.*;


// client will create a tsp object and call its r/w, than close it.
// i think this temporary object is the "middleware"
public class TransactionClient implements Runnable
{
    PropertyHandler properties;
    String serverIP;
    int serverPort;
    int numberOfTransactions;
    boolean restartTransactions;
    int numberOfAccounts;
    int startingBalance;
    int accountFrom;
    int accountTo;
    
    public TransactionClient(String clientPropertiesFile, String serverPropertiesFile)
    {
        Properties clientProperties = null;
        Properties serverProperties = null;
        
        try
        {
            clientProperties = new PropertyHandler(clientPropertiesFile);
        }
        catch (IOException ex)
        {
            System.err.println("[TC] Could not open client properties file" + ex);
            System.exit(1);
        }
        
        try
        {
            serverProperties = new PropertyHandler(serverPropertiesFile);
        }
        catch (IOException ex)
        {
            System.err.println("[TC] Could not open server properties file" + ex);
            System.exit(1);
        }
        
        // get server ip
        serverIP = serverProperties.getProperty("SERVER_IP");
        if (serverIP == null)
        {
            System.err.println("[TC] Error getting server IP: ");
            System.exit(1);
        }
        
        // get server port
        serverPort = 0;
        try
        {
            serverPort = Integer.parseInt(serverProperties.getProperty("SERVER_PORT"));
        }
        catch (NumberFormatException ex)
        {
            System.err.println("[TC] Error getting server port: " + ex);
            System.exit(1);
        }
        
        // get number of transactions
        numberOfTransactions = 0;
        try
        {
            numberOfTransactions = Integer.parseInt(clientProperties.getProperty("NUMBER_OF_TRANSACTIONS"));
        }
        catch (NumberFormatException ex)
        {
            System.err.println("[TC] Error getting number of transactions: " + ex);
            System.exit(1);
        }
        
        // get restart transactions flag
        restartTransactions = true;
        try
        {
            restartTransactions = Boolean.parseBoolean(clientProperties.getProperty("RESTART_TRANSACTIONS"));
        }
        catch (NumberFormatException ex)
        {
            System.err.println("[TC] Error getting restart transactions flag: " + ex);
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
            System.err.println("[TC] Error getting number of accounts: " + ex);
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
            System.err.println("[TC] Error getting starting balance: " + ex);
            System.exit(1);
        }
        
        accountFrom = (int) (Math.floor(Math.random() * startingBalance));
        accountTo = (int) (Math.floor(Math.random() * startingBalance));
    }
    
    public class TransactionThread extends Thread implements MessageTypes, Runnable
    {
        int transactionNumber;
        
        public TransactionThread(int transactionNumber)
        {
            this.transactionNumber = transactionNumber;
        }
        
        @Override
        public void run()
        {
            System.out.println("[TC] Sending transactionThread to TransactionServerProxy");
            (new TransactionServerProxy(transactionNumber, serverIP, serverPort)).run();
        }
    }
    
    @Override
    public void run()
    {
        System.out.println("[TC] Running TransactionThread");
        
        for (int index = 0; index < numberOfTransactions; index++)
        {
            //create a new TransactionThread for 0 -> numberofTransactions
            (new TransactionThread(index)).start();
            
        }
        
        
    }
    
    public static void main(String[] args)
    {
        String clientPropertiesFile = null;
        String serverPropertiesFile = null;
        
        try
        {
            clientPropertiesFile = args[0];
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            clientPropertiesFile = "TransactionClient.properties";
        }
        
        try
        {
            serverPropertiesFile = args[1];
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            serverPropertiesFile = "TransactionServer.properties";
        }
        System.out.println("[TC] creating connection..");
        (new TransactionClient(clientPropertiesFile, serverPropertiesFile)).run();
    }
}
