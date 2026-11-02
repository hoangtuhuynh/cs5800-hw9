package musicstreaming;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MusicServerTest {
    private MusicServer server;
    private Song billieJean;
    private Song beatIt;
    private Song thriller;

    @BeforeEach
    void setUp() {
        server = new MusicServer(0);
        billieJean = new Song(4, "Billie Jean", "Michael Jackson", "Thriller", 294);
        beatIt = new Song(5, "Beat It", "Michael Jackson", "Thriller", 258);
        thriller = new Song(6, "Thriller", "Michael Jackson", "Thriller", 357);
        server.addSong(billieJean);
        server.addSong(beatIt);
        server.addSong(thriller);
        server.addSong(new Song(7, "Smells Like Teen Spirit", "Nirvana", "Nevermind", 301));
    }

    @Test
    void searchByIdFindsSong() {
        assertSame(beatIt, server.searchById(5));
    }

    @Test
    void searchByIdReturnsNullWhenMissing() {
        assertNull(server.searchById(99));
    }

    @Test
    void searchByTitleIgnoresCase() {
        assertEquals(List.of(billieJean), server.searchByTitle("billie JEAN"));
    }

    @Test
    void searchByTitleReturnsEmptyWhenMissing() {
        assertTrue(server.searchByTitle("Imagine").isEmpty());
    }

    @Test
    void searchByAlbumReturnsAllSongsInOrder() {
        assertEquals(List.of(billieJean, beatIt, thriller), server.searchByAlbum("Thriller"));
    }

    @Test
    void searchByAlbumReturnsEmptyWhenMissing() {
        assertTrue(server.searchByAlbum("Abbey Road").isEmpty());
    }

    @Test
    void addingSongWithSameIdReplacesIt() {
        Song remaster = new Song(5, "Beat It (Remastered)", "Michael Jackson", "Thriller", 258);
        server.addSong(remaster);
        assertSame(remaster, server.searchById(5));
    }

    @Test
    void rejectsNullInput() {
        assertThrows(IllegalArgumentException.class, () -> server.addSong(null));
        assertThrows(IllegalArgumentException.class, () -> server.searchById(null));
        assertThrows(IllegalArgumentException.class, () -> server.searchByTitle(null));
        assertThrows(IllegalArgumentException.class, () -> server.searchByAlbum(null));
    }

    @Test
    void newServerStartsEmpty() {
        assertTrue(new MusicServer(0).searchByAlbum("Thriller").isEmpty());
    }

    @Test
    void realServerCallsAreSlow() {
        MusicServer slowServer = new MusicServer(200);
        long start = System.nanoTime();
        slowServer.searchById(1);
        long millis = (System.nanoTime() - start) / 1_000_000;
        assertTrue(millis >= 200, "Expected at least 200 ms but took " + millis);
    }
}
