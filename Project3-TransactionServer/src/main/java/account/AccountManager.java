package account;

import java.util.HashMap;

public class AccountManager
{
    protected HashMap<Integer, Integer> accounts;
    
    public AccountManager()
    {
        accounts = new HashMap<>();
    }
    
    public int read(int accountNumber)
    {
        return accounts.get(accountNumber);
    }
    
    public void write(int accountNumber, int newBalance)
    {
        accounts.put(accountNumber, newBalance);
    }
}
