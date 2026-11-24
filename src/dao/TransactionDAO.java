package dao;

import model.Transaction;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class TransactionDAO
{
    public void insert(String accountNumber, Transaction t)
    {
        String sql = "INSERT INTO transactions (transaction_id, account_number, type, amount, reference, "
                + "balance_after, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DataBase.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, t.getTransactionID());
            ps.setString(2, accountNumber);
            ps.setString(3, t.getType());
            ps.setDouble(4, t.getAmount());
            ps.setString(5, t.getReference());
            ps.setDouble(6, t.getBalanceAfter());
            ps.setString(7, t.getTimestamp().toString());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not save transaction: " + e.getMessage(), e);
        }
    }


    public List<Transaction> findByAccount(String accountNumber)
    {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY timestamp";
        try (Connection connection = DataBase.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    list.add(new Transaction(
                            rs.getString("transaction_id"),
                            rs.getString("type"),
                            rs.getDouble("amount"),
                            rs.getString("reference"),
                            rs.getDouble("balance_after"),
                            LocalDateTime.parse(rs.getString("timestamp"))));
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not read transactions: " + e.getMessage(), e);
        }
        return list;
    }

    public int deleteByAccount(String accountNumber)
    {
        String sql = "DELETE FROM transactions WHERE account_number = ?";
        try (Connection connection = DataBase.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, accountNumber);
            return ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not delete transactions: " + e.getMessage(), e);
        }
    }
}