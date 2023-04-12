package account;

import java.util.ArrayList;

public class AccountManager
{
    protected ArrayList<Account> accounts;
    
    public AccountManager(int numberOfAccounts, int startingBalance)
    {
        accounts = new ArrayList<>();
        
        for (int index = 1; index <= numberOfAccounts; index++)
        {
            accounts.add(new Account(index, startingBalance));
        }
    }
    
    //returns account balance from passed in account #
    public int read(int accountNumber)
    {
        return accounts.get(accountNumber).getAccountBalance();
    }
    
    //sets the balance for the passed in account n#
    public void write(int accountNumber, int newBalance)
    {
        accounts.get(accountNumber).setAccountBalance(newBalance);
    }
}
