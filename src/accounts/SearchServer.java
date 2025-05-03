package accounts;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Server component for a text search engine application.
 * This server loads a database file, processes search requests, 
 * and serves requested page information.
 * 
 * Port: 12345
 */
public class SearchServer {
    private static final int PORT = 3001;
    private static final String DATABASE_FILE = "searchDatabase.txt";
    private List<Page> database = new ArrayList<>();

    /**
     * Page class to store database entries
     */
    private static class Page {
        private int id;
        private String title;
        private String description;

        public Page(int id, String title, String description) {
            this.id = id;
            this.title = title;
            this.description = description;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return id + ";" + title + ";" + description;
        }
    }

    /**
     * Main method to start the server
     */
    public static void main(String[] args) {
        SearchServer server = new SearchServer();
        server.start();
    }

    /**
     * Starts the server and begins accepting client connections
     */
    public void start() {
        // Load the database
        loadDatabase();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                try {
                    // Wait for a client to connect
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    // Handle client communication in a separate method
                    handleClient(clientSocket);
                } catch (IOException e) {
                    System.err.println("Error handling client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    /**
     * Loads the database from the file
     */
    private void loadDatabase() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", 3);
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[0].trim());
                    String title = parts[1].trim();
                    String description = parts[2].trim();
                    database.add(new Page(id, title, description));
                }
            }
            System.out.println("Database loaded. " + database.size() + " entries found.");
        } catch (IOException e) {
            System.err.println("Error loading database: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing page ID: " + e.getMessage());
        }
    }

    /**
     * Handles communication with a client
     */
    private void handleClient(Socket clientSocket) {
        try (
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String input;
            while ((input = in.readLine()) != null) {
                if (input.equals("QUIT")) {
                    break;
                }

                try {
                    // Check if the input is a search query or a request for page details
                    if (input.startsWith("SEARCH:")) {
                        String searchTerm = input.substring(7).toLowerCase();
                        List<Page> results = searchDatabase(searchTerm);
                        sendSearchResults(out, results);
                    } else if (input.startsWith("GET:")) {
                        int index = Integer.parseInt(input.substring(4));
                        sendPageDescription(out, index);
                    }
                } catch (Exception e) {
                    out.println("ERROR: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error communicating with client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client disconnected.");
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    /**
     * Searches the database for the given term
     */
    private List<Page> searchDatabase(String searchTerm) {
        List<Page> results = new ArrayList<>();
        for (Page page : database) {
            if (page.getTitle().toLowerCase().contains(searchTerm) || 
                page.getDescription().toLowerCase().contains(searchTerm)) {
                results.add(page);
            }
        }
        return results;
    }

    /**
     * Sends search results to the client
     */
    private void sendSearchResults(PrintWriter out, List<Page> results) {
        if (results.isEmpty()) {
            out.println("NO_RESULTS");
        } else {
            StringBuilder resultStr = new StringBuilder();
            for (int i = 0; i < results.size(); i++) {
                if (i > 0) {
                    resultStr.append("|");
                }
                resultStr.append(results.get(i).getTitle());
            }
            out.println("RESULTS:" + resultStr.toString());
        }
    }

    /**
     * Sends the description of a specific page to the client
     */
    private void sendPageDescription(PrintWriter out, int index) {
        if (index >= 0 && index < database.size()) {
            out.println("PAGE:" + database.get(index).getDescription());
        } else {
            out.println("ERROR: Invalid page index");
        }
    }
}