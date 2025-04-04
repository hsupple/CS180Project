import java.time.LocalDateTime;

public class Item implements ItemInterface {

    public int getItemId() { // Unique item ID
    }

    public String getTitle() { // Item name
    }

    public void setTitle(String title) {
    }

    public String getDescription() {
    }

    public void setDescription(String description) {
    }

    public double getStartingPrice() { // Auction starting price
    }

    public void setStartingPrice(double price) {
    }

    public String getCategory() { // e.g., "Electronics", "Clothing"
    }

    public void setCategory(String category) {
    }

    public String getSellerUsername() { // Used for linking to SellerInterface profile
    }

    public LocalDateTime getListingTime() { // When item was posted
    }

    public void setListingTime(LocalDateTime time) {
    }

    public String getImageUrl() { // Link to image (for extra credit)
    }

    public void setImageUrl(String url) {
    }

    public boolean isSold() { // Whether it has been purchased
    }

    public void markAsSold() { // Set sold status
    }

}
