package model;

public class ChequeAccount extends Account {
    private String employerName;
    private String employerAddress;

    public ChequeAccount(String accountNumber, String branch, IndividualCustomer owner,
                         String employerName, String employerAddress) {
        super(accountNumber, branch, checkOwner(owner));

        if (employerName == null || employerName.isBlank()) {
            throw new IllegalArgumentException("Enter employer name");
        }
        if (employerAddress == null || employerAddress.isBlank()) {
            throw new IllegalArgumentException("Enter employer address");
        }

        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    private static IndividualCustomer checkOwner(IndividualCustomer owner)
    {
        if(owner != null && !owner.isEmployed())
        {
            throw new IllegalArgumentException("A Cheque account can only be opened by an employed customer ");
        }
        return owner;
    }

    public void creditSalary(double amount) {
        deposit(amount, "Salary - " + employerName);
    }


    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public String getEmployerAddress() {
        return employerAddress;
    }

    public void setEmployerAddress(String employerAddress) {
        this.employerAddress = employerAddress;
    }
}
