package gui.buyer;

import accounts.AuctionClient;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

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
                String time = Listings[i].split(",")[8].strip();
        
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

                // Add bid panel to the left side of the listing panel
                leftPanel.add(bidPanel);

                // Add left panel to the WEST of the listing panel
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

                        // Timer label (bigger and to the right of image)
                        

                    } catch (IOException ex) {
                        System.err.println("Error rotating image: " + ex.getMessage());
                    }
                }
                JLabel timer = new JLabel("Auction Ends at " + time);
                        timer.setFont(new Font("SansSerif", Font.BOLD, 18));
                        timer.setHorizontalAlignment(SwingConstants.LEFT);
                        timer.setVerticalAlignment(SwingConstants.CENTER);
                        timer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                        imagePanel.add(timer, BorderLayout.CENTER);

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

}