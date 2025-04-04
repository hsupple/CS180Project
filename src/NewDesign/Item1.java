package src.NewDesign;

public interface Item1 {
    String getId();
    String getName();
    String getDescription();
    String getSellerId();
    double getStartingPrice();
    Double getBuyNowPrice(); // Nullable - if not available
    String getStatus(); // "Active", "Ended"
    void setStatus(String status);
    // Additional methods as needed
}
