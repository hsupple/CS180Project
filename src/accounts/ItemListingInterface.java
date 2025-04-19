
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
public interface ItemListingInterface {

    // Interface to house all class methods
    int getItemId();

    double getBuyNowItemPrice();

    void endListing();
    void setItemName(String itemName);
    void setItemDescription(String itemDescription);
    void setBuyNowItemPrice(double buyNowItemPrice);
    void placeBid(double bidPrice, String buyer);
    void setBidItemPrice(double bidItemPrice);
 
    boolean buyNow(String buyer);
    boolean isActive();
    
    String getItemName();
    String getItemDescription();

}