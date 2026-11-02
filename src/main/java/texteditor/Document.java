package texteditor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Document {
    private final CharacterPropertiesFactory factory;
    private final List<DocumentCharacter> characters = new ArrayList<>();

    public Document(CharacterPropertiesFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("A document needs a properties factory");
        }
        this.factory = factory;
    }

    public void addText(String text, String font, String color, int size) {
        insertText(characters.size(), text, font, color, size);
    }

    public void addCharacter(char value, String font, String color, int size) {
        addText(String.valueOf(value), font, color, size);
    }

    public void insertText(int index, String text, String font, String color, int size) {
        if (text == null) {
            throw new IllegalArgumentException("Text is required");
        }
        if (index < 0 || index > characters.size()) {
            throw new IndexOutOfBoundsException("Index " + index + " is outside the document");
        }
        CharacterProperties properties = factory.getProperties(font, color, size);
        for (int i = 0; i < text.length(); i++) {
            characters.add(index + i, new DocumentCharacter(text.charAt(i), properties));
        }
    }

    public void replaceCharacter(int index, char value) {
        DocumentCharacter old = characters.get(index);
        characters.set(index, new DocumentCharacter(value, old.getProperties()));
    }

    public void deleteCharacter(int index) {
        characters.remove(index);
    }

    public void changeProperties(int start, int end, String font, String color, int size) {
        if (start < 0 || end > characters.size() || start > end) {
            throw new IndexOutOfBoundsException("Range " + start + " to " + end + " is outside the document");
        }
        CharacterProperties properties = factory.getProperties(font, color, size);
        for (int i = start; i < end; i++) {
            characters.set(i, new DocumentCharacter(characters.get(i).getValue(), properties));
        }
    }

    public DocumentCharacter getCharacter(int index) {
        return characters.get(index);
    }

    public List<DocumentCharacter> getCharacters() {
        return Collections.unmodifiableList(characters);
    }

    public int length() {
        return characters.size();
    }

    public String getText() {
        StringBuilder text = new StringBuilder();
        for (DocumentCharacter character : characters) {
            text.append(character.getValue());
        }
        return text.toString();
    }

    public CharacterPropertiesFactory getFactory() {
        return factory;
    }

    // One line per character: "<char code>,<font>,<color>,<size>". The char is saved as a
    // number so commas and line breaks in the text don't break the format.
    public void save(Path file) throws IOException {
        List<String> lines = new ArrayList<>();
        for (DocumentCharacter character : characters) {
            CharacterProperties p = character.getProperties();
            lines.add((int) character.getValue() + "," + p.getFont() + "," + p.getColor() + "," + p.getSize());
        }
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    public static Document load(Path file, CharacterPropertiesFactory factory) throws IOException {
        Document document = new Document(factory);
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            String[] parts = line.split(",", -1);
            if (parts.length != 4) {
                throw new IOException("Line " + (i + 1) + " is malformed: " + line);
            }
            try {
                char value = (char) Integer.parseInt(parts[0]);
                document.addCharacter(value, parts[1], parts[2], Integer.parseInt(parts[3]));
            } catch (IllegalArgumentException e) {
                throw new IOException("Line " + (i + 1) + " is malformed: " + line, e);
            }
        }
        return document;
    }
}
