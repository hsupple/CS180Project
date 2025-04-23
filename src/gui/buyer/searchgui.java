package gui.buyer;

import accounts.AuctionClient;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class searchgui {

    private static String user;
    private static String password;
    private static AuctionClient client;
    private static String[] Listings;

    public searchgui(String user, String password, String query) {
        this.user = user;
        this.password = password;

        try {
            this.client = new AuctionClient();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.Listings = client.searchFor(query).toString().split(",");

        JFrame frame = new JFrame("Buyer Interface");
        frame.setSize(1250, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel, frame, client);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel, JFrame frame, AuctionClient client) {
        panel.setLayout(new BorderLayout());
        JPanel verticalContent = new JPanel();
    verticalContent.setLayout(new BoxLayout(verticalContent, BoxLayout.Y_AXIS));
        // Header Panel with BorderLayout to arrange title and info panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.LIGHT_GRAY);
        headerPanel.setPreferredSize(new Dimension(1250, 100));
        headerPanel.setMaximumSize(new Dimension(1250, 100));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // padding

        // Title (centered in header)
        JLabel title = new JLabel("Purdue Auction House", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerPanel.add(title, BorderLayout.CENTER);

        // Info panel on the left
        JPanel headerInfoPanel = new JPanel();
        headerInfoPanel.setLayout(new BoxLayout(headerInfoPanel, BoxLayout.Y_AXIS)); // Stack vertically
        headerInfoPanel.setBackground(Color.LIGHT_GRAY);
        headerInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0)); // padding
        headerInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel welcomeLabel = new JLabel("Welcome " + user + "!");
        JLabel typeLabel = new JLabel("Account Type: Buyer");
        typeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        headerInfoPanel.add(welcomeLabel);
        headerInfoPanel.add(typeLabel);

        JButton ReturnButton = new JButton("Return");
        ReturnButton.setPreferredSize(new Dimension(165, 45));
        ReturnButton.setMaximumSize(new Dimension(165, 45));

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel returnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        returnPanel.setBackground(Color.LIGHT_GRAY);
        returnPanel.add(ReturnButton);
        
        // Add button panel to header
        headerPanel.add(headerInfoPanel, BorderLayout.WEST);
        headerPanel.add(returnPanel, BorderLayout.EAST);

        ReturnButton.addActionListener(e -> {
            frame.dispose();
            new buyergui(user, password);
        });
        

        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(350, 100));
        formPanel.setLayout(null);

        // Button panel
        JPanel formButtonPanel = new JPanel();
        formButtonPanel.setBackground(Color.WHITE);
        formButtonPanel.setPreferredSize(new Dimension(350, 50));

        // Create scrollable panel for listings
        JPanel listingsPanel = new JPanel();
        listingsPanel.setLayout(new BoxLayout(listingsPanel, BoxLayout.Y_AXIS));
        listingsPanel.setBackground(Color.WHITE);
        
        // Check if there are any listings
        if (Listings.length < 3) {
            JLabel noListingsLabel = new JLabel("There are no search results!");
            noListingsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            noListingsLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
            listingsPanel.add(Box.createVerticalStrut(20));
            listingsPanel.add(noListingsLabel);
            frame.dispose();
        } else {
            for (int i = 1; i < Listings.length; i++) {
                
                JPanel listingPanel = createListingPanel(Listings[i]);
                listingsPanel.add(listingPanel);
                listingsPanel.add(Box.createVerticalStrut(10)); 
                
            }
        }

        JScrollPane scrollPane = new JScrollPane(listingsPanel);
        scrollPane.setPreferredSize(new Dimension(1000, 500)); 
        scrollPane.setMaximumSize(new Dimension(1000, 500));   
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT); 
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JPanel scrollContainer = new JPanel();
        scrollContainer.setLayout(new BorderLayout());
        scrollContainer.setMaximumSize(new Dimension(1000, 500));
        scrollContainer.setPreferredSize(new Dimension(1000, 500));
        scrollContainer.add(scrollPane, BorderLayout.CENTER);
                
        // Add the content panel to the main panel
        panel.add(contentPanel, BorderLayout.CENTER);

        verticalContent.add(headerPanel);
        verticalContent.add(formPanel);
        verticalContent.add(formButtonPanel);
        verticalContent.add(scrollContainer);

        panel.add(verticalContent, BorderLayout.CENTER);
    }

    // Helper method to create a panel for each listing
    private static JPanel createListingPanel(String listing) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.setMaximumSize(new Dimension(1200, 100));
        
        // Assume listing string has some structure - modify this according to your data format
        JLabel listingLabel = new JLabel(listing);
        listingLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(listingLabel, BorderLayout.CENTER);
        
        // Add buttons for listing actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);
        
        JButton viewButton = new JButton("View Details");
        viewButton.setPreferredSize(new Dimension(100, 25));
        actionPanel.add(viewButton);
        
        
        panel.add(actionPanel, BorderLayout.EAST);
        
        return panel;
    }
}

