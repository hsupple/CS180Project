package seller;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Seller implements SellerInterface{

    private final String Username;
    private String Password;

    private ArrayList<String> messages;

    private double rating;
    private int ratingCount = 0;
    private boolean Active;

    private final ReentrantLock fileLock = new ReentrantLock();

    public Seller(String Username, String Password) {
        this.Username = Username;
        this.Password = Password;
        this.rating = 0;

        Active = true;
        writeline();
    }

    private void writeline() {
        fileLock.lock();

        try (BufferedReader reader = new BufferedReader(new FileReader("SellerList.txt"))) {
            String line;
            ArrayList<String> lines = new ArrayList<>();
            int LineFound = 0;
            while ((line = reader.readLine()) != null) {
                if (line.contains(this.Username)) {
                    line = this.Username + "," + this.Password + "," + this.rating + "," + this.ratingCount + "," + this.Active;
                    LineFound = 1;   
                }
                lines.add(line);
                
            }
            if (LineFound == 0) {
                lines.add(this.Username + "," + this.Password + "," + this.rating + "," + this.ratingCount + "," + this.Active);
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("SellerList.txt", false))) { 
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

    @Override
    public void sendMessageToBuyer(String buyerUsername, String message) {
        
        this.messages.add(buyerUsername);
        this.messages.add(message);

    }

    @Override
    public void setPassword(String Password) {

        this.Password = Password;

    }

    @Override
    public ArrayList<String> getListings() {
        fileLock.lock();
        ArrayList<String> myListings = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("AuctionList.txt"))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[4].equals(this.Username)) {
                    myListings.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileLock.unlock();
        }

        return myListings;
    }

    @Override
    public void deleteAccount() {

        this.Active = false;
        writeline();

    }

    @Override
    public String getRating() {
        
        try (BufferedReader reader = new BufferedReader(new FileReader("SellerList.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(this.Username)) {
                    return parts[2];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getUsername() {

        return this.Username;

    }

    @Override
    public String getPassword() {

        return this.Password;

    }

    @Override
    public boolean isActive() {

        return this.Active;

    }

    @Override
    public ArrayList<String> getMessages(String User) {

        return messages;

    }

}