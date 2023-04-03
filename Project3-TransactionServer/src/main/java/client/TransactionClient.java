package client;

import utils.*;
import java.util.*;
import java.io.*;

import message.*;

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
            System.err.println("Could not open client properties file" + ex);
            System.exit(1);
        }
        
        try
        {
            serverProperties = new PropertyHandler(serverPropertiesFile);
        }
        catch (IOException ex)
        {
            System.err.println("Could not open server properties file" + ex);
            System.exit(1);
        }
        
        // get server ip
        serverIP = serverProperties.getProperty("SERVER_IP");
        if (serverIP == null)
        {
            System.err.println("Error getting server IP: ");
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
            System.err.println("Error getting server port: " + ex);
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
            System.err.println("Error getting number of transactions: " + ex);
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
            System.err.println("Error getting restart transactions flag: " + ex);
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
            System.err.println("Error getting number of accounts: " + ex);
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
            System.err.println("Error getting starting balance: " + ex);
            System.exit(1);
        }
        
        accountFrom = (int) (Math.floor(Math.random() * startingBalance));
        accountTo = (int) (Math.floor(Math.random() * startingBalance));
    }
    
    void openTransaction(int transactionID)
    {
        
    }
    
    void closeTransaction(int transactionID)
    {
        
    }
    
    public class TransactionThread extends Thread implements MessageTypes, Runnable
    {
        
        @Override
        public void run()
        {
            (new TransactionServerProxy()).run();
        }
    }
    
    @Override
    public void run()
    {
        System.out.println("[TC] Run");
        
        for (int index = 0; index < numberOfTransactions; index++)
        {
            (new TransactionServerProxy(index)).start();
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
        (new TransactionClient(propertiesFile)).run();
    }
}
