package gui.buyer;

import accounts.AuctionClient;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.*;

public class searchgui {

    private static String user;
    private static String password;
    private static AuctionClient client;
    private static String[] Listings;
    private static String[] Sellers;
    private static Map<String, Timer> auctionTimers = new HashMap<>();

    public searchgui(String user, String password, String query) {
        this.user = user;
        this.password = password;

        try {
            this.client = new AuctionClient();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Debugging output
        System.out.println("Raw search results: " + client.searchFor(query).toString());
        
        // Fix: properly handle the search results format
        String searchResults = client.searchFor(query).toString();
        
        // Extract sellers and listings
        List<String> listingsData = new ArrayList<>();
        List<String> sellersData = new ArrayList<>();
        
        // Check if there are sellers in the results
        if (searchResults.contains("Sellers, ")) {
            int sellersStart = searchResults.indexOf("Sellers, ") + 9;
            int sellersEnd = searchResults.indexOf("Listings, ");
            
            if (sellersEnd == -1) {
                String sellersPart = searchResults.substring(sellersStart);
                
                if (sellersPart.endsWith("]")) {
                    sellersPart = sellersPart.substring(0, sellersPart.length() - 1);
                }
                if (sellersPart.endsWith("]")) {
                    sellersPart = sellersPart.substring(0, sellersPart.length() - 1);
                }
                
                // Split by comma and space
                String[] sellerArray = sellersPart.split(", ");
                for (String seller : sellerArray) {
                    if (!seller.isEmpty() && !seller.trim().equals("Listings")) {
                        sellersData.add(seller.trim());
                    }
                }
            } else {
                String sellersPart = searchResults.substring(sellersStart, sellersEnd).trim();
                
                if (sellersPart.endsWith(",")) {
                    sellersPart = sellersPart.substring(0, sellersPart.length() - 1);
                }
                
                String[] sellerArray = sellersPart.split(", ");
                for (String seller : sellerArray) {
                    if (!seller.isEmpty()) {
                        sellersData.add(seller.trim());
                    }
                }
            }
        }
        
        // Pattern to match each listing (starts with 9 followed by digits, then data separated by backslashes)
        Pattern pattern = Pattern.compile("9\\d+\\\\[^,]+(?:\\\\[^,]*){7}");
        Matcher matcher = pattern.matcher(searchResults);
        
        while (matcher.find()) {
            listingsData.add(matcher.group());
        }
        
        this.Listings = listingsData.toArray(new String[0]);
        this.Sellers = sellersData.toArray(new String[0]);
        
        // Debug the extracted data
        System.out.println("Found " + Listings.length + " listings:");
        for (String listing : Listings) {
            System.out.println("Extracted listing: " + listing);
        }
        
        System.out.println("Found " + Sellers.length + " sellers:");
        for (String seller : Sellers) {
            System.out.println("Extracted seller: " + seller);
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
            // Stop all timers before disposing the frame
            for (Timer timer : auctionTimers.values()) {
                timer.stop();
            }
            auctionTimers.clear();
            
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

        // Create the main content panel with sections for listings and sellers
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        
        // Create sellers panel first
        JPanel sellersPanel = new JPanel();
        sellersPanel.setLayout(new BoxLayout(sellersPanel, BoxLayout.Y_AXIS));
        sellersPanel.setBackground(Color.WHITE);
        
        // Create a header for sellers section if there are sellers
        if (Sellers != null && Sellers.length > 0) {
            JLabel sellersHeader = new JLabel("Sellers");
            sellersHeader.setFont(new Font("SansSerif", Font.BOLD, 18));
            sellersHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
            sellersPanel.add(sellersHeader);
            sellersPanel.add(Box.createVerticalStrut(10));
            
            // Add each seller as a panel
            for (String seller : Sellers) {
                JPanel sellerPanel = new JPanel();
                sellerPanel.setLayout(new BorderLayout());
                sellerPanel.setBackground(new Color(245, 245, 245));
                sellerPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                sellerPanel.setMaximumSize(new Dimension(1200, 60));
                
                JLabel sellerLabel = new JLabel("Seller: " + seller);
                sellerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

                JLabel sellerRating = new JLabel("Rating: " + client.getRating(seller));
                sellerRating.setFont(new Font("SansSerif", Font.PLAIN, 14));
                
                JButton messageButton = new JButton("Send Message");
                messageButton.setPreferredSize(new Dimension(150, 30));

                JButton setRating = new JButton("Set Rating");
                setRating.setPreferredSize(new Dimension(250, 30));
                
                final String sellerName = seller;
                messageButton.addActionListener(e -> {
                    try {
                        new gui.messages.newmessage(user, sellerName, "");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Failed to send message: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });

                setRating.addActionListener(e -> {
                    new gui.messages.rating(user, sellerName);
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
            
            // Add sellers panel to the main content
            mainContentPanel.add(sellersPanel);
            mainContentPanel.add(Box.createVerticalStrut(20));
        }
        
        // Create listings panel
        JPanel listingsPanel = new JPanel();
        listingsPanel.setLayout(new BoxLayout(listingsPanel, BoxLayout.Y_AXIS));
        listingsPanel.setBackground(Color.WHITE);
        
        // Create a header for listings section if there are listings
        if (Listings != null && Listings.length > 0) {
            JLabel listingsHeader = new JLabel("Listings");
            listingsHeader.setFont(new Font("SansSerif", Font.BOLD, 18));
            listingsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
            listingsPanel.add(listingsHeader);
            listingsPanel.add(Box.createVerticalStrut(10));
            
            for (String listing : Listings) {
                try {
                    // Log for debugging
                    System.out.println("Processing listing: " + listing);
                    
                    // Split by backslash - this is the delimiter between fields in each listing
                    String[] listingParts = listing.split("\\\\");
                    
                    // Ensure we have enough parts
                    if (listingParts.length < 9) {
                        System.out.println("Warning: Listing has too few parts: " + listing);
                        continue;
                    }
                    
                    // Extract the listing data
                    String itemId = listingParts[0];
                    String itemName = listingParts[1].replace("/", " ");
                    double buyNowPrice = Double.parseDouble(listingParts[2]);
                    String description = listingParts[3].replace("/", " ");
                    String seller = listingParts[4];
                    boolean isSold = Boolean.parseBoolean(listingParts[5]);
                    // listingParts[6] is "None" or buyer name
                    double currentBid = Double.parseDouble(listingParts[7]);
                    String endTime = listingParts[8];
                    
                    // Skip sold items
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

                    // Item name (aligned to the WEST)
                    JPanel leftPanel = new JPanel();
                    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
                    leftPanel.setBackground(new Color(245, 245, 245));

                    JLabel nameLabel = new JLabel("Item: " + itemName);
                    nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
                    leftPanel.add(nameLabel);

                    // Description (aligned to the WEST)
                    JLabel descLabel = new JLabel("Description: " + description);
                    descLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
                    leftPanel.add(descLabel);
                    
                    // Seller (aligned to the WEST)
                    JLabel sellerLabel = new JLabel("Seller: " + seller);
                    sellerLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
                    leftPanel.add(sellerLabel);

                    // Current Bid (aligned to the WEST)
                    JLabel bidLabel = new JLabel("Current Bid: $" + currentBid);
                    bidLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
                    leftPanel.add(bidLabel);

                    if (buyNowPrice > 0) {
                        JLabel buyNowLabel = new JLabel("Buy Now Price: $" + buyNowPrice);
                        buyNowLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
                        leftPanel.add(buyNowLabel);
                    }

                    // Bid input and button (aligned to the WEST)
                    JPanel bidPanel = new JPanel();
                    bidPanel.setLayout(new BoxLayout(bidPanel, BoxLayout.X_AXIS));
                    bidPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

                    JTextField bidText = new JTextField();
                    bidText.setPreferredSize(new Dimension(100, 25));
                    bidText.setMaximumSize(new Dimension(100, 25));
                    bidText.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    bidText.setToolTipText("$ Bid Amount");

                    JButton bidButton = new JButton("Make Bid");
                    bidButton.setPreferredSize(new Dimension(100, 25));
                    bidButton.setMaximumSize(new Dimension(100, 25));

                    bidButton.addActionListener(e -> {
                        try {
                            double bid = Double.parseDouble(bidText.getText());
                            if (bid <= currentBid) {
                                JOptionPane.showMessageDialog(frame, "Bid must be over current bid.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            client.makeBid(itemName.replace(" ", "/"), user, bid);
                            
                            // Stop all timers before disposing the frame
                            for (Timer timer : auctionTimers.values()) {
                                timer.stop();
                            }
                            auctionTimers.clear();
                            
                            frame.dispose();
                            new buyergui(user, password);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(frame, "Enter a valid number for the bid.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });

                    JButton sendMess = new JButton("Send Message");
                    sendMess.setPreferredSize(new Dimension(150, 25));
                    sendMess.setMaximumSize(new Dimension(150, 25));
                    
                    sendMess.addActionListener(e -> {
                        try {
                            new gui.messages.newmessage(user, seller, itemName);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, "Failed to send message: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });

                    // Add bid input and button to bid panel
                    bidPanel.add(bidText);
                    bidPanel.add(Box.createHorizontalStrut(10));
                    bidPanel.add(bidButton);
                    bidPanel.add(Box.createHorizontalStrut(10));
                    bidPanel.add(sendMess);

                    if (buyNowPrice > 0) {
                        JButton buyNowButton = new JButton("Buy Now");
                        buyNowButton.setPreferredSize(new Dimension(100, 25));
                        buyNowButton.setMaximumSize(new Dimension(100, 25));
                        buyNowButton.addActionListener(e -> {
                            try {
                                client.buyNow(itemName.replace(" ", "/"), user);
                                
                                for (Timer timer : auctionTimers.values()) {
                                    timer.stop();
                                }
                                auctionTimers.clear();
                                
                                frame.dispose();
                                new buyergui(user, password);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(frame, "Failed to buy now: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        });
                        bidPanel.add(buyNowButton);
                    }

                    leftPanel.add(bidPanel);

                    listingPanel.add(leftPanel, BorderLayout.WEST);

                    JPanel imagePanel = new JPanel(new BorderLayout());
                    imagePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // some padding

                    File imageDir = new File("src/gui/img/" + itemName.replaceAll("\\s+", "_") + ".png");
                    if (imageDir.exists()) {
                        try {
                            BufferedImage originalImage = ImageIO.read(imageDir);

                            // Rotate image
                            BufferedImage rotatedImage = new BufferedImage(
                                originalImage.getHeight(), 
                                originalImage.getWidth(), 
                                originalImage.getType()
                            );
                            Graphics2D g2d = rotatedImage.createGraphics();
                            AffineTransform transform = new AffineTransform();
                            transform.translate(originalImage.getHeight(), 0);
                            transform.rotate(Math.PI / 2);
                            g2d.setTransform(transform);
                            g2d.drawImage(originalImage, 0, 0, null);
                            g2d.dispose();

                            // Scale and set image
                            Image scaledImage = rotatedImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                            imagePanel.add(imageLabel, BorderLayout.WEST);

                        } catch (IOException ex) {
                            System.err.println("Error rotating image: " + ex.getMessage());
                        }
                    }
                    
                    // Create a countdown timer label
                    JLabel timerLabel = new JLabel();
                    timerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
                    timerLabel.setHorizontalAlignment(SwingConstants.LEFT);
                    timerLabel.setVerticalAlignment(SwingConstants.CENTER);
                    timerLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                    timerLabel.setText("Loading countdown...");
                    imagePanel.add(timerLabel, BorderLayout.CENTER);
                    
                    // Set up the timer with SwingWorker to avoid UI freezing
                    setupCountdownTimer(endTime, timerLabel, itemId);

                    listingPanel.add(imagePanel, BorderLayout.EAST);

                    listingsPanel.add(listingPanel);
                    listingsPanel.add(Box.createVerticalStrut(10));
                } catch (Exception e) {
                    System.err.println("Error processing listing: " + listing);
                    e.printStackTrace();
                }
            }
            
            // Add listings panel to main content
            mainContentPanel.add(listingsPanel);
        }
        
        // Check if there are no results
        if ((Sellers == null || Sellers.length == 0) && (Listings == null || Listings.length == 0)) {
            JLabel noResultsLabel = new JLabel("There are no search results!");
            noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            noResultsLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
            mainContentPanel.add(Box.createVerticalStrut(20));
            mainContentPanel.add(noResultsLabel);
        }

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
                
        // Add the content panel to the main panel
        panel.add(contentPanel, BorderLayout.CENTER);

        verticalContent.add(headerPanel);
        verticalContent.add(formPanel);
        verticalContent.add(formButtonPanel);
        verticalContent.add(scrollContainer);

        panel.add(verticalContent, BorderLayout.CENTER);
    }
    
    private static void setupCountdownTimer(String endTimeStr, JLabel timerLabel, String itemId) {
        try {
            // Parse the end time
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            Date endTime = format.parse(endTimeStr);
            
            // Calculate initial time difference
            Calendar cal = Calendar.getInstance();
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endTime);
            
            // Set the end calendar to today with the specified time
            endCal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
            endCal.set(Calendar.MONTH, cal.get(Calendar.MONTH));
            endCal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
            
            // If the end time is already past for today, set it to tomorrow
            if (endCal.before(cal)) {
                endCal.add(Calendar.DAY_OF_MONTH, 1);
            }
            
            // Create and start the timer
            Timer timer = new Timer(1000, e -> {
                // Get current time
                Calendar currentCal = Calendar.getInstance();
                
                // Calculate remaining time
                long diffMillis = endCal.getTimeInMillis() - currentCal.getTimeInMillis();
                
                if (diffMillis <= 0) {
                    // Auction has ended
                    timerLabel.setText("Auction Ended");
                    timerLabel.setForeground(Color.RED);
                    ((Timer)e.getSource()).stop();
                    auctionTimers.remove(itemId);
                } else {
                    // Calculate hours, minutes, seconds
                    long hours = diffMillis / (60 * 60 * 1000);
                    diffMillis %= (60 * 60 * 1000);
                    long minutes = diffMillis / (60 * 1000);
                    diffMillis %= (60 * 1000);
                    long seconds = diffMillis / 1000;
                    
                    // Format and set the countdown text
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
            
            // Store the timer for cleanup
            auctionTimers.put(itemId, timer);
            timer.start();
            
        } catch (ParseException e) {
            // Handle parsing errors
            timerLabel.setText("Error: " + endTimeStr);
            System.err.println("Error parsing end time: " + e.getMessage());
        }
    }
}