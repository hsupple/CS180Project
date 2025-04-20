package serverclient;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AuctionServerTest {

    private AuctionServer auctionServer;
    private final String auctionFilePath = "txt/AuctionList.txt";
    private final String buyerFilePath = "txt/BuyerList.txt";
    private final String sellerFilePath = "txt/SellerList.txt";
    @BeforeEach
    public void setUp() throws IOException {
        auctionServer = new AuctionServer();
    }

    @AfterEach
    public void cleanup() {
        auctionServer.stopServer(); // Stop the server
    }


    @Test
    void testServerStarts() {
        assertNotNull(auctionServer);
    }
    

    @Test
    void testNewBuyer() {
        String buyerName = "buyer" + System.currentTimeMillis();
        String result = auctionServer.newBuyer(buyerName, "testPassword");
//        assertEquals("Successfully added user: " + buyerName, result);
        assertTrue(isUserInFile(buyerFilePath, buyerName), "User should be added to the BuyerList.txt");
    }
    @Test
    void testNewSeller() {
        String sellerName = "seller" + System.currentTimeMillis();
        String result = auctionServer.newSeller(sellerName, "testPassword");
        assertEquals("Successfully added user: " + sellerName, result);
        assertTrue(isUserInFile(sellerFilePath, sellerName), "User should be added to the SellerList.txt");
    }

    @Test
    void testUpdateItem() {
        auctionServer.startAuction("1", "Item1", 100.0, "Description", "testSeller", false, "", 0);
        String updatedDesc = "descUpdate" + System.currentTimeMillis();
        String result = auctionServer.updateItem("1", "Item1", updatedDesc, 150.0, "testSeller", false, "", 0);
        assertEquals("Existing item updated successfully: 1", result);
    }

    @Test
    void testSendMessage() {
        String user1= "buyer" + System.currentTimeMillis();
        String user2= "seller" + System.currentTimeMillis();
        String result = auctionServer.sendMess(user1, user2, "Hi There,Is this still available?");
        assertEquals("Message sent successfully from " + user1 + " to " + user2, result);
    }

    @Test
    void testGetMessages() {
        String user1= "buyer" + System.currentTimeMillis();
        String user2= "seller" + System.currentTimeMillis();
        auctionServer.sendMess(user1, user2, "Hi There,Is this still available?");
        String result = auctionServer.getMess(user1, user2);
        assertTrue(result.contains("Hi There,Is this still available?"));
    }


    @Test
    void testSetPasswordForExistingUser() {
        String buyerName = "buyer" + System.currentTimeMillis();
        auctionServer.newBuyer(buyerName, "firstpassword");
        auctionServer.setPass(buyerName, "passwordUpdated");
        List<String> buyers = readFile(buyerFilePath);
        for (int i = 0; i < buyers.size(); i++) {
            String[]  buyer = buyers.get(i).split(",");
            if (buyer[0].equals(buyerName)) {
                assertEquals(buyer[1], "passwordUpdated");
            }
        }
    }

    @Test
    void testDeleteBuyer() {
        String buyerName = "buyer" + System.currentTimeMillis();
        auctionServer.newBuyer(buyerName, "password");
        assertTrue(isUserInFile(buyerFilePath, buyerName), "User should be added to the BuyerList.txt");
        auctionServer.delete(buyerName, "password");
        List<String> buyers = readFile(buyerFilePath);
        assertFalse(buyers.stream().anyMatch(b -> b.startsWith(buyerName)));
    }

    @Test
    void testDeleteSeller() {
        String sellerName = "seller" + System.currentTimeMillis();
        auctionServer.newSeller(sellerName, "password");
        assertTrue(isUserInFile(sellerFilePath, sellerName), "User should be added to the SellerList.txt");
        auctionServer.delete(sellerName, "password");
        List<String> buyers = readFile(sellerFilePath);
        assertFalse(buyers.stream().anyMatch(b -> b.startsWith(sellerName)));
    }

    @Test
    void testSetandGetRating() {
        String sellerName = "seller" + System.currentTimeMillis();
        auctionServer.newSeller(sellerName, "password");
        auctionServer.setRating(sellerName, 4.60);
        String rating = auctionServer.getRating(sellerName);
        assertEquals("4.60", rating, "Rating is not correct");
    }

    @Test
    void testGetRatingForNonexistentUser() {
        String result = auctionServer.getRating("nonexistentUser");
        assertEquals("User not found", result);
    }

    @Test
    void testBuyItemSuccessfully() {
        String itemId = "itemid" + System.currentTimeMillis();
        auctionServer.startAuction(itemId, "item1", 22.0, "description", "seller1", false, "", 0.0);
        String result = auctionServer.buyItem(itemId, "buyer1");
        assertEquals("Item bought successfully: " + itemId, result);
        List<String> items = readFile(auctionFilePath);
        String[] parts;
        for (int i = 0; i < items.size(); i++) {
            parts = items.get(i).split(",");
            if (parts[0].equals(itemId)) {
                assertEquals("true", parts[5]); // isSold should be true
            }
        }
    }

    @Test
    void testBuyNonExistentItem() {
        String result = auctionServer.buyItem("999", "buyer1");
        assertEquals("Item not found: 999", result);
    }

    @Test
    void testStartAuction() {
        String itemId = "itemid" + System.currentTimeMillis();
        String itemName = "itemName" + System.currentTimeMillis();
        String result = auctionServer.startAuction(itemId, itemName, 15.0, "description", "seller1", false, "", 0.0);
        assertEquals("Auction started successfully for item: "+itemId, result);
        List<String> items = readFile(auctionFilePath);
        assertTrue(items.stream().anyMatch(line -> line.startsWith(itemId)));
    }

    @Test
    void testEndAuctionSuccessfully() {
        String itemId = "itemid" + System.currentTimeMillis();
        auctionServer.startAuction(itemId, "testItem", 10.0, "description", "seller1", false, "", 0.0);
        List<String> items = readFile(auctionFilePath);
        assertTrue(items.stream().anyMatch(line -> line.startsWith(itemId)));
        String result = auctionServer.endAuction(itemId);
        assertEquals("Auction ended successfully for item: "+itemId, result);

        List<String> itemsReadAgain = readFile(auctionFilePath);

        for (int i = 0; i < itemsReadAgain.size(); i++) {
            String[] parts = itemsReadAgain.get(i).split(",");
            if (parts[0].equals(itemId)) {
                assertEquals("true", parts[5]); // isSold should be true
            }
        }
    }

    @Test
    void testEndNonExistentAuction() {
        String result = auctionServer.endAuction("999");
        assertEquals("Item not found", result);
    }

    @Test
    void testBidItemSuccessfully() {
        String itemId = "itemid" + System.currentTimeMillis();
        auctionServer.startAuction(itemId, "testItem", 10.0, "description", "seller1", false, "", 0.0);
        String result = auctionServer.bidItem(itemId, "buyer1", 12.0);
        assertEquals("Bid placed successfully for item: "+itemId, result);

        // Verify the bid is updated
        List<String> items = readFile(auctionFilePath);
        for (int i = 0; i < items.size(); i++) {
            String[] parts = items.get(i).split(",");
            if (parts[0].equals(itemId)) {
                assertEquals("12.0", parts[7]);
            }
        }
    }

    @Test
    void testBidItemWithLowPrice() {
        String itemId = "itemid" + System.currentTimeMillis();
        auctionServer.startAuction(itemId, "testItem", 10.0, "description", "seller1", false, "", 8.0);
        String result = auctionServer.bidItem(itemId, "buyer1", 5.0); // Low bid
        assertEquals("Bid price is too low for item: "+itemId, result);
    }

    @Test
    void testBidNonExistentItem() {
        String result = auctionServer.bidItem("999", "buyer1", 12.0);
        assertEquals("Item not found", result);
    }

    @Test
    void testGetMyListings() {
        String sellerName = "seller" + System.currentTimeMillis();
        auctionServer.newSeller(sellerName, "testPassword");
        String itemId = sellerName+ "_Item_";
        String itemIdOtherSeller = "Item"+ + System.currentTimeMillis();;
        auctionServer.startAuction(itemId, "testItem", 10.0, "description", sellerName, false, "", 0.0);
        auctionServer.startAuction(itemIdOtherSeller, "testItem", 10.0, "description", "differentSeller", false, "", 0.0);
        String results = auctionServer.getMyListings(sellerName);
        assertTrue(results.contains(itemId), "Seller's should have listings.");
        assertFalse(results.contains(itemIdOtherSeller), "Other Seller's listings should not be included.");
    }

    @Test
    void testGetMyListingsForNonexistentUser() {
        String result = auctionServer.getMyListings("nonexistent");
        assertEquals("[]", result, "Should return empty list for a user with no listings.");
    }

    @Test
    void testSearchForItem() {
        String itemId =  "Item_"+ System.currentTimeMillis();;
        String itemName =  "ItemName_"+ System.currentTimeMillis();;
        auctionServer.startAuction(itemId, itemName, 10.0, "description", "testSeller", false, "", 0.0);
        String result = auctionServer.search(itemName);
        assertTrue(result.contains("Listings"));
        assertTrue(result.contains(itemName), "Item should be found in the auction listings.");
    }

    @Test
    void testSearchWithNoResults() {
        String result = auctionServer.search("nonexistent");
        assertTrue(result.contains("Sellers"));
        assertFalse(result.contains("seller1"), "No sellers should match the query.");
        assertTrue(result.contains("Listings"));
        assertFalse(result.contains("item1"), "No items should match the query.");
    }

    @Test
    void testSearchForSeller() {
        String sellerName = "seller" + System.currentTimeMillis();
        auctionServer.newSeller(sellerName, "testPassword");
        String result = auctionServer.search(sellerName);
        assertTrue(result.contains(sellerName));
    }

    private boolean isUserInFile(String filePath, String username) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // User not found
    }

    private List<String> readFile(String filePath) {
        List<String> contentBuilder = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder;
    }
}

