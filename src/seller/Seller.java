package seller;

import java.io.*;
import java.util.ArrayList;
import serverclient.AuctionClient;

public class Seller implements SellerInterface {
    private final String username;
    private String password;
    private double rating;
    private int ratingCount;
    private boolean active;
    private AuctionClient client;

    public Seller(String username, String password) {
        this.username = username;
        this.password = password;
        this.rating = 0.0;
        this.ratingCount = 0;
        this.active = true;
        try {
            this.client = new AuctionClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void sendMessageToBuyer(String buyer, String message) {
        client.sendMessage(this.username, buyer, message);
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
        client.setPassword(this.username, password);
    }

    @Override
    public void deleteAccount() {
        this.active = false;
        client.deleteAccount(this.username, this.password);
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
        client.getRating(this.username);
        return String.valueOf(rating);
    }

    public int getRatingCount() {
        return ratingCount;
    }

    @Override
    public boolean isActive() {
        client.isActive(this.username);
        return active; 
    }

    /*@Override
    public ArrayList<String> getListings() {
        /*## IMPLEMENT LOGIC HERE ##*/
    /*}
    */

    @Override
    public ArrayList<String> getMessages(String buyer) {
        ArrayList<String> messages = new ArrayList<>();
        try {
            messages = client.getMessages(this.username, buyer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return messages;
    }

}
