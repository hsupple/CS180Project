package src.NewDesign;

public class SellerImpl extends UserImpl implements Seller1 {
    public SellerImpl(String id, String username, String password, String role) {
        super(id, username, password, role);
    }

    @Override
    public void createItem(Item1 item) {
        // Logic for creating an item listing
    }

    @Override
    public void deleteItem(String itemId) {
        // Logic for deleting an item listing
    }

    @Override
    public void respondToMessage(Message1 message) {
        // Logic for responding to a message
    }
}
