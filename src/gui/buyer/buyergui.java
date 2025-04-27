package gui.buyer;

import accounts.AuctionClient;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;

public class buyergui implements Runnable {

    private static String user;
    private static String password;
    private static AuctionClient client = null; 
    private static String[] Listings; 
    private static JFrame frame = null;
    private static Map<String, Timer> auctionTimers = new HashMap<>();
    private Thread WatchThread;

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

        frame = new JFrame("Buyer Interface");
        frame.setSize(1250, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel, frame, client);

        frame.setVisible(true);
        
        // Add window listener to clean up timers when frame is closed
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                // Stop all timers
                for (Timer timer : auctionTimers.values()) {
                    timer.stop();
                }
                auctionTimers.clear();
            }
        });

        WatchThread = new Thread(this);
        WatchThread.start();
    }

    public void run() {
        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path path = Paths.get("src/serverclient/txt");
            path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            while (true) {
                WatchKey key = watcher.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();

                        if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                            // Optional: Check if it's specifically the file you care about
                            System.out.println("File changed. Reloading GUI...");
                            
                            SwingUtilities.invokeLater(() -> {
                                // Stop timers
                                for (Timer timer : auctionTimers.values()) {
                                    timer.stop();
                                }
                                auctionTimers.clear();

                                frame.dispose();
                                new buyergui(user, password);
                            });
                            return; // Exit the thread once reloaded
                        }
                    }
                    key.reset();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            // Stop all timers before disposing the frame
            for (Timer timer : auctionTimers.values()) {
                timer.stop();
            }
            auctionTimers.clear();
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
                // Stop all timers before disposing the frame
                for (Timer timer : auctionTimers.values()) {
                    timer.stop();
                }
                auctionTimers.clear();
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
                // Stop all timers before disposing the frame
                for (Timer timer : auctionTimers.values()) {
                    timer.stop();
                }
                auctionTimers.clear();
                new searchgui(user, password, searchQuery);
                frame.dispose();
            }
        });


        // Button panel
        JPanel formButtonPanel = new JPanel();
        formButtonPanel.setPreferredSize(new Dimension(350, 50));

        // Create scrollable panel for listings
        JPanel listingsPanel = new JPanel();
        listingsPanel.setLayout(new BoxLayout(listingsPanel, BoxLayout.Y_AXIS));
        
        for (int i = 1; i < Listings.length; i++) {
            if (Listings[i].split(",")[5].strip().equals("false")) {
        
                String itemName = Listings[i].split(",")[1].strip().replace("/", " ");
                String description = Listings[i].split(",")[3].strip().replace("/", " ");
                double buyNowPrice = Double.parseDouble(Listings[i].split(",")[2].strip());
                double currentBid = Double.parseDouble(Listings[i].split(",")[7].strip());
                String endTime = Listings[i].split(",")[8].strip();
        
                JPanel listingPanel = new JPanel();
                listingPanel.setLayout(new BorderLayout()); // Change to BorderLayout
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
                
                String seller = Listings[i].split(",")[4].strip();
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
                setupCountdownTimer(endTime, timerLabel, itemName);

                listingPanel.add(imagePanel, BorderLayout.EAST);

                listingsPanel.add(listingPanel);
                listingsPanel.add(Box.createVerticalStrut(10));
            }
        }

        JButton Messages = new JButton("Messages");
        Messages.setBounds(250, 175, 350, 25);
        Messages.setMinimumSize(new Dimension(350, 25));
        formButtonPanel.add(Messages);
        Messages.addActionListener(e -> {
            // Stop all timers before disposing the frame
            for (Timer timer : auctionTimers.values()) {
                timer.stop();
            }
            auctionTimers.clear();
            new gui.messages.messagesgui(user, password);
            frame.dispose();
        });

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
    
    /**
     * Sets up a countdown timer for an auction
     * @param endTimeStr The end time string in HH:MM:SS format
     * @param timerLabel The JLabel to update with the countdown
     * @param itemId An identifier for the auction
     */
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