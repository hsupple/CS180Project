package src.NewDesign;

public interface Seller1 extends User1 {
    void createItem(Item1 item);
    void deleteItem(String itemId);
    void respondToMessage(Message1 message);
    // Additional seller-specific methods as needed
}