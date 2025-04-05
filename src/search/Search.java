package search;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Search {

    private final String query;
    private final ArrayList<String> users = new ArrayList<>();
    private final ArrayList<String> listings = new ArrayList<>();

    public Search(String query) {
        this.query = query;
        this.users.addAll(getUsers(query));
        this.listings.addAll(getListings(query));
    }

    private ArrayList<String> getUsers(String query) {
        ArrayList<String> users = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("SellerList.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(query)) {
                    String[] parts = line.split(",");
                    users.add(parts[0]);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;

    }

    private ArrayList<String> getListings(String query) {
        ArrayList<String> Listings = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("SellerList.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(query)) {
                    String[] parts = line.split(",");
                    Listings.add(parts[0]);
                    Listings.add(parts[1]);
                    Listings.add(parts[2]);
                    Listings.add(parts[7]);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Listings;

    }

    public ArrayList<String> getSearchResults() {
        ArrayList<String> results = new ArrayList<>();
        results.addAll(users);
        results.addAll(listings); 
    return results;
}

}