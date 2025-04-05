package buyer;

import java.util.ArrayList;
/**
 * Interface to align to the Buyer class.
 *
 * <p>Purdue University -- CS18000 -- Spring 2025</p>
 *
 * @author @Phaynes742
           @hsupple
           @jburkett013
           @addy-ops
 * @version April, 2025
 */
public interface BuyerInterface {

    void sendMessageToSeller(String sellerUsername, String message);
    void setPassword(String password);
    void makeBid(String itemID, double price);
    void deleteAccount();
    void rateSeller(String sellerUsername, double rating);

    String getUsername();
    String getPassword();

    boolean isActive(); 
    ArrayList<String> getMessages(String user);

}