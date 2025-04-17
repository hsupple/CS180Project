package search;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SearchTest {

    private static final String TEST_SELLER_FILE = "SellerList.txt";

    @BeforeEach
    void setup() throws IOException {
        String mockData = String.join("\n",
                "seller1,4.9,5,desc1,category1,title1,image1,listing1",
                "seller2,4.2,3,desc2,category2,title2,image2,listing2",
                "badline-without-enough-fields"
        );
        Files.write(Paths.get(TEST_SELLER_FILE), mockData.getBytes());
    }

    @AfterEach
    void cleanup() throws IOException, InterruptedException {
        // Small delay helps prevent file lock issues on Windows
        Thread.sleep(100);
        Files.deleteIfExists(Paths.get(TEST_SELLER_FILE));
    }

    @Test
    void testSearchMatchFound() {
        Search search = new Search("seller1");
        ArrayList<String> results = search.getSearchResults();

        assertTrue(results.contains("seller1"), "Expected seller username not found");
        assertTrue(results.contains("listing1"), "Expected listing not found");
    }

    @Test
    void testSearchNoMatch() {
        Search search = new Search("nonexistent");
        ArrayList<String> results = search.getSearchResults();

        assertTrue(results.isEmpty(), "Results should be empty for unmatched query");
    }

    @Test
    void testSearchPartialMatch() {
        Search search = new Search("listing");
        ArrayList<String> results = search.getSearchResults();

        assertTrue(results.contains("listing1"), "Partial match 'listing1' not found");
        assertTrue(results.contains("listing2"), "Partial match 'listing2' not found");
    }

    @Test
    void testHandlesMalformedLines() {
        Search search = new Search("badline");
        ArrayList<String> results = search.getSearchResults();

        
        assertTrue(results.contains("badline-without-enough-fields"),
                "Should include matched malformed username safely");

      
        assertEquals(1, results.size(),
                "Only the username part from the malformed line should be returned");
    }
}
