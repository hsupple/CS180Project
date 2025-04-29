package gui;

import accounts.AuctionClient;
import javax.swing.*;

public class LoginGUI {
    private AuctionClient client = null; 

    public LoginGUI() {
        try {
            this.client = new AuctionClient();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Login Interface");
        frame.setSize(350, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel, frame, client);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel, JFrame frame, AuctionClient client) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("User:");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(100, 20, 165, 25);
        panel.add(userText);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 50, 165, 25);
        panel.add(passwordText);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(100, 80, 80, 25);
        panel.add(loginButton);

        JButton makeAccount = new JButton("Make Account");
        makeAccount.setBounds(190, 80, 130, 25);
        panel.add(makeAccount);

        loginButton.addActionListener(e -> {
            String user = userText.getText();
            String password = new String(passwordText.getPassword());
            Boolean accountType = client.isBuyer(user); 

            if (!client.getPassword(user).equals(password)) {
                if (client.isActive(user).equals("User / Listing not found")) {
                    JOptionPane.showMessageDialog(null, "Username does not exist, please make an account");
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Password.");
                }
            } else {
            if (accountType == true) {
                    new gui.buyer.buyergui(user, password); 
                } else {
                    new gui.seller.sellergui(user, password); 
                }
                frame.dispose();
            }
        });

        makeAccount.addActionListener(e -> {
            frame.dispose();
            new newacct();   
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginGUI());
    }
}
