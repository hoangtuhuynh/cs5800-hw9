package texteditor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DocumentCharacterTest {
    private final CharacterProperties properties = new CharacterPropertiesFactory().getProperties("Arial", "Red", 12);

    @Test
    void getValue() {
        assertEquals('H', new DocumentCharacter('H', properties).getValue());
    }

    @Test
    void getProperties() {
        assertSame(properties, new DocumentCharacter('H', properties).getProperties());
    }

    @Test
    void toStringShowsCharacterAndProperties() {
        assertEquals("'H' (Arial, Red, 12)", new DocumentCharacter('H', properties).toString());
    }

    @Test
    void propertiesAreRequired() {
        assertThrows(IllegalArgumentException.class, () -> new DocumentCharacter('H', null));
    }
}
