import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
  * Test for the Buyer object, testing to ensure all creations and functions work.
  *
  * <p>Purdue University -- CS18000 -- Spring 2025</p>
  *
  * @author @Phaynes742
            @hsupple
            @jburkett013
            @addy-ops
  * @version April, 2025
  */

class BuyerTest {

    private static final String TEST_BUYER_FILE = "BuyerList.txt";
    private static final String TEST_AUCTION_FILE = "AuctionList.txt";
    private static final String TEST_SELLER_FILE = "SellerList.txt";

    @BeforeEach
    void setup() throws IOException {
        Files.write(Paths.get(TEST_BUYER_FILE), new byte[0]);
        Files.write(Paths.get(TEST_AUCTION_FILE), "item1,item,desc,cat,time,price,none,0\n".getBytes());
        Files.write(Paths.get(TEST_SELLER_FILE), "seller1,4.0,2\n".getBytes());
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_BUYER_FILE));
        Files.deleteIfExists(Paths.get(TEST_AUCTION_FILE));
        Files.deleteIfExists(Paths.get(TEST_SELLER_FILE));
    }

    @Test
    void testCreateBuyerWritesToFile() throws IOException {
        Buyer buyer = new Buyer("testUser", "testPass");
        String content = Files.readString(Paths.get(System.getProperty("user.dir") 
                                                    + "/src/serverclient/txt/" + TEST_BUYER_FILE));
        assertTrue(content.contains("testUser") && content.contains("testPass"), "Buyer info not found");
    }

    @Test
    void testSetPasswordUpdatesFile() throws IOException {
        Buyer buyer = new Buyer("testUser2", "oldPass");
        buyer.setPassword("newPass");
        String content = Files.readString(Paths.get(System.getProperty("user.dir") + "/src/serverclient/txt/" 
                                                    + TEST_BUYER_FILE));
        assertTrue(content.contains("testUser2,newPass"), "Password not updated in file");
    }

    // MUST REMOVE THE MSG FILES BEFORE RUNNING THIS TEST
    @Test
    void testSendMessageToSellerStoresMessages() {
        Buyer buyer = new Buyer("buyer1", "pass");
        Seller seller = new Seller("seller1", "password");
        buyer.sendMessageToSeller("seller1", "Hello!");
        ArrayList<String> messages = buyer.getMessages("seller1");

        assertTrue(messages.contains("[buyer1: Hello!]"), "Buyer not found in messages" 
                   + messages.toString());
    }

    @Test
    void testMakeBidUpdatesAuctionFile() throws IOException {
        ItemListing item = new ItemListing("item1", "desc", 10, "seller1", 2000);
        Buyer buyer = new Buyer("buyer1", "pass");
        buyer.makeBid("item1", 99.99);
        String content = Files.readString(Paths.get(System.getProperty("user.dir") + "/src/serverclient/txt/" 
                                                    + TEST_AUCTION_FILE));
        assertTrue(content.contains("buyer1,99.99"), "Bid not updated" + content);
    }

    @Test
    void testRateSellerUpdatesRating() throws IOException {
        Buyer buyer = new Buyer("buyer1", "pass");
        Seller seller = new Seller("seller1", "password");
        buyer.rateSeller("seller1", 5.0);
        String content = Files.readString(Paths.get(System.getProperty("user.dir") + "/src/serverclient/txt/" 
                                                    + TEST_SELLER_FILE));
        assertTrue(content.contains("1"), "Rating count not updated");
    }

    @Test
    void testDeleteAccountDeactivatesBuyer() {
        Buyer buyer = new Buyer("buyer1", "pass");
        buyer.deleteAccount("buyer1", "pass");
        assertFalse(buyer.isActive("buyer1"), "Buyer should be deactivated");
    }
}
