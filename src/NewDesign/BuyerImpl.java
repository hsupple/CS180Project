package src.NewDesign;

import java.util.ArrayList;
import java.util.List;

public class BuyerImpl extends UserImpl implements Buyer1 {
    public BuyerImpl(String id, String username, String password, String role) {
        super(id, username, password, role);
    }

    // Implementing Buyer-specific methods
    @Override
    public void placeBid(Bid bid) {
        // Logic for placing a bid
    }

    @Override
    public void purchaseItem(String itemId) {
        // Logic for purchasing via "Buy It Now"
    }

    @Override
    public List<Bid> getBids() {
        // Logic for retrieving buyer's bids; dummy return for now
        List<Bid> bids = new ArrayList<>();
        return bids;
    }
}
