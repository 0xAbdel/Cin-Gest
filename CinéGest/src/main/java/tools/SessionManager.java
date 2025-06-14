package tools;

import bdd.BddManager;
import models.Session;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    public static void addSession(Session session) {
        String sql = "INSERT INTO sessions (movie_id, room_id, start_time, price, available_tickets) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = BddManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, session.getMovieId());
            pstmt.setInt(2, session.getRoomId());
            pstmt.setTimestamp(3, Timestamp.valueOf(session.getStartTime()));
            pstmt.setDouble(4, session.getPrice());
            pstmt.setInt(5, session.getAvailableTickets());

            pstmt.executeUpdate();
            System.out.println("Séance ajoutée avec succès !");

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la séance : " + e.getMessage());
        }
    }

    public static List<Session> getAllSessions() {
        List<Session> sessions = new ArrayList<>();
        String sql = """
            SELECT s.*, m.title as movie_title, r.name as room_name 
            FROM sessions s
            LEFT JOIN movies m ON s.movie_id = m.id
            LEFT JOIN rooms r ON s.room_id = r.id
            ORDER BY s.start_time ASC
            """;

        try (Connection conn = BddManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Session session = new Session();
                session.setId(rs.getInt("id"));
                session.setMovieId(rs.getInt("movie_id"));
                session.setRoomId(rs.getInt("room_id"));
                session.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                session.setPrice(rs.getDouble("price"));
                session.setAvailableTickets(rs.getInt("available_tickets"));
                session.setSoldTickets(rs.getInt("sold_tickets"));
                sessions.add(session);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des séances : " + e.getMessage());
        }

        return sessions;
    }

    public static List<Session> getTodaysSessions() {
        List<Session> sessions = new ArrayList<>();
        String sql = """
            SELECT s.*, m.title as movie_title, r.name as room_name 
            FROM sessions s
            LEFT JOIN movies m ON s.movie_id = m.id
            LEFT JOIN rooms r ON s.room_id = r.id
            WHERE DATE(s.start_time) = CURDATE()
            ORDER BY s.start_time ASC
            """;

        try (Connection conn = BddManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Session session = new Session();
                session.setId(rs.getInt("id"));
                session.setMovieId(rs.getInt("movie_id"));
                session.setRoomId(rs.getInt("room_id"));
                session.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                session.setPrice(rs.getDouble("price"));
                session.setAvailableTickets(rs.getInt("available_tickets"));
                session.setSoldTickets(rs.getInt("sold_tickets"));
                sessions.add(session);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des séances du jour : " + e.getMessage());
        }

        return sessions;
    }

    public static boolean sellTickets(int sessionId, int quantity) {
        String checkSql = "SELECT available_tickets FROM sessions WHERE id = ?";
        String updateSql = "UPDATE sessions SET available_tickets = available_tickets - ?, sold_tickets = sold_tickets + ? WHERE id = ?";

        try (Connection conn = BddManager.getConnection()) {

            // Vérifier la disponibilité
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

            // Mettre à jour les billets
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, quantity);
                updateStmt.setInt(2, quantity);
                updateStmt.setInt(3, sessionId);

                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Billets vendus avec succès !");
                    return true;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la vente des billets : " + e.getMessage());
        }

        return false;
    }

    public static Session getSessionById(int id) {
        String sql = """
            SELECT s.*, m.title as movie_title, r.name as room_name 
            FROM sessions s
            LEFT JOIN movies m ON s.movie_id = m.id
            LEFT JOIN rooms r ON s.room_id = r.id
            WHERE s.id = ?
            """;

        try (Connection conn = BddManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Session session = new Session();
                session.setId(rs.getInt("id"));
                session.setMovieId(rs.getInt("movie_id"));
                session.setRoomId(rs.getInt("room_id"));
                session.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                session.setPrice(rs.getDouble("price"));
                session.setAvailableTickets(rs.getInt("available_tickets"));
                session.setSoldTickets(rs.getInt("sold_tickets"));
                return session;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la séance : " + e.getMessage());
        }

        return null;
    }

    public static double getDailyRevenue() {
        String sql = """
            SELECT SUM(sold_tickets * price) as revenue 
            FROM sessions 
            WHERE DATE(start_time) = CURDATE()
            """;

        try (Connection conn = BddManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("revenue");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul des revenus : " + e.getMessage());
        }

        return 0.0;
    }
}