import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class BuyerTest {

    private static final Path BUYER_FILE = Paths.get("BuyerList.txt");

    @BeforeEach
    void setup() throws IOException {
        Files.write(BUYER_FILE, List.of("testUser,testPass,true"));
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(BUYER_FILE);
    }

    @Test
    void testCreateBuyer() {
        Buyer buyer = new Buyer("testUser", "testPass");
        assertEquals("testUser", buyer.getUsername());
        assertEquals("testPass", buyer.getPassword());
    }

    @Test
    void testSetPassword() {
        Buyer buyer = new Buyer("testUser", "testPass");
        buyer.setPassword("newPass");
        assertEquals("newPass", buyer.getPassword());
    }

    @Test
    void testSetPasswordWithNullDoesNotCrash() {
        Buyer buyer = new Buyer("testUser", "testPass");
        assertDoesNotThrow(() -> buyer.setPassword(null));
    }

    @Test
    void testIsActiveReturnsTrue() {
        Buyer buyer = new Buyer("testUser", "testPass");
        assertTrue(buyer.isActive("testUser"));
    }
}