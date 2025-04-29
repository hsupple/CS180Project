package gui.buyer;

import accounts.AuctionClient;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.text.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
/**
     * Class to run new gui for buyer object
     *
     * <p>Purdue University -- CS18000 -- Spring 2025</p>
     *
     * @author @Phaynes742
               @hsupple
               @addy-ops
    * @version April, 2025
    */
public class buyergui implements Runnable {
    // define all private fields
    private static String user;
    private static String password;
    private static AuctionClient client = null; 
    private static String[] Listings; 
    private static JFrame frame = null;
    private static Map<String, Timer> auctionTimers = new HashMap<>();
    private Thread WatchThread;

    // construct new gui fro buyer
    public buyergui(String user, String password) {
        this.user = user;
        this.password = password;

        try {
            this.client = new AuctionClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // get all valid listings
        this.Listings = client.getMyListings("ALL").toString().substring(1, client.getMyListings("ALL").toString().length() - 2).split("9000");
        for (int i = 0; i < Listings.length; i++) {
            System.out.println(Listings[i]);
        }

        // create new buyer frame
        frame = new JFrame("Buyer Interface");
        frame.setSize(1250, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel, frame, client);

        frame.setVisible(true);
        // find timer and stop when cleared
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                for (Timer timer : auctionTimers.values()) {
                    timer.stop();
                }
                auctionTimers.clear();
            }
        });
        // create new watcher to find new listings
        WatchThread = new Thread(this);
        WatchThread.start();
    }

    // run thread to find all new lisitngs
    public void run() {
        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path path = Paths.get("src/serverclient/txt");
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
                        for (Timer timer : auctionTimers.values()) {
                            timer.stop();
                        }
                        auctionTimers.clear();
                        
                        frame.dispose();
                        new buyergui(user, password);
                    });
                    return;
                }
                
                key.reset();
            }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    // place all components within layout
    private static void placeComponents(JPanel panel, JFrame frame, AuctionClient client) {
        panel.setLayout(new BorderLayout());
        JPanel verticalContent = new JPanel();
        verticalContent.setLayout(new BoxLayout(verticalContent, BoxLayout.Y_AXIS));
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.LIGHT_GRAY);
        headerPanel.setPreferredSize(new Dimension(1250, 100));
        headerPanel.setMaximumSize(new Dimension(1250, 100));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // All header items
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

        // Buttons used for logout and delete
        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(100, 30));
        logoutButton.setMaximumSize(new Dimension(100, 30));

        JButton deleteButton = new JButton("Delete Account");
        deleteButton.setPreferredSize(new Dimension(150, 30));
        deleteButton.setMaximumSize(new Dimension(150, 30));

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.LIGHT_GRAY);

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(Color.LIGHT_GRAY);
        logoutPanel.add(logoutButton);
        
        JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        deletePanel.setBackground(Color.LIGHT_GRAY);
        deletePanel.add(deleteButton);
        
        buttonPanel.add(logoutPanel);
        buttonPanel.add(deletePanel);

        headerPanel.add(headerInfoPanel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        logoutButton.addActionListener(e -> {
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

        // New Search layout with text and submit button
        JLabel userLabel = new JLabel("Search:");
        userLabel.setBounds(50, 50, 80, 25);
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        formPanel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(250, 25, 750, 75);
        userText.setFont(new Font("SansSerif", Font.PLAIN, 24));
        formPanel.add(userText);

        JButton searchButton = new JButton("Search Listings");
        searchButton.setBounds(250, 125, 150, 25);
        formPanel.add(searchButton);

        searchButton.addActionListener(e -> {
            String searchQuery = userText.getText();
            if (!searchQuery.isEmpty()) {
                for (Timer timer : auctionTimers.values()) {
                    timer.stop();
                }
                auctionTimers.clear();
                new searchgui(user, password, searchQuery);
                frame.dispose();
            }
        });

        JPanel formButtonPanel = new JPanel();
        formButtonPanel.setPreferredSize(new Dimension(350, 50));

        JPanel listingsPanel = new JPanel();
        listingsPanel.setLayout(new BoxLayout(listingsPanel, BoxLayout.Y_AXIS));
        
        // Find all listings that are active and create new panel with item lisitng
        for (int i = 1; i < Listings.length; i++) {
            if (Listings[i].split(",")[5].strip().equals("false")) {
                
                // find all data based on stored info
                String itemName = Listings[i].split(",")[1].strip().replace("/", " ");
                String description = Listings[i].split(",")[3].strip().replace("/", " ");
                double buyNowPrice = Double.parseDouble(Listings[i].split(",")[2].strip());
                double currentBid = Double.parseDouble(Listings[i].split(",")[7].strip());
                String endTime = Listings[i].split(",")[8].strip();
        
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

                JPanel bidPanel = new JPanel();
                bidPanel.setLayout(new BoxLayout(bidPanel, BoxLayout.X_AXIS));
                bidPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

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
                        new buyergui(user, password);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Enter a valid number for the bid.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });

                // new button to send message to seller
                JButton sendMess = new JButton("Send Message");
                sendMess.setPreferredSize(new Dimension(150, 25));
                sendMess.setMaximumSize(new Dimension(150, 25));
                
                // ensure valid message
                String seller = Listings[i].split(",")[4].strip();
                sendMess.addActionListener(e -> {
                    try {
                        new gui.messages.newmessage(user, seller, itemName);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Failed to send message: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });

                // add all to new panel
                bidPanel.add(bidText);
                bidPanel.add(Box.createHorizontalStrut(10));
                bidPanel.add(bidButton);
                bidPanel.add(Box.createHorizontalStrut(10));
                bidPanel.add(sendMess);

                // Ensure buy now is properly setup and displayed
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
                imagePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                // Load file if in img folder
                File imageDir = new File("src/gui/img/" + itemName.replaceAll("\\s+", "_") + ".png");
                if (imageDir.exists()) {
                    try {
                        BufferedImage Image = ImageIO.read(imageDir);

                        Image scaledImage = Image.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                        imagePanel.add(imageLabel, BorderLayout.WEST);

                    } catch (IOException ex) {
                        System.err.println("Error" + ex.getMessage());
                    }
                }
                
                // setup timerlabel
                JLabel timerLabel = new JLabel();
                timerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
                timerLabel.setHorizontalAlignment(SwingConstants.LEFT);
                timerLabel.setVerticalAlignment(SwingConstants.CENTER);
                timerLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                imagePanel.add(timerLabel, BorderLayout.CENTER);
                
                setupCountdownTimer(endTime, timerLabel, itemName);

                listingPanel.add(imagePanel, BorderLayout.EAST);

                listingsPanel.add(listingPanel);
                listingsPanel.add(Box.createVerticalStrut(10));
            }
        }

        // create messages button at top to view all
        JButton Messages = new JButton("Messages");
        Messages.setBounds(250, 175, 350, 25);
        Messages.setMinimumSize(new Dimension(350, 25));
        formButtonPanel.add(Messages);
        Messages.addActionListener(e -> {
            for (Timer timer : auctionTimers.values()) {
                timer.stop();
            }
            auctionTimers.clear();
            new gui.messages.messagesgui(user, password);
            frame.dispose();
        });

        // Ensure pane can scroll when overflowed with listings
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
                
        panel.add(contentPanel, BorderLayout.CENTER);

        verticalContent.add(headerPanel);
        verticalContent.add(formPanel);
        verticalContent.add(formButtonPanel);
        verticalContent.add(scrollContainer);

        panel.add(verticalContent, BorderLayout.CENTER);
    }
    
    // Setup new countdown timer with current time
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
}