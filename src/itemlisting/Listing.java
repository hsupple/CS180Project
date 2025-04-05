package itemlisting;

public interface Listing {

    int getItemId();

    double getBuyNowItemPrice();

    void saveToFile();
    void setItemName(String itemName);
    void setItemDescription(String itemDescription);
    void setBuyNowItemPrice(double buyNowItemPrice);
    void placeBid(double bidPrice, String buyer);
    void setBidItemPrice(double bidItemPrice);
    void updateItemLine();

    boolean buyNow(String buyer);
    boolean isAuctionActive();

    String formatItem();
    String getItemName();
    String getItemDescription();

}