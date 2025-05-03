package gui.messages;

import accounts.AuctionClient;
import java.awt.*;
import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;
import javax.swing.*;

/**
     * Class to run new gui for messages from users recieved
     *
     * <p>Purdue University -- CS18000 -- Spring 2025</p>
     *
     * @author @Phaynes742
               @hsupple
    * @version May, 2025
    */

public class MessagesGui implements Runnable {
    // Define all private fields
    private static String user;
    private static String password;
    private static AuctionClient client = null; 
    private ArrayList<String> listingsList = new ArrayList<>(); 
    private JFrame frame = null;
    private JPanel messagesListPanel;

    // construct new gui for messages
    public MessagesGui(String user, String password) {
        MessagesGui.user = user;
        MessagesGui.password = password;
        
        try {
            this.client = new AuctionClient();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadConversations();

        frame = new JFrame("Messenger Client");
        frame.setSize(1250, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
        
        // Watch on thread to find new messages
        Thread watcherThread = new Thread(this);
        watcherThread.setDaemon(true);
        watcherThread.start();
    }

    // Check for all conversations (files with matching user names) and append to list
    private void loadConversations() {
        listingsList.clear();
        
        String directoryPath = System.getProperty("user.dir") + "/../src/serverclient/msg";
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().contains(user) && file.getName().endsWith(".txt")) {
                        String fileName = file.getName().replace(".txt", "");
                        String otherUser;
                        
                        if (fileName.startsWith(user + "_to_")) {
                            otherUser = fileName.substring((user + "_to_").length());
                        } else if (fileName.endsWith("_to_" + user)) {
                            otherUser = fileName.substring(0, fileName.length() - ("_to_" + user).length());
                        } else if (fileName.contains("_to_")) {
                            String[] parts = fileName.split("_to_");
                            if (parts.length == 2) {
                                if (parts[0].equals(user)) {
                                    otherUser = parts[1];
                                } else {
                                    otherUser = parts[0];
                                }
                            } else {
                                continue;
                            }
                        } else {
                            continue;
                        }
                        
                        if (!listingsList.contains(otherUser)) {
                            listingsList.add(otherUser);
                        }
                    }
                }
            }
        }
    }

    // Run watcher to find when new message is sent
    public void run() {
        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(System.getProperty("user.dir") + "/../src/serverclient/msg");
            System.out.println("Watching directory: " + path.toString());
            path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, 
                         StandardWatchEventKinds.ENTRY_MODIFY);
            
            while (true) {
                WatchKey key = watcher.take();
                boolean shouldUpdate = false;
                
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE || 
                        kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        
                        Path changedPath = (Path) event.context();
                        String fileName = changedPath.toString();
                        
                        if (fileName.contains(user) && fileName.endsWith(".txt")) {
                            System.out.println("Relevant file changed: " + fileName);
                            shouldUpdate = true;
                        }
                    }
                }
                
                if (shouldUpdate) {
                    SwingUtilities.invokeLater(() -> {
                        int oldSize = listingsList.size();
                        
                        loadConversations();
                        
                        if (oldSize != listingsList.size()) {
                            updateMessagesPanel();
                        }
                    });
                }
                
                key.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Create new messages panel with message panel per user messaged
    private void updateMessagesPanel() {
        if (messagesListPanel != null) {
            messagesListPanel.removeAll();
            
            if (listingsList.isEmpty()) {
                JLabel noMessagesLabel = new JLabel("You have no messages.");
                noMessagesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                messagesListPanel.add(Box.createVerticalGlue());
                messagesListPanel.add(noMessagesLabel);
                messagesListPanel.add(Box.createVerticalGlue());
            } else {
                for (String loadUser : listingsList) {
                    JPanel listingPanel = createMessagePanel(loadUser);
                    messagesListPanel.add(Box.createVerticalStrut(10));
                    messagesListPanel.add(listingPanel);
                }
            }
            
            messagesListPanel.revalidate();
            messagesListPanel.repaint();
        }
    }

    // Place components into frame and layout properly
    private void placeComponents(JPanel panel) {
        panel.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.LIGHT_GRAY);
        headerPanel.setPreferredSize(new Dimension(1250, 100));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // All title works
        JLabel title = new JLabel("Purdue Auction House Messenger Client", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerPanel.add(title, BorderLayout.CENTER);

        JPanel headerInfoPanel = new JPanel();
        headerInfoPanel.setLayout(new BoxLayout(headerInfoPanel, BoxLayout.Y_AXIS));
        headerInfoPanel.setBackground(Color.LIGHT_GRAY);
        headerInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));
        headerInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel welcomeLabel = new JLabel("Welcome " + user + "!");
        JLabel typeLabel = new JLabel("Account Type: " + 
                                     (client.isBuyer(user) ? "Buyer" : "Seller"));
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        typeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        headerInfoPanel.add(welcomeLabel);
        headerInfoPanel.add(typeLabel);

        JPanel headerButtonPanel = new JPanel();
        headerButtonPanel.setLayout(new BoxLayout(headerButtonPanel, BoxLayout.Y_AXIS));
        headerButtonPanel.setBackground(Color.LIGHT_GRAY);

        // Logout and return buttons
        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(100, 30));
        logoutButton.setMaximumSize(new Dimension(100, 30));

        JButton returnButton = new JButton("Return to Home");
        returnButton.setPreferredSize(new Dimension(150, 30));
        returnButton.setMaximumSize(new Dimension(150, 30));
        
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(Color.LIGHT_GRAY);
        logoutPanel.add(logoutButton);
        
        JPanel returnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        returnPanel.setBackground(Color.LIGHT_GRAY);
        returnPanel.add(returnButton);
        
        headerButtonPanel.add(logoutPanel);
        headerButtonPanel.add(returnPanel);

        headerPanel.add(headerInfoPanel, BorderLayout.WEST);
        headerPanel.add(headerButtonPanel, BorderLayout.EAST);

        logoutButton.addActionListener(e -> {
            frame.dispose();
        });
        
        returnButton.addActionListener(e -> {
            frame.dispose();
            if (client.isBuyer(user)) {
                new gui.buyer.BuyerGui(user, password);
            } else {
                new gui.seller.SellerGui(user, password);
            }
        });

        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel listingsTitlePanel = new JPanel(new BorderLayout());
        listingsTitlePanel.setBackground(Color.WHITE);
        
        JLabel listingsTitle = new JLabel("My Messages");
        listingsTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        listingsTitlePanel.add(listingsTitle, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);
        
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        
        // Search user function
        searchPanel.add(new JLabel("Find user: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Create new message to user
        JButton newMessageButton = new JButton("New Message");
        searchPanel.add(newMessageButton);
        
        newMessageButton.addActionListener(e -> {
            String recipient = JOptionPane.showInputDialog(frame, 
                                                         "Enter username to message:", 
                                                         "New Message", 
                                                         JOptionPane.QUESTION_MESSAGE);
            if (recipient != null && !recipient.trim().isEmpty()) {
                frame.dispose();
                new MessengerGui(user, password, recipient.trim());
            }
        });
        
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            if (!searchText.isEmpty()) {
                for (String otherUser : listingsList) {
                    if (otherUser.contains(searchText)) {
                        frame.dispose();
                        new MessengerGui(user, password, otherUser);
                        return;
                    }
                }
                JOptionPane.showMessageDialog(frame, 
                                            "No matching conversations found.", 
                                            "Search Results", 
                                            JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        listingsTitlePanel.add(searchPanel, BorderLayout.EAST);

        messagesListPanel = new JPanel();
        messagesListPanel.setLayout(new BoxLayout(messagesListPanel, BoxLayout.Y_AXIS));
        messagesListPanel.setBackground(Color.WHITE);

        if (listingsList.isEmpty()) {
            JLabel noMessagesLabel = new JLabel("You have no messages.");
            noMessagesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            messagesListPanel.add(Box.createVerticalGlue());
            messagesListPanel.add(noMessagesLabel);
            messagesListPanel.add(Box.createVerticalGlue());
        } else {
            for (String otherUser : listingsList) {
                JPanel listingPanel = createMessagePanel(otherUser);
                messagesListPanel.add(Box.createVerticalStrut(10));
                messagesListPanel.add(listingPanel);
            }
        }

        JScrollPane scrollPane = new JScrollPane(messagesListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        contentPanel.add(listingsTitlePanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(contentPanel, BorderLayout.CENTER);
    }

    // create new panel with all users that have messaged client
    private JPanel createMessagePanel(String otherUser) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        container.setMaximumSize(new Dimension(1200, 80));
        container.setPreferredSize(new Dimension(1200, 80));
        
        JLabel userLabel = new JLabel(otherUser);
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton viewButton = new JButton("View Conversation");
        viewButton.addActionListener(e -> {
            frame.dispose();
            new MessengerGui(user, password, otherUser);
        });
        
        buttonPanel.add(viewButton);
        
        container.add(userLabel, BorderLayout.WEST);
        container.add(buttonPanel, BorderLayout.EAST);
        
        return container;
    }
    
    public static void main(String[] args) {
        // invoke with dummy args
        SwingUtilities.invokeLater(() -> {
            String testUser = "testuser";
            String testPass = "testpass";
            new MessagesGui(testUser, testPass);
        });
    }
}