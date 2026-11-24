package dao;
import model.InterestPayment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InterestPaymentDAO
{
    public void insert(InterestPayment payment)
    {
        String sql = "INSERT INTO interest_payments (payment_id, account_number, amount, payment_date, status) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DataBase.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, payment.getPaymentID());
            ps.setString(2, payment.getAccount().getAccountNumber());
            ps.setDouble(3, payment.getAmount());
            if (payment.getPaymentDate() != null)
            {
                ps.setString(4, payment.getPaymentDate().toString());
            }
            else
            {
                ps.setNull(4, Types.VARCHAR);       // rejected payments have no payment date
            }
            ps.setString(5, payment.getStatus());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not save interest payment: " + e.getMessage(), e);
        }
    }
    public List<String> findByAccount(String accountNumber)
    {
        List<String> lines = new ArrayList<>();
        String sql = "SELECT * FROM interest_payments WHERE account_number = ? ORDER BY payment_date";
        try (Connection connection = DataBase.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    lines.add(rs.getString("payment_id") + " | " + rs.getString("status")
                            + " | BWP " + String.format("%.2f", rs.getDouble("amount"))
                            + " | " + rs.getString("payment_date"));
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not read interest payments: " + e.getMessage(), e);
        }
        return lines;
    }
}