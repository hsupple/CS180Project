package src.NewDesign;

public class Bid {
    private String id;
    private String itemId;
    private String userId;
    private double bidAmount;

    public Bid(String id, String itemId, String userId, double bidAmount) {
        this.id = id;
        this.itemId = itemId;
        this.userId = userId;
        this.bidAmount = bidAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(double bidAmount) {
        this.bidAmount = bidAmount;
    }
}