import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class User
{
    private final String userID;
    private final String username;
    private String passwordHash;
    private String email;
    private String phoneNumber;
    private String address;
    private boolean loggedin;

    public User(String userID, String username, String password,String email, String phoneNumber, String address)
    {
        if (username == null || username.isBlank()){
            throw new IllegalArgumentException("Enter Username");
        }
        if (password == null || password.isBlank()){
            throw new IllegalArgumentException("Enter password");
        }
        this.userID = userID;
        this.username = username;
        this.passwordHash = hash(password);
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;

    }
    public boolean login(String password)
    {
     boolean ok = passwordHash.equals(hash(password));
     this.loggedin = ok;
     return ok;
      }

      public void logout()
      {
          this.loggedin = false;
      }
      public void resetPassword(String newPassword)
      {
          this.passwordHash = hash(newPassword);
      }
      public abstract String getRole();

    public String getUserID()
    {
        return userID;
    }

    public String getUsername()

    {
        return username;
    }

    public String getEmail()
    {
        return email;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public String getAddress()
    {
        return address;
    }

    public boolean isLogin()
    {
        return loggedin;
    }

    // setters

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private static String hash(String text)
    {
     try
     {
         MessageDigest md = MessageDigest.getInstance("SHA-256");
         byte[]bytes = md.digest(text.getBytes(StandardCharsets.UTF_8));
         StringBuilder sb = new StringBuilder();
         for (byte b : bytes) sb.append(String.format("%02x",b));
         return sb.toString();
     } catch(NoSuchAlgorithmException e)
     {
         throw new IllegalStateException("SHA-256 not available", e);
     }
     }
}
