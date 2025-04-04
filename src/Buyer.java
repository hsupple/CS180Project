import java.util.List;

/**
 * Defines buyer-specific behaviors on the platform.
 */
public interface Buyer extends User {

    // Bidding
    boolean placeBid(int auctionId, double amount);
    List<Auction> getActiveBids();

    // Buy It Now
    boolean buyNow(int auctionId);

    // Messaging
    void sendMessageToSeller(String sellerUsername, String content);

    // History
    List<Auction> getWonAuctions();
    List<Auction> getBidHistory();

    // Ratings
    void leaveRatingForSeller(String sellerUsername, double rating);
}