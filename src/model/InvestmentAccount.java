package model;

public class InvestmentAccount extends Account implements InterestBearing {
    private final double interestRate = 0.05; // 5% per month
    private final double minDeposit = 500.00; // minimum deposit when opening Investment Acc

    public InvestmentAccount(String accountNumber, String branch, Customer owner, double openingDeposit)
    {
        super(accountNumber, branch, checkOpeningDeposit(owner, openingDeposit));
        deposit(openingDeposit, "Opening deposit");
    }

    public InvestmentAccount(String accountNumber, String branch, Customer owner)
    {
        super(accountNumber, branch, owner);
    }


    private static Customer checkOpeningDeposit(Customer owner, double openingDeposit)
    {
        if (openingDeposit < 500.0)
        {
            throw new IllegalArgumentException("An Investment account must be opened with at least BWP500.00");
        }
        return owner;
    }
    //from model.InterestBearing: 5% of the current balance
    @Override
    public double calculateInterest()
    {
        return Math.round(getBalance() * interestRate * 100.0) / 100.0;
    }
    @Override
    public double getInterestRate()
    {
        return interestRate;
    }
    public double getMinDeposit()
    {
        return minDeposit;
    }
}
