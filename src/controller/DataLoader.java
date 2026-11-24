package controller;

import dao.*;
import model.*;
import java.util.ArrayList;
import java.util.List;


public class DataLoader
{
    public static void loadAll(LoginController loginController)
    {
        DataBase.createSchema();
        SampleData.insertIfEmpty();

        // staff who can log in
        for (Teller member : new StaffDAO().findAll())
        {
            loginController.addStaff(member);
        }

        // customers, and each customer's accounts (with their transaction history)
        List<Customer> customers = new CustomerDAO().findAll();
        List<Account> accounts = new ArrayList<>();
        AccountDAO accountDAO = new AccountDAO();
        for (Customer customer : customers)
        {
            accounts.addAll(accountDAO.findByCustomer(customer));
        }
        Teller.restoreRecords(customers, accounts);
    }
}