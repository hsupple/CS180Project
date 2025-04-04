import java.io.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class ItemListing {

    private int itemId;
    private String itemName;
    private String itemDescription;
    private double buyNowItemPrice;
    private double bidItemPrice;
    private int itemLine;
    private static final ReentrantLock fileLock = new ReentrantLock(); 
    private static final List<String> items = new CopyOnWriteArrayList<>();

    public ItemListing(String itemName, String itemDescription, double bidItemPrice) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.buyNowItemPrice = -1;
        this.bidItemPrice = bidItemPrice;
        this.itemId = generateItemId();
        loadItemsFromFile();
        addItemToList();
    }

    private int generateItemId() {
        int maxId = 0;
        fileLock.lock(); 
        try (BufferedReader reader = new BufferedReader(new FileReader("AuctionList.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                items.add(line);
                String[] parts = line.split(",");
                if (parts.length >= 1) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        maxId = Math.max(maxId, id);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileLock.unlock(); 
        }
        return maxId + 1;
    }

    private void loadItemsFromFile() {
        fileLock.lock();
        try {
            items.clear();
            File file = new File("AuctionList.txt");
            if (!file.exists()) return;
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    items.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileLock.unlock();
        }
    }

    private void addItemToList() {
        String newItem = formatItem();
        items.add(newItem);
        saveToFile();
    }

    private String formatItem() {
        return itemId + "," + itemName + "," + bidItemPrice + "," + buyNowItemPrice + "," + itemDescription;
    }

    public void saveToFile() {
        fileLock.lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("AuctionList.txt", false))) {
            for (String line : items) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileLock.unlock();
        }
    }

    public synchronized void setItemName(String itemName) {
        this.itemName = itemName;
        updateItemLine();
    }

    public synchronized void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
        updateItemLine();
    }

    public synchronized void setBuyNowItemPrice(double buyNowItemPrice) {
        this.buyNowItemPrice = buyNowItemPrice;
        updateItemLine();
    }

    public synchronized void setBidItemPrice(double bidItemPrice) {
        this.bidItemPrice = bidItemPrice;
        updateItemLine();
    }

    private void updateItemLine() {
        if (itemLine >= 0 && itemLine < items.size()) {
            items.set(itemLine, formatItem());
            saveToFile();
        } else {
            System.out.println("Error: Invalid item index.");
        }
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public double getBuyNowItemPrice() {
        return buyNowItemPrice;
    }
}
