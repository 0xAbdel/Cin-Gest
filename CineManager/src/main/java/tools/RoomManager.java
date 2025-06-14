package tools;

import bdd.BddManager;
import models.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomManager {

    public static void addRoom(Room room) {
        String sql = "INSERT INTO rooms (name, capacity, movie_id, is_available) VALUES (?, ?, ?, ?)";

        try (Connection conn = BddManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, room.getName());
            pstmt.setInt(2, room.getCapacity());
            if (room.getMovieId() > 0) {
                pstmt.setInt(3, room.getMovieId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.setBoolean(4, room.isAvailable());

            pstmt.executeUpdate();
            System.out.println("Salle ajoutée avec succès !");

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la salle : " + e.getMessage());
        }
    }

    public static List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = """
            SELECT r.*, m.title as movie_title 
            FROM rooms r 
            LEFT JOIN movies m ON r.movie_id = m.id
            """;

        try (Connection conn = BddManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setName(rs.getString("name"));
                room.setCapacity(rs.getInt("capacity"));
                room.setMovieId(rs.getInt("movie_id"));
                room.setMovieTitle(rs.getString("movie_title"));
                room.setAvailable(rs.getBoolean("is_available"));
                rooms.add(room);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des salles : " + e.getMessage());
        }

        return rooms;
    }

    public static void assignMovieToRoom(int roomId, int movieId) {
        String sql = "UPDATE rooms SET movie_id = ? WHERE id = ?";

        try (Connection conn = BddManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movieId);
            pstmt.setInt(2, roomId);

            pstmt.executeUpdate();
            System.out.println("Film assigné à la salle avec succès !");

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'assignation du film : " + e.getMessage());
        }
    }
}