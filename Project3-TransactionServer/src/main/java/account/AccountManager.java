package account;

import java.util.HashMap;

public class AccountManager
{
    protected HashMap<Integer, Integer> accounts;
    
    public AccountManager(int numberOfAccounts, int startingBalance)
    {
        accounts = new HashMap<>();
        
        for (int index = 1; index <= numberOfAccounts; index++)
        {
            accounts.put(index, startingBalance);
        }
    }
    
    public int read(int accountNumber)
    {
        return accounts.get(accountNumber);
    }
    
    public void write(int accountNumber, int newBalance)
    {
        accounts.replace(accountNumber, newBalance);
    }
}
