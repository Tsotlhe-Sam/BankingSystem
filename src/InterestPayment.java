import java.time.LocalDate;

public class InterestPayment
{
    // the states from the state diagram
    public static final String PENDING = "PENDING";
    public static final String CALCULATED = "CALCULATED";
    public static final String PAID = "PAID";
    public static final String REJECTED = "REJECTED";

    private static int counter = 1;

    private final String paymentID;
    private final Account account;      // "earns" association
    private double amount;
    private LocalDate paymentDate;
    private String status;

    public InterestPayment(Account account)
    {
        if (account == null)
        {
            throw new IllegalArgumentException("Interest payment needs an account");
        }
        this.paymentID = String.format("IP%05d", counter++);
        this.account = account;
        this.status = PENDING;
    }

    // only Savings and Investment accounts with money in them qualify
    public boolean checkEligibility()
    {
        if (account instanceof InterestBearing && account.getBalance() > 0)
        {
            return true;
        }
        status = REJECTED;
        return false;
    }

    // polymorphism: each account type uses its own rate
    public double calculateAmount()
    {
        if (!status.equals(PENDING))
        {
            throw new IllegalStateException("Payment " + paymentID + " is already " + status);
        }
        amount = ((InterestBearing) account).calculateInterest();
        status = CALCULATED;
        return amount;
    }

    public void creditAccount()
    {
        if (!status.equals(CALCULATED))
        {
            throw new IllegalStateException("Calculate the amount before crediting");
        }
        if (amount <= 0)
        {
            status = REJECTED;     // balance too small to earn any thebe
            return;
        }
        account.creditInterest(amount, paymentID);
        paymentDate = LocalDate.now();
        status = PAID;
    }

    public String getPaymentID()      { return paymentID; }
    public Account getAccount()       { return account; }
    public double getAmount()         { return amount; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public String getStatus()         { return status; }
}
