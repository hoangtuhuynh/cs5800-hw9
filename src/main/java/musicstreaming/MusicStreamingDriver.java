package musicstreaming;

import java.util.function.Supplier;

public class MusicStreamingDriver {
    public static void main(String[] args) {
        MusicServer server = new MusicServer();
        server.addSong(new Song(1, "Bohemian Rhapsody", "Queen", "A Night at the Opera", 354));
        server.addSong(new Song(2, "You're My Best Friend", "Queen", "A Night at the Opera", 170));
        server.addSong(new Song(3, "Hotel California", "Eagles", "Hotel California", 391));
        server.addSong(new Song(4, "Billie Jean", "Michael Jackson", "Thriller", 294));
        server.addSong(new Song(5, "Beat It", "Michael Jackson", "Thriller", 258));
        server.addSong(new Song(6, "Thriller", "Michael Jackson", "Thriller", 357));
        server.addSong(new Song(7, "Smells Like Teen Spirit", "Nirvana", "Nevermind", 301));
        server.addSong(new Song(8, "Yesterday", "The Beatles", "Help!", 125));

        SongService songs = new SongServiceProxy(server);

        System.out.println("=== searchById ===");
        timed("searchById(1)        [first time, goes to server]", () -> songs.searchById(1));
        timed("searchById(1)        [cached]", () -> songs.searchById(1));

        System.out.println("\n=== searchByTitle ===");
        timed("searchByTitle(\"Hotel California\") [first time, goes to server]",
                () -> songs.searchByTitle("Hotel California"));
        timed("searchByTitle(\"hotel california\") [cached, case-insensitive]",
                () -> songs.searchByTitle("hotel california"));

        System.out.println("\n=== searchByAlbum ===");
        timed("searchByAlbum(\"Thriller\") [first time, goes to server]", () -> songs.searchByAlbum("Thriller"));
        timed("searchByAlbum(\"Thriller\") [cached]", () -> songs.searchByAlbum("Thriller"));

        System.out.println("\n=== Songs found by an album search are now cached by id too ===");
        timed("searchById(5)        [cached by the album search]", () -> songs.searchById(5));
        timed("searchById(8)        [never fetched, goes to server]", () -> songs.searchById(8));

        System.out.println("\n=== Missing song ===");
        timed("searchById(99)       [not on server]", () -> songs.searchById(99));
    }

    private static void timed(String label, Supplier<Object> search) {
        long start = System.nanoTime();
        Object result = search.get();
        long millis = (System.nanoTime() - start) / 1_000_000;
        System.out.println(label + " -> " + millis + " ms");
        System.out.println("    " + (result == null ? "not found" : result));
    }
}
