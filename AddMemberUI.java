package deliverable4;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddMemberUI extends VBox {
    private TextField customerIdField2;
    private Button addMemberButton;

    private String URL;
    private String USER;
    private String PASSWORD;

    public AddMemberUI(String url, String user, String password) {
        this.URL = url;
        this.USER = user;
        this.PASSWORD = password;

        customerIdField2 = new TextField();
        addMemberButton = new Button("Add Member");
        addMemberButton.setOnAction(this::addMember);

        this.getChildren().addAll(
            new Label("Enter Customer ID:"), customerIdField2,
            addMemberButton
        );
        this.setSpacing(10);
        this.setPadding(new Insets(20));
    }

    private void addMember(ActionEvent event) {
        String customerId = customerIdField2.getText().trim();
        if (!customerId.isEmpty()) {
            try {
                Class.forName("org.mariadb.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.out.println("Mariadb JDBC Driver not found.");
                e.printStackTrace();
                return;
            }
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String query = "INSERT INTO Members (customer_id) VALUES (?)";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, Integer.parseInt(customerId));
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        showAlert("Success", "Member added successfully.");
                        System.out.println("Member added successfully.");
                    } else {
                        showAlert("Error", "Failed to add member.");
                        System.out.println("Failed to add member.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Please enter member ID.");
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
