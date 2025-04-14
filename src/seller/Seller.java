import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Seller implements SellerInterface {
    private final String username;
    private String password;
    private double rating;
    private int ratingCount;
    private final CopyOnWriteArrayList<String> messages = new CopyOnWriteArrayList<>();
    private boolean active;
    private final ReentrantLock fileLock = new ReentrantLock();

    public Seller(String username, String password) {
        this.username = username;
        this.password = password;
        this.rating = 0.0;
        this.ratingCount = 0;
        this.active = true;
        writeToFile();
    }

    private void writeToFile() {
        fileLock.lock();
        try {
            File file = new File("SellerList.txt");
            List<String> lines = new ArrayList<>();
            boolean updated = false;
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length >= 5 && parts[0].equals(this.username)) {
                            line = this.username + "," + this.password + "," + this.rating + "," + this.ratingCount + "," + this.active;
                            updated = true;
                        }
                        lines.add(line);
                    }
                }
            }
            if (!updated) {
                lines.add(this.username + "," + this.password + "," + this.rating + "," + this.ratingCount + "," + this.active);
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
                for (String l : lines) {
                    writer.write(l);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileLock.unlock();
        }
    }

    @Override
    public synchronized void sendMessageToBuyer(String buyer, String message) {
        messages.add(buyer);
        messages.add(message);
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
        writeToFile();
    }

    @Override
    public void deleteAccount() {
        this.active = false;
        writeToFile();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getRating() {
        return String.valueOf(rating);
    }

    public int getRatingCount() {
        return ratingCount;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public ArrayList<String> getListings() {
        ArrayList<String> listings = new ArrayList<>();
        File file = new File("AuctionList.txt");
        if (!file.exists()) {
            return listings;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(this.username)) {
                    listings.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listings;
    }

    @Override
    public ArrayList<String> getMessages(String buyer) {
        ArrayList<String> filtered = new ArrayList<>();
        for (int i = 0; i < messages.size(); i += 2) {
            if (messages.get(i).equals(buyer)) {
                filtered.add(messages.get(i + 1));
            }
        }
        return filtered;
    }

    public void loadFromFile() {
        fileLock.lock();
        try (BufferedReader reader = new BufferedReader(new FileReader("SellerList.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[0].equals(this.username)) {
                    this.password = parts[1];
                    this.rating = Double.parseDouble(parts[2]);
                    this.ratingCount = Integer.parseInt(parts[3]);
                    this.active = Boolean.parseBoolean(parts[4]);
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileLock.unlock();
        }
    }
}
