package dao;

import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * All JDBC code for the accounts table: Create, Read, Update, Delete.
 * Savings, Investment and Cheque accounts share one table; account_type says which is which.
 */
public class AccountDAO
{
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public void insert(Account account)
    {
        String sql = "INSERT INTO accounts (account_number, customer_number, account_type, branch, balance, "
                + "interest_rate, min_deposit, employer_name, employer_address) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DataBase.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, account.getAccountNumber());
            ps.setString(2, account.getOwner().getCustomerNumber());
            ps.setString(3, typeOf(account));
            ps.setString(4, account.getBranch());
            ps.setDouble(5, account.getBalance());

            if (account instanceof InterestBearing ib)
            {
                ps.setDouble(6, ib.getInterestRate());
            }
            else
            {
                ps.setNull(6, Types.REAL);
            }

            if (account instanceof InvestmentAccount inv)
            {
                ps.setDouble(7, inv.getMinDeposit());
            }
            else
            {
                ps.setNull(7, Types.REAL);
            }

            if (account instanceof ChequeAccount chq)
            {
                ps.setString(8, chq.getEmployerName());
                ps.setString(9, chq.getEmployerAddress());
            }
            else
            {
                ps.setNull(8, Types.VARCHAR);
                ps.setNull(9, Types.VARCHAR);
            }
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not save account " + account.getAccountNumber() + ": " + e.getMessage(), e);
        }

        for (Transaction t : account.getTransactions())
        {
            transactionDAO.insert(account.getAccountNumber(), t);
        }
    }

    public Account findByNumber(String accountNumber)
    {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection connection = DataBase.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery())
            {
                if (!rs.next())
                {
                    return null;
                }
                Customer owner = customerDAO.findByNumber(rs.getString("customer_number"));
                return buildAccount(rs, owner);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not read account: " + e.getMessage(), e);
        }
    }

    public List<Account> findByCustomer(Customer owner)
    {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_number = ? ORDER BY account_number";
        try (Connection connection = DataBase.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, owner.getCustomerNumber());
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    list.add(buildAccount(rs, owner));
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not read accounts: " + e.getMessage(), e);
        }
        return list;
    }

    public void updateBalance(Account account)
    {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (Connection connection = DataBase.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setDouble(1, account.getBalance());
            ps.setString(2, account.getAccountNumber());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not update balance: " + e.getMessage(), e);
        }
    }
    public void recordTransaction(Account account, Transaction t)
    {
        String updateSql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        String insertSql = "INSERT INTO transactions (transaction_id, account_number, type, amount, reference, "
                + "balance_after, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DataBase.getConnection())
        {
            connection.setAutoCommit(false);              // start: nothing is saved until commit()
            try (PreparedStatement update = connection.prepareStatement(updateSql);
                 PreparedStatement insert = connection.prepareStatement(insertSql))
            {
                update.setDouble(1, account.getBalance());
                update.setString(2, account.getAccountNumber());
                update.executeUpdate();

                insert.setString(1, t.getTransactionID());
                insert.setString(2, account.getAccountNumber());
                insert.setString(3, t.getType());
                insert.setDouble(4, t.getAmount());
                insert.setString(5, t.getReference());
                insert.setDouble(6, t.getBalanceAfter());
                insert.setString(7, t.getTimestamp().toString());
                insert.executeUpdate();

                connection.commit();                      // both saved together
            }
            catch (SQLException e)
            {
                connection.rollback();                    // something failed: undo both
                throw e;
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not save the transaction: " + e.getMessage(), e);
        }
    }
    public boolean delete(String accountNumber)
    {
        String sql = "DELETE FROM accounts WHERE account_number = ?";
        try (Connection connection = DataBase.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, accountNumber);
            return ps.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not delete account: " + e.getMessage(), e);
        }
    }

    private String typeOf(Account account)
    {
        if (account instanceof SavingsAccount)    return "SAVINGS";
        if (account instanceof InvestmentAccount) return "INVESTMENT";
        return "CHEQUE";
    }

    private Account buildAccount(ResultSet rs, Customer owner) throws SQLException
    {
        String number = rs.getString("account_number");
        String branch = rs.getString("branch");
        Account account;

        switch (rs.getString("account_type"))
        {
            case "SAVINGS"    -> account = new SavingsAccount(number, branch, owner);
            case "INVESTMENT" -> account = new InvestmentAccount(number, branch, owner);   // loading constructor
            default           -> account = new ChequeAccount(number, branch, (IndividualCustomer) owner,
                    rs.getString("employer_name"), rs.getString("employer_address"));
        }

        account.restoreBalance(rs.getDouble("balance"));
        for (Transaction t : transactionDAO.findByAccount(number))
        {
            account.restoreTransaction(t);
        }
        return account;
    }
}