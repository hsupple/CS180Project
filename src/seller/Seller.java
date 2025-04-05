import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class that creates a Seller user, which is password protected and 
 may make listings. Also has a rating from the seller class.
 *
 * <p>Purdue University -- CS18000 -- Spring 2025</p>
 *
 * @author @Phaynes742
           @hsupple
           @jburkett013
           @addy-ops
 * @version April, 2025
 */

public class Seller {

    // Declare all private variables
    private final String username;
    private String password;
    private double rating;
    private int ratingCount;
    private ArrayList<String> messages;
    private boolean active;
    private final ReentrantLock fileLock = new ReentrantLock();

    // Public constructor with username and password for new Seller
    public Seller(String username, String password) {
        this.username = username;
        this.password = password;
        this.rating = 0.0;
        this.ratingCount = 0;
        this.messages = new ArrayList<>();
        this.active = true;
        writeToFile();
    }

    // Private class to write new line to the file
    private void writeToFile() {
        fileLock.lock();
        try {
            File file = new File("SellerList.txt");
            List<String> lines = new ArrayList<>();

            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    boolean updated = false;

                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length >= 5 && parts[0].equals(this.username)) {
                            line = this.username + "," + this.password + "," 
                                + this.rating + "," + this.ratingCount + "," + this.active;
                            updated = true;
                        }
                        lines.add(line);
                    }

                    if (!updated) {
                        lines.add(this.username + "," + this.password + "," + this.rating 
                                  + "," + this.ratingCount + "," + this.active);
                    }
                }
            } else {
                lines.add(this.username + "," + this.password + "," + this.rating + "," 
                          + this.ratingCount + "," + this.active);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileLock.unlock();
        }
    }

    // Void method to set a new password
    public void setPassword(String newPassword) {
        this.password = newPassword;
        writeToFile();
    }

    // Getter method to return password
    public String getPassword() {
        return this.password;
    }

    // Getter method to return username
    public String getUsername() {
        return this.username;
    }

    // Void method to send a message over the server
    public void sendMessageToBuyer(String buyer, String message) {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        this.messages.add(buyer);
        this.messages.add(message);
    }

    // Getter method to return all message logs
    public ArrayList<String> getMessages() {
        return this.messages;
    }

    // Getter method to return messages from buyer
    public ArrayList<String> getMessages(String buyer) {
        ArrayList<String> filtered = new ArrayList<>();
        for (int i = 0; i < messages.size(); i += 2) {
            String storedBuyer = messages.get(i);
            String msg = messages.get(i + 1);
            if (storedBuyer.equals(buyer)) {
                filtered.add(msg);
            }
        }
        return filtered;
    }

    // Getter method for seller rating
    public double getRating() {
        return this.rating;
    }

    // Getter method for Active status
    public boolean isActive() {
        return this.active;
    }

    // Void method for deleting account
    public void deleteAccount() {
        this.active = false;
        writeToFile();
    }

    // Void method to load all values from file
    public void loadFromFile() {
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
        }
    }

    // Getter method to return all listings from buyer
    public List<String> getListings() {
        List<String> listings = new ArrayList<>();
        File file = new File("AuctionList.txt");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return listings;
            }
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
}
