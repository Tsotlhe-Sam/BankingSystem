import dao.*;
import model.*;

import java.io.File;
import java.util.List;

public class ShowDatabase
{
    public static void main(String[] args)
    {
        String fileName = "bank.db";                       // change to "test.db" to look at test data
        if (!new File(fileName).exists())
        {
            System.out.println(fileName + " not found. Run Launcher once first so the database is created.");
            return;
        }
        DataBase.useFile(fileName);

        StaffDAO staffDAO = new StaffDAO();
        CustomerDAO customerDAO = new CustomerDAO();
        AccountDAO accountDAO = new AccountDAO();
        InterestPaymentDAO interestPaymentDAO = new InterestPaymentDAO();

        // ================= STAFF =================
        List<Teller> staff = staffDAO.findAll();
        heading("STAFF (" + staff.size() + ")");
        System.out.printf("%-8s %-10s %-10s %-22s %-12s%n", "ID", "Role", "Username", "Name", "Branch");
        for (Teller t : staff)
        {
            System.out.printf("%-8s %-10s %-10s %-22s %-12s%n", t.getEmployeeID(), t.getRole(),
                    t.getUsername(), t.getFirstName() + " " + t.getSurname(), t.getBranch());
        }

        // ================= CUSTOMERS =================
        List<Customer> customers = customerDAO.findAll();
        heading("CUSTOMERS (" + customers.size() + ")");
        System.out.printf("%-6s %-11s %-36s %-24s %-10s%n", "No.", "Type", "Name", "Address", "Employed");
        for (Customer c : customers)
        {
            String type = (c instanceof IndividualCustomer) ? "Individual" : "Company";
            String employed = (c instanceof IndividualCustomer ind) ? (ind.isEmployed() ? "Yes" : "No") : "-";
            System.out.printf("%-6s %-11s %-36s %-24s %-10s%n", c.getCustomerNumber(), type,
                    c.getDisplayName(), c.getAddress(), employed);
        }

        // ================= ACCOUNTS, TRANSACTIONS, INTEREST =================
        int accountCount = 0;
        int transactionCount = 0;
        double totalHeld = 0;

        heading("ACCOUNTS WITH THEIR TRANSACTIONS");
        for (Customer c : customers)
        {
            List<Account> accounts = accountDAO.findByCustomer(c);
            if (accounts.isEmpty())
            {
                continue;
            }
            System.out.println("\n" + c.getCustomerNumber() + " - " + c.getDisplayName());

            for (Account a : accounts)
            {
                accountCount++;
                totalHeld += a.getBalance();
                System.out.printf("   %-18s %s   balance BWP %,.2f%s%n",
                        a.getClass().getSimpleName(), a.getAccountNumber(), a.getBalance(), extra(a));

                for (Transaction t : a.getTransactions())
                {
                    transactionCount++;
                    System.out.println("        " + t.getDetails());
                }
                for (String payment : interestPaymentDAO.findByAccount(a.getAccountNumber()))
                {
                    System.out.println("        interest payment: " + payment);
                }
            }
        }

        // ================= SUMMARY =================
        heading("SUMMARY");
        System.out.println("Staff:         " + staff.size());
        System.out.println("Customers:     " + customers.size());
        System.out.println("Accounts:      " + accountCount);
        System.out.println("Transactions:  " + transactionCount);
        System.out.printf ("Total held:    BWP %,.2f%n", totalHeld);
    }

    // extra details that only some account types have (polymorphism with instanceof)
    private static String extra(Account a)
    {
        if (a instanceof ChequeAccount chq)
        {
            return "   employer: " + chq.getEmployerName() + ", " + chq.getEmployerAddress();
        }
        if (a instanceof InterestBearing ib)
        {
            return String.format("   interest: %.2f%% per month", ib.getInterestRate() * 100);
        }
        return "";
    }

    private static void heading(String title)
    {
        System.out.println("\n==================== " + title + " ====================");
    }
}
