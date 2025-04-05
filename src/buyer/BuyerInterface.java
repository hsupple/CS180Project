package buyer;

import java.util.ArrayList;

public interface BuyerInterface {

    void sendMessageToSeller(String sellerUsername, String message);
    void setPassword(String Password);
    void makeBid(String itemID, double price);
    void deleteAccount();
    void rateSeller(String sellerUsername, double rating);

    String getUsername();
    String getPassword();

    boolean isActive(); 
    ArrayList<String> getMessages(String User);

}