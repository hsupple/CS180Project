import java.util.ArrayList;

/**
 * Interface for the Seller class to align to.
 *
 * <p>Purdue University -- CS18000 -- Spring 2025</p>
 *
 * @author @Phaynes742
           @hsupple
           @jburkett013
           @addy-ops
 * @version April, 2025
 */

public interface SellerInterface {

    // Define all Seller public methods
    void sendMessageToBuyer(String buyerUsername, String message);
    void setPassword(String password);

    void deleteAccount(String username, String password);

    String endAuction(String itemID);
    String getUsername();
    String getPassword();
    String getRating();

    boolean isActive(String name);

    ArrayList<String> getMyListings();
    ArrayList<String> getMessages(String buyer);

}
