package itemlisting;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemListingTest {

    private static final String TEST_AUCTION_FILE = "AuctionList.txt";

    @BeforeEach
    void setup() throws IOException {
        Files.write(Paths.get(TEST_AUCTION_FILE), new byte[0]); // clear test file
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_AUCTION_FILE));
    }

    @Test
    void testCreateItemWritesToFile() throws IOException {
        ItemListing item = new ItemListing("Laptop", "Gaming laptop", 300, "seller1", 2000);
        List<String> lines = Files.readAllLines(Paths.get(TEST_AUCTION_FILE));
        assertFalse(lines.isEmpty(), "Auction file should not be empty after item creation");
        assertTrue(lines.get(0).contains("Laptop"), "Item not written correctly to file");
    }

    @Test
    void testSetItemName() throws IOException {
        ItemListing item = new ItemListing("OldName", "desc", 100, "seller1", 2000);
        item.setItemName("NewName");
        String lastLine = getLastLine(TEST_AUCTION_FILE);
        assertTrue(lastLine.contains("NewName"), "Item name not updated in file");
    }

    @Test
    void testSetItemDescription() throws IOException {
        ItemListing item = new ItemListing("Item", "Old desc", 100, "seller1", 2000);
        item.setItemDescription("Updated desc");
        String lastLine = getLastLine(TEST_AUCTION_FILE);
        assertTrue(lastLine.contains("Updated desc"), "Description not updated in file");
    }

    @Test
    void testSetBuyNowItemPrice() throws IOException {
        ItemListing item = new ItemListing("Item", "desc", 100, "seller1", 2000);
        item.setBuyNowItemPrice(500);
        String lastLine = getLastLine(TEST_AUCTION_FILE);
        assertTrue(lastLine.contains("500.0"), "Buy-now price not updated");
    }

    @Test
    void testPlaceValidBid() throws IOException {
        ItemListing item = new ItemListing("Item", "desc", 200, "seller1", 2000);
        item.placeBid(250, "buyer1");
        String lastLine = getLastLine(TEST_AUCTION_FILE);
        assertTrue(lastLine.contains("buyer1"), "Valid bid not recorded in file");
    }

    @Test
    void testPlaceInvalidBid() throws IOException {
        ItemListing item = new ItemListing("Item", "desc", 200, "seller1", 2000);
        item.placeBid(100, "buyer1");  // Too low
        String lastLine = getLastLine(TEST_AUCTION_FILE);
        assertFalse(lastLine.contains("buyer1"), "Invalid bid should not be accepted");
    }

    @Test
    void testBuyNowSuccess() throws IOException {
        ItemListing item = new ItemListing("Item", "desc", 200, "seller1", 2000);
        item.setBuyNowItemPrice(400);
        boolean success = item.buyNow("buyer1");
        assertTrue(success, "Buy-now should succeed with valid price");
    }

    @Test
    void testBuyNowFailsWithoutPrice() throws IOException {
        ItemListing item = new ItemListing("Item", "desc", 200, "seller1", 2000);
        boolean success = item.buyNow("buyer1");
        assertFalse(success, "Buy-now should fail when price is not set");
    }

    @Test
    void testAuctionEndsAutomatically() throws Exception {
        ItemListing item = new ItemListing("Item", "desc", 200, "seller1", 500); // 0.5 second
        Thread.sleep(1000);  // wait for it to expire
        assertFalse(item.isActive(), "Auction should automatically end after duration");
    }

    private String getLastLine(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        return lines.get(lines.size() - 1);
    }
}
