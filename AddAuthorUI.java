package deliverable4;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddAuthorUI extends VBox {
    private TextField authorNameField;
    private Button addAuthorButton;

    private String URL;
    private String USER;
    private String PASSWORD;

    public AddAuthorUI(String url, String user, String password) {
        this.URL = url;
        this.USER = user;
        this.PASSWORD = password;

        authorNameField = new TextField();
        addAuthorButton = new Button("Add Author");

        addAuthorButton.setOnAction(this::addAuthor);

        this.getChildren().addAll(
            new Label("Author Name:"), authorNameField,
            addAuthorButton
        );
        this.setSpacing(10);
        this.setPadding(new Insets(20));
    }

    private void addAuthor(ActionEvent event) {
        String authorName = authorNameField.getText().trim();
        if (!authorName.isEmpty()) {
            try {
                Class.forName("org.mariadb.jdbc.Driver");
                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    String query = "INSERT INTO Authors (name) VALUES (?)";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, authorName);
                        int rowsAffected = stmt.executeUpdate();
                        if (rowsAffected > 0) {
                            showAlert("Success", "Author added successfully.");
                        } else {
                            showAlert("Error", "Failed to add author.");
                        }
                    }
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "Please enter author name.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
