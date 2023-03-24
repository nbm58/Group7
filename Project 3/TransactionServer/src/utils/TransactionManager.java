package src.utils;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.print.attribute.HashAttributeSet;

public class TransactionManager implements MessageTypes {
    //counter for transactionIDs
    private static int transactionIDCounter = 0;

    //lists of transactions
    private static final ArrayList<Transaction> runningTransactions = new ArrayList<>();
    private static final ArrayList<Transaction> abortedTransactions = new ArrayList<>();
    private static final HashMap<Integer, Transaction> committedTransactions = new HashMap<>(); //specific to OCC


    //transaction counter number specific to OCC
    private static final int transactionNumberCounter = 0;

}

//default constructor
public TransactionManager()
{

}


//helper method for returning aborted transactons, returns list of aborted transactions
public synchronized void runTransaction(Socket client)
{
    (new TransactionManagerWorker(client)).start();
}

//validates transactions according to OCC, implementing backwards validation
public boolean validateTransaction(Transaction transaction)
{
    int transactionNumber;
    int lastCommitedTransactionNumber;
    int transactionNumberIndex;

    ArrayList<Integer> readSet = transaction.getReadSet();
    HashMap<Integer, Integer> checkedTransactionWriteSet;
    Iterator<Integer> readSetIterator;

    Transaction checkedTransaction;
    Integer checkedAccount;

    //assign a transaction number to this one
    transactionNumber = ++transactionNumberCounter;
    transaction.setTransactionNumber(transactionNumber);

    //get the transaction number of the last committed transaction right before this one started.
    lastCommittedTransactionNumber = transaction.getLastCommittedTransactionNumber();

    //run through all overlapping transactions
    for(transactionNumberIndex = lastCommitedTransactionNumber+1;
         transactionNumberIndex < transactionNumber;
                                            transactionNumberIndex++)
    {
        //get transaction with transaction number of transactionNumberIndex
        checkedTransaction = committedTransactions.get(transactionNumberIndex);

        //make sure transaction with transactionNumberIndex was not aborted before
        if(checkedTransaction != null)
        {
            // check our own read set against the write set of the checkedTransaction
            checkedTransactionWriteSet = checkedTransaction.getWriteSet();

            readSetIterator = readSet.iterator();
            while(readSetIterator.hasNext())
            {
                // is an account in the read set part of the write set in the checkedTransaction?
                checkedAccount = reeadSetIterator.next();
                if(checkedTransactionWriteSet.containsKey(checkedAccount))
                {
                    transaction.log("[TransactionManager.validateTransaction] Transaction #" + transaction.getTransactionID() +
                    " failed: r/w conflict on account #" + checkedAccount + " with Transaction #" + checkedTransaction.getTransactionID());

                    return false;
                }
            }
        }

    }
    transaction.log("[TransactionManager.validateTransaction] Transaction #" + transaction.getTransactionID() + " successfully validated");
    return true;

}

//write the write set of a transacton into the operation data
public void writeTransaction(Transaction transaction)
{
    HashMap<Integer, Integer> transactionWriteSet = transaction.getWriteSet();
    int account;
    int balance;

    //get all the entries of this write set
    for(Map.Entry<Integer, Integer> entry : transactionWriteSet.entrySet())
    {
     account = entry.getKey();
     balance = entry.getValue();

     //write this record into operational data
     Transaction.accountManager.write(account, balance);
     
     transaction.log("[TransactionManager.writeTransaction] Transaction #" + transaction.getTransactionID() + " written");

    }
}

//objects of this inner class run transaction, one thread runs one transaction on behalf of a client
public class TransactionManagerWorker extends Thread
{
 //netowrking communication related fields
 Socket client = null;
 ObjectInputStream readFromNet = null;
 ObjectOutputStream writeToNet = null;
 Message message = null;

 //transaction related fields
 Transaction transaction = null;
 int accountNumber = 0;
 int balance = 0;

 //flag for jumping out of while loop after this transaction closed
 boolean keepgoing = true;

 //the constructor just opens up the network channels
 private TransactionManagerWorker(Socket client)
 {
  this.client = client;
  //setting up object streams
  try
  {
   readFromNet = new ObjectInputStream(client.getInputStream());
   writeToNet = new ObjectOutputStream(client.getOutputStream());
  }  
 }
}
