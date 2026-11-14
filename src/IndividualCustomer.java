public class IndividualCustomer extends Customer
{
    private String firstName;
    private String surname;
    private String nationalID;
    private boolean employed;

    public IndividualCustomer(String userID, String username,String password, String email, String phoneNumber
                              ,String address, String customerNumber,String firstName, String surname, String nationalID, boolean employed)
    {
        super(userID, username,password,email,phoneNumber,address,customerNumber);

        if (firstName == null || firstName.isBlank())
        {
            throw new IllegalArgumentException("Please Enter your firstName");
        }
        if (surname == null || surname.isBlank())
        {
            throw new IllegalArgumentException("Enter surname");
        }
        if (nationalID == null || nationalID.isBlank())
            throw new IllegalArgumentException("Enter national ID (OmangNO)");

        this.firstName = firstName;
        this.surname = surname;
        this.nationalID = nationalID;
        this.employed = employed;
    }
    @Override
    public String getDisplayName()
    {
        return firstName + "  " + surname;
    }

    public boolean isEmployed()
    {
        return employed;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getSurname()
    {
        return surname;
    }

    public String getNationalID()
    {
        return nationalID;
    }

    // setters


    public void setEmployed(boolean employed)
    {
        this.employed = employed;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }
}
