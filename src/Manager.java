import java.util.ArrayList;
import java.util.List;

    public class Manager extends Teller
    {
        public Manager(String userID, String username, String password, String email, String phoneNumber,
                       String address, String employeeID, String branch, String firstName, String surname)
        {
            super(userID, username, password, email, phoneNumber, address, employeeID, branch, firstName, surname);
        }

        // only managers can see every account in the bank
        public List<Account> viewAllAccounts()
        {
            if (!isLoggedin())
            {
                throw new IllegalStateException("Manager must be logged in first");
            }
            return new ArrayList<>(allAccounts);
        }

        @Override
        public String getRole()
        {
            return "Manager";
        }
    }