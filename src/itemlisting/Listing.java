package itemlisting;

public interface Listing {

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