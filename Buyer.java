import java.util.List;

/**
 * Buyer class, connects to BuyerInterface
 *
 * @author Jasmine Burkett
 * @version 3 April 2025
 */

public class Buyer implements BuyerInterface {

    public Auction auction;
    public List<Auction> wonAuctions;
    public List<Auction> activeBids;
    public List<Auction> bidHistory;

    public List<Auction> getActiveBids() {
        return activeBids;
    }

    // Messaging
    public void sendMessageToSeller(String sellerUsername, String content) {
    }

    public List<Auction> getWonAuctions() {
        return wonAuctions;
    }

    public List<Auction> getBidHistory() {
        return bidHistory;
    }

    public void leaveRatingForSeller(String sellerUsername, double rating) {

    }

}
