package texteditor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CharacterPropertiesFactory {
    private final Map<String, CharacterProperties> cache = new HashMap<>();

    public CharacterProperties getProperties(String font, String color, int size) {
        checkName(font, "Font");
        checkName(color, "Color");
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }
        String key = font + "," + color + "," + size;
        return cache.computeIfAbsent(key, k -> new CharacterProperties(font, color, size));
    }

    public int getCount() {
        return cache.size();
    }

    public Collection<CharacterProperties> getAll() {
        return Collections.unmodifiableCollection(cache.values());
    }

    // Commas and line breaks would break the save file format.
    private static void checkName(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " is required");
        }
        if (value.contains(",") || value.contains("\n") || value.contains("\r")) {
            throw new IllegalArgumentException(label + " cannot contain commas or line breaks");
        }
    }
}
