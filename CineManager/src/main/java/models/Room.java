package models;

public class Room {
    private int id;
    private String name;
    private int capacity;
    private int movieId;
    private String movieTitle;
    private boolean isAvailable;

    public Room() {}

    public Room(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
        this.isAvailable = true;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    @Override
    public String toString() {
        return name + " (Capacité: " + capacity + " places)";
    }
}