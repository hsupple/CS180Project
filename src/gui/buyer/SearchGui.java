package gui.buyer;

import accounts.AuctionClient;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
     * Class to run new gui for search object
     *
     * <p>Purdue University -- CS18000 -- Spring 2025</p>
     *
     * @author @Phaynes742
               @hsupple
    * @version May, 2025
    */
public class SearchGui {

    // Define all private fields
    private static String user;
    private static String password;
    private static String query;
    private static JFrame frame;
    private static AuctionClient client;
    private static String[] listings;
    private static String[] sellers;
    private static Map<String, Timer> auctionTimers = new HashMap<>();

    // Construct new gui for search
    public SearchGui(String user, String password, String query) {
        this.user = user;
        this.password = password;
        this.query = query;

        try {
            this.client = new AuctionClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // get search results with user query
        String searchResults = client.searchFor(query).toString();
                
        List<String> listingsData = new ArrayList<>();
        List<String> sellersData = new ArrayList<>();


        // ensure all formatting is proper then split at Listings and Sellers
        searchResults = searchResults.replaceAll("\\[\\[", "").replaceAll("\\]\\]", "").trim();
        String[] majorParts = searchResults.split("Listings,\\s*", 2);

        if (majorParts.length > 0) {
            String sellerPart = majorParts[0].replace("Sellers,", "").trim();
            
            if (!sellerPart.isEmpty()) {
                String[] sellerArray = sellerPart.split(",");
                for (String seller : sellerArray) {
                    seller = seller.trim();
                    if (!seller.isEmpty() && !seller.equals("Listings")) {
                        sellersData.add(seller);
                    }
                }
            }
        }

        if (majorParts.length > 1) {
            String listingsPart = majorParts[1].trim();
            String[] parts = listingsPart.split(",\\s*(?=9\\d+\\\\)");
            
            for (String part : parts) {
                part = part.trim();
                
                if (part.matches("9\\d+\\\\.*")) {
                    listingsData.add(part);
                }
            }
        }

        // append array to private fields
        this.listings = listingsData.toArray(new String[0]);
        this.sellers = sellersData.toArray(new String[0]);

        this.frame = new JFrame("Buyer Interface");
        frame.setSize(1250, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        JPanel panel = new JPanel();
        frame.add(panel);

        placeComponents(panel, client);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel, AuctionClient newClient) {
        panel.setLayout(new BorderLayout());
        JPanel verticalContent = new JPanel();
        verticalContent.setLayout(new BoxLayout(verticalContent, BoxLayout.Y_AXIS));
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.LIGHT_GRAY);
        headerPanel.setPreferredSize(new Dimension(1250, 100));
        headerPanel.setMaximumSize(new Dimension(1250, 100));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Add all header information
        JLabel title = new JLabel("Purdue Auction House", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerPanel.add(title, BorderLayout.CENTER);

        JPanel headerInfoPanel = new JPanel();
        headerInfoPanel.setLayout(new BoxLayout(headerInfoPanel, BoxLayout.Y_AXIS));
        headerInfoPanel.setBackground(Color.LIGHT_GRAY);
        headerInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));
        headerInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel welcomeLabel = new JLabel("Welcome " + user + "!");
        JLabel typeLabel = new JLabel("Account Type: Buyer");
        typeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        headerInfoPanel.add(welcomeLabel);
        headerInfoPanel.add(typeLabel);

        // Return button
        JButton returnButton = new JButton("Return");
        returnButton.setPreferredSize(new Dimension(165, 45));
        returnButton.setMaximumSize(new Dimension(165, 45));

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel returnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        returnPanel.setBackground(Color.LIGHT_GRAY);
        returnPanel.add(returnButton);
        
        headerPanel.add(headerInfoPanel, BorderLayout.WEST);
        headerPanel.add(returnPanel, BorderLayout.EAST);

        // Return to buyergui and clear all timers
        returnButton.addActionListener(e -> {
            for (Timer timer : auctionTimers.values()) {
                timer.stop();
            }
            auctionTimers.clear();
            
            frame.dispose();
            new BuyerGui(user, password);
        });
        
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(350, 100));
        formPanel.setLayout(null);

