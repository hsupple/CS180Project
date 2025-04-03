import java.time.LocalDateTime;

public class Auction implements AuctionInterface {

    public Auction(ItemInterface item, double startingPrice, int durationMinutes) {
    }

    public int getAuctionId() { // Unique auction ID
    }

    public ItemInterface getItem(ItemInterface item) { // The item being auctioned
    }

    public String getSellerUsername(User username) { // Owner of the item
    }

    public LocalDateTime getStartTime() { // Start time of the auction
    }

    public LocalDateTime getEndTime() { // Scheduled end time
    }

    public boolean placeBid(String bidderUsername, double bidAmount) { // Returns true if successful
    }

    public double getCurrentHighestBid() { // Current winning bid
    }

    public String getHighestBidderUsername() { // UserInterface with highest bid
    }

    public void startAuction() { // Sets auction to active
    }

    public void endAuction() { // Ends normally
    }

    public boolean isActive() { // True if auction is live and not expired
    }

    public boolean hasEnded() { // True if ended due to timeout, buy now, or early termination
    }

    public boolean hasBuyItNowOption() { // Is Buy It Now enabled?
    }

    public void setBuyItNowPrice(double price) { // Set Buy It Now price
    }

    public double getBuyItNowPrice() { // Retrieve Buy It Now price
    }

    public boolean buyItNow(String buyerUsername) { // Instantly purchase and end auction
    }

    public void endAuctionEarly() { // Ends auction early (by seller)
    }

    public String getWinner() { // Username of buyer (or null if unsold)
    }

    public double getFinalPrice() {
        return getCurrentHighestBid();
    }

}
