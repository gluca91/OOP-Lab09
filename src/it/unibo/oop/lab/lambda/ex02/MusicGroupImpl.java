package it.unibo.oop.lab.lambda.ex02;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return songs.stream().map(i -> i.getSongName()).sorted();
    }

    @Override
    public Stream<String> albumNames() {
        return albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        return albums.keySet().stream().filter(i -> albums.get(i).equals(Integer.valueOf(year)));
    }

    @Override
    public int countSongs(final String albumName) {
        return (int) (songs.stream().filter(i -> i.getAlbumName().equals(Optional.of(albumName))).count());
        // SCORRETTO: se il nome album fosse-> "" ritornerebbe-> true
//        return (int) (songs.stream().filter(i -> i.getAlbumName().orElse("").equals(albumName)).count());
        // SCORRETTO: se è-> Optional.empty(), il metodo-> get() lancia eccezione->
        // NoSuchElementException
//        return (int) (songs.stream().filter(i -> i.getAlbumName().get().equals(albumName)).count());
    }

    @Override
    public int countSongsInNoAlbum() {
        return (int) (songs.stream().filter(i -> i.getAlbumName().equals(Optional.empty())).count());
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
//        songs.stream().filter(i -> i.getAlbumName().equals(Optional.of(albumName))).map(i -> i.getDuration())
//                .reduce((i, j) -> i = i + j).map(i -> i / countSongs(albumName));
        return OptionalDouble.of(songs.stream().filter(i -> i.getAlbumName().equals(Optional.of(albumName)))
                .map(i -> i.getDuration()).reduce((i, j) -> i = i + j).map(i -> i / countSongs(albumName)).get());
    }

    @Override
    public Optional<String> longestSong() {

//        return Optional.of(songs.stream().max(new Comparator<Song>() {
//            @Override
//            public int compare(final Song o1, final Song o2) {
//                return Double.compare(o1.getDuration(), o2.getDuration());
//            }
//        }).get().getSongName());

        return Optional.of(
                songs.stream().max((o1, o2) -> Double.compare(o1.getDuration(), o2.getDuration())).get().getSongName());
    }

    @Override
    public Optional<String> longestAlbum() {
//        return albums.keySet().stream().max(new Comparator<String>() {
//            @Override
//            public int compare(final String o1, final String o2) {
//                final Double a = songs.stream().filter(i -> i.getAlbumName().equals(Optional.of(o1)))
//                        .map(i -> i.getDuration()).reduce((j, k) -> j = j + k).get();
//                final Double b = songs.stream().filter(i -> i.getAlbumName().equals(Optional.of(o2)))
//                        .map(i -> i.getDuration()).reduce((j, k) -> j = j + k).get();
//                return a.compareTo(b);
//            }
//        });
//        return albums.keySet().stream()
//                .max((o1, o2) -> songs.stream().filter(i -> i.getAlbumName().equals(Optional.of(o1)))
//                        .map(i -> i.getDuration()).reduce((j, k) -> j = j + k).get()
//                        .compareTo(songs.stream().filter(i -> i.getAlbumName().equals(Optional.of(o2)))
//                                .map(i -> i.getDuration()).reduce((j, k) -> j = j + k).get()));
        return albums.keySet().stream().max((o1, o2) -> albumLength(o1).compareTo(albumLength(o2)));
    }

    private Double albumLength(final String albumName) {
        return songs.stream().filter(i -> i.getAlbumName().equals(Optional.of(albumName))).map(i -> i.getDuration())
                .reduce((j, k) -> j = j + k).get();
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
