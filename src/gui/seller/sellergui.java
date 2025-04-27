package gui.seller;
    
import accounts.AuctionClient;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;

public class sellergui {

    private static String user;
    private static String password;
    private static AuctionClient client = null; 
    private static String[] Listings; 
    private static JFrame frame = null;

    public sellergui(String user, String password) {

        this.user = user;
        this.password = password;
        
        try {
            this.client = new AuctionClient();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.Listings = client.getMyListings(user).toString().substring(1, client.getMyListings(user).toString().length() - 2).split("9000");
        for (int i = 0; i < Listings.length; i++) {
            System.out.println(Listings[i]);
        }
        frame = new JFrame("Seller Interface");
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
        JButton deleteButton = new JButton("Delete Account");
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

        // Add header panel to the main panel at the top
        panel.add(headerPanel, BorderLayout.NORTH);

        JButton Messages = new JButton("Messages");
        Messages.setBounds(250, 175, 350, 25);
        Messages.setMinimumSize(new Dimension(350, 25));
        Messages.addActionListener(e -> {
            new gui.messages.messagesgui(user, password);
            frame.dispose();
        });

        // Create content panel for listings with a title panel at the top
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title panel for listings section with New Auction button at right
        JPanel listingsTitlePanel = new JPanel(new BorderLayout());
        listingsTitlePanel.setBackground(Color.WHITE);
        
        listingsTitlePanel.add(Messages, BorderLayout.NORTH);

        JLabel listingsTitle = new JLabel("My Listings");
        listingsTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        listingsTitlePanel.add(listingsTitle, BorderLayout.WEST);
        
        JButton auctionButton = new JButton("New Auction");
        auctionButton.setPreferredSize(new Dimension(120, 30));
        auctionButton.addActionListener(e -> {
            new newauction(user, password);
            frame.dispose();
        });
        
        JPanel auctionBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        auctionBtnPanel.setBackground(Color.WHITE);
        auctionBtnPanel.add(auctionButton);
        listingsTitlePanel.add(auctionBtnPanel, BorderLayout.EAST);
        
        contentPanel.add(listingsTitlePanel, BorderLayout.NORTH);
        
        // Create scrollable panel for listings
        JPanel listingsPanel = new JPanel();
        listingsPanel.setLayout(new BoxLayout(listingsPanel, BoxLayout.Y_AXIS));
        listingsPanel.setBackground(Color.WHITE);
        
        // Check if there are any listings
        if (Listings.length == 0) {
            JLabel noListingsLabel = new JLabel("You don't have any active listings.");
            noListingsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            noListingsLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
            listingsPanel.add(Box.createVerticalStrut(20));
            listingsPanel.add(noListingsLabel);
        } else {
            for (int i = 1; i < Listings.length; i++) {
                if (Listings[i].split(",")[5].strip().equals("false")) {
                    JPanel listingPanel = createListingPanel(Listings[i]);
                    listingsPanel.add(listingPanel);
                    listingsPanel.add(Box.createVerticalStrut(10)); 
                }
            }
            for (int i = 1; i < Listings.length; i++) {
                if (!Listings[i].split(",")[5].strip().equals("false")) {
                    JPanel listingPanel = createListingPanel(Listings[i]);
                    listingsPanel.add(listingPanel);
                    listingsPanel.add(Box.createVerticalStrut(10)); 
                }
            }
        }
        
        // Make listings panel scrollable
        JScrollPane scrollPane = new JScrollPane(listingsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add the content panel to the main panel
        panel.add(contentPanel, BorderLayout.CENTER);
    }
    
    private static JPanel createListingPanel(String listing) {
        String itemName = listing.split(",")[1].strip().replace("/", " ");
        String description = listing.split(",")[3].strip().replace("/", " ");
        double buyNowPrice = Double.parseDouble(listing.split(",")[2].strip());
        double currentBid = Double.parseDouble(listing.split(",")[7].strip());
    
        JPanel panel = new JPanel(new BorderLayout(10, 0)); // Add horizontal gap
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.setMaximumSize(new Dimension(1200, 100));

        // Create a fixed-size panel for the image
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BorderLayout());
        imagePanel.setPreferredSize(new Dimension(170, 160)); // Fixed size
        imagePanel.setMaximumSize(new Dimension(170, 160));   // Fixed maximum size
        imagePanel.setBackground(Color.WHITE);
    
        // Create a vertical panel for the listing details
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.setBackground(Color.WHITE);
    
        JLabel nameLabel = new JLabel("Item: " + itemName);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        detailPanel.add(nameLabel);
    
        JLabel descLabel = new JLabel("Description: " + description);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        detailPanel.add(descLabel);
    
        JLabel bidLabel = new JLabel("Current Bid: $" + currentBid);
        bidLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        detailPanel.add(bidLabel);
        if (buyNowPrice > 0) {
            JLabel buyNowLabel = new JLabel("Buy Now Price: $" + buyNowPrice);
            buyNowLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
            detailPanel.add(buyNowLabel);
        } else {
            // Create panel that respects the parent BoxLayout
            JPanel buyNowPanel = new JPanel();
            buyNowPanel.setLayout(new BoxLayout(buyNowPanel, BoxLayout.X_AXIS));
            buyNowPanel.setBackground(Color.WHITE);
            buyNowPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Important for proper alignment
            
            JLabel buyNowLabel = new JLabel("Set Buy Now Price: $");
            buyNowLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
            
            JTextField setPrice = new JTextField(8);
            setPrice.setMaximumSize(new Dimension(80, 25));
            
            JButton submitButton = new JButton("Set");
            submitButton.setPreferredSize(new Dimension(60, 25));
            submitButton.setMaximumSize(new Dimension(60, 25));
            
            submitButton.addActionListener(e -> {
                try {
                    String[] listingArr = listing.split(",");
                    double newPrice = Double.parseDouble(setPrice.getText());
                    if (newPrice <= 0) {
                        JOptionPane.showMessageDialog(panel, "Price must be greater than zero", "Invalid Price", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    client.updateItemListing(900000 + Integer.valueOf(listingArr[0]), 
                                             listingArr[1].strip(), 
                                             listingArr[3].strip(), 
                                             newPrice, 
                                             listingArr[4].strip(), 
                                             false, 
                                             listingArr[6].strip(), 
                                             Double.parseDouble(listingArr[7]), 
                                             listingArr[8].strip());
                    
                    frame.dispose();
                    new sellergui(user, password);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "Please enter a valid number", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            });
        
            buyNowPanel.add(buyNowLabel);
            buyNowPanel.add(Box.createRigidArea(new Dimension(5, 0))); // Spacing
            buyNowPanel.add(setPrice);
            buyNowPanel.add(Box.createRigidArea(new Dimension(5, 0))); // Spacing
            buyNowPanel.add(submitButton);
            
            detailPanel.add(buyNowPanel);
        }
        File imageDir = new File("src/gui/img/" + itemName.replaceAll("\\s+", "_") + ".png");
        if (imageDir.exists()) {
            try {
                // Load the original image
                BufferedImage originalImage = ImageIO.read(imageDir);
                
                // Create a new image with swapped dimensions for rotation
                BufferedImage rotatedImage = new BufferedImage(
                    originalImage.getHeight(), 
                    originalImage.getWidth(), 
                    originalImage.getType()
                );
                
                // Get the Graphics2D object and set up rotation transform
                Graphics2D g2d = rotatedImage.createGraphics();
                AffineTransform transform = new AffineTransform();
                
                // For 90 degrees clockwise rotation:
                transform.translate(originalImage.getHeight(), 0);
                transform.rotate(Math.PI/2);
                
                // Apply transform and draw
                g2d.setTransform(transform);
                g2d.drawImage(originalImage, 0, 0, null);
                g2d.dispose();
                
                // Scale the rotated image
                Image scaledImage = rotatedImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                
                // Create and set the icon
                JLabel imageLabel = new JLabel();
                imageLabel.setIcon(new ImageIcon(scaledImage));
                imageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
                imagePanel.add(imageLabel, BorderLayout.EAST);
            } catch (IOException ex) {
                System.err.println("Error rotating image: " + ex.getMessage());
            }
        }

        
        panel.add(detailPanel, BorderLayout.WEST);
        panel.add(imagePanel, BorderLayout.CENTER);
        // Add buttons for listing actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);
    
        if (listing.split(",")[5].strip().equals("false")) {
            JButton deleteButton = new JButton("Delete");
            deleteButton.setPreferredSize(new Dimension(80, 25));
            actionPanel.add(deleteButton);
        
            deleteButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(panel,
                    "Are you sure you want to delete this listing?",
                    "Confirm Listing Deletion",
                    JOptionPane.YES_NO_OPTION);
        
                if (confirm == JOptionPane.YES_OPTION) {
                    client.endListing("9000" + listing.substring(0, 2));
                    JOptionPane.showMessageDialog(panel, "Listing deleted successfully.");
                    frame.dispose();
                    new sellergui(user, password);
                }
            });
        }
    
        panel.add(actionPanel, BorderLayout.EAST);
    
        return panel;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new sellergui(user, password));
    }
}