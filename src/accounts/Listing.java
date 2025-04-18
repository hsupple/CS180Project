
/**
 * Interface for item listing to align item listing class.
 *
 * <p>Purdue University -- CS18000 -- Spring 2025</p>
 *
 * @author @Phaynes742
           @hsupple
           @jburkett013
           @addy-ops
 * @version April, 2025
 */
public interface Listing {

    // Interface to house all class methods
    int getItemId();

    double getBuyNowItemPrice();

    void endListing();
    void saveToFile();
    void setItemName(String itemName);
    void setItemDescription(String itemDescription);
    void setBuyNowItemPrice(double buyNowItemPrice);
    void placeBid(double bidPrice, String buyer);
    void setBidItemPrice(double bidItemPrice);
    void updateItemLine();

    boolean buyNow(String buyer);
    boolean isActive();
    
    String formatItem();
    String getItemName();
    String getItemDescription();

}