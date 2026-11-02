package musicstreaming;

public final class Song {
    private final Integer id;
    private final String title;
    private final String artist;
    private final String album;
    private final int duration;

    public Song(Integer id, String title, String artist, String album, int duration) {
        if (id == null) {
            throw new IllegalArgumentException("A song needs an id");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("A song needs a title");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return String.format("#%d \"%s\" by %s on %s (%d:%02d)",
                id, title, artist, album, duration / 60, duration % 60);
    }
}
