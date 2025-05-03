package gui.buyer;
    
import accounts.AuctionClient;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Gui for seller account view
 *
 * <p>Purdue University -- CS18000 -- Spring 2025</p>
 *
 * @author @Phaynes742
           @hsupple
 * @version May, 2025
 */

public class selleracct {
    // Define all private fields
    private static String user;
    private static String password;
    private static AuctionClient client = null; 
    private static String seller; 
    private static String[] Listings;
    private static JFrame frame = null;
    private static Map<String, Timer> auctionTimers = new HashMap<>();
    
    // Constructor for sellergui
    public selleracct(String user, String password, String seller) {
        this.seller = seller;
        this.user = user;
        this.password = password;
        
        try {
            this.client = new AuctionClient();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get seller listings
        this.Listings = client.getMyListings(seller).toString().substring(1, client.getMyListings(seller).toString().length() - 1).split("9000");
        for (int i = 0; i < Listings.length; i++) {
            System.out.println(Listings[i]);
        }
        
        frame = new JFrame("Seller Account: " + seller);
        frame.setSize(1250, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel, frame, client);

        frame.setVisible(true);
    }

    // Place components into seller gui frame
    private static void placeComponents(JPanel panel, JFrame frame, AuctionClient client) {
        panel.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.LIGHT_GRAY);
        headerPanel.setPreferredSize(new Dimension(1250, 100));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel title = new JLabel(seller, SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerPanel.add(title, BorderLayout.CENTER);

        JPanel headerInfoPanel = new JPanel();
        headerInfoPanel.setLayout(new BoxLayout(headerInfoPanel, BoxLayout.Y_AXIS)); 
        headerInfoPanel.setBackground(Color.LIGHT_GRAY);
        headerInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0)); 
        headerInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel headerButtonPanel = new JPanel();
        headerButtonPanel.setLayout(new BoxLayout(headerButtonPanel, BoxLayout.Y_AXIS));
        headerButtonPanel.setBackground(Color.LIGHT_GRAY);

