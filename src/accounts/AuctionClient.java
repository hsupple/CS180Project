import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class to define client for objects to connect to server.
 *
 * <p>Purdue University -- CS18000 -- Spring 2025</p>
 *
 * @author @Phaynes742
           @hsupple
           @jburkett013
           @addy-ops
 * @version April, 2025
 */

public class AuctionClient implements AuctionClientInterface {
 
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

    @Override
    public String newBuyer(String user, String password) {
        try {
            out.println("NEWBUYER " + user + " " + password);
            out.flush();
            return in.readLine();
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    @Override
    public String newSeller(String user, String password) {
        try {
            out.println("NEWSELLER " + user + " " + password);
            out.flush();
            return in.readLine();
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    @Override
    public String updateItemListing(int itemId, String itemName, String itemDescription, double buyNowItemPrice, String seller, boolean isSold, String buyer, double bidItemPrice) {

        try {
            out.println("UPDATEITEM " + itemId + " " + itemName + " " + itemDescription + " " + buyNowItemPrice + " " + seller + " " + isSold + " " + buyer + " " + bidItemPrice);
            out.flush();
            return in.readLine();
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    @Override
    public int getItemID() {
        try {
            out.println("GETITEMID 0 0");
            out.flush();
            
            String response = in.readLine();
            return Integer.parseInt(response.trim());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public String sendMessage(String user, String recipient, String message) {
        try {
            out.println("SENDMESS " + user + " " + recipient + " " + message);
            out.flush();
            return in.readLine();
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    @Override
    public ArrayList<String> getMessages(String user, String user2) throws IOException {
        out.println("GETMESS " + user + " " + user2);
        out.flush();
        
        ArrayList<String> messages = new ArrayList<>();
        Collections.addAll(messages, in.readLine().split(","));

        return messages;
    }

    @Override
    public String setPassword(String user, String password) {
        try {
            out.println("SETPASSWORD " + user + " " + password);
            out.flush();
            return in.readLine();
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    @Override
    public String deleteAccount(String user, String password) {
        try {
            out.println("DELETE " + user + " " + password);
            out.flush();
            return in.readLine();
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    @Override
    public String isActive(String user) {
        try {
            out.println("ISACTIVE " + user);
            out.flush();
            return in.readLine();
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    @Override
    public String getRating(String user) {
        try {
            out.println("GETRATING " + user);
            out.flush();
            return in.readLine();
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    @Override
    public String setRating(String user, double rating) {
        try {
            out.println("SETRATING " + user + " " + rating);
            out.flush();
            return in.readLine();
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    @Override
    public String receive() throws IOException {
        try {
            return in.readLine();
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    @Override
    public String startAuction(String itemID, String itemName, double buyNowItemPrice, String itemDescription, String seller, boolean isSold, String buyer, double bidItemPrice) {
         try {
        // Use "|" as delimiter for joining multi-word strings (item name and description)
        System.out.print(itemDescription);
        String message = "STARTAUCTION " 
                         + itemID + " "
                         + itemName + " "
                         + buyNowItemPrice + " "
                         + itemDescription + " " 
                         + seller + " "
                         + isSold + " "
                         + buyer + " "
                         + bidItemPrice;
        System.out.print(itemDescription);

        //out.println(message);
        //out.flush();
        return in.readLine();
    } catch (IOException e) {
        e.printStackTrace();
    }
        return "Error";
    }

    @Override
    public String endListing(String itemID) {
        try {
            out.println("ENDLISTING " + itemID);
            out.flush();
            return in.readLine();
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    @Override
    public String buyNow(String itemID, String user) {
        try {
            out.println("BUY " + itemID + " "  + user);
            out.flush();
            return in.readLine();
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    @Override
    public String makeBid(String itemID, String user, double price) {
        try {
            out.println("MAKEBID " + itemID + " " + user + " " + price);
            out.flush();
            return in.readLine();
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    @Override
    public ArrayList<String> searchFor(String query) {
        try {
            out.println("SEARCH " + query);
            out.flush();
            
            ArrayList<String> results = new ArrayList<>();
            Collections.addAll(results, in.readLine().split(","));
            
            return results;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void close() throws IOException {
        socket.close();
    }

}