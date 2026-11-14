import java.util.ArrayList;
import java.util.List;

public abstract class Customer extends User
{
    private String customerNumber;
    private final List<Account> accounts = new ArrayList<>(); //hold 1 to many account

    public Customer(String userID, String username,String password,String email, String phoneNumber, String address,String customerNumber)
    {
        super(userID,username,password,email,phoneNumber,address);

        if ( customerNumber == null || customerNumber.isBlank())
        {
            throw new IllegalArgumentException("Enter customer number");
        }

        this.customerNumber = customerNumber;
    }

    public Account getaccount(String accountNumber)
    {
        for (Account a : accounts){
            if (a.getAccountNumber().equals(accountNumber))
            {
                return a;
            }
        }
        return null;
    }

    public List<Account> Accounts()
    {
        return  new ArrayList<>(accounts);
    }
    public void addaccount(Account account)
    {
        if (account == null)
        {
            throw new IllegalArgumentException("Account  cannot be empty");
        }
        if (!accounts.contains(account))
        {
            accounts.add(account);
        }
    }
    public double getTotalBalance()
    {
        double total = 0;
        for (Account a : accounts)
        {
            total += a.getBalance();
        }
        return total;
    }
    public String getCustomerNumber()
    {
        return customerNumber;
    }

    @Override
    public String getRole()
    {
        return "Customer";
    }
    public abstract String getDisplayName();

    @Override
    public String toString()
    {
        return customerNumber + " - " + getDisplayName() + " (" + accounts.size() + "account(s))";
    }

}
