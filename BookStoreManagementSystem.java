package deliverable4;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;

public class BookStoreManagementSystem extends Application {

    private static final String URL = "jdbc:mariadb://localhost:3306/project";
    private static final String USER = "root";
    private static final String PASSWORD = "night1234";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Bookstore Management System");

        TabPane tabPane = new TabPane();

        Tab addBookTab = new Tab("Add Book", new AddBookUI(URL, USER, PASSWORD));
        Tab addAuthorTab = new Tab("Add Author", new AddAuthorUI(URL, USER, PASSWORD));
        Tab addCustomerTab = new Tab("Add Customer", new AddCustomerUI(URL, USER, PASSWORD));
        Tab searchTab = new Tab("Search", new SearchUI(URL, USER, PASSWORD));
        Tab membersTab = new Tab("Add Member", new AddMemberUI(URL, USER, PASSWORD));
        Tab purchaseTab = new Tab("Purchase", new PurchaseUI(URL, USER, PASSWORD));

        tabPane.getTabs().addAll(addBookTab, addAuthorTab, addCustomerTab, searchTab, membersTab, purchaseTab);

        Scene scene = new Scene(tabPane, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
