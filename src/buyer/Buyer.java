package buyer;

import java.io.*;
import java.util.ArrayList;
import serverclient.AuctionClient;

public class Buyer implements BuyerInterface {
    private final String username;
    private String password;
    private boolean active;
    private AuctionClient client;

    public Buyer(String username, String password) {
        this.username = username;
        this.password = password;
        this.active = true;
        try {
            this.client = new AuctionClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void sendMessageToSeller(String sellerUsername, String message) {
        client.sendMessage(this.username, message, message);
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
        client.setPassword(this.username, password);
    }

    @Override
    public void makeBid(String itemID, double price) {
        client.makeBid(itemID, this.username, price);
    }

    @Override
    public void rateSeller(String sellerUsername, double rating) {
        client.setRating(sellerUsername, rating);
    }

    @Override
    public void deleteAccount() {
        this.active = false;
        client.deleteAccount(this.username, this.password);
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isActive(String user) {
        client.isActive(user);
        return this.active;
    }

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
