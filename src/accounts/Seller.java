import java.io.*;
import java.util.ArrayList;

/**
 * Class representing a seller oobject that can create auctions, end auctions, and perform tasks to provide to the buyer.
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
            client.newSeller(username, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessageToBuyer(String buyer, String message) {
        client.sendMessage(this.username, buyer, message);
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
        client.setPassword(this.username, password);
    }

    @Override
    public void deleteAccount(String username, String password) {
        this.active = false;
        client.deleteAccount(username, password);
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
    public boolean isActive(String name) {
        client.isActive(name);
        return active; 
    }

    @Override
    public String endAuction(String itemID) {
        return client.endListing(itemID);
    }

    @Override
    public ArrayList<String> getMyListings() {
        ArrayList<String> listings;
        
        listings = client.getMyListings(this.username);
        
        return listings;
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

    public static void main(String[] args) {
        // Example usage of the Seller class
        Seller seller = new Seller("seller1", "password123");
        seller.setPassword("newpassword123");
        seller.getMyListings();
        seller.endAuction("1002");
    }
}
