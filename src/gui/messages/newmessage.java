package gui.messages;

import accounts.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
    

public class newmessage {
    
        private static String user;
        private static String user2;
        private static String Auction;
        private static AuctionClient client = null;
    
        public newmessage(String user, String user2, String Auction) {
            this.user = user;
            this.user2 = user2;

            try {
                this.client = new AuctionClient();
            } catch (Exception e) {
                e.printStackTrace();
            } 

    
            JFrame frame = new JFrame("New Message");
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
    
            JLabel titleLabel = new JLabel("New Message to " + user2 + ":");
            titleLabel.setBounds(20, 20, 350, 25);
    
            JTextField messagetextField = new JTextField();
            messagetextField.setBounds(20, 60, 150, 25);
    
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setBounds(90, 100, 150, 30);
    
            JButton submitButton = new JButton("Submit Message");
            submitButton.setBounds(180, 57, 150, 30); 
    
            rightPanel.add(titleLabel);
            rightPanel.add(messagetextField);

            rightPanel.add(submitButton);
            rightPanel.add(cancelButton);
    
            
    
            submitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String message = messagetextField.getText();
                    if (message.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Please enter a message.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    else {
                        try {
                            client.sendMessage(user, user2, message);
                            JOptionPane.showMessageDialog(frame, "Message sent successfully!");
                            frame.dispose();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, "Failed to send message: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
    }


