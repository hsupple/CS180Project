import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Set;

/**
 * Class to run server for data transfer, interaction, and storage.
 *
 * <p>Purdue University -- CS18000 -- Spring 2025</p>
 *
 * @author @Phaynes742
           @hsupple
           @jburkett013
           @addy-ops
 * @version April, 2025
 */

public class AuctionServer implements Runnable {
    // Define all variables for server
    private ServerSocket serverSocket;
    private final int port = 3001;
    private static final Object LOCK = new Object();

    // Define messages for server to handle
    private final Set<String> messages = Set.of("NEWBUYER", "NEWSELLER", "UPDATEITEM",
        "SETPASSWORD", "SENDMESS", "GETMESS", "DELETE",
        "ISACTIVE", "GETRATING", "SETRATING", "STARTAUCTION",
        "MAKEBID", "BUYITEM", "GETITEMID", "SEARCH",
        "GETMYLISTINGS", "ENDLISTING");

    // Constructor to create server socket
    // and set up the server to listen for connections
    public AuctionServer() {
        try {
            serverSocket = new ServerSocket(port);  
        } catch (IOException e) {
            System.err.println("Could not start server: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Class to run clients separately from main server socket.
     *
     * <p>Purdue University -- CS18000 -- Spring 2025</p>
     *
     * @author @Phaynes742
               @hsupple
               @jburkett013
               @addy-ops
    * @version April, 2025
    */

    class ClientHandler implements Runnable {
        // Define all variables for client handler
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private Set<String> messages;

        // Constructor to set up client handler with socket and messages
        // to handle incoming requests
        public ClientHandler(Socket socket, Set<String> messages) {
            this.clientSocket = socket;
            this.messages = messages;
            try {
                this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                System.err.println("Error setting up I/O streams: " + e.getMessage());
            }
        }

        // Run method to handle incoming requests from clients
        @Override
        public void run() {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    String[] clientInput = line.split(" ");
                    String response = "";
                    if (messages.contains(clientInput[0])) {
                        response = handleCommand(clientInput);
                    } else {
                        response = "Invalid command";
                    }

                    out.println(response);
                    System.out.println("Response sent: " + response);
                }
            } catch (IOException e) {
                System.err.println("Client disconnected: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }
        // Method to handle commands from clients
        // and call appropriate methods to process requests
        private String handleCommand(String[] input) {
            try {
                return switch (input[0]) {
                    case "GETITEMID" -> generateID();
                    case "NEWSELLER" -> newSeller(input[1], input[2]);
                    case "NEWBUYER" -> newBuyer(input[1], input[2]);
                    case "UPDATEITEM" -> updateItem(input[1], input[2], input[3], Double.parseDouble(input[4]), 
                                                    input[5], Boolean.parseBoolean(input[6]), input[7], 
                                                    Double.parseDouble(input[8]));
                    case "SETPASSWORD" -> setPass(input[1], input[2]);
                    case "DELETE" -> delete(input[1], input[2]);
                    case "ISACTIVE" -> isActive(input[1]);
                    case "GETRATING" -> getRating(input[1]);
                    case "SETRATING" -> setRating(input[1], Double.parseDouble(input[2]));
                    case "STARTAUCTION" -> startAuction(input[1], input[2], Double.parseDouble(input[3]), input[4], 
                                                        input[5], Boolean.parseBoolean(input[6]), input[7], 
                                                        Double.parseDouble(input[8]));
                    case "ENDLISTING" -> endAuction(input[1]);
                    case "MAKEBID" -> bidItem(input[1], input[2], Double.parseDouble(input[3]));
                    case "BUYITEM" -> buyItem(input[1], input[2]);
                    case "SENDMESS" -> sendMess(input[1], input[2], input[3]);
                    case "GETMESS" -> getMess(input[1], input[2]);
                    case "SEARCH" -> search(input[1]);
                    case "GETMYLISTINGS" -> getMyListings(input[1]);
                    default -> "Invalid";
                };
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }
    }

    // Main method to start the server and listen for incoming connections
    @Override
    public void run() {
        try {
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                ClientHandler handler = new ClientHandler(clientSocket, messages);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    // Properly synchronized file I/O methods
    private ArrayList<String> readFile(String txtFile) {
        ArrayList<String> contentBuilder = new ArrayList<>();
        synchronized (LOCK) {
            try (BufferedReader br = new BufferedReader(new FileReader(txtFile))) {
                String sCurrentLine;
                while ((sCurrentLine = br.readLine()) != null) {
                    contentBuilder.add(sCurrentLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return contentBuilder;
    }

    // Write to file with synchronized access
    private void writeFile(String txtFile, ArrayList<String> content) {
        synchronized (LOCK) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(txtFile))) {
                for (String line : content) {
                    bw.write(line);
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Generate a new ID for items
    private String generateID() {
        synchronized (LOCK) {
            ArrayList<String> items = readFile("txt/AuctionList.txt");
            int maxId = 0;
            for (int i = 0; i < items.size(); i++) {
                String[] parts = items.get(i).split(",");
                if (parts.length > 0 && parts[0] != null) {
                    int id = Integer.parseInt(parts[0]);
                    maxId = Math.max(maxId, id);
                } else {
                    maxId = 1000;
                }
            }
            return String.valueOf(maxId + 1);
        }
    }

    // Log a new buyer account
    private String newBuyer(String user, String password) {
        synchronized (LOCK) {
            String[] parts;
            ArrayList<String> buyers = readFile("txt/BuyerList.txt");
            for (int i = 0; i < buyers.size(); i++) {
                parts = buyers.get(i).split(",");
                if (parts[0].equals(user)) {
                    return "Username already exists!";
                }
            }
            buyers.add(user + "," + password + ",true");
            writeFile("txt/BuyerList.txt", buyers);
            return "Successfully added user: " + user;
        }
    }

    // Log a new seller account    
    private String newSeller(String user, String password) {
        synchronized (LOCK) {
            String[] parts;
            ArrayList<String> sellers = readFile("txt/SellerList.txt");
            for (int i = 0; i < sellers.size(); i++) {
                parts = sellers.get(i).split(",");
                if (parts[0].equals(user)) {
                    return "Username already exists!";
                }
            }
            sellers.add(user + "," + password + ",0,0," + "true");
            writeFile("txt/SellerList.txt", sellers);
            return "Successfully added user: " + user;
        }
    }

    // Update item listing or add new item if it doesn't exist
    private String updateItem(String itemID, String itemName, String itemDescription, 
                              double buyNowItemPrice, String seller, boolean isSold, String buyer, 
                              double bidItemPrice) {
        synchronized (LOCK) {
            ArrayList<String> items = readFile("txt/AuctionList.txt");
            String[] parts;
            for (int i = 0; i < items.size(); i++) {
                parts = items.get(i).split(",");
                if (parts[1].equals(itemName)) {
                    parts[1] = itemName;
                    parts[2] = String.valueOf(buyNowItemPrice);
                    parts[3] = itemDescription;
                    parts[4] = seller;
                    parts[5] = String.valueOf(isSold);
                    parts[6] = buyer;
                    parts[7] = String.valueOf(bidItemPrice);

                    items.set(i, String.join(",", parts));
                    writeFile("txt/AuctionList.txt", items);
                    return "Existing item updated successfully: " + itemID;
                }
            }
            String newItem = itemID + "," + itemName + "," + buyNowItemPrice + "," 
                + itemDescription + "," + seller + "," + isSold + "," + buyer + "," + bidItemPrice;
            items.add(newItem);
            writeFile("txt/AuctionList.txt", items);
            return "Item added successfully: " + itemID;
        }
    }

    // Set password for buyer or seller
    private String setPass(String user, String password) {
        synchronized (LOCK) {
            ArrayList<String> buyers = readFile("txt/BuyerList.txt");
            ArrayList<String> sellers = readFile("txt/SellerList.txt");
            String[] parts;
            for (int i = 0; i < buyers.size(); i++) {
                parts = buyers.get(i).split(",");
                if (parts[0].equals(user)) {
                    parts[1] = password;
                    buyers.set(i, String.join(",", parts));
                    writeFile("txt/BuyerList.txt", buyers); 
                    return "Password set successfully for user: " + user;
                }
            }

            for (int i = 0; i < sellers.size(); i++) {
                parts = sellers.get(i).split(",");
                if (parts[0].equals(user)) {
                    parts[1] = password;
                    sellers.set(i, String.join(",", parts));
                    writeFile("txt/SellerList.txt", sellers);
                    return "Password set successfully for user: " + user;
                }
            }
            return "User not found";
        }
    }

    // Delete buyer or seller account by setting isActive to false
    private String delete(String user, String password) {
        synchronized (LOCK) {
            ArrayList<String> buyers = readFile("txt/BuyerList.txt");
            ArrayList<String> sellers = readFile("txt/SellerList.txt");
            String[] parts;
            for (int i = 0; i < buyers.size(); i++) {
                parts = buyers.get(i).split(",");
                if (parts[0].equals(user) && parts[1].equals(password)) {
                    buyers.remove(i);
                    writeFile("txt/BuyerList.txt", buyers);
                    return "Account deleted successfully for user: " + user;
                }
            }

            for (int i = 0; i < sellers.size(); i++) {
                parts = sellers.get(i).split(",");
                if (parts[0].equals(user) && parts[1].equals(password)) {
                    sellers.remove(i);
                    writeFile("txt/SellerList.txt", sellers);
                    return "Account deleted successfully for user: " + user;
                }
            }
            return "User not found or password incorrect";
        }
    }

    // Get rating of seller
    private String getRating(String user) {
        synchronized (LOCK) {
            ArrayList<String> sellers = readFile("txt/SellerList.txt");
            String[] parts;
            for (int i = 0; i < sellers.size(); i++) {
                parts = sellers.get(i).split(",");
                if (parts[0].equals(user)) {
                    return parts[2];
                }
            }
            return "User not found";
        }
    }

    // Set rating for seller
    // This method averages the new rating with the existing rating
    private String setRating(String user, double rating) {
        synchronized (LOCK) {
            ArrayList<String> sellers = readFile("txt/SellerList.txt");
            String[] parts;

            for (int i = 0; i < sellers.size(); i++) {
                parts = sellers.get(i).split(",");
                if (parts[0].equals(user)) {
                    double currentRating = Double.parseDouble(parts[2]);
                    double ratingCount = Double.parseDouble(parts[3]);
                    double newRating = ((currentRating * ratingCount) + rating) / (ratingCount + 1);
                    parts[2] = String.format("%.2f", newRating);
                    parts[3] = String.valueOf((int) (ratingCount + 1));
                    sellers.set(i, String.join(",", parts));

                    writeFile("txt/SellerList.txt", sellers);
                    return "Rating set successfully for user: " + user;
                }
            }
            return "User not found";
        }
    }
    
    // Check if user or listing is active
    private String isActive(String userItem) {
        synchronized (LOCK) {
            ArrayList<String> buyers = readFile("txt/BuyerList.txt");
            ArrayList<String> sellers = readFile("txt/SellerList.txt");
            ArrayList<String> items = readFile("txt/AuctionList.txt");
            String[] parts;
            for (int i = 0; i < buyers.size(); i++) {
                parts = buyers.get(i).split(",");
                if (parts[1].equals(userItem)) {
                    return "User is active: " + userItem;
                } 
            }
 
            for (int i = 0; i < sellers.size(); i++) {
                parts = sellers.get(i).split(",");
                if (parts[0].equals(userItem) && parts[4].equalsIgnoreCase("true")) {
                    return "User is active: " + userItem;
                } 
            }

            for (int i = 0; i < items.size(); i++) {
                parts = items.get(i).split(",");
                if (parts.length > 4) {
                    if (parts[0].equals(userItem) && parts[5].equalsIgnoreCase("false")) {
                        return "Listing is active: " + userItem;
                    } 
                }
            }
            return "User / Listing not found";
        }
    }
    
    // Buy item now based on user set price
    private String buyItem(String itemID, String buyer) {
        synchronized (LOCK) {
            ArrayList<String> items = readFile("txt/AuctionList.txt");
            String[] parts;
            // Fixed the check to be more reliable
            String activeCheck = isActive(itemID);
            if (activeCheck.contains("not found")) {
                return "Item not found: " + itemID;
            }
            
            if (!activeCheck.contains("active")) {
                return "Item is not active: " + itemID;
            }

            for (int i = 0; i < items.size(); i++) {
                parts = items.get(i).split(",");
                if (parts[0].equals(itemID)) {
                    if (parts[2].equals("-1")) {
                        return "Item is not \"Buy Now\": " + itemID;
                    } else {
                        parts[5] = "true";
                        parts[6] = buyer;
                        items.set(i, String.join(",", parts));
                        writeFile("txt/AuctionList.txt", items);
                        return "Item bought successfully: " + itemID;
                    }
                }
            }
            return "Item not found";
        }
    }

    // Start a new auction or add a new item if it doesn't exist
    private String startAuction(String itemID, String itemName, double buyNowItemPrice, 
                                String itemDescription, String seller, boolean isSold, String buyer, 
                                double bidItemPrice) {
        synchronized (LOCK) {
            ArrayList<String> items = readFile("txt/AuctionList.txt");
            String[] parts;
            for (int i = 0; i < items.size(); i++) {
                parts = items.get(i).split(",");
                if (parts[0].equals(itemID)) {
                    return "Item already exists: " + itemID;
                }
            }
            String newItem = itemID + "," + itemName + "," + buyNowItemPrice + "," + itemDescription + "," 
                + seller + "," + isSold + "," + buyer + "," + bidItemPrice;
            items.add(newItem);
            writeFile("txt/AuctionList.txt", items);
            return "Auction started successfully for item: " + itemID;
        }
    }

    // End a current auction
    private String endAuction(String itemID) {
        synchronized (LOCK) {
            ArrayList<String> items = readFile("txt/AuctionList.txt");
            String[] parts;
            for (int i = 0; i < items.size(); i++) {
                parts = items.get(i).split(",");
                if (parts[0].equals(itemID)) {
                    parts[5] = "true";
                    items.set(i, String.join(",", parts));
                    writeFile("txt/AuctionList.txt", items);
                    return "Auction ended successfully for item: " + itemID;
                }
            }
            return "Item not found";
        }
    }

    // Place a bid on an item
    private String bidItem(String itemID, String user, double price) {
        synchronized (LOCK) {
            ArrayList<String> items = readFile("txt/AuctionList.txt");
            String[] parts;
            for (int i = 0; i < items.size(); i++) {
                parts = items.get(i).split(",");
                if (parts[1].equals(itemID)) {
                    double currentBid = Double.parseDouble(parts[7]);
                    if (currentBid < price) {
                        parts[7] = String.valueOf(price);
                        parts[6] = user;
                        items.set(i, String.join(",", parts));
                        writeFile("txt/AuctionList.txt", items);
                        return "Bid placed successfully for item: " + itemID;
                    } else {
                        return "Bid price is too low for item: " + itemID;
                    }
                }
            }
            return "Item not found";
        }
    }

    // Get messages between two users
    private String getMess(String user, String user2) {
        synchronized (LOCK) {
            // Sort users for consistent file naming
            if (user.compareTo(user2) > 0) {
                String temp = user;
                user = user2;
                user2 = temp;
            }
            
            File messageFile = new File("msg/" + user + "_to_" + user2 + ".txt");
            if (messageFile.exists()) {
                ArrayList<String> messageList = readFile("msg/" + user + "_to_" + user2 + ".txt");
                return messageList.toString();
            } else {
                return "No messages found between " + user + " and " + user2;
            }
        }
    }

    // Send a message from one user to another
    private String sendMess(String user, String user2, String message) {
        synchronized (LOCK) {
            // Sort users for consistent file naming
            if (user.compareTo(user2) > 0) {
                String temp = user;
                user = user2;
                user2 = temp;
            }

            // Ensure directory exists
            File dir = new File("msg");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            File messageFile = new File("msg/" + user + "_to_" + user2 + ".txt");
            ArrayList<String> messageList = new ArrayList<>();
            
            if (messageFile.exists()) {
                messageList = readFile("msg/" + user + "_to_" + user2 + ".txt"); 
            }

            String newMessage = user + ": " + message.replace("/", " ");
            messageList.add(newMessage);
            writeFile("msg/" + user + "_to_" + user2 + ".txt", messageList);
            return "Message sent successfully from " + user + " to " + user2;
        }
    }

    // Search for sellers or items based on query
    private String search(String query) {
        synchronized (LOCK) {
            ArrayList<String> sellers = readFile("txt/SellerList.txt");
            ArrayList<String> items = readFile("txt/AuctionList.txt");
            ArrayList<String> results = new ArrayList<>();
            String[] parts;
            results.add("Sellers");
            for (int i = 0; i < sellers.size(); i++) {
                parts = sellers.get(i).split(",");
                if (parts[0].contains(query) && parts[4].equalsIgnoreCase("true")) {
                    results.add(parts[0]);
                }
            }

            results.add("Listings");
            for (int j = 0; j < items.size(); j++) {
                parts = items.get(j).split(",");
                if (parts[1].contains(query)) {
                    results.add(parts[1] + " BUY NOW $" + parts[2] + " BID AMT $" + parts[7]);
                }
            }
            return results.toString();
        }
    }
    
    // Get all listings from server
    private String getMyListings(String user) {
        synchronized (LOCK) {
            ArrayList<String> items = readFile("txt/AuctionList.txt");
            String[] parts;
            ArrayList<String> results = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                parts = items.get(i).split(",");
                if (parts.length > 4 && parts[4].equals(user)) {
                    results.add(String.join("/", parts));
                }
            }
            return results.toString();
        }
    }
    
    public static void main(String[] args) {
        AuctionServer server = new AuctionServer();
        new Thread(server).start();
    }
}