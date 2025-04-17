package search;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import serverclient.AuctionClient;

/**
 * Class that creates a search, taking a string query and searching for all users and listings.
 *
 * <p>Purdue University -- CS18000 -- Spring 2025</p>
 *
 * @author @Phaynes742
           @hsupple
           @jburkett013
           @addy-ops
 * @version April, 2025
 */

public class Search {

    // Declare all private variables
    private final String query;
    private AuctionClient client;
    private final ArrayList<String> users = new ArrayList<>();
    private final ArrayList<String> listings = new ArrayList<>();
    
    // Public constructor for new query variable.
    public Search(String query) {
        this.query = query;
        this.users.addAll(getUsers());
        this.listings.addAll(getListings());
        try {
            this.client = new AuctionClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // private method to get all matching users to the query
    private ArrayList<String> getUsers() {
        ArrayList<String> userList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("SellerList.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(this.query)) {
                    String[] parts = line.split(",");
                    userList.add(parts[0]);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userList;

    }

    // Private method to get all listings matching query
    private ArrayList<String> getListings() {
        ArrayList<String> listingList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("SellerList.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(this.query)) {
                    String[] parts = line.split(",");
                    listingList.add(parts[0]);
                    listingList.add(parts[1]);
                    listingList.add(parts[2]);
                    listingList.add(parts[7]);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listingList;

    }

    // Public method to return all result
    public ArrayList<String> getSearchResults() {
        ArrayList<String> results = new ArrayList<>();
        results.addAll(users);
        results.addAll(listings); 
        return results;
    }

}