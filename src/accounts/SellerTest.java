import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SellerTest {

    private static final Path SELLER_FILE = Paths.get("SellerList.txt");

    @BeforeEach
    void setup() throws IOException {
        Files.write(SELLER_FILE, List.of("testSeller,testPass,4.0,2,true"));
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(SELLER_FILE);
    }

    @Test
    void testCreateSeller() {
        Seller seller = new Seller("testSeller", "testPass");
        assertEquals("testSeller", seller.getUsername());
    }

    @Test
    void testSetPasswordUpdatesValue() {
        Seller seller = new Seller("testSeller", "testPass");
        seller.setPassword("newPass");
        assertEquals("newPass", seller.getPassword());
    }

    @Test
    void testGetRatingParsesCorrectly() {
        Seller seller = new Seller("testSeller", "testPass");
        seller.loadFromFile();
        assertEquals("4.0", seller.getRating());
    }

    @Test
    void testDeleteAccountDeactivatesSeller() {
        Seller seller = new Seller("testSeller", "testPass");
        seller.deleteAccount();
        assertFalse(seller.isActive());
    }

    public static void main(String[] args) {
        org.junit.platform.console.ConsoleLauncher.main(
                new String[]{"--select-class", "seller.SellerTest"}
        );
    }
}