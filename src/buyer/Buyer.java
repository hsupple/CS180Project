package buyer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Buyer {
    // Define all private values
    private final String Username;
    private String Password;

    private ArrayList<String> messages;

    private boolean Active;

    private final ReentrantLock fileLock = new ReentrantLock();
    
    //Constructor for Buyer class, initialize name and password
    public Buyer(String Username, String Password) {
        this.Username = Username;
        this.Password = Password;

        this.Active = true;
        writeline();
    }

    // writeline method to format and write line to the database files
    private void writeline() {
        fileLock.lock();

        try (BufferedReader reader = new BufferedReader(new FileReader("BuyerList.txt"))) {
            String line;
            List<String> lines = new ArrayList<>();
            int LineFound = 0;
            while ((line = reader.readLine()) != null) {
                if (line.contains(this.Username)) {
                    line = this.Username + "," + this.Password + "," + this.Active;
                    LineFound = 1;   
                }
                lines.add(line);
                
            }
            if (LineFound == 0) {
                lines.add(this.Username + "," + this.Password);
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("BuyerList.txt", false))) { 
                for (String newline : lines) {
                    writer.write(newline); 
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } 
        } catch (IOException e) {
                e.printStackTrace();
            } finally {
                fileLock.unlock();
            }
    }

    // method to add messages to array to be sent across server to client
    public void sendMessageToSeller(String sellerUsername, String message) {
        
        this.messages.add(sellerUsername);
        this.messages.add(message);

    }

    // Setter class to set password for buyer user
    public void setPassword(String Password) {

        this.Password = Password;
        writeline();

    }

    // Buyer method to made a bid using a price and valid itemID
    public void makeBid(String itemID, double price) {
        fileLock.lock();
        try (BufferedReader reader = new BufferedReader(new FileReader("AuctionList.txt"))) {
            String line;
            List<String> lines = new ArrayList<>();
            boolean itemFound = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(itemID)) {
                    parts[6] = this.Username;  
                    parts[7] = String.valueOf(price);
                    line = String.join(",", parts); 
                    itemFound = true;
                }
                lines.add(line);
            }

           if (itemFound) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("AuctionList.txt"))) {
                    for (String updatedLine : lines) {
                        writer.write(updatedLine);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Item not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileLock.unlock();
        }
    }

    // Method used to rate a seller by accessing sellerlist and averaging count and num
    public void rateSeller(String sellerUsername, double rating) {
        List<String> updatedLines = new ArrayList<>();
        fileLock.lock();

        try (BufferedReader reader = new BufferedReader(new FileReader("SellerList.txt"))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts[0].equals(sellerUsername)) {
                    double currentRatingCount = Double.parseDouble(parts[2]);
                    double currentRating = Double.parseDouble(parts[2]) * Double.parseDouble(parts[3]);

                    parts[2] = String.valueOf(currentRatingCount + 1);
                    
                    parts[3] = String.valueOf(Double.parseDouble(parts[3]) + 1);
                    double newRating = (currentRating + rating) / (Double.parseDouble(parts[3]));
                    parts[2] = Double.toString(newRating).substring(0,4);
                }
                updatedLines.add(String.join(",", parts));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("SellerList.txt"))) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileLock.unlock();
        }
    }

    // Void method used to delete Buyer account
    public void deleteAccount() {

        this.Active = false;
        writeline();

    }
    
    // getter method for username
    public String getUsername() {

        return this.Username;

    }

    // Getter password for password
    public String getPassword() {

        return this.Password;

    }

    // Method used to display the status of active
    public boolean isActive() {

        return this.Active;

    }

    // Getter method to get message arraylist
    public ArrayList<String> getMessages(String User) {

        return this.messages;

    }

}