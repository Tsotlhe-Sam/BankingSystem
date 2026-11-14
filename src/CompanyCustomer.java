public class CompanyCustomer extends Customer
{
  private String companyName;
  private final String registrationNumber;
  private String  contactPerson;

  public CompanyCustomer(String userID,String username,String password, String email, String phoneNumber
          ,String address, String customerNumber,String companyName, String registrationNumber, String contactPerson)
  {
      super( userID, username,password,email,phoneNumber, address, customerNumber);
      if (companyName == null || companyName.isBlank())
      {
          throw  new IllegalArgumentException("Enter company name");
      }
      if (registrationNumber == null || registrationNumber.isBlank())
      {
          throw new IllegalArgumentException("Enter registration number");
      }

      this.companyName= companyName;
      this.registrationNumber = registrationNumber;
      this.contactPerson = contactPerson;
  }

  @Override
    public String  getDisplayName()
  {
      return companyName + " (Reg. " + registrationNumber + ")";
  }

    public String getCompanyName()
    {
        return companyName;
    }

    public String getRegistrationNumber()
    {
        return registrationNumber;
    }

    public String getContactPerson()
    {
        return contactPerson;
    }

    //setters

    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    public void setContactPerson(String contactPerson)
    {
        this.contactPerson = contactPerson;
    }
}
