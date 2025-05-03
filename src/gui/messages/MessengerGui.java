package gui.messages;

import accounts.AuctionClient;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import javax.swing.*;

/**
     * Class to run new gui for messenger suite
     *
     * <p>Purdue University -- CS18000 -- Spring 2025</p>
     *
     * @author @Phaynes742
               @hsupple
    * @version May, 2025
    */
public class MessengerGui implements Runnable {
    // declare all private fields
    private static String user;
    private static String password;
    private static AuctionClient client = null; 
    private static JFrame frame = null;
    private static String user2;
    private JPanel messagePanel;
    private JScrollPane scrollPane;

    public MessengerGui(String user, String password, String user2) {
        MessengerGui.user = user;
        MessengerGui.user2 = user2;
        MessengerGui.password = password;
        
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
        placeComponents(panel, client, user2);

        frame.setVisible(true);
        
        // use watcherthread to watch for new modifications to msg
        Thread watcherThread = new Thread(this);
        watcherThread.setDaemon(true);
        watcherThread.start();
    }

    // run thread to ensure messages sent will update gui
    public void run() {
        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(System.getProperty("user.dir") + "/../src/serverclient/msg");
            path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
    
            int lastMessageCount = getMessageCount();
    
            while (true) {
                WatchKey key = watcher.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
    
                    if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {

                        frame.dispose();
                        new MessengerGui(user, password, user2);
                    }
                }
                key.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // place all panels and components properly
    private void placeComponents(JPanel panel, AuctionClient auClient, String secUser) {
        panel.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.LIGHT_GRAY);
        headerPanel.setPreferredSize(new Dimension(1250, 100));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // make new setup for chat with user
        JLabel title = new JLabel("Chat with " + secUser, SwingConstants.CENTER);
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

        // Button to go back to messages 
        JPanel headerButtonPanel = new JPanel();
        headerButtonPanel.setBackground(Color.LIGHT_GRAY);
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            frame.dispose();
            new MessagesGui(user, password);
        });
        headerButtonPanel.add(backButton);

        headerPanel.add(headerInfoPanel, BorderLayout.WEST);
        headerPanel.add(headerButtonPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // new message panel for user communication
        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(Color.WHITE);

        messagePanel = getMessages(user, secUser, messagePanel);

        scrollPane = new JScrollPane(messagePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
            verticalBar.setValue(verticalBar.getMaximum());
        });

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextField messageField = new JTextField();
        // Button to send new text contingent on isEmpty
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                try {
                    auClient.sendMessage(user, secUser, message);
                    messageField.setText("");

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        panel.add(inputPanel, BorderLayout.SOUTH);
    }

    // Method used to return all messages between two users and append to messagepanel
    public static JPanel getMessages(String firUser, String secUser, JPanel messagePanel) {
        String u1 = firUser;
        String u2 = secUser;
        if (u1.compareTo(u2) > 0) {
            String temp = u1;
            u1 = u2;
            u2 = temp;
        }
        // Read file contents
        File messageFile = new File(System.getProperty("user.dir") + "/../src/serverclient/msg/" 
            + u1 + "_to_" + u2 + ".txt");
        try (BufferedReader br = new BufferedReader(new FileReader(messageFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String sender = line.substring(0, line.indexOf(":"));
                
                // Create new panel as text bubble
                JPanel messageBubble = new JPanel();
                messageBubble.setLayout(new BorderLayout());
                messageBubble.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                
                JLabel messageLabel = new JLabel(line);
                messageLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
                
                // Put message on side contingent on user/sender status
                if (sender.equals(user)) {
                    messageBubble.setBackground(new Color(220, 248, 198));
                    messageBubble.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    messageLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    messageBubble.setBackground(new Color(240, 240, 240));
                    messageBubble.setAlignmentX(Component.LEFT_ALIGNMENT);
                }
                
                messageBubble.add(messageLabel);
                
                JPanel wrapperPanel = new JPanel(new BorderLayout());
                wrapperPanel.setOpaque(false);
                if (sender.equals(user)) {
                    wrapperPanel.add(messageBubble, BorderLayout.EAST);
                } else {
                    wrapperPanel.add(messageBubble, BorderLayout.WEST);
                }
                
                messagePanel.add(wrapperPanel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messagePanel;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MessengerGui("user1", "password", "user2"));
    }
    
    // Count messages in file
    private int getMessageCount() {
        int count = 0;
        try {
            String u1 = user;
            String u2 = user2;
            if (u1.compareTo(u2) > 0) {
                String temp = u1;
                u1 = u2;
                u2 = temp;
            }
    
            File messageFile = new File(System.getProperty("user.dir") + "/src/serverclient/msg/" 
                + u1 + "_to_" + u2 + ".txt");
            if (messageFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(messageFile))) {
                    while (reader.readLine() != null) {
                        count++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }
}