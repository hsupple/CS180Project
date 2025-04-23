package accounts;
import java.io.*;
import java.util.ArrayList;

/**
 * Class representing a seller oobject that can create auctions,
 end auctions, and perform tasks to provide to the buyer.
 *
 * <p>Purdue University -- CS18000 -- Spring 2025</p>
 *
 * @author @Phaynes742
           @hsupple
           @jburkett013
           @addy-ops
 * @version April, 2025
 */

public class Seller implements SellerInterface {
    // Define all variables for seller
    private final String username;
    private String password;
    private double rating;
    private int ratingCount;
    private boolean active;
    private AuctionClient client;

    // Constructor for seller and database connection + registration
    public Seller(String username, String password) {
        this.username = username;
        this.password = password;
        this.rating = 0.0;
        this.ratingCount = 0;
        this.active = true;
        try {
            this.client = new AuctionClient();
            client.newSeller(username, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Send message through client to buyer
    @Override
    public void sendMessageToBuyer(String buyer, String message) {
        client.sendMessage(this.username, buyer, message);
    }

    // Set password locally and through client to database
    @Override
    public void setPassword(String pass) {
        this.password = password;
        client.setPassword(this.username, pass);
    }

    // Delete account globally to the database
    @Override
    public void deleteAccount(String user, String pass) {
        this.active = false;
        client.deleteAccount(user, pass);
    }

    // Returns username
    @Override
    public String getUsername() {
        return username;
    }

    // Returns password
    @Override
    public String getPassword() {
        return password;
    }

    // Returns rating of seller
    @Override
    public String getRating() {
        return String.valueOf(client.getRating(this.username));
    }

    // Returns numbers of ratings
    public int getRatingCount() {
        return ratingCount;
    }

    // Returns active status of users or listings
    @Override
    public boolean isActive(String name) {
        return Boolean.valueOf(client.isActive(name));
    }

    // Allows for ending of an auction through client
    @Override
    public String endAuction(String itemID) {
        return client.endListing(itemID);
    }

    // Returns all listings of the seller
    @Override
    public ArrayList<String> getMyListings() {
        ArrayList<String> listings;
        
        listings = client.getMyListings(this.username);
        
        return listings;
    }

    // Returns arraylist of messages to a buyer
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
