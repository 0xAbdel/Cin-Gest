package models;

import java.time.LocalDateTime;

public class Ticket {
    private int id;
    private int sessionId;
    private String customerName;
    private double price;
    private LocalDateTime purchaseTime;
    private String ticketType; // standard, étudiant, senior

    public Ticket() {}

    public Ticket(int sessionId, String customerName, double price, String ticketType) {
        this.sessionId = sessionId;
        this.customerName = customerName;
        this.price = price;
        this.ticketType = ticketType;
        this.purchaseTime = LocalDateTime.now();
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public LocalDateTime getPurchaseTime() { return purchaseTime; }
    public void setPurchaseTime(LocalDateTime purchaseTime) { this.purchaseTime = purchaseTime; }

    public String getTicketType() { return ticketType; }
    public void setTicketType(String ticketType) { this.ticketType = ticketType; }
}