package src.NewDesign;

public class ItemImpl implements Item1 {
    private String id;
    private String name;
    private String description;
    private String sellerId;
    private double startingPrice;
    private Double buyNowPrice;
    private String status;

    public ItemImpl(String id, String name, String description, String sellerId, double startingPrice, Double buyNowPrice, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sellerId = sellerId;
        this.startingPrice = startingPrice;
        this.buyNowPrice = buyNowPrice;
        this.status = status;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSellerId() {
        return null;
    }

    @Override
    public double getStartingPrice() {
        return 0;
    }

    @Override
    public Double getBuyNowPrice() {
        return null;
    }

    @Override
    public String getStatus() {
        return null;
    }

    @Override
    public void setStatus(String status) {

    }
}
