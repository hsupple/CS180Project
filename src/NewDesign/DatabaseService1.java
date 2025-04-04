package src.NewDesign;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class DatabaseService1 {
    private static final Object lock = new Object();
    private final String userDBfilePath = "users.txt";
    private final String itemsDBFilePath = "items.txt";
    private final String bidsDBFilePath = "bids.txt";
    private final String messagesDBFilePath = "messages.txt";
    private List<UserImpl> users = loadUsersFromFile();
    private List<ItemImpl> items = loadItemsFromFile();


    public void saveUser(UserImpl user) {
        synchronized (lock) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(userDBfilePath, true))) {
                writer.write(user.getId() + "," + user.getUsername() + "," + user.getPassword() + "," + user.getRole());
                writer.newLine();
                System.out.println("User saved successfully to " + userDBfilePath);
            } catch (IOException e) {
                System.out.println("An error occurred while saving users to file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    public List<UserImpl> loadUsersFromFile() {
        List<UserImpl> users = new ArrayList<>();
        synchronized (lock) {
            // Using try-with-resources to ensure that BufferedReader is closed automatically
            try (BufferedReader reader = new BufferedReader(new FileReader(userDBfilePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        String userId = parts[0].trim();
                        String userName = parts[1].trim();
                        String password = parts[2].trim();
                        String role = parts[3].trim();
                        users.add(new UserImpl(userId, userName, password, role));
                    } else {
                        System.out.println("Skipping line due to insufficient data: " + line);
                    }
                }
                System.out.println("Users loaded successfully from " + userDBfilePath);
            } catch (IOException e) {
                System.out.println("An error occurred while loading users from file: " + e.getMessage());
                 users = new ArrayList<>(); // Initialize to an empty list on load failure to avoid null references
            }
        }
        return users;
    }


    public void saveItemToFile(ItemImpl item) {
        synchronized (lock) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(itemsDBFilePath, true))) {
                writer.write(item.getId() + "," + item.getName() + "," + item.getDescription() + "," + item.getSellerId() + "," + item.getStartingPrice() + "," + item.getBuyNowPrice() + "," + item.getStatus());
                writer.newLine();
                System.out.println("Item saved successfully to " + itemsDBFilePath);
            } catch (IOException e) {
                System.out.println("An error occurred while saving item to file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public List<ItemImpl> loadItemsFromFile() {
        List<ItemImpl> items = new ArrayList<>();
        synchronized (lock) {
            // Using try-with-resources to ensure that BufferedReader is closed automatically
            try (BufferedReader reader = new BufferedReader(new FileReader(itemsDBFilePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 7) {
                        String itemId = parts[0].trim();
                        String itemName = parts[1].trim();
                        String itemDesc = parts[2].trim();
                        String itemSellerId = parts[3].trim();
                        double itemStartingPrice = Double.parseDouble(parts[4].trim());
                        double itemBINPrice =  Double.parseDouble(parts[5].trim());
                        String itemStatus = parts[6].trim();
                        items.add(new ItemImpl(itemId, itemName, itemDesc, itemSellerId,itemStartingPrice,itemBINPrice,itemStatus));
                    } else {
                        System.out.println("Skipping line due to insufficient data: " + line);
                    }
                }
                System.out.println("Items loaded successfully from " + itemsDBFilePath);
            } catch (IOException e) {
                System.out.println("An error occurred while loading users from file: " + e.getMessage());
                users = new ArrayList<>(); // Initialize to an empty list on load failure to avoid null references
            }
        }
        return items;
    }

    public void saveBid(Bid bid) {
        synchronized (lock) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(bidsDBFilePath, true))) {
                writer.write(bid.getId() + "," + bid.getItemId() + "," + bid.getUserId() + "," + bid.getBidAmount());
                writer.newLine();
                System.out.println("Bid saved successfully to " + bidsDBFilePath);
            } catch (IOException e) {
                System.out.println("An error occurred while saving bid to file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Method to load bids for a specific item
    public List<Bid> loadBidsForItem(String itemId) {
        List<Bid> bidsList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(bidsDBFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens[1].equals(itemId)) {
                    Bid bid = new Bid(tokens[0], tokens[1], tokens[2], Double.parseDouble(tokens[3]));
                    bidsList.add(bid);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bidsList;
    }

    public void saveMessage(Message1 message) {
        synchronized (lock) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(messagesDBFilePath, true))) {
                writer.write(message.getId() + "," + message.getSenderId() + "," + message.getRecipientId() + "," + message.getContent() + "," + message.getTimestamp());
                writer.newLine();
                System.out.println("Message saved successfully to " + messagesDBFilePath);
            } catch (IOException e) {
                System.out.println("An error occurred while saving bid to file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public List<Message1> getMessagesForUser(String userId) {
        List<Message1> messagesList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(messagesDBFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 5) { // Ensure there are enough tokens
                    String messageId = tokens[0];
                    String senderId = tokens[1];
                    String recipientId = tokens[2];
                    String content = tokens[3];
                    String timestamp = tokens[4];

                    // Check if the current message is associated with the given userId
                    if (recipientId.equals(userId) || senderId.equals(userId)) {
                        Message1 message = new Message1(messageId, senderId, recipientId, content, timestamp);
                        messagesList.add(message);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messagesList;
    }

}
