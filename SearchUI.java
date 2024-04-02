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
import java.util.ArrayList;
import java.util.List;

public class SearchUI extends VBox {
    private TextField searchField;
    private RadioButton titleRadioButton;
    private RadioButton isbnRadioButton;
    private TextArea searchResultArea;
    private Button searchButton;

    private String URL;
    private String USER;
    private String PASSWORD;

    public SearchUI(String url, String user, String password) {
        this.URL = url;
        this.USER = user;
        this.PASSWORD = password;

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

        this.getChildren().addAll(
            new Label("Search for a book:"), searchField,
            titleRadioButton, isbnRadioButton,
            searchButton, searchResultArea
        );
        this.setSpacing(10);
        this.setPadding(new Insets(20));
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

