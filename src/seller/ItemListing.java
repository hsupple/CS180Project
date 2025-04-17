package itemlisting;

import java.io.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class that creates a n item listing with a generated ID, a name, a description, a buyer, a price, and checker values.
 *
 * <p>Purdue University -- CS18000 -- Spring 2025</p>
 *
 * @author @Phaynes742
           @hsupple
           @jburkett013
           @addy-ops
 * @version April, 2025
 */

public class ItemListing implements Listing {

    // Define all private values
    private int itemLine;

    private String itemName;
    private String itemDescription;
    private String buyer;

    private boolean isSold;
    private boolean isActive;

    private double buyNowItemPrice;
    private double bidItemPrice;
    private double currentBidPrice;

    private final double auctionDuration;

    private final int itemId;
    private final String seller;
    private static final ReentrantLock FILELOCK = new ReentrantLock();
    private static final List<String> ITEMS = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // Constructor used to build an itemlisting with name, 
    //description, item prices, seller, and duration
    public ItemListing(String itemName, String itemDescription, 
                       double bidItemPrice, String seller, double auctionDuration) {
        this.auctionDuration = auctionDuration;
        this.seller = seller;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.buyNowItemPrice = -1;
        this.bidItemPrice = bidItemPrice;
        this.currentBidPrice = 0;
        this.isSold = false;
        this.buyer = "None";
        loadItemsFromFile();

        this.itemId = generateItemId();
        addItemToList();
        startListing();
    }

    // Void method used to start a listing with a duration
    private void startListing() {
        scheduler.schedule(this::endListing, (long) auctionDuration, TimeUnit.MILLISECONDS);
        this.isActive = true;
    }

    // private method used to generate the next ItemID
    private int generateItemId() {
        int maxId = 0;
        FILELOCK.lock();
        try (BufferedReader reader = new BufferedReader(new FileReader("AuctionList.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        maxId = Math.max(maxId, id);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FILELOCK.unlock();
        }
        return maxId + 1;
    }

    // Private method used to load all items into file
    private void loadItemsFromFile() {
        FILELOCK.lock();
        try {
            ITEMS.clear();
            File file = new File("AuctionList.txt");
            if (!file.exists()) return;
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    ITEMS.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FILELOCK.unlock();
        }
    }

    // Private method used to add a new item to the value list
    private void addItemToList() {
        String newItem = formatItem();
        ITEMS.add(newItem);
        itemLine = ITEMS.size() - 1; 
        saveToFile();
    }

    // Public formatter method used to format the line to be written
    @Override
    public String formatItem() {
        return itemId + "," + itemName + "," + buyNowItemPrice + "," 
            + itemDescription + "," + seller + "," + isSold + "," + buyer + "," + bidItemPrice;
    }

    // public method used to save lines to auction file
    @Override
    public void saveToFile() {
        FILELOCK.lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("AuctionList.txt", false))) {
            for (String line : ITEMS) {
                writer.write(line);
                writer.newLine();  
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FILELOCK.unlock();
        }
    }

    // Void method used to set listing name
    @Override
    public synchronized void setItemName(String itemName) {
        this.itemName = itemName;
        updateItemLine();
    }

    // Void method used to set new Description
    @Override
    public synchronized void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
        updateItemLine();
    }

    // Void method used to set a buy now price
    @Override
    public synchronized void setBuyNowItemPrice(double buyNowItemPrice) {
        this.buyNowItemPrice = buyNowItemPrice;
        updateItemLine();
    }

    // Void method used to se a new bid item price minimum
    @Override
    public synchronized void setBidItemPrice(double bidItemPrice) {
        this.bidItemPrice = bidItemPrice;
        updateItemLine();
    }

    // Void method used to set a new item line
    @Override
    public void updateItemLine() {
        if (itemLine >= 0 && itemLine < ITEMS.size()) {
            ITEMS.set(itemLine, formatItem());
            saveToFile();
        } else {
            System.out.println("Error: Invalid item index (" + itemLine + ")");
        }
    }

    // Void method used to end the listing marking as inactive and sold
    @Override
    public void endListing() {
        this.isActive = false;
        this.isSold = true;
        updateItemLine();
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
            updateItemLine();
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
            updateItemLine();
            return true;
        }
        return false;
    }
}
