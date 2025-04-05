import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

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
        String content = Files.readString(Paths.get(TEST_BUYER_FILE));
        assertTrue(content.contains("testUser,testPass"), "Buyer not found in file");
    }

    @Test
    void testSetPasswordUpdatesFile() throws IOException {
        Buyer buyer = new Buyer("testUser2", "oldPass");
        buyer.setPassword("newPass");
        String content = Files.readString(Paths.get(TEST_BUYER_FILE));
        assertTrue(content.contains("testUser2,newPass"), "Password not updated in file");
    }

    @Test
    void testSendMessageToSellerStoresMessages() {
        Buyer buyer = new Buyer("buyer1", "pass");
        buyer.sendMessageToSeller("seller1", "Hello!");
        ArrayList<String> messages = buyer.getMessages("buyer1");

        assertTrue(messages.contains("seller1"), "Seller not found in messages");
        assertTrue(messages.contains("Hello!"), "Message not stored properly");
    }

    @Test
    void testMakeBidUpdatesAuctionFile() throws IOException {
        Buyer buyer = new Buyer("buyer1", "pass");
        buyer.makeBid("item1", 99.99);
        String content = Files.readString(Paths.get(TEST_AUCTION_FILE));
        assertTrue(content.contains("buyer1,99.99"), "Bid not updated");
    }

    @Test
    void testRateSellerUpdatesRating() throws IOException {
        Buyer buyer = new Buyer("buyer1", "pass");
        buyer.rateSeller("seller1", 5.0);
        String content = Files.readString(Paths.get(TEST_SELLER_FILE));
        assertTrue(content.contains("3"), "Rating count not updated");
    }

    @Test
    void testDeleteAccountDeactivatesBuyer() {
        Buyer buyer = new Buyer("buyer1", "pass");
        buyer.deleteAccount();
        assertFalse(buyer.isActive(), "Buyer should be deactivated");
    }
}

       

   
