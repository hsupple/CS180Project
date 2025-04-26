package gui.messages;

import accounts.AuctionClient;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

public class messengergui {

    private static String user;
    private static String password;
    private static AuctionClient client = null; 
    private static ArrayList<String> listingsList = new ArrayList<>(); 
    private static JFrame frame = null;

    public messengergui(String user, String password, String user2) {

        this.user = user;
        this.password = password;
        
        try {
            this.client = new AuctionClient();
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("Messenger Client");
        frame.setSize(1250, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel, frame, client, user2);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel, JFrame frame, AuctionClient client, String user2) {
        panel.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.LIGHT_GRAY);
        headerPanel.setPreferredSize(new Dimension(1250, 100));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel title = new JLabel("Chat with " + user2, SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerPanel.add(title, BorderLayout.CENTER);

        JPanel headerInfoPanel = new JPanel();
        headerInfoPanel.setLayout(new BoxLayout(headerInfoPanel, BoxLayout.Y_AXIS));
        headerInfoPanel.setBackground(Color.LIGHT_GRAY);
        headerInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));

        JLabel welcomeLabel = new JLabel("Welcome " + user + "!");
        JLabel typeLabel = new JLabel("Messenger View");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        typeLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

        headerInfoPanel.add(welcomeLabel);
        headerInfoPanel.add(typeLabel);

        JPanel headerButtonPanel = new JPanel();
        headerButtonPanel.setBackground(Color.LIGHT_GRAY);
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            frame.dispose();
            new messagesgui(user, password);
        });
        headerButtonPanel.add(backButton);

        headerPanel.add(headerInfoPanel, BorderLayout.WEST);
        headerPanel.add(headerButtonPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Message list panel
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(Color.WHITE);

        messagePanel = getMessages(user, user2, messagePanel);

        JScrollPane scrollPane = new JScrollPane(messagePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextField messageField = new JTextField();
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                try {
                    client.sendMessage(user, user2, message);
                    messageField.setText("");
                    JLabel messageLabel = new JLabel(message);
                    messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    frame.dispose();
                    new messengergui(user, password, user2);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        panel.add(inputPanel, BorderLayout.SOUTH);
    }

    public static JPanel getMessages(String user, String user2, JPanel messagePanel) {
        String u1 = user;
        String u2 = user2;
        if (u1.compareTo(u2) > 0) {
            String temp = u1;
            u1 = u2;
            u2 = temp;
        }

        File messageFile = new File(System.getProperty("user.dir") + "/src/serverclient/msg/" + u1 + "_to_" + u2 + ".txt");
        try (BufferedReader br = new BufferedReader(new FileReader(messageFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                JLabel messageLabel = new JLabel(line);
                messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                messagePanel.add(messageLabel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messagePanel;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new messagesgui(user, password));
    }




}
