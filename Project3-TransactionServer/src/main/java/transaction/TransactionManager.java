package transaction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import message.*;
import server.*;


public class TransactionManager implements MessageTypes {
    
    // counter for transactionIDs
    private static int transactionIDCounter = 0;

    // lists of transactions
    private static final ArrayList<Transaction> runningTransactions = new ArrayList<>();
    private static final ArrayList<Transaction> abortedTransactions = new ArrayList<>();
    private static final HashMap<Integer, Integer> committedTransactions = new HashMap<>(); //specific to OCC


    // transaction counter number specific to OCC
    private static final int transactionNumberCounter = 0;

    // default constructor
    public TransactionManager()
    {

    }

    // helper method for returning aborted transactons, returns list of aborted transactions
    public ArrayList<Transaction> getAbortedTransactions()
    {
        return abortedTransactions;
    }
    
    // 
    public synchronized void runTransaction(Socket client)
    {
        (new TransactionManagerWorker(client)).start();
    }

    // validates transactions according to OCC, implementing backwards validation
    // when validating, the transaction checks its own read set against the 
    // hashmap/write set(overlapping transactions).
    public static boolean validateTransaction(Transaction transaction)
    {
        int transactionNumber;
        int lastCommittedTransactionNumber;
        int transactionNumberIndex;

        ArrayList<Integer> readSet = transaction.getReadSet();
        HashMap<Integer, Integer> checkedTransactionWriteSet;
        Iterator<Integer> readSetIterator;

        Transaction checkedTransaction;
        Integer checkedAccount;

        //assign a transaction number to this one
        transactionNumber = 1 + transactionNumberCounter;
        transaction.setTransactionNumber(transactionNumber);

        //get the transaction number of the last committed transaction right before this one started.
        lastCommittedTransactionNumber = transaction.getLastCommittedTransactionNumber();

        //run through all overlapping transactions
        for (transactionNumberIndex = lastCommittedTransactionNumber + 1;
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
                    checkedAccount = readSetIterator.next();
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
    public static void writeTransaction(Transaction transaction)
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
            TransactionServer.accountManager.write(account, balance);
            
            transaction.log("[TransactionManager.writeTransaction] Transaction #" + transaction.getTransactionID() + " written");
        }
    }

    //primarily interacts with TransactionServer Proxy, it's "counterpart"
    //objects of this inner class run transaction, one thread runs one transaction on behalf of a client
    public static class TransactionManagerWorker extends Thread
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
            catch(Exception e)
            {
                System.out.println("[TransactionManagerWorker.run] Failed to open object steams");
                e.printStackTrace();
                System.out.println("Error: " + e);
                System.exit(1);
            }
        }

        @Override
        public void run()
        {
         //loop is left when transaction closes
         while(keepgoing)
         {
          //reading message
          try
          {
           message = (Message) readFromNet.readObject();
          }
          catch (IOException | ClassNotFoundException e)
          {
            System.out.println("[TransactionManagerWorker.run] Message could not be read from object stream.");
            System.exit(1);
          }
          //processing message
          switch(message.getType())
          {
            case OPEN_TRANSACTION:
                synchronized(runningTransactions)
                {

                 System.out.println("[TMW] OPEN_TRANSACTION Request");   

                 //assign a new transaction ID, also pass in the last assigned transaction number
                 //as to the latter, that number may refer to a (prior, non-overlapping) transaction that needed
                 //to be aborted
                 transaction = new Transaction(++transactionIDCounter, transactionNumberCounter);
                 runningTransactions.add(transaction);
                }
                try
                {
                 writeToNet.writeObject(transaction.getTransactionID());
                }
                catch (IOException e)
                {
                 System.err.println("[TransactionManagerWorker.run] OPEN_TRANSACTION #" + transaction.getTransactionID() + " - Error writing transactionID");
                }

                //transaction.log("[TransactionManagerWorker.run] " + OPEN_COLOR + "OPEN_TRANSACTION" + RESET_COLOR + " #" + transaction.getTransactionID());

                break;

            case CLOSE_TRANSACTION:
                synchronized(runningTransactions)
                {
                    System.out.println("[TMW] CLOSE_TRANSACTION Request");
                    runningTransactions.remove(transaction);

                    if(validateTransaction(transaction))
                    {
                     //add this transaction to the committed transactions
                     committedTransactions.put(transaction.getTransactionNumber(), transaction);
                    }
                    try
                    {
                    writeToNet.writeObject(transaction.getTransactionID());
                    }
                    catch (IOException e)
                    {
                    System.err.println("[TransactionManagerWorker.run] OPEN_TRANSACTION #" + transaction.getTransactionID() + " - Error writing transactionID");
                    }

                    //transaction.log("[TransactionManagerWorker.run] " + OPEN_COLOR + "OPEN_TRANSACTION" + RESET_COLOR + " #" + transaction.getTransactionID());

                    break;
                }

                case READ_REQUEST:
                synchronized(runningTransactions)
                {
                    System.out.println("[TMW] Read Request");

                    break;
                }

                case WRITE_REQUEST:
                synchronized(runningTransactions)
                {
                    System.out.println("[TMW] Write Request");
                    writeTransaction(transaction);
                    break;
                }
          }
         }
        }


    }
}