import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class that creates an item listing with a generated ID, a name, a description, a buyer, a price, and checker values.
 *
 * <p>Purdue University -- CS18000 -- Spring 2025</p>
 *
 * @author @Phaynes742
           @hsupple
           @jburkett013
           @addy-ops
 * @version April, 2025
 */

public class ItemListing implements ItemListingInterface {

    // Define all private values
    private String itemName;
    private String itemDescription;
    private String buyer;

    private boolean isSold;
    private boolean isActive;

    private double buyNowItemPrice;
    private double bidItemPrice;
    private double currentBidPrice;

    private final double auctionDuration;

    private int itemId;
    private final String seller;
    private AuctionClient client;
    private static final ReentrantLock FILELOCK = new ReentrantLock();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // Constructor used to build an itemlisting with name, 
    //description, item prices, seller, and duration
    public ItemListing(String itemName, String itemDescription, 
                       double bidItemPrice, String seller, double auctionDuration) {
        this.auctionDuration = auctionDuration;
        this.seller = seller;
        this.itemName = itemName.replace(" ", "/"); 
        this.itemDescription = itemDescription.replace(" ", "/");
        this.buyNowItemPrice = -1;
        this.bidItemPrice = bidItemPrice;
        this.currentBidPrice = 0;
        this.isSold = false;
        this.buyer = "None";

        try {
            this.client = new AuctionClient();
            this.itemId = generateItemId();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startListing();
        formatItem();
    }

    // Void method used to start a listing with a duration
    private void startListing() {
        scheduler.schedule(this::endListing, (long) auctionDuration, TimeUnit.MILLISECONDS);
        this.isActive = true;
    }

    // private method used to generate the next ItemID
    private int generateItemId() {
        return client.getItemID();
    }

    // Public formatter method used to format the line to be written
    private String formatItem() {
        client.updateItemListing(itemId, itemName.replace(" ", "/"), itemDescription.replace(" ", "/"), buyNowItemPrice, seller, isSold, buyer, bidItemPrice);
        return itemId + "," + itemName + "," + buyNowItemPrice + "," 
            + itemDescription + "," + seller + "," + isSold + "," + buyer + "," + bidItemPrice;
    }

    // Void method used to set listing name
    @Override
    public synchronized void setItemName(String itemName) {
        this.itemName = itemName;
        formatItem();
    }

    // Void method used to set new Description
    @Override
    public synchronized void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
        formatItem();
    }

    // Void method used to set a buy now price
    @Override
    public synchronized void setBuyNowItemPrice(double buyNowItemPrice) {
        this.buyNowItemPrice = buyNowItemPrice;
        formatItem();
    }

    // Void method used to se a new bid item price minimum
    @Override
    public synchronized void setBidItemPrice(double bidItemPrice) {
        this.bidItemPrice = bidItemPrice;
        formatItem();
    }

    // Void method used to end the listing marking as inactive and sold
    @Override
    public void endListing() {
        this.isActive = false;
        this.isSold = true;
        formatItem();
    }

    // Getter boolean to return active status
    @Override
    public boolean isActive() {
        return isActive;
    }

    // Getter method to get item ID
    @Override
    public int getItemId() {
        return itemId;
    }

    // Getter method to get Item Name
    @Override
    public String getItemName() {
        return itemName;
    }

    // Getter method to get item description
    @Override
    public String getItemDescription() {
        return itemDescription;
    }

    // Getter method used to get buy now price
    @Override
    public double getBuyNowItemPrice() {
        return buyNowItemPrice;
    }

    // Void method to place a bid on the item
    @Override
    public synchronized void placeBid(double bidPrice, String currentBuyer) {
        if (!isSold && bidPrice > currentBidPrice && bidPrice >= bidItemPrice) {
            this.currentBidPrice = bidPrice;
            this.buyer = currentBuyer;
            formatItem();
        } else {
            System.out.println("Bid not accepted. Please check the conditions.");
        }
    }

    // Buy now method to end the auction and purchase item
    @Override
    public synchronized boolean buyNow(String currentBuyer) {
        if (!this.isSold && this.buyNowItemPrice > 0) {
            this.isSold = true;
            this.buyer = currentBuyer;
            formatItem();
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        ItemListing item = new ItemListing("item2", "This is a test item", 10.0, "Seller1", 10000);
    }

}
