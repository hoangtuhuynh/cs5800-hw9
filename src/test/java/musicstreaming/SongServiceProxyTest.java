package musicstreaming;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SongServiceProxyTest {
    private CountingServer server;
    private SongServiceProxy proxy;
    private Song bohemian;
    private Song bestFriend;

    @BeforeEach
    void setUp() {
        server = new CountingServer();
        bohemian = new Song(1, "Bohemian Rhapsody", "Queen", "A Night at the Opera", 354);
        bestFriend = new Song(2, "You're My Best Friend", "Queen", "A Night at the Opera", 170);
        server.addSong(bohemian);
        server.addSong(bestFriend);
        server.addSong(new Song(3, "Hotel California", "Eagles", "Hotel California", 391));
        proxy = new SongServiceProxy(server);
    }

    @Test
    void proxyIsASongService() {
        SongService service = proxy;
        assertSame(bohemian, service.searchById(1));
    }

    @Test
    void searchByIdFetchesFromServerOnlyOnce() {
        assertSame(bohemian, proxy.searchById(1));
        assertSame(bohemian, proxy.searchById(1));
        assertSame(bohemian, proxy.searchById(1));
        assertEquals(1, server.idCalls);
    }

    @Test
    void searchByIdDoesNotFetchUntilAsked() {
        assertEquals(0, server.idCalls);
        assertFalse(proxy.isCachedById(1));
        proxy.searchById(1);
        assertTrue(proxy.isCachedById(1));
    }

    @Test
    void differentIdsAreFetchedSeparately() {
        proxy.searchById(1);
        proxy.searchById(2);
        proxy.searchById(1);
        proxy.searchById(2);
        assertEquals(2, server.idCalls);
    }

    @Test
    void searchByTitleFetchesFromServerOnlyOnce() {
        assertEquals(List.of(bohemian), proxy.searchByTitle("Bohemian Rhapsody"));
        assertEquals(List.of(bohemian), proxy.searchByTitle("Bohemian Rhapsody"));
        assertEquals(1, server.titleCalls);
    }

    @Test
    void titleCacheIgnoresCase() {
        proxy.searchByTitle("Bohemian Rhapsody");
        assertEquals(List.of(bohemian), proxy.searchByTitle("bohemian rhapsody"));
        assertEquals(1, server.titleCalls);
    }

    @Test
    void searchByAlbumFetchesFromServerOnlyOnce() {
        List<Song> expected = List.of(bohemian, bestFriend);
        assertEquals(expected, proxy.searchByAlbum("A Night at the Opera"));
        assertEquals(expected, proxy.searchByAlbum("a night at the opera"));
        assertEquals(1, server.albumCalls);
    }

    @Test
    void songsFoundByAlbumAreCachedById() {
        proxy.searchByAlbum("A Night at the Opera");
        assertSame(bestFriend, proxy.searchById(2));
        assertEquals(0, server.idCalls);
    }

    @Test
    void songsFoundByTitleAreCachedById() {
        proxy.searchByTitle("Hotel California");
        assertEquals("Eagles", proxy.searchById(3).getArtist());
        assertEquals(0, server.idCalls);
    }

    @Test
    void missingSongsAreNotCached() {
        assertNull(proxy.searchById(42));
        assertTrue(proxy.searchByTitle("Imagine").isEmpty());

        Song imagine = new Song(42, "Imagine", "John Lennon", "Imagine", 183);
        server.addSong(imagine);
        assertSame(imagine, proxy.searchById(42));
        assertEquals(List.of(imagine), proxy.searchByTitle("Imagine"));
        assertEquals(2, server.idCalls);
        assertEquals(2, server.titleCalls);
    }

    @Test
    void cachedListsCannotBeModified() {
        List<Song> songs = proxy.searchByAlbum("A Night at the Opera");
        assertThrows(UnsupportedOperationException.class, songs::clear);
        assertEquals(2, proxy.searchByAlbum("A Night at the Opera").size());
    }

    @Test
    void rejectsNullInputWithoutCallingServer() {
        assertThrows(IllegalArgumentException.class, () -> proxy.searchById(null));
        assertThrows(IllegalArgumentException.class, () -> proxy.searchByTitle(null));
        assertThrows(IllegalArgumentException.class, () -> proxy.searchByAlbum(null));
        assertEquals(0, server.idCalls + server.titleCalls + server.albumCalls);
    }

    @Test
    void proxyNeedsAServer() {
        assertThrows(IllegalArgumentException.class, () -> new SongServiceProxy(null));
    }

    @Test
    void cachedLookupsSkipTheSlowServer() {
        MusicServer slowServer = new MusicServer(500);
        slowServer.addSong(bohemian);
        SongService cachedService = new SongServiceProxy(slowServer);

        cachedService.searchById(1);
        cachedService.searchByTitle("Bohemian Rhapsody");
        cachedService.searchByAlbum("A Night at the Opera");

        assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            cachedService.searchById(1);
            cachedService.searchByTitle("Bohemian Rhapsody");
            cachedService.searchByAlbum("A Night at the Opera");
        });
    }

    private static class CountingServer implements SongService {
        private final MusicServer server = new MusicServer(0);
        int idCalls;
        int titleCalls;
        int albumCalls;

        void addSong(Song song) {
            server.addSong(song);
        }

        @Override
        public Song searchById(Integer songID) {
            idCalls++;
            return server.searchById(songID);
        }

        @Override
        public List<Song> searchByTitle(String title) {
            titleCalls++;
            return server.searchByTitle(title);
        }

        @Override
        public List<Song> searchByAlbum(String album) {
            albumCalls++;
            return server.searchByAlbum(album);
        }
    }
}
