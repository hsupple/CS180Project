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

    public ArrayList<String> getMessages(String user, String user2) throws IOException {
        out.println("GETMESS " + user + " " + user2);
        out.flush();
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

    public String receive() throws IOException {
        try {
            return in.readLine();
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    public String startAuction(String itemID, String itemName, double buyNowItemPrice, String itemDescription, String seller, boolean isSold, String buyer, double bidItemPrice) {
        try {
            out.println("STARTAUCTION " + itemID + " " + itemName + " " + buyNowItemPrice + " " + itemDescription + " " + seller + " " + isSold + " " + buyer + " " + bidItemPrice);
            out.flush();
            return in.readLine();
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

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

    public void close() throws IOException {
        socket.close();
    }

}