package texteditor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DocumentTest {
    @TempDir
    Path tempDir;

    private CharacterPropertiesFactory factory;
    private Document document;

    @BeforeEach
    void setUp() {
        factory = new CharacterPropertiesFactory();
        document = new Document(factory);
    }

    @Test
    void newDocumentIsEmpty() {
        assertEquals(0, document.length());
        assertEquals("", document.getText());
    }

    @Test
    void addTextAppendsCharacters() {
        document.addText("Hello", "Arial", "Red", 12);
        document.addText("World", "Calibri", "Blue", 14);
        assertEquals("HelloWorld", document.getText());
        assertEquals(10, document.length());
    }

    @Test
    void addCharacterAppendsOneCharacter() {
        document.addCharacter('A', "Arial", "Red", 12);
        assertEquals("A", document.getText());
        assertEquals("Arial", document.getCharacter(0).getProperties().getFont());
    }

    @Test
    void charactersWithSamePropertiesShareOneObject() {
        document.addText("Hello", "Arial", "Red", 12);
        document.addText("!!", "Arial", "Red", 12);
        CharacterProperties shared = document.getCharacter(0).getProperties();
        for (DocumentCharacter character : document.getCharacters()) {
            assertSame(shared, character.getProperties());
        }
        assertEquals(1, factory.getCount());
    }

    @Test
    void charactersWithDifferentPropertiesDoNotShare() {
        document.addText("Hi", "Arial", "Red", 12);
        document.addText("Yo", "Verdana", "Black", 16);
        assertNotSame(document.getCharacter(0).getProperties(), document.getCharacter(2).getProperties());
        assertEquals(2, factory.getCount());
    }

    @Test
    void insertTextPlacesCharactersAtIndex() {
        document.addText("HeWorld", "Arial", "Red", 12);
        document.insertText(2, "llo", "Calibri", "Blue", 14);
        assertEquals("HelloWorld", document.getText());
        assertEquals("Calibri", document.getCharacter(2).getProperties().getFont());
        assertEquals("Arial", document.getCharacter(5).getProperties().getFont());
    }

    @Test
    void insertTextOutsideDocumentFails() {
        document.addText("Hi", "Arial", "Red", 12);
        assertThrows(IndexOutOfBoundsException.class, () -> document.insertText(3, "x", "Arial", "Red", 12));
        assertThrows(IndexOutOfBoundsException.class, () -> document.insertText(-1, "x", "Arial", "Red", 12));
    }

    @Test
    void replaceCharacterKeepsProperties() {
        document.addText("Cat", "Verdana", "Black", 16);
        CharacterProperties before = document.getCharacter(0).getProperties();
        document.replaceCharacter(0, 'B');
        assertEquals("Bat", document.getText());
        assertSame(before, document.getCharacter(0).getProperties());
    }

    @Test
    void deleteCharacterRemovesOnlyTheCharacter() {
        document.addText("Hello", "Arial", "Red", 12);
        document.deleteCharacter(4);
        assertEquals("Hell", document.getText());
        assertEquals(1, factory.getCount());
    }

    @Test
    void changePropertiesRestylesARange() {
        document.addText("HelloWorld", "Arial", "Red", 12);
        document.changeProperties(5, 10, "Calibri", "Blue", 14);
        assertEquals("HelloWorld", document.getText());
        assertEquals("Arial", document.getCharacter(4).getProperties().getFont());
        for (int i = 5; i < 10; i++) {
            assertEquals("Calibri", document.getCharacter(i).getProperties().getFont());
            assertEquals("Blue", document.getCharacter(i).getProperties().getColor());
            assertEquals(14, document.getCharacter(i).getProperties().getSize());
        }
    }

    @Test
    void changePropertiesReusesExistingFlyweight() {
        document.addText("Hi", "Arial", "Red", 12);
        document.addText("Yo", "Verdana", "Black", 16);
        document.changeProperties(2, 4, "Arial", "Red", 12);
        assertSame(document.getCharacter(0).getProperties(), document.getCharacter(3).getProperties());
        assertEquals(2, factory.getCount());
    }

    @Test
    void changePropertiesWithBadRangeFails() {
        document.addText("Hi", "Arial", "Red", 12);
        assertThrows(IndexOutOfBoundsException.class, () -> document.changeProperties(0, 3, "Arial", "Red", 12));
        assertThrows(IndexOutOfBoundsException.class, () -> document.changeProperties(2, 1, "Arial", "Red", 12));
        assertThrows(IndexOutOfBoundsException.class, () -> document.changeProperties(-1, 1, "Arial", "Red", 12));
    }

    @Test
    void charactersListCannotBeModifiedDirectly() {
        document.addText("Hi", "Arial", "Red", 12);
        assertThrows(UnsupportedOperationException.class, () -> document.getCharacters().clear());
    }

    @Test
    void documentNeedsAFactory() {
        assertThrows(IllegalArgumentException.class, () -> new Document(null));
    }

    @Test
    void saveAndLoadKeepsTextAndProperties() throws IOException {
        document.addText("Hello", "Arial", "Red", 12);
        document.addText("World", "Calibri", "Blue", 14);
        Path file = tempDir.resolve("doc.txt");
        document.save(file);

        Document loaded = Document.load(file, new CharacterPropertiesFactory());
        assertEquals("HelloWorld", loaded.getText());
        for (int i = 0; i < document.length(); i++) {
            CharacterProperties expected = document.getCharacter(i).getProperties();
            CharacterProperties actual = loaded.getCharacter(i).getProperties();
            assertEquals(expected.getFont(), actual.getFont());
            assertEquals(expected.getColor(), actual.getColor());
            assertEquals(expected.getSize(), actual.getSize());
        }
        assertEquals(2, loaded.getFactory().getCount());
    }

    @Test
    void loadingWithSameFactoryReusesFlyweights() throws IOException {
        document.addText("Hello", "Arial", "Red", 12);
        Path file = tempDir.resolve("doc.txt");
        document.save(file);

        Document loaded = Document.load(file, factory);
        assertSame(document.getCharacter(0).getProperties(), loaded.getCharacter(0).getProperties());
        assertEquals(1, factory.getCount());
    }

    @Test
    void saveAndLoadHandlesCommasSpacesAndNewlines() throws IOException {
        document.addText("a, b\nc d", "Times New Roman", "Dark Green", 11);
        Path file = tempDir.resolve("doc.txt");
        document.save(file);

        Document loaded = Document.load(file, new CharacterPropertiesFactory());
        assertEquals("a, b\nc d", loaded.getText());
        assertEquals("Times New Roman", loaded.getCharacter(0).getProperties().getFont());
    }

    @Test
    void saveEmptyDocumentLoadsEmpty() throws IOException {
        Path file = tempDir.resolve("empty.txt");
        document.save(file);
        assertTrue(Files.exists(file));
        assertEquals(0, Document.load(file, factory).length());
    }

    @Test
    void loadingMalformedFileFails() throws IOException {
        Path wrongFieldCount = tempDir.resolve("bad1.txt");
        Files.writeString(wrongFieldCount, "72,Arial,Red\n");
        assertThrows(IOException.class, () -> Document.load(wrongFieldCount, factory));

        Path notANumber = tempDir.resolve("bad2.txt");
        Files.writeString(notANumber, "H,Arial,Red,12\n");
        assertThrows(IOException.class, () -> Document.load(notANumber, factory));

        Path badSize = tempDir.resolve("bad3.txt");
        Files.writeString(badSize, "72,Arial,Red,0\n");
        assertThrows(IOException.class, () -> Document.load(badSize, factory));
    }

    @Test
    void loadingMissingFileFails() {
        assertThrows(IOException.class, () -> Document.load(tempDir.resolve("missing.txt"), factory));
    }
}
