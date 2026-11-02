package musicstreaming;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SongTest {
    @Test
    void storesMetadata() {
        Song song = new Song(1, "Bohemian Rhapsody", "Queen", "A Night at the Opera", 354);
        assertEquals(1, song.getId());
        assertEquals("Bohemian Rhapsody", song.getTitle());
        assertEquals("Queen", song.getArtist());
        assertEquals("A Night at the Opera", song.getAlbum());
        assertEquals(354, song.getDuration());
    }

    @Test
    void toStringShowsDurationAsMinutesAndSeconds() {
        Song song = new Song(8, "Yesterday", "The Beatles", "Help!", 125);
        assertEquals("#8 \"Yesterday\" by The Beatles on Help! (2:05)", song.toString());
    }

    @Test
    void rejectsInvalidSongs() {
        assertThrows(IllegalArgumentException.class, () -> new Song(null, "T", "A", "B", 10));
        assertThrows(IllegalArgumentException.class, () -> new Song(1, null, "A", "B", 10));
        assertThrows(IllegalArgumentException.class, () -> new Song(1, " ", "A", "B", 10));
        assertThrows(IllegalArgumentException.class, () -> new Song(1, "T", "A", "B", 0));
    }
}
