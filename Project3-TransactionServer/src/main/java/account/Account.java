package account;

public class Account
{
    protected int accountNumber;
    protected int accountBalance;
    
    public Account(int accountNumber, int startingBalance)
    {
        this.accountNumber = accountNumber;
        this.accountBalance = startingBalance;
    }
    
    public int getAccountBalance()
    {
        return this.accountBalance;
    }
    
    public void setAccountBalance(int balance)
    {
        this.accountBalance = balance;
    }
    
    public int getAccountNumber()
    {
        return this.accountNumber;
    }
}
