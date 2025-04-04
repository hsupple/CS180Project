import java.util.List;

/**
 * Defines buyer-specific behaviors on the platform.
 */
public interface BuyerInterface {

    // Bidding
    List<Auction> getActiveBids();

    // Messaging
    void sendMessageToSeller(String sellerUsername, String content);

    // History
    List<Auction> getWonAuctions();
    List<Auction> getBidHistory();

    // Ratings
    void leaveRatingForSeller(String sellerUsername, double rating);
}