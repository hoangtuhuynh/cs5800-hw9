package musicstreaming;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MusicStreamingDriverTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void captureOutput() {
        originalOut = System.out;
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOut);
    }

    @Test
    void driverSearchesWithAllThreeMethods() {
        MusicStreamingDriver.main(new String[0]);
        String printed = output.toString();
        assertTrue(printed.contains("#1 \"Bohemian Rhapsody\" by Queen on A Night at the Opera (5:54)"));
        assertTrue(printed.contains("#3 \"Hotel California\" by Eagles on Hotel California (6:31)"));
        assertTrue(printed.contains("#4 \"Billie Jean\""));
        assertTrue(printed.contains("#5 \"Beat It\""));
        assertTrue(printed.contains("#6 \"Thriller\""));
        assertTrue(printed.contains("#8 \"Yesterday\""));
        assertTrue(printed.contains("searchById(99)       [not on server] -> "));
    }
}
