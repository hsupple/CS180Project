


import java.util.ArrayList;

public interface SellerInterface {

    void sendMessageToBuyer(String buyerUsername, String message);
    void setPassword(String Password);

    void deleteAccount();

    String getUsername();
    String getPassword();
    String getRating();

    boolean isActive();

    ArrayList<String> getListings();
    ArrayList<String> getMessages(String User);

}
