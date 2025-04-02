import java.time.LocalDateTime;

public interface Auction {

    // Basic Auction Info
    int getAuctionId();                      // Unique auction ID
    Item getItem();                          // The item being auctioned
    String getSellerUsername();              // Owner of the item
    LocalDateTime getStartTime();            // Start time of the auction
    LocalDateTime getEndTime();              // Scheduled end time

    // Bidding
    boolean placeBid(String bidderUsername, double bidAmount);  // Returns true if successful
    double getCurrentHighestBid();                              // Current winning bid
    String getHighestBidderUsername();                          // User with highest bid

    //Auction Control
    void startAuction();                    // Sets auction to active
    void endAuction();                      // Ends normally
    boolean isActive();                     // True if auction is live and not expired
    boolean hasEnded();                     // True if ended due to timeout, buy now, or early termination

    //Buy It Now (Extra Credit)
    boolean hasBuyItNowOption();            // Is Buy It Now enabled?
    void setBuyItNowPrice(double price);    // Set Buy It Now price
    double getBuyItNowPrice();              // Retrieve Buy It Now price
    boolean buyItNow(String buyerUsername); // Instantly purchase and end auction

    //
    void endAuctionEarly();                 // Ends auction early (by seller)

    //
    String getWinner();                     // Username of buyer (or null if unsold)
    double getFinalPrice();                 // Final sale price (bid or Buy It Now)
}