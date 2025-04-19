package serverclient;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

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
    private ServerSocket serverSocket;
    private final int port = 3001;
    private final Set<String> messages = Set.of("NEWBUYER", "NEWSELLER", "UPDATEITEM",
    "SETPASSWORD", "SENDMESS", "GETMESS", "DELETE",
    "ISACTIVE", "GETRATING", "SETRATING", "STARTAUCTION",
    "MAKEBID", "BUYITEM", "GETITEMID", "SEARCH");
    private static final ReentrantLock FILELOCK = new ReentrantLock();

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
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private Set<String> messages;

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

        @Override
        public void run() {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    String[] clientInput = line.split(" ");
                    String response = "";

                    if (clientInput.length > 1 && messages.contains(clientInput[0])) {
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

        private String handleCommand(String[] input) {
            try {

                return switch (input[0]) {
                    case "GETITEMID" -> generateID();
                    case "NEWSELLER" -> newSeller(input[1], input[2]);
                    case "NEWBUYER" -> newBuyer(input[1], input[2]);
                    case "UPDATEITEM" -> updateItem(input[1], input[2], input[3], Double.parseDouble(input[4]), input[5], Boolean.parseBoolean(input[6]), input[7], Double.parseDouble(input[8]));
                    case "SETPASSWORD" -> setPass(input[1], input[2]);
                    case "DELETE" -> delete(input[1], input[2]);
                    case "ISACTIVE" -> isActive(input[1]);
                    case "GETRATING" -> getRating(input[1]);
                    case "SETRATING" -> setRating(input[1], Double.parseDouble(input[2]));
                    case "STARTAUCTION" -> startAuction(input[1], input[2], Double.parseDouble(input[3]), input[4], input[5], Boolean.parseBoolean(input[6]), input[7], Double.parseDouble(input[8]));
                    case "MAKEBID" -> bidItem(input[1], input[2], Double.parseDouble(input[3]));
                    case "BUYITEM" -> buyItem(input[1], input[2]);
                    case "SENDMESS" -> sendMess(input[1], input[2], input[3]);
                    case "GETMESS" -> getMess(input[1], input[2]);
                    case "SEARCH" -> search(input[1]);
                    default -> "Invalid";
                };
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }

    }

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

    private String generateID() {
        FILELOCK.lock();
        try {
            ArrayList<String> Items = readFile("txt/AuctionList.txt");
            int maxId = 0;
            for (int i = 0; i < Items.size(); i++) {
                String[] parts = Items.get(i).split(",");
                if (parts.length > 0 && parts[0] != null) {
                    int id = Integer.parseInt(parts[0]);
                    maxId = Math.max(maxId, id);
                }
            }
            return String.valueOf(maxId + 1);
        } finally {
            FILELOCK.unlock();
        }
    }

    private String newBuyer(String user, String password) {
        FILELOCK.lock();
        String[] parts;
        try {
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
        } finally {
            FILELOCK.unlock();
        }
    }

    private String newSeller(String user, String password) {
        FILELOCK.lock();
        String[] parts;
        try {
            ArrayList<String> Sellers = readFile("txt/SellerList.txt");
            for (int i = 0; i < Sellers.size(); i++) {
                parts = Sellers.get(i).split(",");
                if (parts[0].equals(user)) {
                    return "Username already exists!";
                }
            }
            Sellers.add(user + "," + password + ",0,0," + "true");
            writeFile("txt/SellerList.txt", Sellers);
            return "Successfully added user: " + user;
        } finally {
            FILELOCK.unlock();
        }
    }

    private String updateItem(String itemID, String itemName, String itemDescription, double buyNowItemPrice, String seller, boolean isSold, String buyer, double bidItemPrice) {
        FILELOCK.lock();
        try {
            ArrayList<String> Items = readFile("txt/AuctionList.txt");
            String[] parts;
            for (int i = 0; i < Items.size(); i++) {
                parts = Items.get(i).split(",");
                if (parts[1].equals(itemName)) {
                    parts[1] = itemName;
                    parts[2] = String.valueOf(buyNowItemPrice);
                    parts[3] = itemDescription;
                    parts[4] = seller;
                    parts[5] = String.valueOf(isSold);
                    parts[6] = buyer;
                    parts[7] = String.valueOf(bidItemPrice);

                    // Update the item in the list
                    Items.set(i, String.join(",", parts));
                    writeFile("txt/AuctionList.txt", Items);
                    return "Existing item updated successfully: " + itemID;
                }
            }
            String newItem = itemID + "," + itemName + "," + buyNowItemPrice + "," + itemDescription + "," + seller + "," + isSold + "," + buyer + "," + bidItemPrice;
            Items.add(newItem);
            writeFile("txt/AuctionList.txt", Items);
            return "Item added successfully: " + itemID;
        } finally {
            FILELOCK.unlock();
        }
    }

    private ArrayList<String> readFile(String txtFile) {
        FILELOCK.lock();
        try {
            ArrayList<String> contentBuilder = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(txtFile))) {
                String sCurrentLine;
                while ((sCurrentLine = br.readLine()) != null) {
                    contentBuilder.add(sCurrentLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            return contentBuilder;
        } finally {
            FILELOCK.unlock();
        }
    }

    private void writeFile(String txtFile, ArrayList<String> content) {
        FILELOCK.lock();
        try {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(txtFile))) {
                for (String line : content) {
                    bw.write(line);
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            FILELOCK.unlock();
        }
    }

    private String setPass(String user, String password) {
        FILELOCK.lock();
        try {
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
        } finally {
            FILELOCK.unlock();
        }
    }

    private String delete(String user, String password) {
        FILELOCK.lock();
        try {
            ArrayList <String> Buyers = readFile("txt/BuyerList.txt");
            ArrayList <String> Sellers = readFile("txt/SellerList.txt");
            String[] parts;
            for (int i = 0; i < Buyers.size(); i++) {
                parts = Buyers.get(i).split(",");
                if (parts[0].equals(user) && parts[1].equals(password)) {
                    Buyers.remove(i);
                    writeFile("txt/BuyerList.txt", Buyers);
                    return "Account deleted successfully for user: " + user;
                }
            }

            for (int i = 0; i < Sellers.size(); i++) {
                parts = Sellers.get(i).split(",");
                if (parts[0].equals(user) && parts[1].equals(password)) {
                    Sellers.remove(i);
                    writeFile("txt/SellerList.txt", Sellers);
                    return "Account deleted successfully for user: " + user;
                }
            }
            return "User not found or password incorrect";
        } finally {
            FILELOCK.unlock();
        }
    }

    private String getRating(String user) {
        FILELOCK.lock();
        try {
            ArrayList<String> Sellers = readFile("txt/SellerList.txt");
            String[] parts;
            for (int i = 0; i < Sellers.size(); i++) {
                parts = Sellers.get(i).split(",");
                if (parts[0].equals(user)) {
                    return parts[2];
                }
            }
            return "User not found";
        } finally {
            FILELOCK.unlock();
        }
    }

    private String setRating(String user, double rating) {
        FILELOCK.lock();
        try {
            ArrayList<String> Sellers = readFile("txt/SellerList.txt");
            String[] parts;

            for (int i = 0; i < Sellers.size(); i++) {
                parts = Sellers.get(i).split(",");
                if (parts[0].equals(user)) {
                    double currentRating = Double.parseDouble(parts[2]);
                    double ratingCount = Double.parseDouble(parts[3]);
                    double newRating = ((currentRating * ratingCount) + rating) / (ratingCount + 1);
                    parts[2] = String.format("%.2f", newRating);
                    parts[3] = String.valueOf((int) (ratingCount + 1));
                    Sellers.set(i, String.join(",", parts));

                    writeFile("txt/SellerList.txt", Sellers);
                    return "Rating set successfully for user: " + user;
                }
            }
            return "User not found";
        } finally {
            FILELOCK.unlock();
        }
    }
    
    private String isActive(String UserItem) {
        FILELOCK.lock();
        try {
            ArrayList<String> Buyers = readFile("txt/BuyerList.txt");
            ArrayList<String> Sellers = readFile("txt/SellerList.txt");
            ArrayList<String> Items = readFile("txt/AuctionList.txt");
            String[] parts;
            for (int i = 0; i < Buyers.size(); i++) {
                parts = Buyers.get(i).split(",");
                if (parts[0].equals(UserItem)) {
                    return "User is active: " + UserItem;
                } 
            }

            for (int i = 0; i < Sellers.size(); i++) {
                parts = Sellers.get(i).split(",");
                if (parts[4].equals("True")) {
                    return "User is active: " + UserItem;
                } 
            }

            for (int i = 0; i < Items.size(); i++) {
                parts = Items.get(i).split(",");
                if (parts.length > 4){
                    if (parts[5].equals("False")) {
                        return "Listing is active: " + UserItem;
                    } 
                }
            }
            return "User / Listing not found";
        } finally {
            FILELOCK.unlock();
        }
    }
    
    private String buyItem(String itemID, String buyer) {
        FILELOCK.lock();
        try {
            ArrayList<String> Items = readFile("txt/AuctionList.txt");
            String[] parts;
            if (isActive(itemID).contains("not active")) {
                return "Item is not active: " + itemID;
            }

            for (int i = 0; i < Items.size(); i++) {
                parts = Items.get(i).split(",");
                if (parts[0].equals(itemID)) {
                    if (parts[2].equals("-1")) {
                        return "Item is not \"Buy Now\": " + itemID;
                    } else {
                        parts[5] = "True";
                        parts[6] = buyer;
                        Items.set(i, String.join(",", parts));
                        writeFile("txt/AuctionList.txt", Items);
                        return "Item bought successfully: " + itemID;
                    }
                }
            }
            return "Item not found";
        } finally {
            FILELOCK.unlock();
        }
    }

    private String startAuction(String itemID, String itemName, double buyNowItemPrice, String itemDescription, String seller, boolean isSold, String buyer, double bidItemPrice) {
        FILELOCK.lock();
        try {
            ArrayList<String> Items = readFile("txt/AuctionList.txt");
            String[] parts;
            for (int i = 0; i < Items.size(); i++) {
                parts = Items.get(i).split(",");
                if (parts[0].equals(itemID)) {
                    return "Item already exists: " + itemID;
                }
            }
            String newItem = itemID + "," + itemName + "," + buyNowItemPrice + "," + itemDescription + "," + seller + "," + isSold + "," + buyer + "," + bidItemPrice;
            Items.add(newItem);
            writeFile("txt/AuctionList.txt", Items);
            return "Auction started successfully for item: " + itemID;
        } finally {
            FILELOCK.unlock();
        }
    }

    private String bidItem(String itemID, String user, double price) {
        FILELOCK.lock();
        try {
            ArrayList<String> Items = readFile("txt/AuctionList.txt");
            String[] parts;
            for (int i = 0; i < Items.size(); i++) {
                parts = Items.get(i).split(",");
                if (parts[1].equals(itemID)) {
                    if (Double.parseDouble(parts[7]) < price) {
                        parts[7] = String.valueOf(price);
                        parts[6] = user;
                        Items.set(i, String.join(",", parts));
                        writeFile("txt/AuctionList.txt", Items);
                        return "Bid placed successfully for item: " + itemID;
                    } else {
                        return "Bid price is too low for item: " + itemID;
                    }
                }
            }
            return "Item not found";
        } finally {
            FILELOCK.unlock();
        }
    }

    private String getMess(String user, String user2) {
        FILELOCK.lock();
        try {
            if (user.compareTo(user2) < 1) {
                String temp = user;
                user = user2;
                user2 = temp;
            }
            ArrayList<String> Messages = new ArrayList<>();
            if (new File("msg/" + user + "_to_" + user2 + ".txt").exists()) {
                Messages = readFile("msg/" + user + "_to_" + user2 + ".txt");
                return Messages.toString();
            } else {
                return "No messages found between " + user + " and " + user2;
            }
            
        } finally {
            FILELOCK.unlock();
        }
    }

    private String sendMess(String user, String user2, String message) {
        FILELOCK.lock();
        try {
            if (user.compareTo(user2) < 1) {
                String temp = user;
                user = user2;
                user2 = temp;
            }
            ArrayList<String> Messages = new ArrayList<>();
            if (new File("msg/" + user + "_to_" + user2 + ".txt").exists()) {
                String line;
                Messages = readFile("msg/" + user + "_to_" + user2 + ".txt"); 
            }

            String newMessage = user + ": " + message.replace("/", " ");
            Messages.add(newMessage);
            writeFile("msg/" + user + "_to_" + user2 + ".txt", Messages);
            return "Message sent successfully from " + user + " to " + user2;
        } finally {
            FILELOCK.unlock();
        }
    }

    private String search(String query) {

        ArrayList<String> Sellers = readFile("txt/SellerList.txt");
        ArrayList<String> Items = readFile("txt/AuctionList.txt");
        ArrayList<String> results = new ArrayList<>();
        String[] parts;
        results.add("Sellers");
        for (int i = 0; i < Sellers.size(); i++) {
            parts = Sellers.get(i).split(",");
            if (parts[0].contains(query) && parts[4].equals("true")) {
                results.add(parts[0]);
            }
        }

        results.add("Listings");
        for (int j = 0; j < Items.size(); j++) {
            parts = Items.get(j).split(",");
            if (parts[1].contains(query)) {
                results.add(parts[1] + " BUY NOW $" + parts[2] + " BID AMT $" + parts[7]);
            }
        }
        return results.toString();
    }
    public static void main(String[] args) {
        AuctionServer server = new AuctionServer();
        new Thread(server).start();
    }
}