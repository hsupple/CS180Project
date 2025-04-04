package src.NewDesign;

public interface Auction1 {
    void startAuction(String itemId);
    void endAuction(String itemId);
    void placeBid(String itemId, Bid bid);
    // Additional methods as needed
}