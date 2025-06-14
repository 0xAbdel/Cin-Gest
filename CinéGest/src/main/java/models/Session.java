package models;

import java.time.LocalDateTime;

public class Session {
    private int id;
    private int movieId;
    private int roomId;
    private LocalDateTime startTime;
    private double price;
    private int availableTickets;
    private int soldTickets;

    public Session() {}

    public Session(int movieId, int roomId, LocalDateTime startTime, double price, int availableTickets) {
        this.movieId = movieId;
        this.roomId = roomId;
        this.startTime = startTime;
        this.price = price;
        this.availableTickets = availableTickets;
        this.soldTickets = 0;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getAvailableTickets() { return availableTickets; }
    public void setAvailableTickets(int availableTickets) { this.availableTickets = availableTickets; }

    public int getSoldTickets() { return soldTickets; }
    public void setSoldTickets(int soldTickets) { this.soldTickets = soldTickets; }

    public double getTotalRevenue() {
        return soldTickets * price;
    }

    @Override
    public String toString() {
        return "Séance ID: " + id + " - " + startTime.toString() + " (" + price + "€)";
    }
}