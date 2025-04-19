import java.io.*;
import java.util.ArrayList;

public class Buyer implements BuyerInterface{
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
            client.newBuyer(username, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void sendMessageToSeller(String sellerUsername, String message) {
        client.sendMessage(this.username, sellerUsername, message.replace(" ", "/"));
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
    public void deleteAccount(String username, String password) {
        this.active = false;
        client.deleteAccount(username, password);
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
    public ArrayList<String> search(String query) {

        ArrayList<String> results = client.searchFor(query);
        return results;
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
        Buyer buyer = new Buyer("buyer1", "password123");
        buyer.sendMessageToSeller("seller1", "Hello, I am interested in your item.");
        buyer.setPassword("newpassword123");
        buyer.makeBid("item1", 100.0);
        buyer.search("seller");
    }
}
