package controller;

import model.*;
import java.util.ArrayList;
import java.util.List;


public class CustomerController
{
    private final LoginController loginController;

    public CustomerController(LoginController loginController)
    {
        this.loginController = loginController;
    }

    public Result registerIndividual(String customerNumber, String firstName, String surname,
                                     String nationalID, String email, String phone, String address,
                                     boolean employed, String username, String password)
    {
        if (!loginController.isLoggedIn())
        {
            return Result.fail("Please log in first");
        }
        try
        {
            IndividualCustomer customer = new IndividualCustomer("U" + clean(customerNumber),
                    clean(username), password, clean(email), clean(phone), clean(address),
                    clean(customerNumber), clean(firstName), clean(surname), clean(nationalID), employed);
            loginController.getCurrentUser().registerCustomer(customer);
            return Result.ok("Registered " + customer.getDisplayName() + " as " + customer.getCustomerNumber());
        }
        catch (RuntimeException e)
        {
            return Result.fail(e.getMessage());     // the model's rule, e.g. "Enter surname"
        }
    }

    public Result registerCompany(String customerNumber, String companyName, String registrationNumber,
                                  String contactPerson, String email, String phone, String address,
                                  String username, String password)
    {
        if (!loginController.isLoggedIn())
        {
            return Result.fail("Please log in first");
        }
        try
        {
            CompanyCustomer customer = new CompanyCustomer("U" + clean(customerNumber),
                    clean(username), password, clean(email), clean(phone), clean(address),
                    clean(customerNumber), clean(companyName), clean(registrationNumber), clean(contactPerson));
            loginController.getCurrentUser().registerCustomer(customer);
            return Result.ok("Registered " + customer.getDisplayName() + " as " + customer.getCustomerNumber());
        }
        catch (RuntimeException e)
        {
            return Result.fail(e.getMessage());
        }
    }


    public String getCustomerName(String customerNumber)
    {
        Customer c = find(customerNumber);
        return (c == null) ? null : c.getDisplayName();
    }


    public List<String> getAccountSummaries(String customerNumber)
    {
        List<String> lines = new ArrayList<>();
        Customer c = find(customerNumber);
        if (c != null)
        {
            for (Account a : c.getAccounts())
            {
                lines.add(a.getClass().getSimpleName() + " " + a.getAccountNumber()
                        + " | BWP " + String.format("%.2f", a.getBalance()));
            }
        }
        return lines;
    }

    // used by AccountController (same package), not by the views
    Customer find(String customerNumber)
    {
        if (!loginController.isLoggedIn() || customerNumber == null || customerNumber.isBlank())
        {
            return null;
        }
        return loginController.getCurrentUser().findCustomer(customerNumber.trim());
    }

    private static String clean(String text)
    {
        return (text == null) ? null : text.trim();
    }
}
