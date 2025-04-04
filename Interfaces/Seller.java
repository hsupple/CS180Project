import java.util.List;

/**
 * Seller class, connects to SellerInterface
 *
 * @author Jasmine Burkett
 * @version 3 April 2025
 */

public class Seller implements SellerInterface {

    public List<Auction> activeAuctions;
    Auction auction;
    public List<Auction> soldAuctions;

    public Seller(List<Auction> activeAuctions, List<Auction> soldAuctions) {
        this.activeAuctions = activeAuctions;
        this.soldAuctions = soldAuctions;
    }

    public void createAuction(ItemInterface item, double startingPrice, int durationMinutes) {
        Auction auction = new Auction(item, startingPrice, durationMinutes);
        activeAuctions.add(auction);
    }

    public void deleteAuction(int auctionID) {
        auction.getAuctionId();
        activeAuctions.remove(auctionID);
    }

    public List<Auction> getActiveAuctions() {
        return activeAuctions;
    }

    public List<Auction> getSoldAuctions() {
        return soldAuctions;
    }

    //Messaging
    public void respondToMessage(String messageId, String response) {
    }

    public String getStorePage() {
        return activeAuctions.toString();
    }

    public String getStoreCategory() {
    }

    public void setStoreCategory(String category) {
    }



}
