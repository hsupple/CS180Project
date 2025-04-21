import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
  * Test for the Seller object, testing to ensure all creations oand functions work.
  *
  * <p>Purdue University -- CS18000 -- Spring 2025</p>
  *
  * @author @Phaynes742
            @hsupple
            @jburkett013
            @addy-ops
  * @version April, 2025
  */

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
        Seller seller = new Seller("testUser3", "oldPass");
        seller.setPassword("newPass");
        String content = Files.readString(Paths.get(System.getProperty("user.dir") 
                                                    + "/src/serverclient/txt/" + SELLER_FILE));
        assertTrue(content.contains("testUser3,newPass"), "Password not updated in file");
    }

    // MUST REMOVE THE MSG FILES BEFORE RUNNING THIS TEST
    @Test
    void testSendMessageToBuyer() {
        Seller seller = new Seller("seller2", "pass");
        Buyer buyer = new Buyer("buyer1", "pass");
        seller.sendMessageToBuyer("buyer1", "Hi!");

        List<String> messages = seller.getMessages("buyer1"); 
        assertTrue(messages.contains("[buyer1: Hi!]"), "Message should be stored for buyer." + messages);
    }

    @Test
    void testDeleteAccountCallsDeactivate() {
        Seller seller = new Seller("seller4", "pass");
        seller.deleteAccount("seller4", "pass");
        assertFalse(seller.isActive("seller4"), "deleteAccount() should deactivate seller.");
    }

    @Test
    void testRatingsAreReadCorrectly() {
        Seller seller = new Seller("rated1", "pass123");
        Buyer buyer = new Buyer("rater1", "pass123");
        buyer.rateSeller("rated1", 4.5);
        assertEquals(4.5, Double.parseDouble(seller.getRating()), 0.01, "Rating should be fetched from file");
    }
}