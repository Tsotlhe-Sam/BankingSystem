package dao;

import model.*;

public class SampleData
{
    public static void insertIfEmpty()
    {
        StaffDAO staffDAO = new StaffDAO();
        if (staffDAO.findAll().isEmpty())
        {
            staffDAO.insert(new Teller("U100", "teller", "teller123", "teller@bank.co.bw", "3901234",
                    "Francistown", "E001", "Galo Mall", "Kabo", "Motse"));
            staffDAO.insert(new Manager("U200", "manager", "manager123", "manager@bank.co.bw", "3905678",
                    "Gaborone", "E002", "Main Mall", "Lorato", "Setlhare"));
        }

        CustomerDAO customerDAO = new CustomerDAO();
        if (!customerDAO.findAll().isEmpty())
        {
            return;                                   // already has data: don't add it twice
        }
        AccountDAO accountDAO = new AccountDAO();

        // ---------- 7 individual customers ----------
        IndividualCustomer thabo = individual(1, "Thabo", "Molefe", "Plot 123, Gaborone", "71234567", true);
        IndividualCustomer naledi = individual(2, "Naledi", "Kgosi", "Ward 4, Maun", "72345678", false);
        IndividualCustomer kagiso = individual(3, "Kagiso", "Seretse", "Block 8, Francistown", "73456789", true);
        IndividualCustomer lesego = individual(4, "Lesego", "Phiri", "Extension 2, Lobatse", "74567890", true);
        IndividualCustomer mpho = individual(5, "Mpho", "Dube", "Area W, Francistown", "75678901", false);
        IndividualCustomer boitumelo = individual(6, "Boitumelo", "Ramotswa", "Tati Siding, Francistown", "76789012", true);
        IndividualCustomer tumelo = individual(7, "Tumelo", "Mokgosi", "Maruapula, Gaborone", "77890123", true);

        // ---------- 3 company customers ----------
        CompanyCustomer okavango = company(8, "Okavango Tours", "BW00012345", "Naledi Kgosi", "Maun", "6861111");
        CompanyCustomer kalahari = company(9, "Kalahari Logistics", "BW00067890", "Kagiso Seretse", "Ghanzi", "6592222");
        CompanyCustomer chobe = company(10, "NKK EXPRESs", "BW00024680", "Lesego Phiri", "Gaborone", "6253333");

        for (Customer c : new Customer[] { thabo, naledi, kagiso, lesego, mpho, boitumelo, tumelo, okavango, kalahari, chobe })
        {
            customerDAO.insert(c);
        }

        // ---------- accounts (numbers 1000001 ...) ----------
        int n = 1000001;

        SavingsAccount s1 = savings(n++, thabo, 1500);
        InvestmentAccount i1 = new InvestmentAccount(String.valueOf(n++), "Main Mall", thabo, 5000);
        i1.withdraw(500);
        ChequeAccount c1 = new ChequeAccount(String.valueOf(n++), "Main Mall", thabo, "SAM", "Jwaneng");
        c1.creditSalary(8500);
        c1.withdraw(1200);

        SavingsAccount s2 = savings(n++, naledi, 800);

        ChequeAccount c3 = new ChequeAccount(String.valueOf(n++), "Main Mall", kagiso,
                "Botswana Power Corporation", "Gaborone");
        c3.creditSalary(12000);
        InvestmentAccount i3 = new InvestmentAccount(String.valueOf(n++), "Main Mall", kagiso, 2500);

        SavingsAccount s4 = savings(n++, lesego, 3200);

        InvestmentAccount i5 = new InvestmentAccount(String.valueOf(n++), "Main Mall", mpho, 1000);

        ChequeAccount c6 = new ChequeAccount(String.valueOf(n++), "Main Mall", boitumelo, "Choppies", "Francistown");
        c6.creditSalary(6400);
        SavingsAccount s6 = savings(n++, boitumelo, 500);

        SavingsAccount s7 = savings(n++, tumelo, 250);

        InvestmentAccount i8 = new InvestmentAccount(String.valueOf(n++), "Main Mall", okavango, 25000);
        SavingsAccount s8 = savings(n++, okavango, 10000);

        InvestmentAccount i9 = new InvestmentAccount(String.valueOf(n++), "Main Mall", kalahari, 50000);

        SavingsAccount s10 = savings(n++, chobe, 4000);

        for (Account a : new Account[] { s1, i1, c1, s2, c3, i3, s4, i5, c6, s6, s7, i8, s8, i9, s10 })
        {
            accountDAO.insert(a);                    // also saves each account's transactions
        }
    }

    private static IndividualCustomer individual(int i, String first, String surname, String address,
                                                 String phone, boolean employed)
    {
        return new IndividualCustomer(String.format("U%03d", i), first.toLowerCase(), "pass123",
                first.toLowerCase() + "@mail.co.bw", phone, address, String.format("C%03d", i),
                first, surname, String.format("%09d", 100000000 + i * 1111), employed);
    }

    private static CompanyCustomer company(int i, String name, String regNo, String contact,
                                           String address, String phone)
    {
        String username = name.split(" ")[0].toLowerCase();
        return new CompanyCustomer(String.format("U%03d", i), username, "pass123",
                "info@" + username + ".co.bw", phone, address, String.format("C%03d", i),
                name, regNo, contact);
    }

    private static SavingsAccount savings(int number, Customer owner, double openingDeposit)
    {
        SavingsAccount account = new SavingsAccount(String.valueOf(number), "Main Mall", owner);
        account.deposit(openingDeposit, "Opening deposit");
        return account;
    }
}