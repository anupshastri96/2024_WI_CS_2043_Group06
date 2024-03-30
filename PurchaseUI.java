package deliverable4;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PurchaseUI extends VBox {
    private TextField customerIdField;
    private CheckBox memberCheckBox;
    private TextField bookIdsField;
    private TextField isbnsField;
    private TextArea billTextArea;
    private Button purchaseButton;

    private String URL;
    private String USER;
    private String PASSWORD;

    public PurchaseUI(String url, String user, String password) {
        this.URL = url;
        this.USER = user;
        this.PASSWORD = password;

        customerIdField = new TextField();
        memberCheckBox = new CheckBox("Use your points for discount");
        bookIdsField = new TextField();
        isbnsField = new TextField();
        billTextArea = new TextArea();
        purchaseButton = new Button("Purchase");
        purchaseButton.setOnAction(this::purchase);

        this.getChildren().addAll(
            new Label("Enter Customer ID:"), customerIdField,
            memberCheckBox,
            new Label("Enter Book IDs (comma separated):"), bookIdsField,
            new Label("Enter ISBNs (comma separated):"), isbnsField,
            purchaseButton, billTextArea
        );
        this.setSpacing(10);
        this.setPadding(new Insets(20));
    }

    private void purchase(ActionEvent event) {
        String customerId = customerIdField.getText().trim();
        boolean isSelected = memberCheckBox.isSelected();
        String bookIds = bookIdsField.getText().trim();
        String isbns = isbnsField.getText().trim();
        double totalPrice = 0;

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
            
            String updateBooksQuery = "UPDATE Books SET purchased = true WHERE book_id = ? AND isbn = ?";
            try (PreparedStatement updateBooksStmt = conn.prepareStatement(updateBooksQuery)) {
                for (int i = 0; i < bookIdArray.length; i++) {
                    int bookId = Integer.parseInt(bookIdArray[i]);
                    long isbn = Long.parseLong(isbnArray[i]);
                    updateBooksStmt.setInt(1, bookId);
                    updateBooksStmt.setLong(2, isbn);
                    updateBooksStmt.executeUpdate();
                }
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

                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String dateTime = now.format(formatter);

                        StringBuilder bill = new StringBuilder();
                        bill.append("Customer Name: ").append(customerNameFromDB).append("\n");
                        bill.append("Date and Time: ").append(dateTime).append("\n\n");
                        bill.append("Price Breakdown:\n");

                        for (int i = 0; i < bookIdArray.length; i++) {
                            int bookId = Integer.parseInt(bookIdArray[i]);
                            long isbn = Long.parseLong(isbnArray[i]);

                            String priceQuery = "SELECT price, added_percentage FROM Books NATURAL JOIN BookTypes WHERE book_id = ? AND isbn = ?";
                            try (PreparedStatement priceStmt = conn.prepareStatement(priceQuery)) {
                                priceStmt.setInt(1, bookId);
                                priceStmt.setLong(2, isbn);
                                try (ResultSet priceResult = priceStmt.executeQuery()) {
                                    if (priceResult.next()) {
                                        double basePrice = priceResult.getDouble("price");
                                        double addedPercentage = priceResult.getDouble("added_percentage");

                                        double totalPriceForBook = basePrice * (1 + addedPercentage / 100);
                                        totalPrice += totalPriceForBook;

                                        bill.append("Book ID: ").append(bookId).append(", ISBN: ").append(isbn).append("\n");
                                        bill.append("Base Price: ").append(basePrice).append("\n");
                                        bill.append("Added Percentage: ").append(addedPercentage).append("%\n");
                                        bill.append("Total Price for Book: ").append(totalPriceForBook).append("\n\n");
                                    }
                                }
                            }
                        }

                        bill.append("Total Price: ").append(totalPrice).append("\n");

                        if (isMember && isSelected) {
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

                            String updatePointsQuery = "UPDATE Members SET points = points - ? WHERE customer_id = ?";
                            try (PreparedStatement updatePointsStmt = conn.prepareStatement(updatePointsQuery)) {
                                updatePointsStmt.setInt(1, deduction);
                                updatePointsStmt.setInt(2, Integer.parseInt(customerId));
                                updatePointsStmt.executeUpdate();
                            }

                            totalPrice -= discountAmount;
                            bill.append("Discount Applied: $").append(discountAmount).append("\n");
                        }

                        bill.append("Final Price Owed: ").append(totalPrice).append("\n");

                        if (isMember) {
                            String updatePointsQuery = "UPDATE Members SET points = points + ? WHERE customer_id = ?";
                            try (PreparedStatement updatePointsStmt = conn.prepareStatement(updatePointsQuery)) {
                                updatePointsStmt.setInt(1, (int) Math.floor(totalPrice / 10));
                                updatePointsStmt.setInt(2, Integer.parseInt(customerId));
                                updatePointsStmt.executeUpdate();
                            }
                        }

                        billTextArea.setText(bill.toString());
                    } else {
                        System.out.println("Customer not found.");
                    }
                }
            }

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
                        return false;
                    }
                }
            }
            return true;
        }
    }
}

