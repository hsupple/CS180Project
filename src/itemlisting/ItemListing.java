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

    private void startListing() {
        scheduler.schedule(this::endListing, (long) auctionDuration, TimeUnit.MILLISECONDS);
        this.isActive = true;
    }

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

    private void addItemToList() {
        String newItem = formatItem();
        ITEMS.add(newItem);
        itemLine = ITEMS.size() - 1; 
        saveToFile();
    }

    @Override
    public String formatItem() {
        return itemId + "," + itemName + "," + buyNowItemPrice + "," 
            + itemDescription + "," + seller + "," + isSold + "," + buyer + "," + bidItemPrice;
    }

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

    @Override
    public synchronized void setItemName(String itemName) {
        this.itemName = itemName;
        updateItemLine();
    }

    @Override
    public synchronized void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
        updateItemLine();
    }

    @Override
    public synchronized void setBuyNowItemPrice(double buyNowItemPrice) {
        this.buyNowItemPrice = buyNowItemPrice;
        updateItemLine();
    }

    @Override
    public synchronized void setBidItemPrice(double bidItemPrice) {
        this.bidItemPrice = bidItemPrice;
        updateItemLine();
    }

    @Override
    public void updateItemLine() {
        if (itemLine >= 0 && itemLine < ITEMS.size()) {
            ITEMS.set(itemLine, formatItem());
            saveToFile();
        } else {
            System.out.println("Error: Invalid item index (" + itemLine + ")");
        }
    }

    @Override
    public void endListing() {
        this.isActive = false;
        this.isSold = true;
        updateItemLine();
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public int getItemId() {
        return itemId;
    }

    @Override
    public String getItemName() {
        return itemName;
    }

    @Override
    public String getItemDescription() {
        return itemDescription;
    }

    @Override
    public double getBuyNowItemPrice() {
        return buyNowItemPrice;
    }

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
