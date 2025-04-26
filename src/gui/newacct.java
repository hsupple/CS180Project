package gui;

import accounts.*;
import javax.swing.*;

public class newacct {
    private AuctionClient client = null;
    private JFrame frame = null;

    public newacct() {
        try {
            this.client = new AuctionClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.frame = new JFrame("Create New Account");
        frame.setSize(350, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("New User:");
        userLabel.setBounds(10, 35, 80, 25);
        panel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(100, 35, 165, 25);
        panel.add(userText);

        JLabel passwordLabel = new JLabel("New Password:");
        passwordLabel.setBounds(10, 65, 100, 25);
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 65, 165, 25);
        panel.add(passwordText);

        String[] accountTypes = { "Buyer", "Seller"};
        JComboBox<String> accountSelector = new JComboBox<>(accountTypes);
        accountSelector.setBounds(100, 5, 80, 25);
        panel.add(accountSelector);

        JButton createButton = new JButton("Create");
        createButton.setBounds(100, 95, 80, 25);
        panel.add(createButton);

        createButton.addActionListener(e -> {
            String user = userText.getText();
            String password = new String(passwordText.getPassword());
            String accountType = (String) accountSelector.getSelectedItem();
            System.out.println("-" + this.client.isActive(user) + "-");

            if (this.client.isActive(user).equals("User / Listing not found") && user.contains(" ") == false) {
                if (accountType.equals("Buyer")) {
                    Buyer buyer = new Buyer(user, password);
                } else if (accountType.equals("Seller")) {
                    Seller seller = new Seller(user, password);
                }
                frame.dispose();
                new LoginGUI();
            } else if (user.contains(" ")) { 
                JOptionPane.showMessageDialog(panel, "Username cannot contain spaces!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(panel, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(185, 95, 80, 25);
        panel.add(cancelButton);
        cancelButton.addActionListener(e -> {
            frame.dispose();
            new LoginGUI();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new newacct());
    }
}