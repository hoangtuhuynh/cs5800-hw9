package texteditor;

import java.io.IOException;
import java.nio.file.Path;

public class TextEditorDriver {
    public static void main(String[] args) throws IOException {
        Path file = Path.of(args.length > 0 ? args[0] : "HelloWorldCS5800.txt");
        CharacterPropertiesFactory factory = new CharacterPropertiesFactory();

        Document document = new Document(factory);
        document.addText("Hello", "Arial", "Red", 12);
        document.addText("World", "Calibri", "Blue", 14);
        document.addText("CS", "Verdana", "Black", 16);
        document.addText("5800", "Arial", "Blue", 16);

        System.out.println("=== Created document ===");
        printDocument(document);

        document.save(file);
        System.out.println("\nSaved to " + file.toAbsolutePath());

        Document loaded = Document.load(file, factory);
        System.out.println("\n=== Loaded document ===");
        printDocument(loaded);

        loaded.changeProperties(0, 1, "Verdana", "Red", 16);
        loaded.save(file);
        System.out.println("\n=== After restyling 'H' and saving again ===");
        printDocument(Document.load(file, factory));
    }

    private static void printDocument(Document document) {
        System.out.println("Text: " + document.getText());
        for (DocumentCharacter character : document.getCharacters()) {
            System.out.println("  " + character);
        }
        System.out.println(document.length() + " characters share "
                + document.getFactory().getCount() + " character property objects");
    }
}
