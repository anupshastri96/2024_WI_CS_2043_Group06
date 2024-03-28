import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.geometry.*;
import javafx.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import javafx.scene.control.Alert.AlertType;

public class BookStoreManagementSystem extends Application {
    private TextField isbnField;
    private TextField titleField;
    private TextField authorField;
    private TextField typeField;
    private TextField priceField;
    private Button addBookButton;

    private TextField authorNameField;
    private Button addAuthorButton;

    private TextField customerNameField;
    private TextField emailField;
    private TextField phoneField;
    private Button addCustomerButton;

    // JDBC URL, username, and password of MySQL server
    private static final String URL = "jdbc:mariadb://localhost:3306/project";
    private static final String USER = "root";
    private static final String PASSWORD = "night1234";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Bookstore Management System");

        // Add Book Section
        Label isbnLabel = new Label("ISBN:");
        isbnField = new TextField();
        Label titleLabel = new Label("Title:");
        titleField = new TextField();
        Label authorLabel = new Label("Author:");
        authorField = new TextField();
        Label typeLabel = new Label("Book Type:");
        typeField = new TextField();
        Label priceLabel = new Label("Price:");
        priceField = new TextField();
        addBookButton = new Button("Add Book");
        addBookButton.setOnAction(this::addBook);

        VBox addBookLayout = new VBox(10);
        addBookLayout.getChildren().addAll(isbnLabel, isbnField, titleLabel, titleField, authorLabel, authorField, typeLabel, typeField, priceLabel, priceField, addBookButton);
        addBookLayout.setPadding(new Insets(20, 20, 20, 20));

        // Add Author Section
        Label authorNameLabel = new Label("Author Name:");
        authorNameField = new TextField();
        addAuthorButton = new Button("Add Author");
        addAuthorButton.setOnAction(this::addAuthor);

        VBox addAuthorLayout = new VBox(10);
        addAuthorLayout.getChildren().addAll(authorNameLabel, authorNameField, addAuthorButton);
        addAuthorLayout.setPadding(new Insets(20, 20, 20, 20));

        // Add Member Section
        Label customerNameLabel = new Label("Customer Name:");
        customerNameField = new TextField();
        Label emailLabel = new Label("Email:");
        emailField = new TextField();
        Label phoneLabel = new Label("Phone:");
        phoneField = new TextField();
        addCustomerButton = new Button("Add Customer");
        addCustomerButton.setOnAction(this::addCustomer);

        VBox addCustomerLayout = new VBox(10);
        addCustomerLayout.getChildren().addAll(customerNameLabel, customerNameField, emailLabel, emailField, phoneLabel, phoneField, addCustomerButton);
        addCustomerLayout.setPadding(new Insets(20, 20, 20, 20));

        TabPane tabPane = new TabPane();
        Tab addBookTab = new Tab("Add Book", addBookLayout);
        Tab addAuthorTab = new Tab("Add Author", addAuthorLayout);
        Tab addCustomerTab = new Tab("Add Customer", addCustomerLayout);
        tabPane.getTabs().addAll(addBookTab, addAuthorTab, addCustomerTab);

        Scene scene = new Scene(tabPane, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addBook(ActionEvent event) {
        String isbn = isbnField.getText().trim();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String type = typeField.getText().trim();
        String priceStr = priceField.getText().trim();
        if (!title.isEmpty() && !author.isEmpty() && !priceStr.isEmpty()) {
            try {
                Class.forName("org.mariadb.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.out.println("Mariadb JDBC Driver not found.");
                e.printStackTrace();
                return;
            }
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String query = "INSERT INTO Books (isbn, title, author_id, type_id, price) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setLong(1, Long.parseLong(isbn));
                    stmt.setString(2, title);
                    int author_id = Integer.parseInt(author);
                    stmt.setInt(3, author_id);
                    int type_id = Integer.parseInt(type);
                    stmt.setInt(4, type_id);
                    double price = Double.parseDouble(priceStr);
                    stmt.setDouble(5, price);
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        showAlert("Success", "Book added successfully.");
                        System.out.println("Book added successfully.");
                    } else {
                        showAlert("Error", "Failed to add book.");
                        System.out.println("Failed to add book.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Please fill in all fields.");
        }
    }

    private void addAuthor(ActionEvent event) {
        String authorName = authorNameField.getText().trim();
        if (!authorName.isEmpty()) {
            try {
                Class.forName("org.mariadb.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.out.println("Mariadb JDBC Driver not found.");
                e.printStackTrace();
                return;
            }
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String query = "INSERT INTO Authors (name) VALUES (?)";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, authorName);
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        showAlert("Success", "Author added successfully.");
                        System.out.println("Author added successfully.");
                    } else {
                        showAlert("Error", "Failed to add author.");
                        System.out.println("Failed to add author.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Please enter author name.");
        }
    }

    private void addCustomer(ActionEvent event) {
        String customerName = customerNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        if (!customerName.isEmpty() && !email.isEmpty() && !phone.isEmpty()) {
            try {
                Class.forName("org.mariadb.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.out.println("Mariadb JDBC Driver not found.");
                e.printStackTrace();
                return;
            }
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String query = "INSERT INTO Customers (name, email, phone) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, customerName);
                    stmt.setString(2, email);
                    stmt.setString(3, phone);
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        showAlert("Success", "Customer added successfully.");
                        System.out.println("Customer added successfully.");
                    } else {
                        showAlert("Error", "Failed to add customer.");
                        System.out.println("Failed to add Customer.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Please fill in all fields.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
