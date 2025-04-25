package gui.buyer;

import accounts.AuctionClient;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class buyergui {

    private static String user;
    private static String password;
    private static AuctionClient client = null; 
    private static String[] Listings; 
    private static JFrame frame = null;

    public buyergui(String user, String password) {
        this.user = user;
        this.password = password;

        try {
            this.client = new AuctionClient();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.Listings = client.getMyListings("ALL").toString().substring(1, client.getMyListings("ALL").toString().length() - 2).split("9000");
        for (int i = 0; i < Listings.length; i++) {
            System.out.println(Listings[i]);
        }

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

        // Logout button aligned to the right
        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(100, 30));
        logoutButton.setMaximumSize(new Dimension(100, 30));

        // Delete account button
        JButton deleteButton = new JButton("Delete Account");
        deleteButton.setPreferredSize(new Dimension(150, 30));
        deleteButton.setMaximumSize(new Dimension(150, 30));

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        

        // Create a panel for the buttons with FlowLayout
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.LIGHT_GRAY);

        // Add buttons to separate panels to stack them vertically
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(Color.LIGHT_GRAY);
        logoutPanel.add(logoutButton);
        
        JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        deletePanel.setBackground(Color.LIGHT_GRAY);
        deletePanel.add(deleteButton);
        
        buttonPanel.add(logoutPanel);
        buttonPanel.add(deletePanel);

        // Add button panel to header
        headerPanel.add(headerInfoPanel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        logoutButton.addActionListener(e -> {
            frame.dispose();
        });
        
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to delete your account?",
                "Confirm Account Deletion",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                String enterpassword = JOptionPane.showInputDialog(frame, "Enter your password to confirm deletion:");
                if (enterpassword == null || enterpassword.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                client.deleteAccount(user, enterpassword);
                frame.dispose();
            }
        });

        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(350, 100));
        formPanel.setLayout(null);

        JLabel userLabel = new JLabel("Search:");
        userLabel.setBounds(50, 50, 80, 25);
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        formPanel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(250, 25, 750, 75);
        userText.setFont(new Font("SansSerif", Font.PLAIN, 24)); // 👈 Bigger font
        formPanel.add(userText);

        JButton searchButton = new JButton("Search Listings");
        searchButton.setBounds(250, 125, 150, 25);
        formPanel.add(searchButton);

        searchButton.addActionListener(e -> {
            String searchQuery = userText.getText();
            if (!searchQuery.isEmpty()) {
                new searchgui(user, password, searchQuery);
                frame.dispose();
            }
        });


        // Button panel
        JPanel formButtonPanel = new JPanel();
        formButtonPanel.setBackground(Color.WHITE);
        formButtonPanel.setPreferredSize(new Dimension(350, 50));

        // Create scrollable panel for listings
        JPanel listingsPanel = new JPanel();
        listingsPanel.setLayout(new BoxLayout(listingsPanel, BoxLayout.Y_AXIS));
        listingsPanel.setBackground(Color.WHITE);
        
        for (int i = 1; i < Listings.length; i++) {
            if (Listings[i].split(",")[5].strip().equals("false")) {
                JPanel listingPanel = createListingPanel(Listings[i]);
                listingsPanel.add(listingPanel);
                listingsPanel.add(Box.createVerticalStrut(10));
                
                JTextField bidText = new JTextField(20);
                bidText.setPreferredSize(new Dimension(20, 10));
                bidText.setMaximumSize(new Dimension(20, 10));
                bidText.setFont(new Font("SansSerif", Font.PLAIN, 14));
                bidText.setToolTipText("$ Bid Amount");
                listingPanel.add(bidText, BorderLayout.NORTH);

                JButton bidButton = new JButton("Make Bid");
                bidButton.setPreferredSize(new Dimension(100, 25));
                bidButton.setMaximumSize(new Dimension(100, 25));
                bidButton.addActionListener(e -> {
                    Double bid =  Double.parseDouble(bidText.getText());
                    if (bid < 0) {
                        JOptionPane.showMessageDialog(frame, "Bid Must be over Current Bid.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    client.makeBid("Penis", user, bid);
                    frame.dispose();
                    new buyergui(user, password);
                });
                listingPanel.add(bidButton, BorderLayout.EAST);
            }
        }

        JScrollPane scrollPane = new JScrollPane(listingsPanel);
        scrollPane.setPreferredSize(new Dimension(1000, 355)); 
        scrollPane.setMaximumSize(new Dimension(1000, 355));   
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT); 
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JPanel scrollContainer = new JPanel();
        scrollContainer.setLayout(new BorderLayout());
        scrollContainer.setMaximumSize(new Dimension(1000, 355));
        scrollContainer.setPreferredSize(new Dimension(1000, 355));
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