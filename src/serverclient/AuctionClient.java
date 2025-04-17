package serverclient;

import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class AuctionClient {
 
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private final int port = 3001;
    private final String host = "localhost";

    public AuctionClient() throws IOException {
        this.socket = new Socket(this.host, this.port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void sendMessage(String user, String recipient, String message) {
        out.println("SENDMESS " + user + ":" + recipient + ":" + message);
    }

    public ArrayList<String> getMessages(String user, String user2) throws IOException {
        out.println("GETMESS " + user + " " + user2);
        String response = in.readLine();
        ArrayList<String> messages = new ArrayList<>();
        if (response != null && !response.isEmpty()) {
            String[] parts = response.split(",");
            for (String part : parts) {
                messages.add(part.trim());
            }
        }
        return messages;
    }

    public void setPassword(String user, String password) {
        out.println("SETPASSWORD " + user + " " + password);
    }

    public void deleteAccount(String user, String password) {
        out.println("DELETE " + user + " " + password);
    }

    public void isActive(String user) {
        out.println("ISACTIVE" + user);
    }

    public void getRating(String user) {
        out.println("GETRATING " + user);
    }

    public void setRating(String user, double rating) {
        out.println("SETRATING " + user + " " + rating);
    }

    public String receive() throws IOException {
        return in.readLine();
    }

    public void startAuction(String itemID, String itemName, double buyNowItemPrice, String itemDescription, String seller, boolean isSold, String buyer, double bidItemPrice) {
        out.println("STARTAUCTION " + itemID + " " + itemName + " " + buyNowItemPrice + " " + itemDescription + " " + seller + " " + isSold + " " + buyer + " " + bidItemPrice);
    }

    public void endListing(String itemID) {
        out.println("ENDLISTING " + itemID);
    }

    public void buyNow(String itemID, String user) {
        out.println("BUY " + itemID + " "  + user);
    }

    public void makeBid(String itemID, String user, double price) {
        out.println("MAKEBID " + itemID + " " + user + " " + price);
    }

    public void close() throws IOException {
        socket.close();
    }

}