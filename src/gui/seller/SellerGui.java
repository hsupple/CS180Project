package gui.seller;
    
import accounts.AuctionClient;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;

/**
     * Gui for seller object login
     *
     * <p>Purdue University -- CS18000 -- Spring 2025</p>
     *
     * @author @Phaynes742
               @hsupple
    * @version May, 2025
    */

public class SellerGui implements Runnable {
    //Define all private fields
    private static String user;
    private static String password;
    private static AuctionClient client = null; 
    private static String[] listings; 
    private static JFrame frame = null;
    private Thread watchThread;

    // Constructor for sellergui
    public SellerGui(String user, String password) {

        this.user = user;
        this.password = password;
        
        try {
            this.client = new AuctionClient();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get user listings
        String listingsStr = client.getMyListings(user).toString();
        if (listingsStr.length() > 2) {
            this.listings = listingsStr.substring(1, listingsStr.length() - 1).split("9000");
        } else {
            this.listings = new String[0];
        }

        frame = new JFrame("Seller Interface");
        frame.setSize(1250, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel, frame, client);

        frame.setVisible(true);
        watchThread = new Thread(this);
        watchThread.start();
    }

    // run thread to find all new lisitngs
    public void run() {
        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(System.getProperty("user.dir") + "/../src/serverclient/txt");
            path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

            while (true) {
                WatchKey key = watcher.take();
                boolean shouldReload = false;
                
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                    Path filename = pathEvent.context();
                    
                    // Check if the file is not SellerList.txt
                    if (kind == StandardWatchEventKinds.ENTRY_MODIFY && 
                        !filename.toString().equals("SellerList.txt")) {
                        
                        System.out.println("File changed: " + filename + ". Reloading GUI...");
                        shouldReload = true;
                    }
                }
                
                if (shouldReload) {
                    SwingUtilities.invokeLater(() -> {
                        frame.dispose();
                        new SellerGui(user, password);
                    });
                    return;
                }
                
                key.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Place components into seller gui frame
    private static void placeComponents(JPanel panel, JFrame newFrame, AuctionClient newClient) {
        panel.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.LIGHT_GRAY);
        headerPanel.setPreferredSize(new Dimension(1250, 100));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel title = new JLabel("Purdue Auction House", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerPanel.add(title, BorderLayout.CENTER);

        JPanel headerInfoPanel = new JPanel();
        headerInfoPanel.setLayout(new BoxLayout(headerInfoPanel, BoxLayout.Y_AXIS)); 
        headerInfoPanel.setBackground(Color.LIGHT_GRAY);
        headerInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0)); 
        headerInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel welcomeLabel = new JLabel("Welcome " + user + "!");
        JLabel typeLabel = new JLabel("Account Type: Seller");
        typeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        headerInfoPanel.add(welcomeLabel);
        headerInfoPanel.add(typeLabel);

        JPanel headerButtonPanel = new JPanel();
        headerButtonPanel.setLayout(new BoxLayout(headerButtonPanel, BoxLayout.Y_AXIS));
        headerButtonPanel.setBackground(Color.LIGHT_GRAY);

        // Main panel buttons for logout and delete
        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(100, 30));
        logoutButton.setMaximumSize(new Dimension(100, 30));

        JButton deleteButton = new JButton("Delete Account");
        deleteButton.setPreferredSize(new Dimension(150, 30));
        deleteButton.setMaximumSize(new Dimension(150, 30));
        
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(Color.LIGHT_GRAY);
        logoutPanel.add(logoutButton);
        
        JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        deletePanel.setBackground(Color.LIGHT_GRAY);
        deletePanel.add(deleteButton);
        
        headerButtonPanel.add(logoutPanel);
        headerButtonPanel.add(deletePanel);

        headerPanel.add(headerInfoPanel, BorderLayout.WEST);
        headerPanel.add(headerButtonPanel, BorderLayout.EAST);

        logoutButton.addActionListener(e -> {
            newFrame.dispose();
        });
        
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(newFrame,
                "Are you sure you want to delete your account?",
                "Confirm Account Deletion",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                String enterpassword = JOptionPane.showInputDialog(newFrame, 
                    "Enter your password to confirm deletion:");
                if (enterpassword == null || enterpassword.isEmpty()) {
                    JOptionPane.showMessageDialog(newFrame, "Password cannot be empty.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                client.deleteAccount(user, enterpassword);
                newFrame.dispose();
            }
        });

        panel.add(headerPanel, BorderLayout.NORTH);

        JButton messages = new JButton("Messages");
        messages.setBounds(250, 175, 350, 25);
        messages.setMinimumSize(new Dimension(350, 25));
        messages.addActionListener(e -> {
            new gui.messages.MessagesGui(user, password);
            frame.dispose();
        });

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel listingsTitlePanel = new JPanel(new BorderLayout());
        listingsTitlePanel.setBackground(Color.WHITE);
        
        listingsTitlePanel.add(messages, BorderLayout.NORTH);

        JLabel listingsTitle = new JLabel("My Listings");
        listingsTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        listingsTitlePanel.add(listingsTitle, BorderLayout.WEST);
        
        // New auction button
        JButton auctionButton = new JButton("New Auction");
        auctionButton.setPreferredSize(new Dimension(120, 30));
        auctionButton.addActionListener(e -> {
            new NewAuction(user, password);
            frame.dispose();
        });
        
