package model;

import java.util.ArrayList;
import java.util.List;

public class Teller extends User
{
    // bank-wide records shared by all staff (replaced by the database in Part 7)
    protected static final List<Customer> allCustomers = new ArrayList<>();
    protected static final List<Account> allAccounts = new ArrayList<>();
    private static int nextAccountNumber = 1000001;

    private final String employeeID;
    private String branch;
    private String firstName;
    private String surname;

    public Teller(String userID, String username, String password, String email, String phoneNumber,
                  String address, String employeeID, String branch, String firstName, String surname)
    {
        super(userID, username, password, email, phoneNumber, address);

        if (employeeID == null || employeeID.isBlank())
        {
            throw new IllegalArgumentException("Enter employee ID");
        }
        this.employeeID = employeeID;
        this.branch = branch;
        this.firstName = firstName;
        this.surname = surname;
    }

    private void checkLoggedIn()
    {
        if (!isLoggedin())
        {
            throw new IllegalStateException(getRole() + " must be logged in first");
        }
    }

    // registerCustomer: OVERLOADED (same name, different parameter types)

    public void registerCustomer(IndividualCustomer customer)
    {
        addCustomer(customer);
    }

    public void registerCustomer(CompanyCustomer customer)
    {
        addCustomer(customer);
    }

    private void addCustomer(Customer customer)
    {
        checkLoggedIn();
        if (customer == null)
        {
            throw new IllegalArgumentException("model.Customer cannot be empty");
        }
        for (Customer c : allCustomers)
        {
            if (c.getCustomerNumber().equals(customer.getCustomerNumber()))
            {
                throw new IllegalArgumentException("model.Customer " + customer.getCustomerNumber() + " is already registered");
            }
        }
        allCustomers.add(customer);
    }

    // openAccount: OVERLOADED

    // opens a Savings or Investment account
    public Account openAccount(Customer owner, String type, double openingDeposit)
    {
        checkLoggedIn();
        checkRegistered(owner);

        Account account;
        if (type.equalsIgnoreCase("SAVINGS"))
        {
            account = new SavingsAccount(nextNumber(), branch, owner);
            if (openingDeposit > 0)
            {
                account.deposit(openingDeposit, "Opening deposit");
            }
        }
        else if (type.equalsIgnoreCase("INVESTMENT"))
        {
            account = new InvestmentAccount(nextNumber(), branch, owner, openingDeposit);
        }
        else
        {
            throw new IllegalArgumentException("Unknown account type: " + type);
        }

        allAccounts.add(account);
        return account;
    }

    // opens a Cheque account (needs employer details, individuals only)
    public ChequeAccount openAccount(IndividualCustomer owner, String employerName, String employerAddress)
    {
        checkLoggedIn();
        checkRegistered(owner);

        ChequeAccount account = new ChequeAccount(nextNumber(), branch, owner, employerName, employerAddress);
        allAccounts.add(account);
        return account;
    }

    private void checkRegistered(Customer owner)
    {
        if (owner == null || !allCustomers.contains(owner))
        {
            throw new IllegalArgumentException("model.Customer must be registered before opening an account");
        }
    }

    private static String nextNumber()
    {
        return String.valueOf(nextAccountNumber++);
    }

    // transactions at the counter

    public void processDeposit(Account account, double amount)
    {
        checkLoggedIn();
        account.deposit(amount, "Counter deposit by " + employeeID);
    }

    public void processWithdraw(Account account, double amount)
    {
        checkLoggedIn();
        account.withdraw(amount);
    }


    public Customer findCustomer(String customerNumber)
    {
        checkLoggedIn();
        for (Customer c : allCustomers)
        {
            if (c.getCustomerNumber().equalsIgnoreCase(customerNumber))
            {
                return c;
            }
        }
        return null;
    }

    public Account findAccount(String accountNumber)
    {
        checkLoggedIn();
        for (Account a : allAccounts)
        {
            if (a.getAccountNumber().equals(accountNumber))
            {
                return a;
            }
        }
        return null;
    }

    @Override
    public String getRole()
    {
        return "Teller";
    }

    public String getEmployeeID() { return employeeID; }
    public String getBranch()     { return branch; }
    public String getFirstName()  { return firstName; }
    public String getSurname()    { return surname; }

    public void setBranch(String branch) { this.branch = branch; }
}