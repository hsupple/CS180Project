package src.NewDesign;

import java.util.List;

public interface Buyer1 extends User1 {
    void placeBid(Bid bid);
    void purchaseItem(String itemId); // For "Buy It Now" purchases
    List<Bid> getBids(); // Retrieve bids made by the buyer
}
