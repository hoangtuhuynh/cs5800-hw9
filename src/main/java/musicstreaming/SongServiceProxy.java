package musicstreaming;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

// Misses (null or empty results) are not cached, so songs added to the server later are still found.
public class SongServiceProxy implements SongService {
    private final SongService server;
    private final Map<Integer, Song> songsById = new HashMap<>();
    private final Map<String, List<Song>> songsByTitle = new HashMap<>();
    private final Map<String, List<Song>> songsByAlbum = new HashMap<>();

    public SongServiceProxy(SongService server) {
        if (server == null) {
            throw new IllegalArgumentException("The proxy needs a server to fetch from");
        }
        this.server = server;
    }

    @Override
    public Song searchById(Integer songID) {
        if (songID == null) {
            throw new IllegalArgumentException("Song id is required");
        }
        Song cached = songsById.get(songID);
        if (cached != null) {
            return cached;
        }
        Song song = server.searchById(songID);
        if (song != null) {
            songsById.put(songID, song);
        }
        return song;
    }

    @Override
    public List<Song> searchByTitle(String title) {
        if (title == null) {
            throw new IllegalArgumentException("Title is required");
        }
        return search(songsByTitle, title.toLowerCase(Locale.ROOT), () -> server.searchByTitle(title));
    }

    @Override
    public List<Song> searchByAlbum(String album) {
        if (album == null) {
            throw new IllegalArgumentException("Album is required");
        }
        return search(songsByAlbum, album.toLowerCase(Locale.ROOT), () -> server.searchByAlbum(album));
    }

    public boolean isCachedById(Integer songID) {
        return songsById.containsKey(songID);
    }

    private List<Song> search(Map<String, List<Song>> cache, String key, Supplier<List<Song>> fetch) {
        List<Song> cached = cache.get(key);
        if (cached != null) {
            return cached;
        }
        List<Song> songs = List.copyOf(fetch.get());
        if (!songs.isEmpty()) {
            cache.put(key, songs);
            for (Song song : songs) {
                songsById.put(song.getId(), song);
            }
        }
        return songs;
    }
}
