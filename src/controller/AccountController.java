package controller;

import model.*;
import java.util.ArrayList;
import java.util.List;

public class AccountController
{
    private final LoginController loginController;
    private final CustomerController customerController;

    public AccountController(LoginController loginController, CustomerController customerController)
    {
        this.loginController = loginController;
        this.customerController= customerController;
    }
    public Result openAccount(String customerNumber, String type, String openingDepositText,
                              String employerName, String employerAddress)
    {
        if (!loginController.isLoggedIn())
        {
            return Result.fail("Please log in first");
        }

        Customer customer = customerController.find(customerNumber);
        if (customer == null)
        {
            return Result.fail("No customer with number " + customerNumber);
        }

        if (type == null || type.isBlank())
        {
            return Result.fail("Choose an account type");
        }

        try
        {
            Teller teller = loginController.getCurrentUser();
            Account account;

            if (type.equalsIgnoreCase("CHEQUE"))
            {
                if (!(customer instanceof IndividualCustomer individual))
                {
                    return Result.fail("Only individual customers can open a Cheque account");
                }
                account = teller.openAccount(individual, employerName, employerAddress);

                double deposit = parseOptionalAmount(openingDepositText);
                if (deposit > 0)
                {
                    account.deposit(deposit, "Opening deposit");
                }
            }
            else
            {
                double deposit = parseOptionalAmount(openingDepositText);
                account = teller.openAccount(customer, type, deposit);
            }

            return Result.ok(type.toUpperCase() + " account " + account.getAccountNumber()
                    + " opened for " + customer.getDisplayName());
        }
        catch (NumberFormatException e)
        {
            return Result.fail("Opening deposit must be a number, e.g. 500.00");
        }
        catch (RuntimeException e)
        {
            return Result.fail(e.getMessage());
        }
    }
  public Result deposit(String accountNumber, String amountText)
  {
    if (!loginController.isLoggedIn())
    {
        return Result.fail("Please log in first");
    }
    try
    {
        Account account = findAccountOrFail(accountNumber);
        double amount = parseAmount(amountText);
        loginController.getCurrentUser().processDeposit(account, amount);
        return Result.ok(String.format("Deposited BWP %.2f. New balance: BWP %.2f",
                amount, account.getBalance()));
    }
    catch (NumberFormatException e)
    {
        return Result.fail("Amount must be a number, e.g. 250.00");
    }
    catch (RuntimeException e)
    {
        return Result.fail(e.getMessage());
    }
  }

  public Result withdraw(String accountNumber, String amountText)
  {
    if (!loginController.isLoggedIn())
    {
        return Result.fail("Please log in first");
    }
    try
    {
        Account account = findAccountOrFail(accountNumber);
        double amount = parseAmount(amountText);
        loginController.getCurrentUser().processWithdraw(account, amount);
        return Result.ok(String.format("Withdrew BWP %.2f. New balance: BWP %.2f",
                amount, account.getBalance()));
    }
    catch (NumberFormatException e)
    {
        return Result.fail("Amount must be a number, e.g. 250.00");
    }
    catch (RuntimeException e)
    {
        return Result.fail(e.getMessage());   // e.g. "Insufficient funds"
    }
  }

  public Result getBalance(String accountNumber)
  {
    if (!loginController.isLoggedIn())
    {
        return Result.fail("Please log in first");
    }
    try
    {
        Account account = findAccountOrFail(accountNumber);
        return Result.ok(String.format("BWP %.2f", account.getBalance()));
    }
    catch (RuntimeException e)
    {
        return Result.fail(e.getMessage());
    }
  }

  public List<String> getStatement(String accountNumber)
  {
    List<String> lines = new ArrayList<>();
    if (!loginController.isLoggedIn())
    {
        return lines;
    }
    Account account = loginController.getCurrentUser().findAccount(clean(accountNumber));
    if (account != null)
    {
        for (Transaction t : account.getTransactions())
        {
            lines.add(t.getDetails());
        }
    }
    return lines;
  }

 public List<String> viewAllAccounts()
  {
    List<String> lines = new ArrayList<>();
    if (loginController.isManager() && loginController.isLoggedIn())
    {
        Manager manager = (Manager) loginController.getCurrentUser();
        for (Account a : manager.viewAllAccounts())
        {
            lines.add(a.toString());
        }
    }
    return lines;
  }
   public Result payMonthlyInterest()
   {
    if (!loginController.isLoggedIn())
    {
        return Result.fail("Please log in first");
    }
    if (!loginController.isManager())
    {
        return Result.fail("Only a Manager can pay monthly interest");
    }

    Manager manager = (Manager) loginController.getCurrentUser();
    int paid = 0;
    double total = 0;

    for (Account a : manager.viewAllAccounts())
    {
        InterestPayment payment = new InterestPayment(a);
        if (payment.checkEligibility())
        {
            payment.calculateAmount();
            payment.creditAccount();
            if (payment.getStatus().equals(InterestPayment.PAID))
            {
                paid++;
                total += payment.getAmount();
            }
        }
    }
    return Result.ok(String.format("Interest paid to %d account(s), total BWP %.2f", paid, total));
   }


private Account findAccountOrFail(String accountNumber)
{
    if (accountNumber == null || accountNumber.isBlank())
    {
        throw new IllegalArgumentException("Enter an account number");
    }
    Account account = loginController.getCurrentUser().findAccount(accountNumber.trim());
    if (account == null)
    {
        throw new IllegalArgumentException("No account with number " + accountNumber.trim());
    }
    return account;
}

// text from a TextField -> a positive number
private static double parseAmount(String text)
{
    if (text == null || text.isBlank())
    {
        throw new IllegalArgumentException("Enter an amount");
    }
    double amount = Double.parseDouble(text.trim().replace(",", ""));
    if (amount <= 0)
    {
        throw new IllegalArgumentException("Amount must be greater than zero");
    }
    return amount;
}

// same as parseAmount, but a blank field means 0 (for optional opening deposits)
  private static double parseOptionalAmount(String text)
 {
    if (text == null || text.isBlank())
    {
        return 0;
    }
    return Double.parseDouble(text.trim().replace(",", ""));
 }

  private static String clean(String text)
 {
    return (text == null) ? null : text.trim();
 }

}
