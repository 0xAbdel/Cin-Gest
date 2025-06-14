package com.example.cinemamanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import models.*;
import tools.*;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // Dashboard elements
    @FXML private Label totalRoomsLabel;
    @FXML private Label totalMoviesLabel;
    @FXML private Label totalTicketsLabel;
    @FXML private Label dailyRevenueLabel;
    @FXML private TableView<Room> dashboardRoomsTable;
    @FXML private TableColumn<Room, String> dashRoomNameCol;
    @FXML private TableColumn<Room, Integer> dashRoomCapacityCol;
    @FXML private TableColumn<Room, String> dashMovieTitleCol;

    // Room Management elements
    @FXML private TextField roomNameField;
    @FXML private TextField roomCapacityField;
    @FXML private ComboBox<Movie> roomMovieCombo;
    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, String> roomNameCol;
    @FXML private TableColumn<Room, Integer> roomCapacityCol;
    @FXML private TableColumn<Room, String> movieTitleCol;
    @FXML private TableColumn<Room, Boolean> roomAvailableCol;

    // Movie Management elements
    @FXML private TextField movieTitleField;
    @FXML private TextField movieGenreField;
    @FXML private TextField movieDurationField;
    @FXML private TextArea movieDescriptionArea;
    @FXML private TableView<Movie> moviesTable;
    @FXML private TableColumn<Movie, String> movieGenreCol;
    @FXML private TableColumn<Movie, Integer> movieDurationCol;

    // Session Management elements
    @FXML private ComboBox<Movie> sessionMovieCombo;
    @FXML private ComboBox<Room> sessionRoomCombo;
    @FXML private DatePicker sessionDatePicker;
    @FXML private TextField sessionTimeField;
    @FXML private TextField sessionPriceField;
    @FXML private TextField sessionTicketsField;
    @FXML private TableView<Session> sessionsTable;
    @FXML private TableColumn<Session, Integer> sessionIdCol;
    @FXML private TableColumn<Session, String> sessionStartTimeCol;
    @FXML private TableColumn<Session, Double> sessionPriceCol;
    @FXML private TableColumn<Session, Integer> sessionAvailableCol;
    @FXML private TableColumn<Session, Integer> sessionSoldCol;

    // Ticketing elements
    @FXML private ComboBox<Session> ticketSessionCombo;
    @FXML private TextField ticketQuantityField;
    @FXML private ComboBox<String> ticketTypeCombo;
    @FXML private TextField customerNameField;
    @FXML private Label ticketPriceLabel;
    @FXML private Label totalPriceLabel;

    // Data lists
    private ObservableList<Room> roomsList = FXCollections.observableArrayList();
    private ObservableList<Movie> moviesList = FXCollections.observableArrayList();
    private ObservableList<Session> sessionsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTables();
        initializeComboBoxes();
        setupEventHandlers();
        loadData();
        updateDashboard();
    }

    private void initializeTables() {
        // Dashboard table
        if (dashRoomNameCol != null) {
            dashRoomNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            dashRoomCapacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));
            dashMovieTitleCol.setCellValueFactory(new PropertyValueFactory<>("movieTitle"));
        }

        // Rooms table
        if (roomNameCol != null) {
            roomNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            roomCapacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));
            movieTitleCol.setCellValueFactory(new PropertyValueFactory<>("movieTitle"));
            roomAvailableCol.setCellValueFactory(new PropertyValueFactory<>("available"));
        }

        // Movies table
        if (movieTitleCol != null) {
            movieTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
            movieGenreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
            movieDurationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        }

        // Sessions table
        if (sessionIdCol != null) {
            sessionIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            sessionStartTimeCol.setCellValueFactory(cellData -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                return new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getStartTime().format(formatter)
                );
            });
            sessionPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
            sessionAvailableCol.setCellValueFactory(new PropertyValueFactory<>("availableTickets"));
            sessionSoldCol.setCellValueFactory(new PropertyValueFactory<>("soldTickets"));
        }
    }

    private void initializeComboBoxes() {
        // Ticket types
        if (ticketTypeCombo != null) {
            ticketTypeCombo.setItems(FXCollections.observableArrayList("Standard", "Étudiant", "Senior", "Enfant"));
            ticketTypeCombo.setValue("Standard");
        }
    }

    private void setupEventHandlers() {
        // Mettre à jour le prix quand le type de billet change
        if (ticketTypeCombo != null) {
            ticketTypeCombo.setOnAction(e -> updateTicketPrice());
        }

        // Mettre à jour le prix quand la quantité change
        if (ticketQuantityField != null) {
            ticketQuantityField.textProperty().addListener((obs, oldVal, newVal) -> updateTicketPrice());
        }

        // Mettre à jour les informations quand une séance est sélectionnée
        if (ticketSessionCombo != null) {
            ticketSessionCombo.setOnAction(e -> updateSessionInfo());
        }
    }

    private void loadData() {
        // Load movies
        moviesList.clear();
        moviesList.addAll(MovieManager.getAllMovies());
        if (moviesTable != null) {
            moviesTable.setItems(moviesList);
        }

        // Load rooms
        roomsList.clear();
        roomsList.addAll(RoomManager.getAllRooms());
        if (roomsTable != null) {
            roomsTable.setItems(roomsList);
        }
        if (dashboardRoomsTable != null) {
            dashboardRoomsTable.setItems(roomsList);
        }

        // Load sessions
        sessionsList.clear();
        sessionsList.addAll(SessionManager.getAllSessions());
        if (sessionsTable != null) {
            sessionsTable.setItems(sessionsList);
        }

        // Update combo boxes
        updateComboBoxes();
    }

    private void updateComboBoxes() {
        if (roomMovieCombo != null) {
            roomMovieCombo.setItems(moviesList);
        }
        if (sessionMovieCombo != null) {
            sessionMovieCombo.setItems(moviesList);
        }
        if (sessionRoomCombo != null) {
            sessionRoomCombo.setItems(roomsList);
        }
        if (ticketSessionCombo != null) {
            ticketSessionCombo.setItems(sessionsList);
        }
    }

    private void updateDashboard() {
        if (totalRoomsLabel != null) {
            totalRoomsLabel.setText(String.valueOf(roomsList.size()));
        }
        if (totalMoviesLabel != null) {
            totalMoviesLabel.setText(String.valueOf(moviesList.size()));
        }

        // Calculate total available tickets
        int totalTickets = sessionsList.stream()
                .mapToInt(Session::getAvailableTickets)
                .sum();
        if (totalTicketsLabel != null) {
            totalTicketsLabel.setText(String.valueOf(totalTickets));
        }

        // Calculate daily revenue
        double dailyRevenue = SessionManager.getDailyRevenue();
        if (dailyRevenueLabel != null) {
            dailyRevenueLabel.setText(String.format("%.2f €", dailyRevenue));
        }
    }

    // ================== MOVIE MANAGEMENT ==================

    @FXML
    private void addMovie() {
        try {
            String title = movieTitleField.getText().trim();
            String genre = movieGenreField.getText().trim();
            int duration = Integer.parseInt(movieDurationField.getText().trim());
            String description = movieDescriptionArea.getText().trim();

            if (title.isEmpty() || genre.isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs obligatoires.");
                return;
            }

            Movie movie = new Movie(title, genre, duration, description);
            MovieManager.addMovie(movie);

            clearMovieFields();
            loadData();
            showInfo("Succès", "Film ajouté avec succès !");

        } catch (NumberFormatException e) {
            showAlert("Erreur", "La durée doit être un nombre valide.");
        }
    }

    @FXML
    private void clearMovieFields() {
        if (movieTitleField != null) movieTitleField.clear();
        if (movieGenreField != null) movieGenreField.clear();
        if (movieDurationField != null) movieDurationField.clear();
        if (movieDescriptionArea != null) movieDescriptionArea.clear();
    }

    // ================== ROOM MANAGEMENT ==================

    @FXML
    private void addRoom() {
        try {
            String name = roomNameField.getText().trim();
            int capacity = Integer.parseInt(roomCapacityField.getText().trim());

            if (name.isEmpty()) {
                showAlert("Erreur", "Veuillez entrer un nom pour la salle.");
                return;
            }

            Room room = new Room(name, capacity);

            Movie selectedMovie = roomMovieCombo.getValue();
            if (selectedMovie != null) {
                room.setMovieId(selectedMovie.getId());
            }

            RoomManager.addRoom(room);

            clearRoomFields();
            loadData();
            showInfo("Succès", "Salle ajoutée avec succès !");

        } catch (NumberFormatException e) {
            showAlert("Erreur", "La capacité doit être un nombre valide.");
        }
    }

    @FXML
    private void clearRoomFields() {
        if (roomNameField != null) roomNameField.clear();
        if (roomCapacityField != null) roomCapacityField.clear();
        if (roomMovieCombo != null) roomMovieCombo.setValue(null);
    }

    // ================== SESSION MANAGEMENT ==================

    @FXML
    private void addSession() {
        try {
            Movie selectedMovie = sessionMovieCombo.getValue();
            Room selectedRoom = sessionRoomCombo.getValue();

            if (selectedMovie == null || selectedRoom == null) {
                showAlert("Erreur", "Veuillez sélectionner un film et une salle.");
                return;
            }

            if (sessionDatePicker.getValue() == null || sessionTimeField.getText().trim().isEmpty()) {
                showAlert("Erreur", "Veuillez remplir la date et l'heure.");
                return;
            }

            // Parse time
            LocalTime time = LocalTime.parse(sessionTimeField.getText().trim());
            LocalDateTime startTime = LocalDateTime.of(sessionDatePicker.getValue(), time);

            double price = Double.parseDouble(sessionPriceField.getText().trim());
            int availableTickets = Integer.parseInt(sessionTicketsField.getText().trim());

            // Vérifier que le nombre de billets ne dépasse pas la capacité de la salle
            if (availableTickets > selectedRoom.getCapacity()) {
                showAlert("Erreur", "Le nombre de billets ne peut pas dépasser la capacité de la salle (" + selectedRoom.getCapacity() + " places).");
                return;
            }

            Session session = new Session(selectedMovie.getId(), selectedRoom.getId(), startTime, price, availableTickets);
            SessionManager.addSession(session);

            clearSessionFields();
            loadData();
            showInfo("Succès", "Séance programmée avec succès !");

        } catch (Exception e) {
            showAlert("Erreur", "Veuillez vérifier tous les champs : " + e.getMessage());
        }
    }

    @FXML
    private void clearSessionFields() {
        if (sessionMovieCombo != null) sessionMovieCombo.setValue(null);
        if (sessionRoomCombo != null) sessionRoomCombo.setValue(null);
        if (sessionDatePicker != null) sessionDatePicker.setValue(null);
        if (sessionTimeField != null) sessionTimeField.clear();
        if (sessionPriceField != null) sessionPriceField.clear();
        if (sessionTicketsField != null) sessionTicketsField.clear();
    }

    // ================== TICKETING ==================

    @FXML
    private void sellTickets() {
        try {
            Session selectedSession = ticketSessionCombo.getValue();
            if (selectedSession == null) {
                showAlert("Erreur", "Veuillez sélectionner une séance.");
                return;
            }

            int quantity = Integer.parseInt(ticketQuantityField.getText().trim());
            if (quantity <= 0) {
                showAlert("Erreur", "La quantité doit être supérieure à 0.");
                return;
            }

            if (quantity > selectedSession.getAvailableTickets()) {
                showAlert("Erreur", "Pas assez de billets disponibles. Disponibles: " + selectedSession.getAvailableTickets());
                return;
            }

            String customerName = customerNameField.getText().trim();
            if (customerName.isEmpty()) {
                customerName = "Client anonyme";
            }

            String ticketType = ticketTypeCombo.getValue();
            double unitPrice = selectedSession.getPrice();

            // Vendre les billets
            boolean success = TicketManager.sellMultipleTickets(
                    selectedSession.getId(),
                    quantity,
                    customerName,
                    ticketType,
                    unitPrice
            );

            if (success) {
                clearTicketFields();
                loadData();
                updateDashboard();

                double finalPrice = TicketManager.calculateTicketPrice(ticketType, unitPrice);
                double totalAmount = finalPrice * quantity;

                showInfo("Vente Réussie",
                        String.format("Billets vendus avec succès !\n" +
                                        "Quantité: %d\n" +
                                        "Prix unitaire: %.2f €\n" +
                                        "Total: %.2f €",
                                quantity, finalPrice, totalAmount));
            } else {
                showAlert("Erreur", "Erreur lors de la vente des billets.");
            }

        } catch (NumberFormatException e) {
            showAlert("Erreur", "La quantité doit être un nombre valide.");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la vente : " + e.getMessage());
        }
    }

    @FXML
    private void clearTicketFields() {
        if (ticketSessionCombo != null) ticketSessionCombo.setValue(null);
        if (ticketQuantityField != null) ticketQuantityField.setText("1");
        if (customerNameField != null) customerNameField.clear();
        if (ticketTypeCombo != null) ticketTypeCombo.setValue("Standard");
        if (ticketPriceLabel != null) ticketPriceLabel.setText("0.00 €");
        if (totalPriceLabel != null) totalPriceLabel.setText("0.00 €");
    }

    private void updateTicketPrice() {
        Session selectedSession = ticketSessionCombo.getValue();
        if (selectedSession == null) return;

        String ticketType = ticketTypeCombo.getValue();
        if (ticketType == null) ticketType = "Standard";

        try {
            int quantity = Integer.parseInt(ticketQuantityField.getText().trim());
            double basePrice = selectedSession.getPrice();
            double unitPrice = TicketManager.calculateTicketPrice(ticketType, basePrice);
            double totalPrice = unitPrice * quantity;

            if (ticketPriceLabel != null) {
                ticketPriceLabel.setText(String.format("%.2f €", unitPrice));
            }
            if (totalPriceLabel != null) {
                totalPriceLabel.setText(String.format("%.2f €", totalPrice));
            }
        } catch (NumberFormatException e) {
            if (ticketPriceLabel != null) ticketPriceLabel.setText("0.00 €");
            if (totalPriceLabel != null) totalPriceLabel.setText("0.00 €");
        }
    }

    private void updateSessionInfo() {
        Session selectedSession = ticketSessionCombo.getValue();
        if (selectedSession == null) {
            if (ticketPriceLabel != null) ticketPriceLabel.setText("0.00 €");
            if (totalPriceLabel != null) totalPriceLabel.setText("0.00 €");
            return;
        }

        updateTicketPrice();
    }

    // ================== UTILITY METHODS ==================

    @FXML
    private void refreshData() {
        loadData();
        updateDashboard();
        showInfo("Actualisation", "Données actualisées avec succès !");
    }

    @FXML
    private void exportPDF() {
        // TODO: Implémenter l'export PDF avec iText
        showInfo("Export PDF", "Fonctionnalité à implémenter : Export des données en PDF");
    }

    @FXML
    private void exportCSV() {
        // TODO: Implémenter l'export CSV
        showInfo("Export CSV", "Fonctionnalité à implémenter : Export des données en CSV");
    }

    @FXML
    private void quit() {
        System.exit(0);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}