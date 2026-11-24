package dao;

import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO
{

    public void insert(Customer customer)
    {
        String sql = "INSERT INTO customers (customer_number, user_id, username, password_hash, email, "
                + "phone_number, address, customer_type, first_name, surname, national_id, employed, "
                + "company_name, registration_number, contact_person) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DataBase.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, customer.getCustomerNumber());
            ps.setString(2, customer.getUserID());
            ps.setString(3, customer.getUsername());
            ps.setString(4, customer.getPasswordHash());
            ps.setString(5, customer.getEmail());
            ps.setString(6, customer.getPhoneNumber());
            ps.setString(7, customer.getAddress());

            if (customer instanceof IndividualCustomer ind)
            {
                ps.setString(8, "INDIVIDUAL");
                ps.setString(9, ind.getFirstName());
                ps.setString(10, ind.getSurname());
                ps.setString(11, ind.getNationalID());
                ps.setInt(12, ind.isEmployed() ? 1 : 0);
                ps.setNull(13, Types.VARCHAR);
                ps.setNull(14, Types.VARCHAR);
                ps.setNull(15, Types.VARCHAR);
            }
            else
            {
                CompanyCustomer com = (CompanyCustomer) customer;
                ps.setString(8, "COMPANY");
                ps.setNull(9, Types.VARCHAR);
                ps.setNull(10, Types.VARCHAR);
                ps.setNull(11, Types.VARCHAR);
                ps.setNull(12, Types.INTEGER);
                ps.setString(13, com.getCompanyName());
                ps.setString(14, com.getRegistrationNumber());
                ps.setString(15, com.getContactPerson());
            }
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            // turn database errors into messages the user understands
            String error = String.valueOf(e.getMessage());
            if (error.contains("customers.customer_number"))
            {
                throw new IllegalArgumentException("Customer " + customer.getCustomerNumber() + " is already registered");
            }
            if (error.contains("customers.username"))
            {
                throw new IllegalArgumentException("Username '" + customer.getUsername() + "' is already taken");
            }
            throw new RuntimeException("Could not save customer " + customer.getCustomerNumber() + ": " + error, e);
        }
    }

    // ======================= READ =======================
    public Customer findByNumber(String customerNumber)
    {
        String sql = "SELECT * FROM customers WHERE customer_number = ?";
        try (Connection connection = DataBase.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, customerNumber);
            try (ResultSet rs = ps.executeQuery())
            {
                return rs.next() ? buildCustomer(rs) : null;
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not read customer: " + e.getMessage(), e);
        }
    }

    public List<Customer> findAll()
    {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY customer_number";
        try (Connection connection = DataBase.getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql))
        {
            while (rs.next())
            {
                customers.add(buildCustomer(rs));
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not read customers: " + e.getMessage(), e);
        }
        return customers;
    }

    // ======================= UPDATE =======================
    /** Saves changed contact details and the employed flag. */
    public void update(Customer customer)
    {
        String sql = "UPDATE customers SET email = ?, phone_number = ?, address = ?, employed = ? "
                + "WHERE customer_number = ?";
        try (Connection connection = DataBase.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, customer.getEmail());
            ps.setString(2, customer.getPhoneNumber());
            ps.setString(3, customer.getAddress());
            if (customer instanceof IndividualCustomer ind)
            {
                ps.setInt(4, ind.isEmployed() ? 1 : 0);
            }
            else
            {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, customer.getCustomerNumber());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not update customer: " + e.getMessage(), e);
        }
    }

    // ======================= DELETE =======================
    /** Deletes the customer; their accounts and transactions go too (ON DELETE CASCADE = composition). */
    public boolean delete(String customerNumber)
    {
        String sql = "DELETE FROM customers WHERE customer_number = ?";
        try (Connection connection = DataBase.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, customerNumber);
            return ps.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not delete customer: " + e.getMessage(), e);
        }
    }

    // ======================= helper: one table row -> one Java object =======================
    private Customer buildCustomer(ResultSet rs) throws SQLException
    {
        Customer customer;
        // a placeholder password is passed to the constructor, then replaced with the SAVED hash
        if ("INDIVIDUAL".equals(rs.getString("customer_type")))
        {
            customer = new IndividualCustomer(rs.getString("user_id"), rs.getString("username"), "placeholder",
                    rs.getString("email"), rs.getString("phone_number"), rs.getString("address"),
                    rs.getString("customer_number"), rs.getString("first_name"), rs.getString("surname"),
                    rs.getString("national_id"), rs.getInt("employed") == 1);
        }
        else
        {
            customer = new CompanyCustomer(rs.getString("user_id"), rs.getString("username"), "placeholder",
                    rs.getString("email"), rs.getString("phone_number"), rs.getString("address"),
                    rs.getString("customer_number"), rs.getString("company_name"),
                    rs.getString("registration_number"), rs.getString("contact_person"));
        }
        customer.setPasswordHash(rs.getString("password_hash"));
        return customer;
    }
}

