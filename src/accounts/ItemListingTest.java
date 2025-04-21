import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
  * Test for the ItemListing object, testing to ensure all creations and functions work.
  *
  * <p>Purdue University -- CS18000 -- Spring 2025</p>
  *
  * @author @Phaynes742
            @hsupple
            @jburkett013
            @addy-ops
  * @version April, 2025
  */
class ItemListingTest {
    
    private static final String TEST_AUCTION_FILE = "AuctionList.txt";

    @BeforeEach
    void cleanup() throws IOException {
        Path auctionListPath = Paths.get(System.getProperty("user.dir") + "/src/serverclient/txt/AuctionList.txt");


        System.out.println("Resetting file at: " + auctionListPath.toAbsolutePath());

        String defaultContent = "1001,example,description,seller";
        Files.createDirectories(auctionListPath.getParent());

        Files.write(auctionListPath, defaultContent.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Test
    void testCreateItemWritesToFile() throws IOException {
        ItemListing item = new ItemListing("Laptop", "Gaming laptop", 300, "seller1", 2000);
        String lines = Files.readString(Paths.get(System.getProperty("user.dir") 
                                                  + "/src/serverclient/txt/" + TEST_AUCTION_FILE));
        assertFalse(lines.equals(""), "Auction file should not be empty after item creation");
        assertTrue(lines.indexOf("Laptop") != -1 , "Item not written correctly to file");
    }

    @Test
    void testSetItemName() throws IOException {
        ItemListing item = new ItemListing("OldName", "desc", 100, "seller1", 2000);
        item.setItemName("NewName");
        String lastLine = getLastLine(System.getProperty("user.dir") + "/src/serverclient/txt/" + TEST_AUCTION_FILE);
        assertTrue(lastLine.contains("NewName"), "Item name not updated in file");
    }

    @Test
    void testSetItemDescription() throws IOException {
        ItemListing item = new ItemListing("Item12", "Old desc", 100, "seller1", 2000);
        item.setItemDescription("Updated desc");
        String lastLine = getLastLine(System.getProperty("user.dir") + "/src/serverclient/txt/" + TEST_AUCTION_FILE);
        assertTrue(lastLine.contains("Updated/desc"), "Description not updated in file");
    }

    @Test
    void testSetBuyNowItemPrice() throws IOException {
        ItemListing item = new ItemListing("Item17", "desc", 100, "seller1", 2000);
        item.setBuyNowItemPrice(500);
        String lastLine = getLastLine(System.getProperty("user.dir") + "/src/serverclient/txt/" + TEST_AUCTION_FILE);
        assertTrue(lastLine.contains("500.0"), "Buy-now price not updated");
    }

    @Test
    void testPlaceValidBid() throws IOException {
        ItemListing item = new ItemListing("Item", "desc", 200, "seller1", 2000);
        item.placeBid(250, "buyer1");
        String lastLine = getLastLine(System.getProperty("user.dir") + "/src/serverclient/txt/" + TEST_AUCTION_FILE);
        assertTrue(lastLine.contains("buyer1"), "Valid bid not recorded in file");
    }

    @Test
    void testPlaceInvalidBid() throws IOException {
        ItemListing item = new ItemListing("Item", "desc", 200, "seller1", 2000);
        item.placeBid(100, "buyer1");  // Too low
        String lastLine = getLastLine(System.getProperty("user.dir") + "/src/serverclient/txt/" + TEST_AUCTION_FILE);
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
        ItemListing item = new ItemListing("Item", "desc", 200, "seller1", 500);
        Thread.sleep(1000); 
        assertFalse(item.isActive(), "Auction should automatically end after duration");
    }

    private String getLastLine(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        return lines.get(lines.size() - 1);
    }
}