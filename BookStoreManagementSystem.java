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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Text;

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

    private TextField searchField;
    private RadioButton titleRadioButton;
    private RadioButton isbnRadioButton;
    private TextArea searchResultArea;
    private Button searchButton;

    private TextField customerIdField2;

    private TextField customerIdField;
    private Button addMemberButton;

    private TextField bookIdsField;
    private TextField isbnsField;
    private CheckBox memberCheckBox;
    private TextArea billTextArea;
    private Button purchaseButton;



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

        // Add Customer Section
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

        // Search Section
        Label searchLabel = new Label("Search for a book:");
    
        searchField = new TextField();
        searchField.setPromptText("Enter search text");

        titleRadioButton = new RadioButton("By Title");
        isbnRadioButton = new RadioButton("By ISBN");

        ToggleGroup searchToggleGroup = new ToggleGroup();
        titleRadioButton.setToggleGroup(searchToggleGroup);
        isbnRadioButton.setToggleGroup(searchToggleGroup);

        searchButton = new Button("Search");
        searchButton.setOnAction(this::search);

        searchResultArea = new TextArea();
        searchResultArea.setEditable(false);
        searchResultArea.setWrapText(true);
        searchResultArea.setPrefHeight(200);

        VBox searchLayout = new VBox(10);
        searchLayout.getChildren().addAll(searchLabel, searchField, titleRadioButton, isbnRadioButton, searchButton, searchResultArea);
        searchLayout.setPadding(new Insets(20, 20, 20, 20));

        // Members section
        Label customerIdLabel2 = new Label("Enter Customer ID:");
        customerIdField2 = new TextField();
        addMemberButton = new Button("Add Member");
        addMemberButton.setOnAction(this::addMember);

        VBox membersLayout = new VBox(10);
        membersLayout.getChildren().addAll(customerIdLabel2, customerIdField2, addMemberButton);
        membersLayout.setPadding(new Insets(20, 20, 20, 20));

        // Purchase section
        Label purchaseLabel = new Label("Purchase Books:");
        Label customerIdLabel = new Label("Enter Customer ID:");
        customerIdField = new TextField();
        memberCheckBox = new CheckBox("Use your points for discount");
        Label bookIdsLabel = new Label("Enter Book IDs (comma separated):");
        bookIdsField = new TextField();
        Label isbnsLabel = new Label("Enter ISBNs (comma separated):");
        isbnsField = new TextField();
        purchaseButton = new Button("Purchase");
        purchaseButton.setOnAction(this::purchase);

        billTextArea = new TextArea();
        billTextArea.setEditable(false);
        billTextArea.setWrapText(true);
        billTextArea.setPrefHeight(200);

        VBox purchaseLayout = new VBox(10);
        purchaseLayout.getChildren().addAll(customerIdLabel, customerIdField, memberCheckBox, bookIdsLabel, bookIdsField, isbnsLabel, isbnsField, purchaseButton, billTextArea);
        purchaseLayout.setPadding(new Insets(20, 20, 20, 20));

        TabPane tabPane = new TabPane();
        Tab addBookTab = new Tab("Add Book", addBookLayout);
        Tab addAuthorTab = new Tab("Add Author", addAuthorLayout);
        Tab addCustomerTab = new Tab("Add Customer", addCustomerLayout);
        Tab searchTab = new Tab("Search", searchLayout);
        Tab membersTab = new Tab("Members", membersLayout);
        Tab purchaseTab = new Tab("Purchase", purchaseLayout);
        tabPane.getTabs().addAll(addBookTab, addAuthorTab, addCustomerTab, searchTab, membersTab, purchaseTab);

        Scene scene = new Scene(tabPane, 600, 400);
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

    private void search(ActionEvent event) {
        String searchText = searchField.getText().trim();
        boolean searchByTitle = titleRadioButton.isSelected();
        boolean searchByISBN = isbnRadioButton.isSelected();
    
        if ((!searchByTitle && !searchByISBN) || searchText.isEmpty()) {
            showAlert("Error", "Please select search criteria (Title or ISBN) and enter search text.");
            return;
        }
    
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String query;
                if (searchByTitle) {
                    query = "SELECT title as name, price as price, count(*) as copies FROM Books WHERE title LIKE ? AND purchased = false GROUP BY isbn";
                } else {
                    query = "SELECT title as name, price as price, count(*) as copies FROM Books WHERE isbn = ? AND purchased = false";
                }
    
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    if (searchByTitle) {
                        stmt.setString(1, "%" + searchText + "%");
                    } else {
                        stmt.setLong(1, Long.parseLong(searchText));
                    }
    
                    ResultSet rs = stmt.executeQuery();
                    List<String> resultsArray = new ArrayList<>();
                    while (rs.next()) {
                        String title = rs.getString("name");
                        double price = rs.getDouble("price");
                        int copies = rs.getInt("copies");
                        String result = "Title: " + title + "\nPrice: " + price + "\nCopies: " + copies;
                        resultsArray.add(result);
                    }
                    displaySearchResults(resultsArray);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void displaySearchResults(List<String> results) {
        StringBuilder result = new StringBuilder();
        for (String r : results) {
            result.append(r).append("\n\n");
        }
        searchResultArea.setText(result.toString());
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

    private void purchase(ActionEvent event) {
        String customerId = customerIdField.getText().trim();
        boolean isSelected = memberCheckBox.isSelected();
        String bookIds = bookIdsField.getText().trim();
        String isbns = isbnsField.getText().trim();
        double totalPrice = 0;

        // Split book IDs and ISBNs
        String[] bookIdArray = bookIds.split(",");
        String[] isbnArray = isbns.split(",");

        if (customerId.isEmpty() || bookIds.isEmpty() || isbns.isEmpty()) {
            System.out.println("Please fill in all fields.");
            return;
        }

        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Mariadb JDBC Driver not found.");
            e.printStackTrace();
            return;
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            // Check if the book IDs and ISBNs exist
            if (!checkBooksExist(conn, bookIdArray, isbnArray)) {
                System.out.println("One or more books do not exist.");
                return;
            }

            String isMemberQuery = "SELECT * FROM Members natural join Customers WHERE customer_id = ?";
            try (PreparedStatement isMemberStmt = conn.prepareStatement(isMemberQuery)) {
                isMemberStmt.setInt(1, Integer.parseInt(customerId));
                try (ResultSet isMemberResult = isMemberStmt.executeQuery()) {
                    boolean isMember = isMemberResult.isBeforeFirst();
                    isMemberResult.next();

                    if (isMember){
                        String customerNameFromDB = isMemberResult.getString("name");
                        int points = isMemberResult.getInt("points");

                        // Get today's date and time
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String dateTime = now.format(formatter);

                        // Generate bill header
                        StringBuilder bill = new StringBuilder();
                        bill.append("Customer Name: ").append(customerNameFromDB).append("\n");
                        bill.append("Date and Time: ").append(dateTime).append("\n\n");
                        bill.append("Price Breakdown:\n");

                        // Calculate and display price breakdown for each book
                        for (int i = 0; i < bookIdArray.length; i++) {
                            int bookId = Integer.parseInt(bookIdArray[i]);
                            long isbn = Long.parseLong(isbnArray[i]);

                            String priceQuery = "SELECT price, added_percentage FROM Books NATURAL JOIN BookTypes WHERE book_id = ? AND isbn = ? and purchased = false";
                            try (PreparedStatement priceStmt = conn.prepareStatement(priceQuery)) {
                                priceStmt.setInt(1, bookId);
                                priceStmt.setLong(2, isbn);
                                try (ResultSet priceResult = priceStmt.executeQuery()) {
                                    if (priceResult.next()) {
                                        double basePrice = priceResult.getDouble("price");
                                        double addedPercentage = priceResult.getDouble("added_percentage");

                                        double totalPriceForBook = basePrice * (1 + addedPercentage / 100);
                                        totalPrice += totalPriceForBook;

                                        // Display price breakdown for the book
                                        bill.append("Book ID: ").append(bookId).append(", ISBN: ").append(isbn).append("\n");
                                        bill.append("Base Price: ").append(basePrice).append("\n");
                                        bill.append("Added Percentage: ").append(addedPercentage).append("%\n");
                                        bill.append("Total Price for Book: ").append(totalPriceForBook).append("\n\n");
                                    }
                                }
                            }
                        }

                        // Display total price
                        bill.append("Total Price: ").append(totalPrice).append("\n");

                        // Check if the customer is a member and wants to use points for discount
                        if (isMember && isSelected) {
                            // Calculate discount
                            int deduction = 0;
                            int discountPoints = points;
                            double discountAmount = 0;
                            if (discountPoints >= 1800) {
                                deduction = 1800;
                                discountAmount = 20;
                            } else if (discountPoints >= 1400 && discountPoints < 1800) {
                                deduction = 1400;
                                discountAmount = 15;
                            } else if (discountPoints >= 1000 && discountPoints < 1400) {
                                deduction = 1000;
                                discountAmount = 10;
                            } else if (discountPoints >= 600 && discountPoints < 1000) {
                                deduction = 600;
                                discountAmount = 5;
                            }

                            // Update points in the members table
                            String updatePointsQuery = "UPDATE Members SET points = points - ? WHERE customer_id = ?";
                            try (PreparedStatement updatePointsStmt = conn.prepareStatement(updatePointsQuery)) {
                                updatePointsStmt.setInt(1, deduction);
                                updatePointsStmt.setInt(2, Integer.parseInt(customerId));
                                updatePointsStmt.executeUpdate();
                            }

                            // Apply discount and update total price
                            totalPrice -= discountAmount;
                            bill.append("Discount Applied: $").append(discountAmount).append("\n");
                        }

                        // Display final price owed
                        bill.append("Final Price Owed: ").append(totalPrice).append("\n");

                        // Update points in the members table if applicable
                        if (isMember) {
                            String updatePointsQuery = "UPDATE Members SET points = points + ? WHERE customer_id = ?";
                            try (PreparedStatement updatePointsStmt = conn.prepareStatement(updatePointsQuery)) {
                                updatePointsStmt.setInt(1, (int) Math.floor(totalPrice / 10));
                                updatePointsStmt.setInt(2, Integer.parseInt(customerId));
                                updatePointsStmt.executeUpdate();
                            }
                        }

                        // Display bill in the TextArea
                        billTextArea.setText(bill.toString());
                    } else {
                        System.out.println("Customer not found.");
                    }
                }
            }

            // After displaying the bill in the TextArea
            String insertPurchaseQuery = "INSERT INTO Purchases (customer_id, book_id, isbn, price_paid, purchase_date) VALUES (?, ?, ?, ?, NOW())";
            try (PreparedStatement insertPurchaseStmt = conn.prepareStatement(insertPurchaseQuery)) {
                for (int i = 0; i < bookIdArray.length; i++) {
                    int bookId = Integer.parseInt(bookIdArray[i]);
                    long isbn = Long.parseLong(isbnArray[i]);
                    insertPurchaseStmt.setInt(1, Integer.parseInt(customerId));
                    insertPurchaseStmt.setInt(2, bookId);
                    insertPurchaseStmt.setLong(3, isbn);
                    insertPurchaseStmt.setDouble(4, totalPrice);
                    insertPurchaseStmt.addBatch();
                }
                insertPurchaseStmt.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean checkBooksExist(Connection conn, String[] bookIdArray, String[] isbnArray) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM Books WHERE book_id = ? OR isbn = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < bookIdArray.length; i++) {
                int bookId = Integer.parseInt(bookIdArray[i]);
                long isbn = Long.parseLong(isbnArray[i]);
                stmt.setInt(1, bookId);
                stmt.setLong(2, isbn);
                try (ResultSet rs = stmt.executeQuery()) {
                    rs.next();
                    int count = rs.getInt("count");
                    if (count == 0) {
                        return false; // Book ID or ISBN does not exist
                    }
                }
            }
            return true; // All book IDs and ISBNs exist
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