        JPanel auctionBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        auctionBtnPanel.setBackground(Color.WHITE);
        auctionBtnPanel.add(auctionButton);
        listingsTitlePanel.add(auctionBtnPanel, BorderLayout.EAST);
        
        contentPanel.add(listingsTitlePanel, BorderLayout.NORTH);
        
        // Panel for listing panel objects
        JPanel listingsPanel = new JPanel();
        listingsPanel.setLayout(new BoxLayout(listingsPanel, BoxLayout.Y_AXIS));
        listingsPanel.setBackground(Color.WHITE);
        
        // If no listings new No Listing panel
        if (listings == null || listings.length == 0) {
            JLabel noListingsLabel = new JLabel("You don't have any active listings.");
            noListingsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            noListingsLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
            listingsPanel.add(Box.createVerticalStrut(20));
            listingsPanel.add(noListingsLabel);
        } else {
            // Else create a new panel first for active listings than for not active ones
            for (String listing : listings) {
                if (listing != null && !listing.trim().isEmpty()) {
                    String[] parts = listing.split(",");
                    if (parts.length >= 6 && parts[5].strip().equals("false")) {
                        JPanel listingPanel = createListingPanel(listing);
                        listingsPanel.add(listingPanel);
                        listingsPanel.add(Box.createVerticalStrut(10));
                    }
                }
            }
            for (String listing : listings) {
                if (listing != null && !listing.trim().isEmpty()) {
                    String[] parts = listing.split(",");
                    if (parts.length >= 6 && !parts[5].strip().equals("false")) {
                        JPanel listingPanel = createListingPanel(listing);
                        listingsPanel.add(listingPanel);
                        listingsPanel.add(Box.createVerticalStrut(10));
                    }
                }
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(listingsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(contentPanel, BorderLayout.CENTER);
    }

    // create new panel for item listings
    private static JPanel createListingPanel(String listing) {
        // Get listing identifiers
        String itemName = listing.split(",")[1].strip().replace("/", " ");
        String description = listing.split(",")[3].strip().replace("/", " ");
        double buyNowPrice = Double.parseDouble(listing.split(",")[2].strip());
        double currentBid = Double.parseDouble(listing.split(",")[7].strip());
    
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.setMaximumSize(new Dimension(1200, 100));

        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BorderLayout());
        imagePanel.setPreferredSize(new Dimension(170, 160)); 
        imagePanel.setMaximumSize(new Dimension(170, 160));   
        imagePanel.setBackground(Color.WHITE);
    
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.setBackground(Color.WHITE);
    
        // Listing identifiers
        JLabel nameLabel = new JLabel("Item: " + itemName);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        detailPanel.add(nameLabel);
    
        JLabel descLabel = new JLabel("Description: " + description);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        detailPanel.add(descLabel);
    
        JLabel bidLabel = new JLabel(String.format("Current Bid: $%.2f", currentBid));
        bidLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        detailPanel.add(bidLabel);
        if (buyNowPrice > 0) {
            JLabel buyNowLabel = new JLabel(String.format("Buy Now Price: $%.2f", buyNowPrice));
            buyNowLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
            detailPanel.add(buyNowLabel);
        } else {
            JPanel buyNowPanel = new JPanel();
            buyNowPanel.setLayout(new BoxLayout(buyNowPanel, BoxLayout.X_AXIS));
            buyNowPanel.setBackground(Color.WHITE);
            buyNowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Set new buy now price if not already set
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
                        JOptionPane.showMessageDialog(panel, "Price must be greater than zero",
                            "Invalid Price", JOptionPane.ERROR_MESSAGE);
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
                    new SellerGui(user, password);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "Please enter a valid number", 
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            // ADD ALL PANELS
            buyNowPanel.add(buyNowLabel);
            buyNowPanel.add(Box.createRigidArea(new Dimension(5, 0))); // Spacing
            buyNowPanel.add(setPrice);
            buyNowPanel.add(Box.createRigidArea(new Dimension(5, 0))); // Spacing
            buyNowPanel.add(submitButton);
            
            detailPanel.add(buyNowPanel);
        }

        // Find file directory and store with underlines
        File imageDir = new File("src/gui/img/" + itemName.replaceAll("\\s+", "_") + ".png");
        if (imageDir.exists()) {
            try {
                // Image is proportioned 90 degrees wrongly, correct with AffineTransform
                BufferedImage image = ImageIO.read(imageDir);
        
                Image scaledImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                
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
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);
    
        if (listing.split(",")[5].strip().equals("false")) {
            JButton deleteButton = new JButton("Delete");
            deleteButton.setPreferredSize(new Dimension(80, 25));
            actionPanel.add(deleteButton);
            
            // Delete button logic
            deleteButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(panel,
                    "Are you sure you want to delete this listing?",
                    "Confirm Listing Deletion",
                    JOptionPane.YES_NO_OPTION);
        
                if (confirm == JOptionPane.YES_OPTION) {
                    client.endListing("9000" + listing.substring(0, 2));
                    JOptionPane.showMessageDialog(panel, "Listing deleted successfully.");
                    frame.dispose();
                    new SellerGui(user, password);
                }
            });
        }
    
        panel.add(actionPanel, BorderLayout.EAST);
        // Return new panel
        return panel;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SellerGui(user, password));
    }
}