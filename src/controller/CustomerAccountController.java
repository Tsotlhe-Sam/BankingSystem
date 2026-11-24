package controller;

import dao.AccountDAO;
import model.Account;
import model.Customer;
import model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class CustomerAccountController
{
    private final LoginController loginController;
    private final AccountDAO accountDAO = new AccountDAO();

    public CustomerAccountController(LoginController loginController)
    {
        this.loginController = loginController;
    }

    public List<String> getMyAccounts()
    {
        List<String> lines = new ArrayList<>();
        Customer me = loginController.getCurrentCustomer();
        if (me != null)
        {
            for (Account a : me.getAccounts())
            {
                lines.add(a.getClass().getSimpleName() + " " + a.getAccountNumber()
                        + " | BWP " + String.format("%.2f", a.getBalance()));
            }
        }
        return lines;
    }

    public Result getTotalBalance()
    {
        Customer me = loginController.getCurrentCustomer();
        if (me == null)
        {
            return Result.fail("Please log in first");
        }
        return Result.ok(String.format("Total across all your accounts: BWP %.2f", me.getTotalBalance()));
    }

    public List<String> getMyStatement(String accountNumber)
    {
        List<String> lines = new ArrayList<>();
        Account account = myAccount(accountNumber);
        if (account == null)
        {
            lines.add("Choose one of your own accounts");
            return lines;
        }
        for (Transaction t : account.getTransactions())
        {
            lines.add(0, t.getDetails());
        }
        if (lines.isEmpty())
        {
            lines.add("No transactions to show");
        }
        return lines;
    }

    public Result deposit(String accountNumber, String amountText)
    {
        Account account = myAccount(accountNumber);
        if (account == null)
        {
            return Result.fail("Choose one of your own accounts");
        }
        try
        {
            double amount = parseAmount(amountText);
            account.deposit(amount, "Deposit by customer");
            accountDAO.recordTransaction(account, last(account));
            return Result.ok(String.format("Deposited BWP %.2f. New balance: BWP %.2f", amount, account.getBalance()));
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
        Account account = myAccount(accountNumber);
        if (account == null)
        {
            return Result.fail("Choose one of your own accounts");
        }
        try
        {
            double amount = parseAmount(amountText);
            account.withdraw(amount);
            accountDAO.recordTransaction(account, last(account));
            return Result.ok(String.format("Withdrew BWP %.2f. New balance: BWP %.2f", amount, account.getBalance()));
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

    private Account myAccount(String accountNumber)
    {
        Customer me = loginController.getCurrentCustomer();
        if (me == null || accountNumber == null || accountNumber.isBlank())
        {
            return null;
        }
        return me.getAccount(accountNumber.trim());
    }

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

    private static Transaction last(Account account)
    {
        List<Transaction> history = account.getTransactions();
        return history.get(history.size() - 1);
    }
}