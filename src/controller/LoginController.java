package controller;

import model.*;
import java.util.ArrayList;
import java.util.List;


 public class LoginController
{
    private final List<Teller> staff = new ArrayList<>();
    private Teller currentUser;


    public void addStaff(Teller member)
    {
        staff.add(member);
    }

    public Result login(String username, String password)
    {
        // 1. check the input
        if (username == null || username.isBlank() || password == null || password.isBlank())
        {
            return Result.fail("Enter your username and password");
        }

        // 2. find the staff member and let the model check the password
        for (Teller member : staff)
        {
            if (member.getUsername().equalsIgnoreCase(username.trim()))
            {
                if (member.login(password))
                {
                    currentUser = member;
                    return Result.ok("Welcome, " + member.getFirstName() + " (" + member.getRole() + ")");
                }
                break;
            }
        }

        // same message either way, so nobody can guess which usernames exist
        return Result.fail("Incorrect username or password");
    }

    public void logout()
    {
        if (currentUser != null)
        {
            currentUser.logout();
            currentUser = null;
        }
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
