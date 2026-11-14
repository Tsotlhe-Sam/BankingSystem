import java.util.ArrayList;
import java.util.List;

public abstract class Account
{
    private final String accountNumber;
    private final String branch;
    private final Customer owner;
    private double balance;
    private List<Transaction>transactions = new ArrayList<>();


    public Account(String accountNumber, String branch,Customer owner)
    {
        if (accountNumber == null || accountNumber.isBlank())
        {
            throw new IllegalArgumentException("Enter account number");
        }
        if (owner == null)
        {
            throw new IllegalArgumentException("account must have owner");
        }
        this.accountNumber = accountNumber;
        this.branch = branch;
        this.owner = owner;

        owner.addaccount(this);
    }

    public void deposit(double amount, String  reference)
    {
     if (amount <= 0 )
     {
         throw new IllegalArgumentException("Enter Valid amount");
     }
     //add to the balance
        balance = Math.round((balance+amount)* 100.0)/100.0;

     // keep a record of deposit
        transactions.add(new Transaction("Deposit",amount, reference, balance));

    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Enter a valid amount");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        balance = Math.round((balance - amount) * 100.0) / 100.0;
        transactions.add(new Transaction("withdrawal", amount, "Withdrawal", balance));
    }

    // getters

    public String getAccountNumber()
    {
        return accountNumber;
    }

    public Customer getOwner()
    {
        return owner;
    }

    public String getBranch()
    {
        return branch;
    }

    public double getBalance()
    {
        return balance;
    }

    // a method that makes no other class to change  history
    public List<Transaction>getTransactions()
    {
        return new ArrayList<>(transactions);

    }
    // 2 account object are the same account they have the same number
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Account other))
        {
            return false;
        }
        return accountNumber.equals(other.accountNumber);
    }

    @Override
    public int hashCode()
    {
        return accountNumber.hashCode();

    }
    @Override
    public String toString()
    {
        return accountNumber + " | " + branch + " | " + owner.getDisplayName() + " | BWP " + balance;

    }
}

