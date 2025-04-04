import java.util.List;

/**
 * Defines seller-specific behaviors on the platform.
 */
public interface Seller extends User {

    // Auction & Listing
    void createAuction(Item item, double startingPrice, int durationMinutes);
    void deleteAuction(int auctionId);
    List<Auction> getActiveAuctions();
    List<Auction> getSoldAuctions();

    // Messaging
    void respondToMessage(String messageId, String response);

    // Store Display
    String getStorePage(); // Could return HTML, Markdown, or string summary
    void updateStoreDescription(String newDescription);

    // Search / Filter
    String getStoreCategory();
    void setStoreCategory(String category);
}