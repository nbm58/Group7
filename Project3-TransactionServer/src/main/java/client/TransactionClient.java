package client;

import utils.*;
import java.util.*;
import java.io.*;

public class TransactionClient implements Runnable
{
    PropertyHandler properties;
    int numberOfTransactions;
    int startingBalance;
    
    public TransactionClient(String propertiesFile)
    {
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
        
        // get number of transactions
        numberOfTransactions = 0;
        try
        {
            numberOfTransactions = Integer.parseInt(properties.getProperty("NUMBER_OF_TRANSACTIONS"));
        }
        catch (NumberFormatException ex)
        {
            System.err.println("Error getting number of transactions: " + ex);
            System.exit(1);
        }
        
        // get number of transactions
        startingBalance = 0;
        try
        {
            startingBalance = Integer.parseInt(properties.getProperty("STARTING_BALANCE"));
        }
        catch (NumberFormatException ex)
        {
            System.err.println("Error getting starting balance: " + ex);
            System.exit(1);
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