        JPanel formButtonPanel = new JPanel();
        formButtonPanel.setBackground(Color.WHITE);
        formButtonPanel.setPreferredSize(new Dimension(350, 50));

        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        
        JPanel sellersPanel = new JPanel();
        sellersPanel.setLayout(new BoxLayout(sellersPanel, BoxLayout.Y_AXIS));
        sellersPanel.setBackground(Color.WHITE);
        
        // Show all sellers
        if (sellers != null && sellers.length > 0) {
            JLabel sellersHeader = new JLabel("Sellers");
            sellersHeader.setFont(new Font("SansSerif", Font.BOLD, 18));
            sellersHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
            sellersPanel.add(sellersHeader);
            sellersPanel.add(Box.createVerticalStrut(10));
            
            // new panel per seller
            for (String seller : sellers) {
                JPanel sellerPanel = new JPanel();
                sellerPanel.setLayout(new BorderLayout());
                sellerPanel.setBackground(new Color(245, 245, 245));
                sellerPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                sellerPanel.setMaximumSize(new Dimension(1200, 60));
                
                // labels for sellers and ratings
                JLabel sellerLabel = new JLabel("Seller: " + seller);
                sellerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

                JLabel sellerRating = new JLabel("       Rating: " + newClient.getRating(seller));
                sellerRating.setFont(new Font("SansSerif", Font.PLAIN, 14));
                
                JButton messageButton = new JButton("Send Message");
                messageButton.setPreferredSize(new Dimension(150, 30));

                JButton setRating = new JButton("Set Rating");
                setRating.setPreferredSize(new Dimension(250, 30));
                
                final String sellerName = seller;
                messageButton.addActionListener(e -> {
                    try {
                        new gui.messages.NewMessage(user, sellerName);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Failed to send message: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });

                setRating.addActionListener(e -> {
                    frame.dispose();
                    new gui.messages.Rating(user, password, query, sellerName, "search");
                });
                
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.setBackground(new Color(245, 245, 245));
                buttonPanel.add(messageButton);
                buttonPanel.add(setRating);
                
                sellerPanel.add(sellerLabel, BorderLayout.WEST);
                sellerPanel.add(sellerRating, BorderLayout.CENTER);
                sellerPanel.add(buttonPanel, BorderLayout.EAST);
                
                sellersPanel.add(sellerPanel);
                sellersPanel.add(Box.createVerticalStrut(10));
            }
            
            mainContentPanel.add(sellersPanel);
            mainContentPanel.add(Box.createVerticalStrut(20));
        }
        
        JPanel listingsPanel = new JPanel();
        listingsPanel.setLayout(new BoxLayout(listingsPanel, BoxLayout.Y_AXIS));
        listingsPanel.setBackground(Color.WHITE);
        
        // Show all listings
        if (listings != null && listings.length > 0) {
            JLabel listingsHeader = new JLabel("Listings");
            listingsHeader.setFont(new Font("SansSerif", Font.BOLD, 18));
            listingsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
            listingsPanel.add(listingsHeader);
            listingsPanel.add(Box.createVerticalStrut(10));
            
            for (String listing : listings) {
                // Per each valid listing, append a new listingpanel
                try {
                    
                    String[] listingParts = listing.split("\\\\");
                    
                    if (listingParts.length < 9) {
                        System.out.println("Warning: Listing has too few parts: " + listing);
                        continue;
                    }
                    
                    String itemId = listingParts[0];
                    String itemName = listingParts[1].replace("/", " ");
                    double buyNowPrice = Double.parseDouble(listingParts[2]);
                    String description = listingParts[3].replace("/", " ");
                    String seller = listingParts[4];
                    boolean isSold = Boolean.parseBoolean(listingParts[5]);
                    double currentBid = Double.parseDouble(listingParts[7]);
                    String endTime = listingParts[8];
                    
                    // ensure only active listings
                    if (isSold) {
                        continue;
                    }
                    
                    JPanel listingPanel = new JPanel();
                    listingPanel.setLayout(new BorderLayout());
                    listingPanel.setBackground(new Color(245, 245, 245));
                    listingPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));

                    JPanel leftPanel = new JPanel();
                    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
                    leftPanel.setBackground(new Color(245, 245, 245));

                    // Show all information about item
                    JLabel nameLabel = new JLabel("Item: " + itemName);
                    nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
                    leftPanel.add(nameLabel);

                    JLabel descLabel = new JLabel("Description: " + description);
                    descLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
                    leftPanel.add(descLabel);
                    
                    JLabel sellerLabel = new JLabel("Seller: " + seller);
                    sellerLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
                    leftPanel.add(sellerLabel);

                    JLabel bidLabel = new JLabel(String.format("Current Bid: $%.2f", currentBid));
                    bidLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
                    leftPanel.add(bidLabel);

                    // Add text to show valid buy now price
                    if (buyNowPrice > 0) {
                        JLabel buyNowLabel = new JLabel(String.format("Buy Now Price: $%.2f", buyNowPrice));
                        buyNowLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
                        leftPanel.add(buyNowLabel);
                    }

                    JPanel bidPanel = new JPanel();
                    bidPanel.setLayout(new BoxLayout(bidPanel, BoxLayout.X_AXIS));
                    bidPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

                    JTextField bidText = new JTextField();
                    bidText.setPreferredSize(new Dimension(100, 25));
                    bidText.setMaximumSize(new Dimension(100, 25));
                    bidText.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    bidText.setToolTipText("$ Bid Amount");

                    // Add button and text field to make new bid
                    JButton bidButton = new JButton("Make Bid");
                    bidButton.setPreferredSize(new Dimension(100, 25));
                    bidButton.setMaximumSize(new Dimension(100, 25));

                    bidButton.addActionListener(e -> {
                        try {
                            double bid = Double.parseDouble(bidText.getText());
                            if (bid <= currentBid) {
                                JOptionPane.showMessageDialog(frame, "Bid must be over current bid.", 
                                    "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            newClient.makeBid(itemName.replace(" ", "/"), user, bid);
                            
                            for (Timer timer : auctionTimers.values()) {
                                timer.stop();
                            }
                            auctionTimers.clear();
                            
                            frame.dispose();
                            new BuyerGui(user, password);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(frame, "Enter a valid number for the bid.", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        } 
                    });

                    // Make button to send message to seller
                    JButton sendMess = new JButton("Send Message");
                    sendMess.setPreferredSize(new Dimension(150, 25));
                    sendMess.setMaximumSize(new Dimension(150, 25));
                    
                    sendMess.addActionListener(e -> {
                        try {
                            new gui.messages.NewMessage(user, seller);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, "Failed to send message: " + ex.getMessage(), 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });

                    bidPanel.add(bidText);
                    bidPanel.add(Box.createHorizontalStrut(10));
                    bidPanel.add(bidButton);
                    bidPanel.add(Box.createHorizontalStrut(10));
                    bidPanel.add(sendMess);
                    bidPanel.add(Box.createHorizontalStrut(10));
                    // Only add buy now button if valid buy now is set
                    if (buyNowPrice > 0) {
                        JButton buyNowButton = new JButton("Buy Now");
                        buyNowButton.setPreferredSize(new Dimension(100, 25));
                        buyNowButton.setMaximumSize(new Dimension(100, 25));
                        buyNowButton.addActionListener(e -> {
                            try {
                                newClient.buyNow(itemName.replace(" ", "/"), user);
                                
                                for (Timer timer : auctionTimers.values()) {
                                    timer.stop();
                                }
                                auctionTimers.clear();
                                
                                frame.dispose();
                                new BuyerGui(user, password);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(frame, "Failed to buy now: " + ex.getMessage(), 
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        });
                        bidPanel.add(buyNowButton);
                    }

                    leftPanel.add(bidPanel);

                    listingPanel.add(leftPanel, BorderLayout.WEST);

                    JPanel imagePanel = new JPanel(new BorderLayout());
                    imagePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                    File imageDir = new File("src/gui/img/" + itemName.replaceAll("\\s+", "_") + ".png");
                    if (imageDir.exists()) {
                        try {
                            BufferedImage image = ImageIO.read(imageDir);

                            Image scaledImage = image.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                            imagePanel.add(imageLabel, BorderLayout.WEST);

                        } catch (IOException ex) {
                            System.err.println("Error rotating image: " + ex.getMessage());
                        }
                    }
                    
                    JLabel timerLabel = new JLabel();
                    timerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
                    timerLabel.setHorizontalAlignment(SwingConstants.LEFT);
                    timerLabel.setVerticalAlignment(SwingConstants.CENTER);
                    timerLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                    imagePanel.add(timerLabel, BorderLayout.CENTER);
                    
                    setupCountdownTimer(endTime, timerLabel, itemId);

                    listingPanel.add(imagePanel, BorderLayout.EAST);

                    listingsPanel.add(listingPanel);
                    listingsPanel.add(Box.createVerticalStrut(10));
                } catch (Exception e) {
                    System.err.println("Error processing listing: " + listing);
                    e.printStackTrace();
                }
            }
            
            mainContentPanel.add(listingsPanel);
        }
        
        // If none, show no search results
        if ((sellers == null || sellers.length == 0) && (listings == null || listings.length == 0)) {
            JLabel noResultsLabel = new JLabel("There are no search results!");
            noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            noResultsLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
            mainContentPanel.add(Box.createVerticalStrut(20));
            mainContentPanel.add(noResultsLabel);
        }

        /// Ensure pane may be scrolled if needed
        JScrollPane scrollPane = new JScrollPane(mainContentPanel);
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
                
        panel.add(contentPanel, BorderLayout.CENTER);

        verticalContent.add(headerPanel);
        verticalContent.add(formPanel);
        verticalContent.add(formButtonPanel);
        verticalContent.add(scrollContainer);

        panel.add(verticalContent, BorderLayout.CENTER);
    }
    
    // Setup new countdown concurrent to current time
    private static void setupCountdownTimer(String endTimeStr, JLabel timerLabel, String itemId) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            Date endTime = format.parse(endTimeStr);
            
            Calendar cal = Calendar.getInstance();
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endTime);
            
            endCal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
            endCal.set(Calendar.MONTH, cal.get(Calendar.MONTH));
            endCal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
            
            if (endCal.before(cal)) {
                endCal.add(Calendar.DAY_OF_MONTH, 1);
            }
            
            Timer timer = new Timer(1000, e -> {
                Calendar currentCal = Calendar.getInstance();
                
                long diffMillis = endCal.getTimeInMillis() - currentCal.getTimeInMillis();
                
                if (diffMillis <= 0) {
                    timerLabel.setText("Auction Ended");
                    timerLabel.setForeground(Color.RED);
                    ((Timer)e.getSource()).stop();
                    auctionTimers.remove(itemId);
                } else {
                    long hours = diffMillis / (60 * 60 * 1000);
                    diffMillis %= (60 * 60 * 1000);
                    long minutes = diffMillis / (60 * 1000);
                    diffMillis %= (60 * 1000);
                    long seconds = diffMillis / 1000;
                    
                    String countdownText = String.format("Time left: %02d:%02d:%02d", hours, minutes, seconds);
                    timerLabel.setText(countdownText);
                    
                    if (hours == 0 && minutes < 10) {
                        timerLabel.setForeground(Color.RED);
                    } else if (hours == 0 && minutes < 30) {
                        timerLabel.setForeground(new Color(255, 140, 0));
                    } else {
                        timerLabel.setForeground(Color.BLACK);
                    }
                }
            });
            
            auctionTimers.put(itemId, timer);
            timer.start();
            // Start new timer

        } catch (ParseException e) {
            timerLabel.setText("Error: " + endTimeStr);
            System.err.println("Error parsing end time: " + e.getMessage());
        }
    }
}