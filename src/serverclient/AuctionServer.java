package serverclient;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class AuctionServer  implements Runnable {
    private ServerSocket serverSocket;
    private final int port = 3001;
    private final Set<String> messages = Set.of("NEWBUYER", "NEWSELLER",
    "SETPASSWORD", "SENDMESS", "GETMESS", "DELETE",
    "ISACTIVE", "GETRATING", "SETRATING", "STARTAUCTION",
    "BIDITEM", "BUYITEM");
    private static final ReentrantLock FILELOCK = new ReentrantLock();

    public AuctionServer() {
        try {
            serverSocket = new ServerSocket(port);  
        } catch (IOException e) {
            System.err.println("Could not start server: " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String[] clientInput;
                String Response = "";

                String line;
                while ((line = in.readLine()) != null) {
                    clientInput = line.split(" ");

                    if (clientInput.length > 1) {
                        if (messages.contains(clientInput[0])) {
                            Response = clientInput[0] + " " + switch (clientInput[0]) {
                                case "NEWSELLER" -> newSeller(clientInput[1], clientInput[2]);
                                case "NEWBUYER" -> newBuyer(clientInput[1], clientInput[2]);
                                case "SETPASSWORD" -> setPass(clientInput[1], clientInput[2]);
                                case "SENDMESS" -> sendMess(clientInput[1], clientInput[2], clientInput[3]);
                                case "GETMESS" -> getMess(clientInput[1], clientInput[2]);
                                case "DELETE" -> delete(clientInput[1], clientInput[2]);
                                case "ISACTIVE" -> isActive(clientInput[1]);
                                case "GETRATING" -> getRating(clientInput[1]);
                                case "SETRATING" -> setRating(clientInput[1], Double.parseDouble(clientInput[2]));
                                case "STARTAUCTION" -> startAuction(clientInput[1], clientInput[2], Double.parseDouble(clientInput[3]), clientInput[4], clientInput[5], Boolean.parseBoolean(clientInput[6]), clientInput[7], Double.parseDouble(clientInput[8]));
                                case "BIDITEM" -> bidItem(clientInput[1], clientInput[2], Double.parseDouble(clientInput[3]));
                                case "BUYITEM" -> buyItem(clientInput[1], clientInput[2]);
                                default -> "Invalid";
                            };
                        } else {
                            Response = "Invalid command";
                        }
                    }

                    out.println(Response);
                    System.out.println("Response sent: " + Response);
                }

                clientSocket.close();
            }
        } catch (SocketException e) {
            System.err.println("Client disconnected: " + e.getMessage());
        } catch (EOFException e) {
            System.err.println("End of file reached: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
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
                if (parts[0].equals(itemID)) {
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
            ArrayList<String> Messages = readFile("txt/Messages.txt");
            String[] parts;
            for (int i = 0; i < Messages.size(); i++) {
                parts = Messages.get(i).split(",");
                if (parts[0].equals(user) && parts[1].equals(user2)) {
                    return "Messages between " + user + " and " + user2 + ": " + parts[2];
                }
            }
            return "No messages found between " + user + " and " + user2;
        } finally {
            FILELOCK.unlock();
        }
    }

    private String sendMess(String user, String user2, String message) {
        FILELOCK.lock();
        try {
            ArrayList<String> Messages = readFile("txt/Messages.txt");
            String newMessage = user + "," + user2 + "," + message;
            Messages.add(newMessage);
            writeFile("txt/Messages.txt", Messages);
            return "Message sent successfully from " + user + " to " + user2;
        } finally {
            FILELOCK.unlock();
        }
    }

    public static void main(String[] args) {
        AuctionServer server = new AuctionServer();
        new Thread(server).start();
    }
}