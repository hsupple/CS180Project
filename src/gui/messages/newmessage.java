package gui.messages;

import accounts.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
    
/**
     * Class to run gui for new message to buyer
     *
     * <p>Purdue University -- CS18000 -- Spring 2025</p>
     *
     * @author @Phaynes742
               @hsupple
    * @version May, 2025
    */
public class NewMessage {
    // Define all private fields
    private static String user;
    private static String user2;
    private static AuctionClient client = null;

    // Constructor for new message gui
    public NewMessage(String user, String user2) {
        this.user = user;
        this.user2 = user2;

        try {
            this.client = new AuctionClient();
        } catch (Exception e) {
            e.printStackTrace();
        } 

        // Declare new message frame with two panels
        JFrame frame = new JFrame("New Message");
        frame.setSize(350, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null); 
        rightPanel.setBounds(0, 0, 350, 200);

        // Show new message to designated target with text field
        JLabel titleLabel = new JLabel("New Message to " + user2 + ":");
        titleLabel.setBounds(20, 20, 350, 25);

        JTextField messagetextField = new JTextField();
        messagetextField.setBounds(20, 60, 150, 25);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(90, 100, 150, 30);

        JButton submitButton = new JButton("Submit Message");
        submitButton.setBounds(180, 57, 150, 30); 

        // Add all buttons
        rightPanel.add(titleLabel);
        rightPanel.add(messagetextField);

        rightPanel.add(submitButton);
        rightPanel.add(cancelButton);

        // Ensure message is sent and not null
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messagetextField.getText();
                if (message.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a message.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                } else {
                    try {
                        client.sendMessage(user, user2, message);
                        JOptionPane.showMessageDialog(frame, "Message sent successfully!");
                        frame.dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Failed to send message: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // dispose frame and return to buyer
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        // Add to panels
        mainPanel.add(rightPanel);

        frame.add(mainPanel);
        frame.setVisible(true);
    }
}
