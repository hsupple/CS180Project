package gui.buyer;

import accounts.AuctionClient;
import java.awt.*;
import javax.swing.*;

public class buyergui {

    private static String user;
    private String password;
    private AuctionClient client = null;

    public buyergui(String user, String password) {
        this.user = user;
        this.password = password;

        try {
            this.client = new AuctionClient();
        } catch (Exception e) {
            e.printStackTrace();
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
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Stack components vertically

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

        JLabel userLabel = new JLabel("User:");
        userLabel.setBounds(10, 10, 80, 25);
        formPanel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(100, 10, 165, 25);
        formPanel.add(userText);

        // Button panel
        JPanel formButtonPanel = new JPanel();
        formButtonPanel.setBackground(Color.WHITE);
        formButtonPanel.setPreferredSize(new Dimension(350, 50));

        // Add panels to the mainPanel
        panel.add(headerPanel);
        panel.add(formPanel);
        panel.add(formButtonPanel);
    }
}