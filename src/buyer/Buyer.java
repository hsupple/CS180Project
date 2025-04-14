import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Buyer implements BuyerInterface {
    private final String username;
    private String password;
    private final CopyOnWriteArrayList<String> messages = new CopyOnWriteArrayList<>();
    private boolean active;
    private final ReentrantLock fileLock = new ReentrantLock();

    public Buyer(String username, String password) {
        this.username = username;
        this.password = password;
        this.active = true;
        writeToFile();
    }

    private void writeToFile() {
        fileLock.lock();
        try (BufferedReader reader = new BufferedReader(new FileReader("BuyerList.txt"))) {
            String line;
            List<String> lines = new ArrayList<>();
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(this.username + ",")) {
                    line = this.username + "," + this.password + "," + this.active;
                    found = true;
                }
                lines.add(line);
            }
            if (!found) {
                lines.add(this.username + "," + this.password + "," + this.active);
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("BuyerList.txt", false))) {
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
    public synchronized void sendMessageToSeller(String sellerUsername, String message) {
        messages.add(sellerUsername);
        messages.add(message);
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
        writeToFile();
    }

    @Override
    public void makeBid(String itemID, double price) {
        fileLock.lock();
        try (BufferedReader reader = new BufferedReader(new FileReader("AuctionList.txt"))) {
            String line;
            List<String> lines = new ArrayList<>();
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(itemID)) {
                    parts[7] = String.valueOf(price);
                    line = String.join(",", parts);
                    found = true;
                }
                lines.add(line);
            }
            if (found) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("AuctionList.txt", false))) {
                    for (String l : lines) {
                        writer.write(l);
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileLock.unlock();
        }
    }

    @Override
    public void rateSeller(String sellerUsername, double rating) {
        List<String> lines = new ArrayList<>();
        fileLock.lock();
        try (BufferedReader reader = new BufferedReader(new FileReader("SellerList.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(sellerUsername)) {
                    double currentRating = Double.parseDouble(parts[2]);
                    double ratingCount = Double.parseDouble(parts[3]);
                    double newRating = ((currentRating * ratingCount) + rating) / (ratingCount + 1);
                    parts[2] = String.format("%.2f", newRating);
                    parts[3] = String.valueOf((int) (ratingCount + 1));
                    line = String.join(",", parts);
                }
                lines.add(line);
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("SellerList.txt", false))) {
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
    public boolean isActive() {
        return active;
    }

    @Override
    public ArrayList<String> getMessages() {
        return new ArrayList<>(messages);
    }
}
