import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

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

public class ItemListingTest {

    private static final Path AUCTION_FILE = Paths.get("AuctionList.txt");

    @BeforeEach
    void setup() throws IOException {
        Files.write(AUCTION_FILE, new byte[0]);
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(AUCTION_FILE);
    }

    @Test
    void testCreateItemInitializesCorrectly() {
        ItemListing item = new ItemListing("Item1", "Description", 50, "seller1", 2000);
        assertEquals("Item1".replace(" ", "/"), item.getItemName());
        assertEquals("Description".replace(" ", "/"), item.getItemDescription());
        assertTrue(item.isActive());
    }

    @Test
    void testSetBuyNowPriceUpdates() {
        ItemListing item = new ItemListing("Item2", "Desc", 100, "seller1", 2000);
        item.setBuyNowItemPrice(500.0);
        assertEquals(500.0, item.getBuyNowItemPrice());
    }

    @Test
    void testBuyNowFailsWithoutPrice() {
        ItemListing item = new ItemListing("Item3", "Desc", 100, "seller1", 2000);
        assertFalse(item.buyNow("buyer1"));
    }

    @Test
    void testAuctionEndsAutomatically() throws Exception {
        ItemListing item = new ItemListing("Item4", "AutoEnd", 100, "seller1", 500);
        Thread.sleep(1000);
        assertFalse(item.isActive(), "Item should no longer be active after duration");
    }

    public static void main(String[] args) {
        org.junit.platform.console.ConsoleLauncher.main(
                new String[]{"--select-class", "item.ItemListingTest"}
        );
    }
}