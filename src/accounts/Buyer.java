package accounts;
import java.io.*;
import java.util.ArrayList;

/**
 * Class representing a buyer object that can bid and buy auctions
 *
 * <p>Purdue University -- CS18000 -- Spring 2025</p>
 *
 * @author @Phaynes742
           @hsupple
           @jburkett013
           @addy-ops
 * @version April, 2025
 */

public class Buyer implements BuyerInterface {

    // Define all variables for buyer
    private final String username;
    private String password;
    private boolean active;
    private AuctionClient client;

    // buyer constructor to register new buyer
    public Buyer(String username, String password) {
        this.username = username;
        this.password = password;
        this.active = true;
        try {
            this.client = new AuctionClient();
            client.newBuyer(username, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Send message through client to seller
    @Override
    public synchronized void sendMessageToSeller(String sellerUsername, String message) {
        client.sendMessage(this.username, sellerUsername, message.replace(" ", "/"));
    }

    // Set password locally and through client to database
    @Override
    public void setPassword(String password) {
        this.password = password;
        client.setPassword(this.username, password);
    }

    // Make a bid on itemId
    @Override
    public void makeBid(String itemID, double price) {
        client.makeBid(itemID, this.username, price);
    }

    // Rate a seller
    @Override
    public void rateSeller(String sellerUsername, double rating) {
        client.setRating(sellerUsername, rating);
    }

    // Delete account through client to database
    @Override
    public void deleteAccount(String user, String pass) {
        this.active = false;
        client.deleteAccount(user, pass);
    }

    // Return username
    @Override
    public String getUsername() {
        return this.username;
    }

    // Return password
    @Override
    public String getPassword() {
        return this.password;
    }

    // Request status of user or listing
    @Override
    public boolean isActive(String user) {
        client.isActive(user);
        return this.active;
    }

    // Return arraylist of listings and sellers
    @Override
    public ArrayList<String> search(String query) {

        ArrayList<String> results = client.searchFor(query);
        return results;
    }

    // Get messages between you and a seller
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
