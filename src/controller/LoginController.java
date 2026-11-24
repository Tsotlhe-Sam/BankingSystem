package controller;

import model.*;
import java.util.ArrayList;
import java.util.List;


 public class LoginController
{
    private final List<Teller> staff = new ArrayList<>();
    private Teller currentUser;
    private Customer currentCustomer;

    public void addStaff(Teller member)
    {
        staff.add(member);
    }

    public void addDefaultStaff()
    {
        addStaff(new Teller("U100", "teller", "teller123", "teller@bank.co.bw", "3901234",
                "Gaborone", "E001", "Main Mall", "Kabo", "Motse"));
        addStaff(new Manager("U200", "manager", "manager123", "manager@bank.co.bw", "3905678",
                "Palapye", "E002", "Main Mall", "Lorato", "Setlhare"));
    }
    public Result login(String username, String password)
    {
        // 1. check the input
        if (username == null || username.isBlank() || password == null || password.isBlank())
        {
            return Result.fail("Enter your username and password");
        }
        logout();


        for (Teller member : staff)
        {
            if (member.getUsername().equalsIgnoreCase(username.trim()))
            {
                if (member.login(password))
                {
                    currentUser = member;
                    return Result.ok("Welcome, " + member.getFirstName() + " (" + member.getRole() + ")");
                }
                return Result.fail("Incorrect username or password");
            }
        }

        Customer customer = Teller.findCustomerByUsername(username.trim());
        if (customer != null && customer.login(password))
        {
            currentCustomer = customer;
            return Result.ok("Welcome, " + customer.getDisplayName() + " (Customer)");
        }


        return Result.fail("Incorrect username or password");
    }
    public void logout()
    {
        if (currentUser != null)
        {
            currentUser.logout();
            currentUser = null;
        }
        if (currentCustomer != null)
        {
            currentCustomer.logout();
            currentCustomer = null;
        }
    }
    public boolean isCustomerLoggedIn()
    {
        return currentCustomer != null;
    }

    Customer getCurrentCustomer()
    {
        return currentCustomer;
    }

    public String getCurrentCustomerName()
    {
        return (currentCustomer == null) ? "" : currentCustomer.getDisplayName();
    }
    public boolean isLoggedIn()
    {
        return currentUser != null && currentUser.isLoggedin();
    }

    public boolean isManager()
    {
        return currentUser instanceof Manager;
    }

    public Teller getCurrentUser()
    {
        return currentUser;
    }
}
