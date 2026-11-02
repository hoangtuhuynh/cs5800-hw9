package texteditor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TextEditorDriverTest {
    @TempDir
    Path tempDir;

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
    void savesHelloWorldCS5800WithAtLeastFourVariations() throws IOException {
        Path file = tempDir.resolve("HelloWorldCS5800.txt");
        TextEditorDriver.main(new String[] {file.toString()});

        Document saved = Document.load(file, new CharacterPropertiesFactory());
        assertEquals("HelloWorldCS5800", saved.getText());

        Set<CharacterProperties> variations = new HashSet<>();
        for (DocumentCharacter character : saved.getCharacters()) {
            variations.add(character.getProperties());
        }
        assertTrue(variations.size() >= 4, "Expected at least 4 variations but got " + variations.size());
        assertEquals(variations.size(), saved.getFactory().getCount());
    }

    @Test
    void printsTheDocumentAndFlyweightCount() throws IOException {
        TextEditorDriver.main(new String[] {tempDir.resolve("out.txt").toString()});
        String printed = output.toString();
        assertTrue(printed.contains("Text: HelloWorldCS5800"));
        assertTrue(printed.contains("16 characters share 4 character property objects"));
        assertTrue(printed.contains("'H' (Arial, Red, 12)"));
        assertTrue(printed.contains("'H' (Verdana, Red, 16)"));
    }
}
