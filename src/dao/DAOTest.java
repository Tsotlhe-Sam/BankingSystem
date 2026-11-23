package dao;

import dao.*;
import model.*;

import java.io.File;
import java.util.List;

public class DAOTest
{
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args)
    {
        new File("test.db").delete();          // start from an empty test database
        DataBase.useFile("test.db");
        DataBase.createSchema();

        CustomerDAO customerDAO = new CustomerDAO();
        AccountDAO accountDAO = new AccountDAO();
        TransactionDAO transactionDAO = new TransactionDAO();

        System.out.println("--- CREATE ---");
        IndividualCustomer thabo = new IndividualCustomer("U001", "thabo", "pass123", "thabo@mail.com",
                "71234567", "Gaborone", "C001", "Thabo", "Molefe", "123456789", true);
        CompanyCustomer okavango = new CompanyCustomer("U002", "okavango", "pass456", "info@okv.co.bw",
                "3951111", "Maun", "C002", "Okavango Tours", "BW00012345", "Naledi Kgosi");
        customerDAO.insert(thabo);
        customerDAO.insert(okavango);
        check("2 customers saved", customerDAO.findAll().size() == 2);

        SavingsAccount savings = new SavingsAccount("1000001", "Main Mall", thabo);
        savings.deposit(1000, "Opening deposit");
        InvestmentAccount invest = new InvestmentAccount("1000002", "Main Mall", thabo, 2000);
        ChequeAccount cheque = new ChequeAccount("1000003", "Main Mall", thabo, "Debswana", "Jwaneng");
        cheque.creditSalary(8500);
        accountDAO.insert(savings);
        accountDAO.insert(invest);
        accountDAO.insert(cheque);
        check("3 accounts saved for Thabo", accountDAO.findByCustomer(customerDAO.findByNumber("C001")).size() == 3);
        check("Opening transactions saved too", transactionDAO.findByAccount("1000002").size() == 1);


        System.out.println("\n--- READ ---");
        Customer loaded = customerDAO.findByNumber("C001");
        check("Individual loaded back as IndividualCustomer", loaded instanceof IndividualCustomer);
        check("Name survives the round trip", loaded.getDisplayName().equals("Thabo Molefe"));
        check("Saved password still works (hash stored, not plain text)", loaded.login("pass123"));
        check("Wrong password still rejected", !loaded.login("wrong"));
        check("Company loaded back as CompanyCustomer", customerDAO.findByNumber("C002") instanceof CompanyCustomer);
        check("Unknown customer returns null", customerDAO.findByNumber("C999") == null);

        Account loadedInvest = accountDAO.findByNumber("1000002");
        check("Investment loaded back as InvestmentAccount", loadedInvest instanceof InvestmentAccount);
        check("Balance survives the round trip (BWP2000)", same(loadedInvest.getBalance(), 2000));
        check("Loaded account still knows its owner", loadedInvest.getOwner().getCustomerNumber().equals("C001"));
        check("Loaded Cheque keeps employer", ((ChequeAccount) accountDAO.findByNumber("1000003")).getEmployerName().equals("Debswana"));
        check("Loaded Savings still refuses withdrawals",
                refuses(() -> accountDAO.findByNumber("1000001").withdraw(10)));

        System.out.println("\n--- UPDATE ---");
        Account acc = accountDAO.findByNumber("1000003");
        acc.withdraw(1200);
        accountDAO.updateBalance(acc);
        List<Transaction> history = acc.getTransactions();
        transactionDAO.insert(acc.getAccountNumber(), history.get(history.size() - 1));
        check("New balance saved (BWP7300)", same(accountDAO.findByNumber("1000003").getBalance(), 7300));
        check("Withdrawal transaction saved (2 in history)", transactionDAO.findByAccount("1000003").size() == 2);

        thabo.setEmail("thabo.new@mail.com");
        customerDAO.update(thabo);
        check("Customer email updated", customerDAO.findByNumber("C001").getEmail().equals("thabo.new@mail.com"));

        System.out.println("\n--- DELETE ---");
        check("Delete one account", accountDAO.delete("1000001"));
        check("Deleted account is gone", accountDAO.findByNumber("1000001") == null);
        check("Its transactions were deleted too (cascade)", transactionDAO.findByAccount("1000001").isEmpty());
        check("Delete customer C001", customerDAO.delete("C001"));
        check("Customer's accounts deleted with them (composition)", accountDAO.findByNumber("1000002") == null);
        check("Only the company customer remains", customerDAO.findAll().size() == 1);


        System.out.println("\n--- DATABASE RULES ---");
        check("Duplicate customer number refused (PRIMARY KEY)", refuses(() -> customerDAO.insert(okavango)));
        check("Account for a customer that doesn't exist refused (FOREIGN KEY)",
                refuses(() -> accountDAO.insert(new SavingsAccount("9999999", "X",
                        new IndividualCustomer("U9", "ghost", "p", null, null, null, "C404", "No", "One", "0", false)))));

        System.out.println("\n========================================");
        System.out.println("RESULT: " + passed + " passed, " + failed + " failed");
        System.out.println("========================================");
    }

    private static void check(String name, boolean ok)
    {
        if (ok) { passed++; System.out.println("PASS  " + name); }
        else    { failed++; System.out.println("FAIL  " + name + "   <-- check this"); }
    }

    private static boolean refuses(Runnable code)
    {
        try { code.run(); return false; }
        catch (RuntimeException e) { return true; }
    }

    private static boolean same(double a, double b)
    {
        return Math.abs(a - b) < 0.001;
    }
}
