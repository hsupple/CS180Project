package buyer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Buyer {

    private final String Username;
    private String Password;

    private ArrayList<String> messages;

    private boolean Active;

    private final ReentrantLock fileLock = new ReentrantLock();

    public Buyer(String Username, String Password) {
        fileLock.lock();
        this.Username = Username;
        this.Password = Password;

        this.Active = true;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("SellerList.txt", true))) { 
            writer.write(Username + "," + Password + "," + this.Active + "\n"); 
            writer.newLine();    
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileLock.unlock();
        }
    }

    public void sendMessageToSeller(String sellerUsername, String message) {
        
        this.messages.add(sellerUsername);
        this.messages.add(message);

    }

    public void setPassword(String Password) {

        this.Password = Password;

    }

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
                        writer.write(updatedLine + "\n");
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

    public void rateSeller(String sellerUsername, double rating) {
        List<String> updatedLines = new ArrayList<>();
        fileLock.lock();

        try (BufferedReader reader = new BufferedReader(new FileReader("SellerList.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(sellerUsername)) {
                    int currentRatingCount = Integer.parseInt(parts[2]);
                    parts[2] = String.valueOf(currentRatingCount + 1);
                    
                    double currentRating = Double.parseDouble(parts[1]);
                    double newRating = (currentRating + rating) / 2;
                    parts[1] = Double.toString(newRating);
                }
                updatedLines.add(String.join(",", parts));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("SellerList.txt"))) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileLock.unlock();
        }
    }


    public void deleteAccount() {

        this.Active = false;

    }
    
    public String getUsername() {

        return this.Username;

    }
    public String getPassword() {

        return this.Password;

    }

    public boolean isActive() {

        return this.Active;

    }
    public ArrayList<String> getMessages(String User) {

        return messages;

    }

}