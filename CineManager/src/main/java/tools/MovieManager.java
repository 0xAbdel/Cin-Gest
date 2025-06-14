package tools;

import bdd.BddManager;
import models.Movie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieManager {

    public static void addMovie(Movie movie) {
        String sql = "INSERT INTO movies (title, genre, duration, description) VALUES (?, ?, ?, ?)";

        try (Connection conn = BddManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, movie.getTitle());
            pstmt.setString(2, movie.getGenre());
            pstmt.setInt(3, movie.getDuration());
            pstmt.setString(4, movie.getDescription());

            pstmt.executeUpdate();
            System.out.println("Film ajouté avec succès !");

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du film : " + e.getMessage());
        }
    }

    public static List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM movies";

        try (Connection conn = BddManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Movie movie = new Movie();
                movie.setId(rs.getInt("id"));
                movie.setTitle(rs.getString("title"));
                movie.setGenre(rs.getString("genre"));
                movie.setDuration(rs.getInt("duration"));
                movie.setDescription(rs.getString("description"));
                movies.add(movie);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des films : " + e.getMessage());
        }

        return movies;
    }

    public static Movie getMovieById(int id) {
        String sql = "SELECT * FROM movies WHERE id = ?";

        try (Connection conn = BddManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Movie movie = new Movie();
                movie.setId(rs.getInt("id"));
                movie.setTitle(rs.getString("title"));
                movie.setGenre(rs.getString("genre"));
                movie.setDuration(rs.getInt("duration"));
                movie.setDescription(rs.getString("description"));
                return movie;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du film : " + e.getMessage());
        }

        return null;
    }
}