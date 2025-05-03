package accounts;
import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;

/**
 * Client component for a text search engine application.
 * This client connects to the SearchServer, allows the user to search for text,
 * and displays the results using JOptionPane.
 * 
 * Uses only java.io, java.net, java.util packages and JOptionPane for GUI.
 * 
 * Server Port: 12345
 */
public class SearchClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    /**
     * Main method to start the client
     */
    public static void main(String[] args) {
        SearchClient client = new SearchClient();
        client.run();
    }

    /**
     * Runs the client application
     */
    public void run() {
        // Welcome the user
        JOptionPane.showMessageDialog(null, 
                "Welcome to the Search Engine Application!", 
                "Welcome", 
                JOptionPane.INFORMATION_MESSAGE);

        // Get connection parameters from user
        String host = JOptionPane.showInputDialog(null, 
                "Enter host name:", 
                "localhost");
        
        if (host == null) {
            showFarewell();
            return;
        }
        
        String portStr = JOptionPane.showInputDialog(null, 
                "Enter port number:", 
                "3001");
        
        if (portStr == null) {
            showFarewell();
            return;
        }

        try {
            int port = Integer.parseInt(portStr);
            
            // Connect to server
            if (!connectToServer(host, port)) {
                JOptionPane.showMessageDialog(null, 
                        "Failed to connect to the server.", 
                        "Connection Error", 
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Show connection established message
            JOptionPane.showMessageDialog(null, 
                    "Connection established with the server.", 
                    "Connected", 
                    JOptionPane.INFORMATION_MESSAGE);
            
            // Main search loop
            boolean continueSearching = true;
            while (continueSearching) {
                // Ask for search text
                String searchText = JOptionPane.showInputDialog(null, 
                        "Enter your search text:", 
                        "Search", 
                        JOptionPane.QUESTION_MESSAGE);
                
                if (searchText == null) {
                    continueSearching = false;
                    continue;
                }
                
                // Send search request to server
                out.println("SEARCH:" + searchText);
                
                try {
                    // Process search results
                    String response = in.readLine();
                    
                    if (response == null || response.equals("NO_RESULTS")) {
                        JOptionPane.showMessageDialog(null, 
                                "No results found for: " + searchText, 
                                "No Results", 
                                JOptionPane.INFORMATION_MESSAGE);
                    } else if (response.startsWith("RESULTS:")) {
                        // Display results and handle selection
                        String resultsStr = response.substring(8);
                        String[] titles = resultsStr.split("\\|");
                        
                        String selectedTitle = (String) JOptionPane.showInputDialog(
                                null, 
                                "Select a result:", 
                                "Search Results", 
                                JOptionPane.QUESTION_MESSAGE, 
                                null, 
                                titles, 
                                titles[0]);
                        
                        if (selectedTitle != null) {
                            // Get the index of the selected title
                            int selectedIndex = -1;
                            for (int i = 0; i < titles.length; i++) {
                                if (titles[i].equals(selectedTitle)) {
                                    selectedIndex = i;
                                    break;
                                }
                            }
                            
                            // Request page description
                            out.println("GET:" + selectedIndex);
                            String pageResponse = in.readLine();
                            
                            if (pageResponse != null && pageResponse.startsWith("PAGE:")) {
                                String description = pageResponse.substring(5);
                                JOptionPane.showMessageDialog(null, 
                                        description, 
                                        selectedTitle, 
                                        JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, 
                                        "Error retrieving page information.", 
                                        "Error", 
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else if (response.startsWith("ERROR:")) {
                        JOptionPane.showMessageDialog(null, 
                                response.substring(6), 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, 
                            "Error communicating with the server: " + e.getMessage(), 
                            "Communication Error", 
                            JOptionPane.ERROR_MESSAGE);
                    continueSearching = false;
                    continue;
                }
                
                // Ask if user wants to search again
                int option = JOptionPane.showConfirmDialog(null, 
                        "Would you like to search again?", 
                        "Continue", 
                        JOptionPane.YES_NO_OPTION);
                
                continueSearching = (option == JOptionPane.YES_OPTION);
            }
            
            // Show farewell message
            showFarewell();
            
            // Close the connection
            closeConnection();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, 
                    "Invalid port number. Please enter a numeric value.", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Connects to the server
     */
    private boolean connectToServer(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Closes the connection to the server
     */
    private void closeConnection() {
        try {
            if (out != null) {
                out.println("QUIT");
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            // Ignore exceptions on closing
        }
    }

    /**
     * Shows a farewell message
     */
    private void showFarewell() {
        JOptionPane.showMessageDialog(null, 
                "Thank you for using the Search Engine Application!", 
                "Goodbye", 
                JOptionPane.INFORMATION_MESSAGE);
    }
}