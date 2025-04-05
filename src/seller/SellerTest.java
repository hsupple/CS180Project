import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SellerTest {

    private static final Path SELLER_FILE = Paths.get("SellerList.txt");
    private static final Path AUCTION_FILE = Paths.get("AuctionList.txt");

    @BeforeEach
    void setup() throws IOException {
        Files.write(SELLER_FILE,
                List.of("seller1,pass123,4.5,3,true")); 

        Files.write(AUCTION_FILE,
                List.of("item1,name,desc,cat,time,price,seller1,0")); 
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(SELLER_FILE);
        Files.deleteIfExists(AUCTION_FILE);
    }

    @Test
    void testSetPasswordUpdatesFile() throws IOException {
        Seller seller = new Seller("seller1", "oldPass");
        seller.setPassword("newPass");

        List<String> lines = Files.readAllLines(SELLER_FILE);
        boolean updated = lines.stream().anyMatch(line -> line.contains("newPass"));
        assertTrue(updated, "New password should be written to file.");
    }

    @Test
    void testAddRatingUpdatesFile() throws IOException {
        Seller seller = new Seller("seller1", "pass123");
        seller.addRating(5.0);

        List<String> lines = Files.readAllLines(SELLER_FILE);
        boolean updated = lines.stream().anyMatch(line -> line.contains("5.0"));
        assertTrue(updated, "Rating should be updated in file.");
    }

    @Test
    void testSendMessageToBuyer() {
        Seller seller = new Seller("seller2", "pass");
        seller.sendMessageToBuyer("buyer1", "Hi!");

        List<String> messages = seller.getMessages("buyer1"); 
        assertTrue(messages.contains("Hi!"), "Message should be stored for buyer.");
    }

    @Test
    void testGetListingsReturnsData() {
        Seller seller = new Seller("seller1", "pass123");
        List<String> listings = seller.getListings(); 

        assertFalse(listings.isEmpty(), "Listings should be returned if present.");
    }

    @Test
    void testDeactivateSetsActiveFalse() {
        Seller seller = new Seller("seller3", "pass");
        seller.deactivate();
        assertFalse(seller.isActive(), "Seller should be inactive after deactivation.");
    }

    @Test
    void testDeleteAccountCallsDeactivate() {
        Seller seller = new Seller("seller4", "pass");
        seller.deleteAccount();
        assertFalse(seller.isActive(), "deleteAccount() should deactivate seller.");
    }

    @Test
    void testRatingsAreReadCorrectly() {
        Seller seller = new Seller("seller1", "pass123");
        seller.loadFromFile();
        assertEquals(4.5, seller.getRating(), 0.01, "Rating should be fetched from file");
        assertEquals(3, seller.getRatingCount(), "Rating count should be fetched from file");
    }
}