        // Main panel buttons for logout and delete
        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(100, 30));
        logoutButton.setMaximumSize(new Dimension(100, 30));

        JButton deleteButton = new JButton("Return");
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
            for (Timer timer : auctionTimers.values()) {
                timer.stop();
            }
            auctionTimers.clear();
            frame.dispose();
        });
        
        deleteButton.addActionListener(e -> {
            for (Timer timer : auctionTimers.values()) {
                timer.stop();
            }
            auctionTimers.clear();
            new buyergui(user, password);
            frame.dispose();
        });

        panel.add(headerPanel, BorderLayout.NORTH);

        JButton Messages = new JButton("Message User: " + seller);
        Messages.addActionListener(e -> {
            for (Timer timer : auctionTimers.values()) {
                timer.stop();
            }
            auctionTimers.clear();
            new gui.messages.newmessage(user, seller);
        });

        JButton rateButton = new JButton("Rate User: " + seller);
        rateButton.addActionListener(e -> {
            // Stop all timers before disposing frame
            for (Timer timer : auctionTimers.values()) {
                timer.stop();
            }
            auctionTimers.clear();
            new gui.messages.rating(user, password, "", seller, "Seller");
        });

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel listingsTitlePanel = new JPanel(new BorderLayout());
        listingsTitlePanel.setBackground(Color.WHITE);
        
        JPanel ListingTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ListingTitlePanel.setBackground(Color.WHITE);

        JLabel listingsTitle = new JLabel(seller + "'s Listings");
        listingsTitle.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel ratingLabel = new JLabel("Rating: " + client.getRating(seller));
        ratingLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

        ListingTitlePanel.add(listingsTitle);
        ListingTitlePanel.add(Box.createHorizontalStrut(10));
        ListingTitlePanel.add(ratingLabel);
        
        listingsTitlePanel.add(ListingTitlePanel, BorderLayout.WEST);

        JPanel auctionBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        auctionBtnPanel.setBackground(Color.WHITE);
        auctionBtnPanel.add(Messages);
        auctionBtnPanel.add(rateButton);
        listingsTitlePanel.add(auctionBtnPanel, BorderLayout.EAST);
        
        contentPanel.add(listingsTitlePanel, BorderLayout.NORTH);
        
        // Panel for listing panel objects
        JPanel listingsPanel = new JPanel();
        listingsPanel.setLayout(new BoxLayout(listingsPanel, BoxLayout.Y_AXIS));
        listingsPanel.setBackground(Color.WHITE);
        
        // If no listings or only one entry (which would be an empty/header entry)
        if (Listings.length <= 1) {
            JLabel noListingsLabel = new JLabel("This seller doesn't have any active listings.");
            noListingsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            noListingsLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
            listingsPanel.add(Box.createVerticalStrut(20));
            listingsPanel.add(noListingsLabel);
        } else {
            // First show active listings, then inactive ones
            // Show active listings first
            for (int i = 1; i < Listings.length; i++) {
                String[] parts = Listings[i].split(",");
                if (parts.length >= 6 && parts[5].strip().equals("false")) {
                    JPanel listingPanel = createListingPanel(Listings[i], i);
                    listingsPanel.add(listingPanel);
                    listingsPanel.add(Box.createVerticalStrut(10));
                }
            }
            
            // Then show inactive listings
            for (int i = 1; i < Listings.length; i++) {
                String[] parts = Listings[i].split(",");
                if (parts.length >= 6 && !parts[5].strip().equals("false")) {
                    JPanel listingPanel = createListingPanel(Listings[i], i);
                    listingsPanel.add(listingPanel);
                    listingsPanel.add(Box.createVerticalStrut(10));
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

    // Create new panel for item listings
    private static JPanel createListingPanel(String listing, int index) {
        String[] parts = listing.split(",");
        if (parts.length < 9) {
            // Handle malformed listing data
            JPanel errorPanel = new JPanel();
            errorPanel.add(new JLabel("Error: Invalid listing data"));
            return errorPanel;
        }
        
        String itemName = parts[1].strip().replace("/", " ");
        String description = parts[3].strip().replace("/", " ");
        double buyNowPrice = Double.parseDouble(parts[2].strip());
        double currentBid = Double.parseDouble(parts[7].strip());
        String endTime = parts[8].strip();
        String itemSeller = parts[4].strip();
        boolean isActive = parts[5].strip().equals("false");

        JPanel listingPanel = new JPanel();
        listingPanel.setLayout(new BorderLayout());
        listingPanel.setBackground(isActive ? new Color(245, 245, 245) : new Color(220, 220, 220));
        listingPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(isActive ? new Color(245, 245, 245) : new Color(220, 220, 220));

        // Display all information
        JLabel nameLabel = new JLabel("Item: " + itemName);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        leftPanel.add(nameLabel);

        JLabel descLabel = new JLabel("Description: " + description);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        leftPanel.add(descLabel);

        JLabel bidLabel = new JLabel("Current Bid: $" + currentBid);
        bidLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        leftPanel.add(bidLabel);

        if (buyNowPrice > 0) {
            JLabel buyNowLabel = new JLabel("Buy Now Price: $" + buyNowPrice);
            buyNowLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
            leftPanel.add(buyNowLabel);
        }
        
        // Add status label if not active
        if (!isActive) {
            JLabel statusLabel = new JLabel("Status: Auction Ended");
            statusLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            statusLabel.setForeground(Color.RED);
            leftPanel.add(statusLabel);
        }

        JPanel bidPanel = new JPanel();
        bidPanel.setLayout(new BoxLayout(bidPanel, BoxLayout.X_AXIS));
        bidPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bidPanel.setBackground(isActive ? new Color(245, 245, 245) : new Color(220, 220, 220));

        // Only display bid controls if the auction is active
        if (isActive) {
            JTextField bidText = new JTextField();
            bidText.setPreferredSize(new Dimension(100, 25));
            bidText.setMaximumSize(new Dimension(100, 25));
            bidText.setFont(new Font("SansSerif", Font.PLAIN, 14));
            bidText.setToolTipText("$ Bid Amount");

            // Create button with text field to make a bid
            JButton bidButton = new JButton("Make Bid");
            bidButton.setPreferredSize(new Dimension(100, 25));
            bidButton.setMaximumSize(new Dimension(100, 25));

            // Ensure new bid is valid
            bidButton.addActionListener(e -> {
                try {
                    double bid = Double.parseDouble(bidText.getText());
                    if (bid <= currentBid) {
                        JOptionPane.showMessageDialog(frame, "Bid must be over current bid.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    client.makeBid(itemName.replace(" ", "/"), user, bid);
                    
                    for (Timer timer : auctionTimers.values()) {
                        timer.stop();
                    }
                    auctionTimers.clear();
                    
                    frame.dispose();
                    new selleracct(user, password, seller);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Enter a valid number for the bid.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Add bid controls to panel
            bidPanel.add(bidText);
            bidPanel.add(Box.createHorizontalStrut(10));
            bidPanel.add(bidButton);
            
            // Ensure buy now is properly setup and displayed
            if (buyNowPrice > 0) {
                JButton buyNowButton = new JButton("Buy Now");
                buyNowButton.setPreferredSize(new Dimension(100, 25));
                buyNowButton.setMaximumSize(new Dimension(100, 25));
                buyNowButton.addActionListener(e -> {
                    try {
                        client.buyNow(itemName.replace(" ", "/"), user);
                        
                        // Stop all timers before disposing frame
                        for (Timer timer : auctionTimers.values()) {
                            timer.stop();
                        }
                        auctionTimers.clear();
                        
                        frame.dispose();
                        new selleracct(user, password, seller);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Failed to buy now: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
                bidPanel.add(Box.createHorizontalStrut(10));
                bidPanel.add(buyNowButton);
            }
        }

        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(bidPanel);

        listingPanel.add(leftPanel, BorderLayout.WEST);

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        imagePanel.setBackground(isActive ? new Color(245, 245, 245) : new Color(220, 220, 220));

        // Load file if in img folder
        File imageDir = new File("src/gui/img/" + itemName.replaceAll("\\s+", "_") + ".png");
        if (imageDir.exists()) {
            try {
                BufferedImage image = ImageIO.read(imageDir);
                Image scaledImage = image.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                imagePanel.add(imageLabel, BorderLayout.WEST);
            } catch (IOException ex) {
                System.err.println("Error loading image: " + ex.getMessage());
            }
        } 
        
        // Setup timer label
        JLabel timerLabel = new JLabel();
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        timerLabel.setHorizontalAlignment(SwingConstants.LEFT);
        timerLabel.setVerticalAlignment(SwingConstants.CENTER);
        timerLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        imagePanel.add(timerLabel, BorderLayout.CENTER);
        
        if (isActive) {
            setupCountdownTimer(endTime, timerLabel, itemName);
        } else {
            timerLabel.setText("Auction Ended");
            timerLabel.setForeground(Color.RED);
        }

        listingPanel.add(imagePanel, BorderLayout.EAST);

        return listingPanel;
    }
    
    private static void setupCountdownTimer(String endTimeStr, JLabel timerLabel, String itemId) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            Date endTime = format.parse(endTimeStr);
            
            Calendar currentCal = Calendar.getInstance();
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endTime);
            
            endCal.set(Calendar.YEAR, currentCal.get(Calendar.YEAR));
            endCal.set(Calendar.MONTH, currentCal.get(Calendar.MONTH));
            endCal.set(Calendar.DAY_OF_MONTH, currentCal.get(Calendar.DAY_OF_MONTH));
            
            if (endCal.getTimeInMillis() < currentCal.getTimeInMillis()) {
                endCal.add(Calendar.DAY_OF_MONTH, 1);
            }
            
            long initialDiffMillis = endCal.getTimeInMillis() - currentCal.getTimeInMillis();
            
            if (initialDiffMillis <= 0) {
                timerLabel.setText("Auction Ended");
                timerLabel.setForeground(Color.RED);
                return; 
            }
            
            Timer timer = new Timer(1000, e -> {
                Calendar nowCal = Calendar.getInstance();
                
                long diffMillis = endCal.getTimeInMillis() - nowCal.getTimeInMillis();
                
                if (diffMillis <= 0) {
                    timerLabel.setText("Auction Ended");
                    timerLabel.setForeground(Color.RED);
                    ((Timer)e.getSource()).stop();
                    auctionTimers.remove(itemId);
                    
                    SwingUtilities.invokeLater(() -> {

                    });
                } else {
                    long hours = diffMillis / (60 * 60 * 1000);
                    diffMillis %= (60 * 60 * 1000);
                    long minutes = diffMillis / (60 * 1000);
                    diffMillis %= (60 * 1000);
                    long seconds = diffMillis / 1000;
                    
                    String countdownText = String.format("Time left: %02d:%02d:%02d", hours, minutes, seconds);
                    timerLabel.setText(countdownText);
                    
                    // Change color based on time remaining
                    if (hours == 0 && minutes < 10) {
                        timerLabel.setForeground(Color.RED);
                    } else if (hours == 0 && minutes < 30) {
                        timerLabel.setForeground(new Color(255, 140, 0)); // Orange
                    } else {
                        timerLabel.setForeground(Color.BLACK);
                    }
                }
            });
            
            auctionTimers.put(itemId, timer);
            timer.setInitialDelay(0);
            timer.start();
            
        } catch (ParseException e) {
            timerLabel.setText("Error: Invalid time format");
            System.err.println("Error parsing end time: " + endTimeStr + " - " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            timerLabel.setText("Error");
            System.err.println("General error in timer setup: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new selleracct("user", "password", "seller"));
    }
}