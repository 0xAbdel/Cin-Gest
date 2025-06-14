module com.example.cinemamanager {
    // Modules requis
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    // Exports des packages (suppression des doublons)
    exports com.example.cinemamanager;
    exports models;
    exports tools;
    exports bdd;

    // Opens pour JavaFX (suppression des doublons)
    opens com.example.cinemamanager to javafx.fxml;
    opens models to javafx.base, javafx.fxml;
    opens tools to javafx.fxml;
    opens bdd to javafx.fxml;
}