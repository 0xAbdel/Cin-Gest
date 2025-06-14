package tools;

import bdd.BddManager;
import models.Ticket;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TicketManager {

    public static boolean sellTicket(Ticket ticket) {
        String sql = "INSERT INTO tickets (session_id, customer_name, price, ticket_type, purchase_time) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = BddManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ticket.getSessionId());
            pstmt.setString(2, ticket.getCustomerName());
            pstmt.setDouble(3, ticket.getPrice());
            pstmt.setString(4, ticket.getTicketType());
            pstmt.setTimestamp(5, Timestamp.valueOf(ticket.getPurchaseTime()));

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Billet vendu avec succès !");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la vente du billet : " + e.getMessage());
        }

        return false;
    }

    public static List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = """
            SELECT t.*, s.start_time, m.title as movie_title 
            FROM tickets t
            LEFT JOIN sessions s ON t.session_id = s.id
            LEFT JOIN movies m ON s.movie_id = m.id
            ORDER BY t.purchase_time DESC
            """;

        try (Connection conn = BddManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(rs.getInt("id"));
                ticket.setSessionId(rs.getInt("session_id"));
                ticket.setCustomerName(rs.getString("customer_name"));
                ticket.setPrice(rs.getDouble("price"));
                ticket.setTicketType(rs.getString("ticket_type"));
                ticket.setPurchaseTime(rs.getTimestamp("purchase_time").toLocalDateTime());
                tickets.add(ticket);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des billets : " + e.getMessage());
        }

        return tickets;
    }

    public static List<Ticket> getTicketsBySession(int sessionId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE session_id = ? ORDER BY purchase_time DESC";

        try (Connection conn = BddManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sessionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(rs.getInt("id"));
                ticket.setSessionId(rs.getInt("session_id"));
                ticket.setCustomerName(rs.getString("customer_name"));
                ticket.setPrice(rs.getDouble("price"));
                ticket.setTicketType(rs.getString("ticket_type"));
                ticket.setPurchaseTime(rs.getTimestamp("purchase_time").toLocalDateTime());
                tickets.add(ticket);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des billets de la séance : " + e.getMessage());
        }

        return tickets;
    }

    public static double calculateTicketPrice(String ticketType, double basePrice) {
        double price = basePrice;

        switch (ticketType.toLowerCase()) {
            case "étudiant":
                price = basePrice * 0.8; // 20% de réduction
                break;
            case "senior":
                price = basePrice * 0.85; // 15% de réduction
                break;
            case "enfant":
                price = basePrice * 0.7; // 30% de réduction
                break;
            case "standard":
            default:
                price = basePrice;
                break;
        }

        return Math.round(price * 100.0) / 100.0; // Arrondir à 2 décimales
    }

    public static int getTotalTicketsSold() {
        String sql = "SELECT COUNT(*) as total FROM tickets";

        try (Connection conn = BddManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du total des billets : " + e.getMessage());
        }

        return 0;
    }

    public static double getDailyTicketRevenue() {
        String sql = "SELECT SUM(price) as revenue FROM tickets WHERE DATE(purchase_time) = CURDATE()";

        try (Connection conn = BddManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("revenue");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul des revenus des billets : " + e.getMessage());
        }

        return 0.0;
    }

    public static boolean sellMultipleTickets(int sessionId, int quantity, String customerName, String ticketType, double unitPrice) {
        Connection conn = null;
        PreparedStatement ticketStmt = null;
        PreparedStatement sessionStmt = null;

        try {
            conn = BddManager.getConnection();
            conn.setAutoCommit(false); // Transaction

            // Vérifier la disponibilité des billets
            String checkSql = "SELECT available_tickets FROM sessions WHERE id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, sessionId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    int availableTickets = rs.getInt("available_tickets");
                    if (availableTickets < quantity) {
                        System.err.println("Pas assez de billets disponibles !");
                        return false;
                    }
                } else {
                    System.err.println("Séance non trouvée !");
                    return false;
                }
            }

            // Calculer le prix avec réduction
            double finalPrice = calculateTicketPrice(ticketType, unitPrice);

            // Insérer les billets
            String ticketSql = "INSERT INTO tickets (session_id, customer_name, price, ticket_type, purchase_time) VALUES (?, ?, ?, ?, ?)";
            ticketStmt = conn.prepareStatement(ticketSql);

            for (int i = 0; i < quantity; i++) {
                ticketStmt.setInt(1, sessionId);
                ticketStmt.setString(2, customerName);
                ticketStmt.setDouble(3, finalPrice);
                ticketStmt.setString(4, ticketType);
                ticketStmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                ticketStmt.addBatch();
            }
            ticketStmt.executeBatch();

            // Mettre à jour la session
            String sessionSql = "UPDATE sessions SET available_tickets = available_tickets - ?, sold_tickets = sold_tickets + ? WHERE id = ?";
            sessionStmt = conn.prepareStatement(sessionSql);
            sessionStmt.setInt(1, quantity);
            sessionStmt.setInt(2, quantity);
            sessionStmt.setInt(3, sessionId);
            sessionStmt.executeUpdate();

            conn.commit();
            System.out.println(quantity + " billets vendus avec succès !");
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Erreur lors du rollback : " + rollbackEx.getMessage());
                }
            }
            System.err.println("Erreur lors de la vente multiple : " + e.getMessage());
            return false;
        } finally {
            try {
                if (ticketStmt != null) ticketStmt.close();
                if (sessionStmt != null) sessionStmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
            }
        }
    }
}