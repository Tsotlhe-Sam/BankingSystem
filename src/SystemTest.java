import controller.*;
import model.*;

public class SystemTest
{
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args)
    {
        // the controllers now save to a database, so tests use their own file (real data stays safe)
        new java.io.File("systemtest.db").delete();
        dao.DataBase.useFile("systemtest.db");
        dao.DataBase.createSchema();

        testModel();
        testControllers();

        System.out.println("\n========================================");
        System.out.println("RESULT: " + passed + " passed, " + failed + " failed, "
                + (passed + failed) + " tests in total");
        System.out.println("========================================");
    }

    // =====================================================================
    // PART A: CORE MODEL (Part 4)
    // =====================================================================
    private static void testModel()
    {
        heading("A1. User: password hashing and login (encapsulation)");
        IndividualCustomer thabo = new IndividualCustomer("U001", "thabo", "pass123", "thabo@mail.com",
                "71234567", "Gaborone", "C100", "Thabo", "Molefe", "123456789", true);
        check("Wrong password is rejected", !thabo.login("wrong"));
        check("Correct password logs in", thabo.login("pass123"));
        check("isLogin() is true after login", thabo.isLoggedin());
        thabo.logout();
        check("isLogin() is false after logout", !thabo.isLoggedin());
        expectError("Blank username is refused", () -> new IndividualCustomer("U002", "", "p", null, null,
                null, "C101", "A", "B", "1", true));

        heading("A2. Customers: inheritance, abstraction, polymorphism");
        CompanyCustomer okavango = new CompanyCustomer("U003", "okavango", "pass789", "info@okv.co.bw",
                "3951111", "Maun", "C102", "Okavango Tours", "BW00012345", "Naledi Kgosi");
        Customer c1 = thabo;      // same type...
        Customer c2 = okavango;
        check("Individual display name = first name + surname", c1.getDisplayName().equals("Thabo Molefe"));
        check("Company display name contains the company name", c2.getDisplayName().contains("Okavango Tours"));
        check("getRole() is overridden in Customer", thabo.getRole().equals("Customer"));
        check("isEmployed() works", thabo.isEmployed());
        expectError("Blank company name is refused", () -> new CompanyCustomer("U004", "x", "p", null, null,
                null, "C103", "", "BW1", "X"));

        heading("A3. Savings account (interface + overriding)");
        SavingsAccount savings = new SavingsAccount("S001", "Main Mall", thabo);
        savings.deposit(1000);
        savings.deposit(500, "Birthday money");                       // overloaded deposit
        check("Deposits add up to BWP1500", same(savings.getBalance(), 1500));
        check("Account joins its owner automatically (composition)", thabo.getAccounts().contains(savings));
        check("Savings rate is 0.05% (0.0005)", same(savings.getInterestRate(), 0.0005));
        check("Monthly interest on BWP1500 = BWP0.75", same(savings.calculateInterest(), 0.75));
        expectError("Savings withdrawal is refused (overridden)", () -> savings.withdraw(100));
        expectError("Zero deposit is refused", () -> savings.deposit(0));
        expectError("Negative deposit is refused", () -> savings.deposit(-50));
        check("Two transactions were recorded", savings.getTransactions().size() == 2);

        heading("A4. Investment account");
        InvestmentAccount invest = new InvestmentAccount("I001", "Main Mall", thabo, 2000);
        check("Opened with BWP2000", same(invest.getBalance(), 2000));
        invest.withdraw(100);
        check("Withdrawal allowed: balance now BWP1900", same(invest.getBalance(), 1900));
        check("Investment rate is 5% (0.05)", same(invest.getInterestRate(), 0.05));
        check("Monthly interest on BWP1900 = BWP95", same(invest.calculateInterest(), 95));
        expectError("Opening with less than BWP500 is refused",
                () -> new InvestmentAccount("I002", "Maun", okavango, 300));
        expectError("Withdrawing more than the balance is refused", () -> invest.withdraw(1_000_000));

        heading("A5. Cheque account (constraint: employed individual)");
        ChequeAccount cheque = new ChequeAccount("Q001", "Main Mall", thabo, "Debswana", "Jwaneng");
        cheque.creditSalary(8500);
        cheque.withdraw(1200);
        check("Salary in, withdrawal out: balance BWP7300", same(cheque.getBalance(), 7300));
        check("Salary transaction mentions the employer", cheque.getTransactions().get(0).getDetails().contains("Debswana"));
        IndividualCustomer naledi = new IndividualCustomer("U005", "naledi", "pass456", null, null,
                "Maun", "C104", "Naledi", "Kgosi", "987654321", false);
        expectError("Unemployed customer cannot open a Cheque account",
                () -> new ChequeAccount("Q002", "Maun", naledi, "Shoprite", "Maun"));

        heading("A6. One customer, many accounts");
        check("Thabo holds 3 accounts", thabo.getAccounts().size() == 3);
        check("Total balance = 1500 + 1900 + 7300 = BWP10700", same(thabo.getTotalBalance(), 10700));

        heading("A7. InterestPayment (states)");
        InterestPayment p1 = new InterestPayment(invest);
        check("New payment starts PENDING", p1.getStatus().equals(InterestPayment.PENDING));
        check("Investment account is eligible", p1.checkEligibility());
        p1.calculateAmount();
        check("After calculating: CALCULATED", p1.getStatus().equals(InterestPayment.CALCULATED));
        p1.creditAccount();
        check("After crediting: PAID", p1.getStatus().equals(InterestPayment.PAID));
        check("Interest added: balance BWP1995", same(invest.getBalance(), 1995));
        InterestPayment p2 = new InterestPayment(cheque);
        check("Cheque account is not eligible", !p2.checkEligibility());
        check("...so its status is REJECTED", p2.getStatus().equals(InterestPayment.REJECTED));
        InterestPayment p3 = new InterestPayment(savings);
        expectError("Cannot credit before calculating", p3::creditAccount);

        heading("A8. Teller and Manager");
        Teller teller = new Teller("U100", "kmotse", "teller123", null, null, "Gaborone",
                "E001", "Main Mall", "Kabo", "Motse");
        Manager manager = new Manager("U200", "lsetlhare", "manager123", null, null, "Gaborone",
                "E002", "Main Mall", "Lorato", "Setlhare");
        IndividualCustomer mpho = new IndividualCustomer("U006", "mpho", "pass", null, null,
                "Francistown", "C105", "Mpho", "Dube", "555666777", true);
        expectError("Teller must log in before working", () -> teller.registerCustomer(mpho));
        teller.login("teller123");
        teller.registerCustomer(mpho);
        check("Teller can find a registered customer", teller.findCustomer("C105") == mpho);
        expectError("Registering the same customer twice is refused", () -> teller.registerCustomer(mpho));
        Account opened = teller.openAccount(mpho, "SAVINGS", 300);
        check("Teller opens an account with a generated number", teller.findAccount(opened.getAccountNumber()) == opened);
        expectError("Unregistered customer cannot get an account",
                () -> teller.openAccount(naledi, "SAVINGS", 100));
        check("getRole() overridden: Teller", teller.getRole().equals("Teller"));
        check("getRole() overridden again: Manager", manager.getRole().equals("Manager"));
        expectError("Manager must log in to view all accounts", manager::viewAllAccounts);
        manager.login("manager123");
        check("Manager can see the account the teller opened", manager.viewAllAccounts().contains(opened));
    }

    // =====================================================================
    // PART B: CONTROLLERS (Part 6), used the way the GUI will use them
    // =====================================================================
    private static void testControllers()
    {
        Teller.restoreRecords(new java.util.ArrayList<>(), new java.util.ArrayList<>());   // fresh start, like opening the app
        LoginController login = new LoginController();
        CustomerController customers = new CustomerController(login);
        AccountController accounts = new AccountController(login, customers);
        login.addStaff(new Teller("U300", "teller1", "t1pass", null, null, "Gaborone",
                "E010", "Main Mall", "Kagiso", "Ramotswa"));
        login.addStaff(new Manager("U400", "manager1", "m1pass", null, null, "Gaborone",
                "E011", "Main Mall", "Boitumelo", "Seretse"));

        heading("B1. LoginController");
        refused("Action before login is refused", customers.registerIndividual("C200", "A", "B", "1",
                null, null, null, true, "a", "p"));
        refused("Blank username and password", login.login("", ""));
        refused("Wrong password", login.login("teller1", "nope"));
        refused("Unknown username", login.login("ghost", "t1pass"));
        ok("Correct login", login.login("teller1", "t1pass"));
        check("isLoggedIn() is true", login.isLoggedIn());
        check("Teller is not a manager", !login.isManager());

        heading("B2. CustomerController");
        ok("Register an individual", customers.registerIndividual("C200", "Lesego", "Phiri", "111222333",
                "lesego@mail.com", "72000000", "Gaborone", true, "lesego", "pass"));
        ok("Register a company", customers.registerCompany("C201", "Kalahari Ltd", "BW999",
                "Lesego Phiri", null, null, "Ghanzi", "kalahari", "pass"));
        refused("Blank surname is refused", customers.registerIndividual("C202", "Neo", "", "1",
                null, null, null, false, "neo", "pass"));
        refused("Duplicate customer number is refused", customers.registerIndividual("C200", "X", "Y", "1",
                null, null, null, false, "x", "pass"));
        check("getCustomerName finds the customer", "Lesego Phiri".equals(customers.getCustomerName("C200")));
        check("Unknown customer returns null", customers.getCustomerName("C999") == null);

        heading("B3. AccountController: opening accounts");
        String sav = number(ok("Open Savings", accounts.openAccount("C200", "SAVINGS", "1000", "", "")));
        String inv = number(ok("Open Investment", accounts.openAccount("C200", "INVESTMENT", "2000", "", "")));
        String chq = number(ok("Open Cheque", accounts.openAccount("C200", "CHEQUE", "", "BPC", "Gaborone")));
        refused("Investment below BWP500", accounts.openAccount("C201", "INVESTMENT", "300", "", ""));
        refused("Company cannot open a Cheque account", accounts.openAccount("C201", "CHEQUE", "", "X", "Y"));
        refused("Deposit typed as text ('abc')", accounts.openAccount("C201", "INVESTMENT", "abc", "", ""));
        refused("Unknown customer", accounts.openAccount("C999", "SAVINGS", "100", "", ""));
        refused("No account type chosen", accounts.openAccount("C200", "", "100", "", ""));
        check("Customer now has 3 accounts", customers.getAccountSummaries("C200").size() == 3);

        heading("B4. AccountController: transactions");
        ok("Deposit into Cheque", accounts.deposit(chq, "8500"));
        ok("Withdraw '1,200' (comma accepted)", accounts.withdraw(chq, "1,200"));
        check("Balance shows BWP 7300.00", accounts.getBalance(chq).getMessage().contains("7300.00"));
        refused("Withdraw from Savings", accounts.withdraw(sav, "100"));
        refused("Withdraw more than the balance", accounts.withdraw(inv, "999999"));
        refused("Negative amount", accounts.deposit(inv, "-50"));
        refused("Amount typed as text", accounts.deposit(inv, "ten"));
        refused("Blank amount", accounts.deposit(inv, ""));
        refused("Unknown account number", accounts.deposit("0000000", "10"));
        check("Statement lists the transactions", accounts.getStatement(chq).size() == 2);

        heading("B5. AccountController: manager-only actions");
        refused("Teller cannot pay interest", accounts.payMonthlyInterest());
        check("Teller cannot view all accounts", accounts.viewAllAccounts().isEmpty());
        login.logout();
        check("Logged out", !login.isLoggedIn());
        refused("Deposit after logout is refused", accounts.deposit(inv, "10"));
        ok("Manager login", login.login("manager1", "m1pass"));
        check("Manager is recognised", login.isManager());
        ok("Manager pays monthly interest", accounts.payMonthlyInterest());
        check("Interest appears on the Investment statement",
                accounts.getStatement(inv).get(accounts.getStatement(inv).size() - 1).contains("Interest"));
        check("Manager can view all accounts", !accounts.viewAllAccounts().isEmpty());
    }

    // =====================================================================
    // helpers
    // =====================================================================
    private static void heading(String title)
    {
        System.out.println("\n--- " + title + " ---");
    }

    private static void check(String name, boolean condition)
    {
        if (condition)
        {
            passed++;
            System.out.println("PASS  " + name);
        }
        else
        {
            failed++;
            System.out.println("FAIL  " + name + "   <-- check this");
        }
    }

    // passes if the code is refused with an error
    private static void expectError(String name, Runnable code)
    {
        try
        {
            code.run();
            check(name + " (no error was thrown!)", false);
        }
        catch (RuntimeException e)
        {
            check(name + "  [" + e.getMessage() + "]", true);
        }
    }

    private static Result ok(String name, Result r)
    {
        check(name + "  [" + r.getMessage() + "]", r.isSuccess());
        return r;
    }

    private static Result refused(String name, Result r)
    {
        check(name + "  [" + r.getMessage() + "]", !r.isSuccess());
        return r;
    }

    private static boolean same(double a, double b)
    {
        return Math.abs(a - b) < 0.001;
    }

    // pulls the account number out of "SAVINGS account 1000001 opened for ..."
    private static String number(Result r)
    {
        for (String word : r.getMessage().split(" "))
        {
            if (word.matches("\\d+"))
            {
                return word;
            }
        }
        return "";
    }
}