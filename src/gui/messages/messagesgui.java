package gui.messages;

import accounts.AuctionClient;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;

public class messagesgui {

    
    
    private static String user;
    private static String password;
    private static AuctionClient client = null; 
    private static ArrayList<String> listingsList = new ArrayList<>(); 
    private static JFrame frame = null;

    public messagesgui(String user, String password) {

        this.user = user;
        this.password = password;
        
        try {
            this.client = new AuctionClient();
        } catch (Exception e) {
            e.printStackTrace();
        }

    String directoryPath = System.getProperty("user.dir") + "/src/serverclient/msg";
    File directory = new File(directoryPath);

    File[] files = directory.listFiles();
    if (directory.exists() && directory.isDirectory()) {
        
        for (File file : files) {
            if (file.isFile() && file.getName().contains(user) && !listingsList.contains(file.getName().replace(".txt", "").replace(user, "").replace("_to_","").strip())) {
                listingsList.add(file.getName().replace(".txt", "").replace(user, "").replace("_to_","").strip());
            }
        }
    }

        frame = new JFrame("Messenger Client");
        frame.setSize(1250, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel, frame, client);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel, JFrame frame, AuctionClient client) {
        panel.setLayout(new BorderLayout()); // Changed to BorderLayout for main panel

        // Header Panel with BorderLayout to arrange title and info panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.LIGHT_GRAY);
        headerPanel.setPreferredSize(new Dimension(1250, 100));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // padding

        // Title (centered in header)
        JLabel title = new JLabel("Purdue Auction House Messenger Client", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerPanel.add(title, BorderLayout.CENTER);

        // Info panel on the left
        JPanel headerInfoPanel = new JPanel();
        headerInfoPanel.setLayout(new BoxLayout(headerInfoPanel, BoxLayout.Y_AXIS)); // Stack vertically
        headerInfoPanel.setBackground(Color.LIGHT_GRAY);
        headerInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0)); // padding
        headerInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel welcomeLabel = new JLabel("Welcome " + user + "!");
        JLabel typeLabel = new JLabel("Account Type: Seller");
        typeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        headerInfoPanel.add(welcomeLabel);
        headerInfoPanel.add(typeLabel);

        // Header buttons panel (for logout and delete account)
        JPanel headerButtonPanel = new JPanel();
        headerButtonPanel.setLayout(new BoxLayout(headerButtonPanel, BoxLayout.Y_AXIS));
        headerButtonPanel.setBackground(Color.LIGHT_GRAY);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(100, 30));
        logoutButton.setMaximumSize(new Dimension(100, 30));

        // Delete account button
        JButton deleteButton = new JButton("Return to Home");
        deleteButton.setPreferredSize(new Dimension(150, 30));
        deleteButton.setMaximumSize(new Dimension(150, 30));
        
        // Add buttons to separate panels to stack them vertically
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(Color.LIGHT_GRAY);
        logoutPanel.add(logoutButton);
        
        JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        deletePanel.setBackground(Color.LIGHT_GRAY);
        deletePanel.add(deleteButton);
        
        headerButtonPanel.add(logoutPanel);
        headerButtonPanel.add(deletePanel);

        // Add button panel to header
        headerPanel.add(headerInfoPanel, BorderLayout.WEST);
        headerPanel.add(headerButtonPanel, BorderLayout.EAST);

        logoutButton.addActionListener(e -> {
            frame.dispose();
        });
        
        deleteButton.addActionListener(e -> {
            frame.dispose();
            if (client.isBuyer(user)) {
                new gui.buyer.buyergui(user, password);
            } else {
                new gui.seller.sellergui(user, password);
            }
        });

        // Add header panel to the main panel at the top
        panel.add(headerPanel, BorderLayout.NORTH);

        // Create content panel for listings with a title panel at the top
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title panel for listings section with New Auction button at right
        JPanel listingsTitlePanel = new JPanel(new BorderLayout());
        listingsTitlePanel.setBackground(Color.WHITE);
        
        JLabel listingsTitle = new JLabel("My Messages");
        listingsTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        listingsTitlePanel.add(listingsTitle, BorderLayout.WEST);

        JPanel messagesListPanel = new JPanel();
        messagesListPanel.setLayout(new BoxLayout(messagesListPanel, BoxLayout.Y_AXIS));
        messagesListPanel.setBackground(Color.WHITE);

        if (listingsList.size() == 0) {
            JLabel noMessagesLabel = new JLabel("You have no messages.");
            noMessagesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            messagesListPanel.add(Box.createVerticalGlue());
            messagesListPanel.add(noMessagesLabel);
            messagesListPanel.add(Box.createVerticalGlue());
        } else {
            for (String listing : listingsList) {
                JPanel listingPanel = createMessagePanel(listing);
                messagesListPanel.add(Box.createVerticalStrut(10)); // spacing
                messagesListPanel.add(listingPanel);
            }
        }

        JScrollPane scrollPane = new JScrollPane(messagesListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Now safely add the scrollPane to the main content panel
        contentPanel.add(listingsTitlePanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add the content panel to the main panel
        panel.add(contentPanel, BorderLayout.CENTER);
        
    }
    String directoryPath = System.getProperty("user.dir") + "/src/serverclient/msg";

    private static JPanel createMessagePanel(String otherUser) {
        JButton messageButton = new JButton("Messages with " + otherUser);
        messageButton.setPreferredSize(new Dimension(100, 50));
        messageButton.setMaximumSize(new Dimension(100, 50));
        messageButton.addActionListener(e -> {
            frame.dispose();
            new messengergui(user, password, otherUser);
        });
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        panel.setPreferredSize(new Dimension(200, 50));
        panel.add(messageButton, BorderLayout.CENTER);
        
        return panel;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new messagesgui(user, password));
    }
}


