package gui.messages;

import accounts.AuctionClient;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import javax.swing.*;

public class messengergui implements Runnable {

    private static String user;
    private static String password;
    private static AuctionClient client = null; 
    private static ArrayList<String> listingsList = new ArrayList<>(); 
    private static JFrame frame = null;
    private static String user2;
    private JPanel messagePanel;
    private JScrollPane scrollPane;

    public messengergui(String user, String password, String user2) {
        messengergui.user = user;
        messengergui.user2 = user2;
        messengergui.password = password;
        
        try {
            this.client = new AuctionClient();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeGUI();
        
        // Start the file watching thread
        Thread watcherThread = new Thread(this);
        watcherThread.setDaemon(true);
        watcherThread.start();
    }

    private void initializeGUI() {
        frame = new JFrame("Messenger Client");
        frame.setSize(1250, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel, frame, client, user2);

        frame.setVisible(true);
    }

    public void run() {
        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path path = Paths.get("src/serverclient/msg");
            path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
    
            // Save the last known number of messages
            int lastMessageCount = getMessageCount();
    
            while (true) {
                WatchKey key = watcher.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
    
                    if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        System.out.println("File changed. Checking for new messages...");
    
                        int currentMessageCount = getMessageCount();
                        if (currentMessageCount > lastMessageCount) {
                            lastMessageCount = currentMessageCount;
                            
                            // Update just the message panel instead of recreating the entire GUI
                            SwingUtilities.invokeLater(() -> {
                                updateMessages();
                            });
                        }
                    }
                }
                // Reset is important for continued watching
                key.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMessages() {
        if (messagePanel != null) {
            messagePanel.removeAll();
            messagePanel = getMessages(user, user2, messagePanel);
            messagePanel.revalidate();
            messagePanel.repaint();
            
            // Auto-scroll to bottom to show new messages
            if (scrollPane != null) {
                SwingUtilities.invokeLater(() -> {
                    JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
                    verticalBar.setValue(verticalBar.getMaximum());
                });
            }
        }
    }

    private void placeComponents(JPanel panel, JFrame frame, AuctionClient client, String user2) {
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
        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(Color.WHITE);

        messagePanel = getMessages(user, user2, messagePanel);

        scrollPane = new JScrollPane(messagePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Auto-scroll to the bottom to show the most recent messages
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
            verticalBar.setValue(verticalBar.getMaximum());
        });

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
                String sender = line.substring(0, line.indexOf(":"));
                
                JPanel messageBubble = new JPanel();
                messageBubble.setLayout(new BorderLayout());
                messageBubble.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                
                JLabel messageLabel = new JLabel(line);
                messageLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
                
                // Style message bubbles differently based on sender
                if (sender.equals(user)) {
                    messageBubble.setBackground(new Color(220, 248, 198)); // Light green for own messages
                    messageBubble.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    messageLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    messageBubble.setBackground(new Color(240, 240, 240)); // Light gray for received messages
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
        SwingUtilities.invokeLater(() -> new messagesgui(user, password));
    }

    private int getMessageCount() {
        int count = 0;
        try {
            String u1 = user;
            String u2 = user2; // Fixed: use the actual user2 field
            if (u1.compareTo(u2) > 0) {
                String temp = u1;
                u1 = u2;
                u2 = temp;
            }
    
            File messageFile = new File(System.getProperty("user.dir") + "/src/serverclient/msg/" + u1 + "_to_" + u2 + ".txt");
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