package deliverable4;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddBookUI extends VBox {
    private TextField isbnField;
    private TextField titleField;
    private TextField authorField;
    private TextField typeField;
    private TextField priceField;
    private Button addBookButton;

    private String URL;
    private String USER;
    private String PASSWORD;

    public AddBookUI(String url, String user, String password) {
        this.URL = url;
        this.USER = user;
        this.PASSWORD = password;

        isbnField = new TextField();
        titleField = new TextField();
        authorField = new TextField();
        typeField = new TextField();
        priceField = new TextField();
        addBookButton = new Button("Add Book");

        addBookButton.setOnAction(this::addBook);

        this.getChildren().addAll(
            new Label("ISBN:"), isbnField,
            new Label("Title:"), titleField,
            new Label("Author ID:"), authorField,
            new Label("Type ID:"), typeField,
            new Label("Price:"), priceField,
            addBookButton
        );
        this.setSpacing(10);
        this.setPadding(new Insets(20));
    }

    private void addBook(ActionEvent event) {
        String isbn = isbnField.getText().trim();
        String title = titleField.getText().trim();
        String authorId = authorField.getText().trim();
        String typeId = typeField.getText().trim();
        String priceStr = priceField.getText().trim();

        if (!isbn.isEmpty() && !title.isEmpty() && !authorId.isEmpty() && !typeId.isEmpty() && !priceStr.isEmpty()) {
            try {
                Class.forName("org.mariadb.jdbc.Driver");
                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    String query = "INSERT INTO Books (isbn, title, author_id, type_id, price) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setLong(1, Long.parseLong(isbn));
                        stmt.setString(2, title);
                        stmt.setInt(3, Integer.parseInt(authorId));
                        stmt.setInt(4, Integer.parseInt(typeId));
                        stmt.setDouble(5, Double.parseDouble(priceStr));
                        int rowsAffected = stmt.executeUpdate();
                        if (rowsAffected > 0) {
                            showAlert("Success", "Book added successfully.");
                        } else {
                            showAlert("Error", "Failed to add book.");
                        }
                    }
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "Please fill in all fields.");
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
