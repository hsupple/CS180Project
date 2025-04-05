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

    void sendMessageToBuyer(String buyerUsername, String message);
    void setPassword(String password);

    void deleteAccount();

    String getUsername();
    String getPassword();
    String getRating();

    boolean isActive();

    ArrayList<String> getListings();
    ArrayList<String> getMessages(String user);

}
