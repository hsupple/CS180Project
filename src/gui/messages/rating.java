package gui.messages;

import accounts.AuctionClient;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class rating {
     private static String user;
        private static String user2;
        private static String Auction;
        private static AuctionClient client = null;
    
        public rating(String user, String user2) {
            this.user = user;
            this.user2 = user2;

            try {
                this.client = new AuctionClient();
            } catch (Exception e) {
                e.printStackTrace();
            } 

    
            JFrame frame = new JFrame("Give Rating");
            frame.setSize(350, 200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
    
            // Main panel with null layout for manual positioning
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(null); // Use null layout to set bounds manually
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding around the panel
    
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(null); 
            rightPanel.setBounds(0, 0, 350, 200);
    
            JLabel titleLabel = new JLabel("Rate " + user2 + ":");
            titleLabel.setBounds(20, 20, 350, 25);
    
            JTextField messagetextField = new JTextField();
            messagetextField.setBounds(20, 60, 150, 25);
    
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setBounds(90, 100, 150, 30);
    
            JButton submitButton = new JButton("Rate!");
            submitButton.setBounds(180, 57, 150, 30); 
    
            rightPanel.add(titleLabel);
            rightPanel.add(messagetextField);

            rightPanel.add(submitButton);
            rightPanel.add(cancelButton);
    
            
    
            submitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String message = messagetextField.getText();
                    
                    // Check if the message is empty
                    if (message.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Please enter a Decimal Rating.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    } else {
                        // Validate if the message is a valid double
                        if (isDouble(message) && Double.parseDouble(message) >= 0 && Double.parseDouble(message) <= 5) {
                            try {
                                client.setRating(user2, Double.parseDouble(message));
                                JOptionPane.showMessageDialog(frame, "Message sent successfully!");
                                frame.dispose();
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(frame, "Failed to send message: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, "Please enter a valid decimal number between 0 and 5!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
    
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                }
            });

    
            mainPanel.add(rightPanel);
    
            frame.add(mainPanel);
            frame.setVisible(true);
        }

        private boolean isDouble(String input) {
            try {
                Double.parseDouble(input);
                return true;
            } catch (NumberFormatException ex) {
                return false; 
            }
        }
}
