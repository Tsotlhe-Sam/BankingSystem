public class SavingsAccount extends Account implements InterestBearing
{
    private final double interestRate = 0.0005; // 0.05 % per month

    public SavingsAccount(String accountNumber, String branch, Customer owner)
    {
        super( accountNumber, branch , owner);
    }
    // savings accounts do not allow withdrawals
    @Override
    public void withdraw(double amount)
    {
        throw new UnsupportedOperationException("Withdrawals are not allowed from a Savings account");
    }
    //from InterestBearing : this month's interest on the current  balance
    @Override
    public double calculateInterest()
    {
        return Math.round(getBalance()* interestRate * 100.0)/ 100.0;
    }

    @Override
    public double getInterestRate()
    {
        return interestRate;
    }
}
