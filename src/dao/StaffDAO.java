package dao;

import model.Manager;
import model.Teller;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** All JDBC code for the staff table. Tellers and managers share one table; role says which. */
public class StaffDAO
{
    // ======================= CREATE =======================
    public void insert(Teller member)
    {
        String sql = "INSERT INTO staff (employee_id, user_id, username, password_hash, email, phone_number, "
                + "address, branch, first_name, surname, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DataBase.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, member.getEmployeeID());
            ps.setString(2, member.getUserID());
            ps.setString(3, member.getUsername());
            ps.setString(4, member.getPasswordHash());
            ps.setString(5, member.getEmail());
            ps.setString(6, member.getPhoneNumber());
            ps.setString(7, member.getAddress());
            ps.setString(8, member.getBranch());
            ps.setString(9, member.getFirstName());
            ps.setString(10, member.getSurname());
            ps.setString(11, (member instanceof Manager) ? "MANAGER" : "TELLER");
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not save staff member: " + e.getMessage(), e);
        }
    }

    // ======================= READ =======================
    public List<Teller> findAll()
    {
        List<Teller> staff = new ArrayList<>();
        String sql = "SELECT * FROM staff ORDER BY employee_id";
        try (Connection connection = DataBase.getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql))
        {
            while (rs.next())
            {
                Teller member;
                if ("MANAGER".equals(rs.getString("role")))
                {
                    member = new Manager(rs.getString("user_id"), rs.getString("username"), "placeholder",
                            rs.getString("email"), rs.getString("phone_number"), rs.getString("address"),
                            rs.getString("employee_id"), rs.getString("branch"),
                            rs.getString("first_name"), rs.getString("surname"));
                }
                else
                {
                    member = new Teller(rs.getString("user_id"), rs.getString("username"), "placeholder",
                            rs.getString("email"), rs.getString("phone_number"), rs.getString("address"),
                            rs.getString("employee_id"), rs.getString("branch"),
                            rs.getString("first_name"), rs.getString("surname"));
                }
                member.setPasswordHash(rs.getString("password_hash"));   // the SAVED hash
                staff.add(member);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not read staff: " + e.getMessage(), e);
        }
        return staff;
    }
}