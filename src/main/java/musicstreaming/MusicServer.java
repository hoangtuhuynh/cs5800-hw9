package musicstreaming;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MusicServer implements SongService {
    private final Map<Integer, Song> songs = new LinkedHashMap<>();
    private final long delayMillis;

    public MusicServer() {
        this(1000);
    }

    public MusicServer(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    public void addSong(Song song) {
        if (song == null) {
            throw new IllegalArgumentException("Song is required");
        }
        songs.put(song.getId(), song);
    }

    @Override
    public Song searchById(Integer songID) {
        simulateNetworkDelay();
        if (songID == null) {
            throw new IllegalArgumentException("Song id is required");
        }
        return songs.get(songID);
    }

    @Override
    public List<Song> searchByTitle(String title) {
        simulateNetworkDelay();
        if (title == null) {
            throw new IllegalArgumentException("Title is required");
        }
        List<Song> matches = new ArrayList<>();
        for (Song song : songs.values()) {
            if (song.getTitle().equalsIgnoreCase(title)) {
                matches.add(song);
            }
        }
        return matches;
    }

    @Override
    public List<Song> searchByAlbum(String album) {
        simulateNetworkDelay();
        if (album == null) {
            throw new IllegalArgumentException("Album is required");
        }
        List<Song> matches = new ArrayList<>();
        for (Song song : songs.values()) {
            if (album.equalsIgnoreCase(song.getAlbum())) {
                matches.add(song);
            }
        }
        return matches;
    }

    private void simulateNetworkDelay() {
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
