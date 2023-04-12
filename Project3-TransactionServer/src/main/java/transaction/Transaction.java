package transaction;

import java.util.ArrayList;
import java.util.HashMap;
import server.*;

public class Transaction
{
    //transaction ID and OCC specific transaction numbers
    int transactionID;
    int transactionNumber;
    int lastCommittedTransactionNumber;

    //the sets of tentative data
    ArrayList<Integer> readSet = new ArrayList<>();
    HashMap<Integer, Integer> writeSet = new HashMap<>();

    StringBuffer log = new StringBuffer("");

    Transaction(int transactionID, int lastCommittedTransactionNumber)
    {
        this.transactionID = transactionID;
        this.lastCommittedTransactionNumber = lastCommittedTransactionNumber;
    }

    public int read(int accountNumber)
    {
        Integer balance;

        //check if value to be read was written by this transaction
        balance = writeSet.get(accountNumber);

        //if notyet set, read the commited version of it, and set
        if(balance == null)
        {
            //balance is = to the balance @ the passed in account #
            balance = TransactionServer.accountManager.read(accountNumber);
        }

        //add the account if it does not exist.
        if(!readSet.contains(accountNumber))
        {
            readSet.add(accountNumber);
        }
        return balance;
    }

    public int write(int accountNumber, int newBalance)
    {
        int oldBalance = read(accountNumber);

        if(!writeSet.containsKey(accountNumber))
        {
            writeSet.put(accountNumber, newBalance);
        }
        return oldBalance;
    }

    public ArrayList getReadSet()
    {
        return this.readSet;
    }

    public HashMap getWriteSet()
    {
        return this.writeSet;
    }

    public int getTransactionID()
    {
        return this.transactionID;
    }

    public int getTransactionNumber()
    {
        return this.transactionNumber;
    }
    
    public void setTransactionNumber(int transactionNumber)
    {
        this.transactionNumber = transactionNumber;
    }
    
    public int getLastCommittedTransactionNumber()
    {
        return this.lastCommittedTransactionNumber;
    }
    
    public void log(String logMessage)
    {
        log.append(logMessage);
    }
}
