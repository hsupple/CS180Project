import java.io.IOException;
import java.util.ArrayList;

/**
 * Interface to align to the Auction client class.
 *
 * <p>Purdue University -- CS18000 -- Spring 2025</p>
 *
 * @author @Phaynes742
           @hsupple
           @jburkett013
           @addy-ops
 * @version April, 2025
 */

public interface AuctionClientInterface {

    int getItemID();

    String newBuyer(String user, String password);
    String newSeller(String user, String password);
    String updateItemListing(int itemId, String itemName, String itemDescription, double buyNowItemPrice, String seller, boolean isSold, String buyer, double bidItemPrice);
    String sendMessage(String user, String recipient, String message);
    String setPassword(String user, String password);
    String deleteAccount(String user, String password);
    String isActive(String user);
    String getRating(String user);
    String setRating(String user, double rating);
    String startAuction(String itemID, String itemName, double buyNowItemPrice, String itemDescription, String seller, boolean isSold, String buyer, double bidItemPrice);
    String buyNow(String itemID, String user);
    String makeBid(String itemID, String user, double price);
    String endListing(String itemID);

    ArrayList<String> getMessages(String user, String user2) throws IOException;
    ArrayList<String> searchFor(String query);
    ArrayList<String> getMyListings(String user);
}