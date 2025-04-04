import java.time.LocalDateTime;

public interface ItemInterface {

    // Identification
    int getItemId();                      // Unique item ID
    String getTitle();                    // Item name
    void setTitle(String title);

    //  Description
    String getDescription();
    void setDescription(String description);

    // Pricing
    double getStartingPrice();            // Auction starting price
    void setStartingPrice(double price);

    // Categorization
    String getCategory();                 // e.g., "Electronics", "Clothing"
    void setCategory(String category);

    // SellerInterface Info
    String getSellerUsername();           // Used for linking to SellerInterface profile

    // Timestamps
    LocalDateTime getListingTime();       // When item was posted
    void setListingTime(LocalDateTime time);

    // Optional: Multimedia
    String getImageUrl();                 // Link to image (for extra credit)
    void setImageUrl(String url);

    // Status
    boolean isSold();                     // Whether it has been purchased
    void markAsSold();                    // Set sold status
}
