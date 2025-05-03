package gui.seller;

import accounts.ItemListing;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
/**
     * Class to run new auction gui for Seller input
     *
     * <p>Purdue University -- CS18000 -- Spring 2025</p>
     *
     * @author @Phaynes742
               @hsupple
    * @version May, 2025
    */
public class NewAuction {

    private static String user;
    private static String password;

    public NewAuction(String user, String password) {
        this.user = user;
        this.password = password;

        // Define new auction frame
        JFrame frame = new JFrame("New Auction Item");
        frame.setSize(750, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left panel for image selection
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(null);
        leftPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        leftPanel.setBounds(0, 0, 375, 500);

        JLabel imageLabel = new JLabel("Drag or Select an Image", SwingConstants.CENTER);
        imageLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        imageLabel.setBounds(25, 100, 325, 100);
        leftPanel.add(imageLabel);

        JButton selectImageButton = new JButton("Select Image");
        selectImageButton.setBounds(125, 230, 125, 30);
        leftPanel.add(selectImageButton);

        // Right panel for user input
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null); 
        rightPanel.setBounds(375, 0, 375, 500);

        // Take all text input
        JLabel titleLabel = new JLabel("Title*:");
        JTextField titleTextField = new JTextField();
        titleLabel.setBounds(20, 20, 100, 25);
        titleTextField.setBounds(120, 20, 200, 25);

        JLabel descriptionLabel = new JLabel("Description*:");
        JTextArea descriptionTextArea = new JTextArea(3, 17);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionTextArea);
        descriptionLabel.setBounds(20, 60, 100, 25);
        descriptionScrollPane.setBounds(120, 60, 200, 80);

        JLabel buyNowLabel = new JLabel("Buy Now Price ($):");
        JTextField buyNowTextField = new JTextField();
        buyNowLabel.setBounds(20, 150, 150, 25);
        buyNowTextField.setBounds(170, 150, 150, 25);

        JLabel minBidLabel = new JLabel("Minimum Bid Price ($)*:");
        JTextField minBidTextField = new JTextField();
        minBidLabel.setBounds(20, 190, 150, 25);
        minBidTextField.setBounds(170, 190, 150, 25);

        JLabel timerLabel = new JLabel("Auction Time Limit (min)*:");
        JTextField timerTextField = new JTextField();
        timerLabel.setBounds(20, 230, 150, 25);
        timerTextField.setBounds(170, 230, 150, 25);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(120, 370, 200, 30);

        JButton submitButton = new JButton("Submit Auction Item");
        submitButton.setBounds(120, 270, 200, 30); 

        // Add all to right panel
        rightPanel.add(titleLabel);
        rightPanel.add(titleTextField);
        rightPanel.add(descriptionLabel);
        rightPanel.add(descriptionScrollPane);
        rightPanel.add(buyNowLabel);
        rightPanel.add(buyNowTextField);
        rightPanel.add(minBidLabel);
        rightPanel.add(minBidTextField);
        rightPanel.add(submitButton);
        rightPanel.add(cancelButton);
        rightPanel.add(timerLabel);
        rightPanel.add(timerTextField);

        // New filechooser for image selection
        selectImageButton.addActionListener(new ActionListener() {
            File imageDir = new File("src/gui/img");

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select Image");
                fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "png", "jpg", "jpeg"));

                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Image selected: " + selectedFile.getPath());
                    System.out.println("Absolute path: " + imageDir.getAbsolutePath());

                    File imageDir = new File("src/gui/img");
                    if (!imageDir.exists()) 
                        imageDir.mkdirs();

                    String uniqueFileName = titleTextField.getText().replaceAll("\\s+", "_") + ".png";
                    File destFile = new File(imageDir, uniqueFileName);
                    try {
                        Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    System.out.println("Image copied to: " + destFile.getPath());
                
                }
            }
        });

        // Submit
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get all variables
                String title = titleTextField.getText();
                String description = descriptionTextArea.getText().strip();
                String buyNowPrice = buyNowTextField.getText();
                if (buyNowPrice.isEmpty()) { 
                    buyNowPrice = "-1.0"; 
                }
                String minBidPrice = minBidTextField.getText();
                String timer = timerTextField.getText();

                double minBid = -1;
                // Ensure all text fields have values
                if (!title.isEmpty() && !description.isEmpty() && !minBidPrice.isEmpty() && !timer.isEmpty()) {
                    try {
                        minBid = Double.parseDouble(minBidPrice); 
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, 
                            "Please enter a valid number for Minimum Bid Price ( >= 0).", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        return; 
                    }
                    
                    if (Double.parseDouble(timer) < 1) {
                        timer = "1";
                    }

                    if (minBid >= 0) {
                        JOptionPane.showConfirmDialog(frame, "Auction item submitted successfully!", "Success", JOptionPane.DEFAULT_OPTION);
                        ItemListing item = new ItemListing(title, description, Double.parseDouble(minBidPrice), user, (Double.parseDouble(timer) * 60000));
                        if (!minBidPrice.isEmpty()) {
                            item.setBuyNowItemPrice(Double.parseDouble(buyNowPrice)); 
                        }
                        frame.dispose(); 
                        new SellerGui(user, password);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please fill in all required fields marked with *.", 
                        "Missing Information", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Cancel go to sellergui
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new SellerGui(user, password);
            }
        });

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        frame.add(mainPanel);
        frame.setVisible(true);
    }
}
