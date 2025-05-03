package gui.messages;

import accounts.AuctionClient;
import gui.buyer.SearchGui;
import gui.buyer.SellerAcct;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
/**
     * Class to run new rating gui for selected seller
     *
     * <p>Purdue University -- CS18000 -- Spring 2025</p>
     *
     * @author @Phaynes742
               @hsupple
    * @version May, 2025
    */
public class Rating {
    // define all private vals
    private static String user;
    private static String user2;
    private static String password;
    private static String past;
    
    private static String query;
    private static AuctionClient client = null;

    // new rating gui constructor
    public Rating(String user, String password, String query, String user2, String past) {
        this.user = user;
        this.user2 = user2;
        this.password = password;
        this.query = query;
        this.past = past;

        try {
            this.client = new AuctionClient();
        } catch (Exception e) {
            e.printStackTrace();
        } 

        // Show frame for give rating
        JFrame frame = new JFrame("Give Rating");
        frame.setSize(350, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null); 
        rightPanel.setBounds(0, 0, 350, 200);

        // Rate User text plus text input
        JLabel titleLabel = new JLabel("Rate " + user2 + ":");
        titleLabel.setBounds(20, 20, 350, 25);

        JTextField messagetextField = new JTextField();
        messagetextField.setBounds(20, 60, 150, 25);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(90, 100, 150, 30);

        JButton submitButton = new JButton("Rate!");
        submitButton.setBounds(180, 57, 150, 30); 

        // Add all to panel
        rightPanel.add(titleLabel);
        rightPanel.add(messagetextField);

        rightPanel.add(submitButton);
        rightPanel.add(cancelButton);

        // New auctionlistener to ensure rating is a number and will suibmit properly
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messagetextField.getText();
                
                if (message.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a Decimal Rating.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                } else {
                    if (isDouble(message) && Double.parseDouble(message) >= 0 && Double.parseDouble(message) <= 5) {
                        try {
                            // Send client signal
                            client.setRating(user2, Double.parseDouble(message));
                            JOptionPane.showMessageDialog(frame, "Message sent successfully!");
                            if (past.equals("search")) {
                                new SearchGui(user, password, query);
                                frame.dispose();
                            } else {
                                new SellerAcct(user, password, user2);
                                frame.dispose();
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, "Failed to send message: " + ex.getMessage(), 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Please enter a valid decimal number between 0 and 5!", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Cancel and return to buyer class
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (past.equals("search")) {
                    new SearchGui(user, password, query);
                    frame.dispose();
                } else {
                    new SellerAcct(user, password, user2);
                    frame.dispose();
                }
            }
        });

        // Add all panels
        mainPanel.add(rightPanel);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    // Determine if isDouble without using try catch within main body code
    private boolean isDouble(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException ex) {
            return false; 
        }
    }
}
