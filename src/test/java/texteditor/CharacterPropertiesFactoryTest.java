package texteditor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CharacterPropertiesFactoryTest {
    private CharacterPropertiesFactory factory;

    @BeforeEach
    void setUp() {
        factory = new CharacterPropertiesFactory();
    }

    @Test
    void storesFontColorAndSize() {
        CharacterProperties properties = factory.getProperties("Arial", "Red", 12);
        assertEquals("Arial", properties.getFont());
        assertEquals("Red", properties.getColor());
        assertEquals(12, properties.getSize());
    }

    @Test
    void samePropertiesReturnTheSameSharedObject() {
        CharacterProperties first = factory.getProperties("Calibri", "Blue", 14);
        CharacterProperties second = factory.getProperties("Calibri", "Blue", 14);
        assertSame(first, second);
        assertEquals(1, factory.getCount());
    }

    @Test
    void differentPropertiesReturnDifferentObjects() {
        CharacterProperties arial = factory.getProperties("Arial", "Black", 16);
        assertNotSame(arial, factory.getProperties("Verdana", "Black", 16));
        assertNotSame(arial, factory.getProperties("Arial", "Red", 16));
        assertNotSame(arial, factory.getProperties("Arial", "Black", 12));
        assertEquals(4, factory.getCount());
    }

    @Test
    void countOnlyGrowsForNewCombinations() {
        for (int i = 0; i < 100; i++) {
            factory.getProperties("Arial", "Red", 12);
            factory.getProperties("Verdana", "Black", 16);
        }
        assertEquals(2, factory.getCount());
        assertEquals(2, factory.getAll().size());
    }

    @Test
    void separateFactoriesDoNotShare() {
        CharacterPropertiesFactory other = new CharacterPropertiesFactory();
        assertNotSame(factory.getProperties("Arial", "Red", 12), other.getProperties("Arial", "Red", 12));
    }

    @Test
    void allPropertiesCannotBeModified() {
        factory.getProperties("Arial", "Red", 12);
        assertThrows(UnsupportedOperationException.class, () -> factory.getAll().clear());
    }

    @Test
    void rejectsInvalidProperties() {
        assertThrows(IllegalArgumentException.class, () -> factory.getProperties(null, "Red", 12));
        assertThrows(IllegalArgumentException.class, () -> factory.getProperties(" ", "Red", 12));
        assertThrows(IllegalArgumentException.class, () -> factory.getProperties("Arial", null, 12));
        assertThrows(IllegalArgumentException.class, () -> factory.getProperties("Arial", "Red", 0));
        assertThrows(IllegalArgumentException.class, () -> factory.getProperties("Arial", "Red", -4));
        assertThrows(IllegalArgumentException.class, () -> factory.getProperties("Ari,al", "Red", 12));
        assertThrows(IllegalArgumentException.class, () -> factory.getProperties("Arial", "Re\nd", 12));
        assertEquals(0, factory.getCount());
    }

    @Test
    void toStringListsAllProperties() {
        assertEquals("Arial, Red, 12", factory.getProperties("Arial", "Red", 12).toString());
    }
}
