import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Transaction
{
    private final String transactionID;
    private final String type;
    private final double amount;
    private final String reference;
    private final double balanceAfter;
    private final LocalDateTime timestamp;

    public Transaction(String type, double amount, String reference, double balanceAfter)
    {
        if (type == null || type.isBlank())
        {
            throw new IllegalArgumentException("Enter transaction type");
        }
        if(amount <= 0)
        {
            throw new IllegalArgumentException("Enter a valid amount");
        }
        this.transactionID= UUID.randomUUID().toString();
        this.type = type;
        this.amount = amount;
        this.reference = reference;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now();
    }

    public String getTransactionID()
    {
        return transactionID;
    }

    public String getType()
    {
        return type;
    }

    public double getAmount()
    {
        return amount;
    }

    public String getReference()
    {
        return reference;
    }

    public double getBalanceAfter()
    {
        return balanceAfter;
    }

    public LocalDateTime getTimestamp()
    {
        return timestamp;
    }

    public String getDetail()
    {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("vvvv-MM-dd HH:mm;ss");
        return timestamp.format(format)
                + "  |  " + type
                + "  |  " + reference
                + "  | BWP " + String.format("%.2f", amount)
                + "  | Balance: BWP " + String.format("%.2f", balanceAfter);
    }

    @Override
    public String toString()
    {
        return "Transaction[" + transactionID + "," + type +", "+ amount + "]";
    }
}
