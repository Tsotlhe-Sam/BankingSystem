import controller.*;
import dao.DataBase;
import controller.DataLoader;
import model.Teller;

import java.io.File;
import java.util.ArrayList;

/**
 * Part 8 integration test: the whole system working together
 * (controllers -> model -> DAOs -> database), including a simulated restart
 * to prove the data is really saved.
 */
public class IntegrationTest
{
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args)
    {
        new File("integration.db").delete();
        DataBase.useFile("integration.db");

        // ---------------- first run of the app ----------------
        System.out.println("--- FIRST RUN ---");
        LoginController login = new LoginController();
        CustomerController customers = new CustomerController(login);
        AccountController accounts = new AccountController(login, customers);
        DataLoader.loadAll(login);

        check("Staff loaded from the database: manager can log in", login.login("manager", "manager123").isSuccess());
        check("10 sample customers loaded", sampleCustomers(customers) == 10);
        check("Sample customer C001 is Thabo Molefe", "Thabo Molefe".equals(customers.getCustomerName("C001")));
        check("Thabo has 3 accounts", customers.getAccountSummaries("C001").size() == 3);
        check("15 sample accounts loaded", accounts.viewAllAccounts().size() == 15);

        check("Register a new customer (saved)", customers.registerIndividual("C011", "Neo", "Tau", "222333444",
                null, "71112222", "Molepolole", true, "neo", "pass").isSuccess());
        Result opened = accounts.openAccount("C011", "INVESTMENT", "750", "", "");
        check("Open an account for them (saved)", opened.isSuccess());
        String newAccount = number(opened);
        check("New account number continues after the sample data (1000016)", "1000016".equals(newAccount));
        check("Deposit 250 (saved)", accounts.deposit(newAccount, "250").isSuccess());
        check("Withdraw from Savings still refused", !accounts.withdraw("1000001", "10").isSuccess());
        check("Duplicate username refused", !customers.registerIndividual("C012", "Other", "Person", "1",
                null, null, null, false, "neo", "pass").isSuccess());
        check("Manager pays monthly interest (saved)", accounts.payMonthlyInterest().isSuccess());
        String balanceBefore = accounts.getBalance(newAccount).getMessage();   // 1000 + 5% = BWP 1050.00
        int historyBefore = accounts.getStatement(newAccount).size();
        login.logout();

        // ---------------- close and reopen the app ----------------
        System.out.println("\n--- RESTART (everything reloaded from the database) ---");
        Teller.restoreRecords(new ArrayList<>(), new ArrayList<>());          // memory wiped, as if the app closed
        login = new LoginController();
        customers = new CustomerController(login);
        accounts = new AccountController(login, customers);
        DataLoader.loadAll(login);

        check("Teller can still log in", login.login("teller", "teller123").isSuccess());
        check("Sample data was NOT added a second time (still 15 + 1 accounts)", accounts.getBalance("1000017").isSuccess() == false
                && customers.getAccountSummaries("C001").size() == 3);
        check("New customer C011 is still there", "Neo Tau".equals(customers.getCustomerName("C011")));
        check("New account's balance survived the restart (" + balanceBefore + ")",
                accounts.getBalance(newAccount).getMessage().equals(balanceBefore) && balanceBefore.equals("BWP 1050.00"));
        check("Its full history survived (opening deposit, deposit, interest)",
                accounts.getStatement(newAccount).size() == historyBefore && historyBefore == 3);
        check("Interest entry appears in the history",
                accounts.getStatement(newAccount).get(historyBefore - 1).contains("Interest"));

        System.out.println("\n========================================");
        System.out.println("RESULT: " + passed + " passed, " + failed + " failed");
        System.out.println("========================================");
    }

    private static int sampleCustomers(CustomerController customers)
    {
        int found = 0;
        for (int i = 1; i <= 10; i++)
        {
            if (customers.getCustomerName(String.format("C%03d", i)) != null) found++;
        }
        return found;
    }

    private static String number(Result r)
    {
        for (String word : r.getMessage().split(" "))
        {
            if (word.matches("\\d+")) return word;
        }
        return "";
    }

    private static void check(String name, boolean ok)
    {
        if (ok) { passed++; System.out.println("PASS  " + name); }
        else    { failed++; System.out.println("FAIL  " + name + "   <-- check this"); }
    }
}