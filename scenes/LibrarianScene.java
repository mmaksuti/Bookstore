package scenes;

import controllers.AuthorsController;
import controllers.BillController;
import controllers.BooksController;
import controllers.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import exceptions.UnauthenticatedException;
import main.Bill;
import stages.SellBooksStage;

import java.io.IOException;

import static java.lang.System.exit;

public class LibrarianScene extends Scene {
    public LibrarianScene(LoginController loginController, BillController billController) {
        super(new BorderPane(), 300, 200);

        BorderPane border = (BorderPane) getRoot();

        Label welcomeLabel;
        try {
            welcomeLabel = new Label();
            welcomeLabel.textProperty().bind(loginController.getWelcomeMessage());
        }
        catch (UnauthenticatedException e) {
            // should never happen
            welcomeLabel = new Label("Unauthenticated");
        }
    
        welcomeLabel.setStyle("-fx-font-size: 15px; -fx-text-alignment: center;");
        border.setCenter(welcomeLabel);

        HBox logoutHBox = new HBox();
        logoutHBox.setAlignment(Pos.CENTER);
        logoutHBox.setPadding(new Insets(10));
        logoutHBox.setSpacing(10);

        BooksController booksController = null;
        try {
            booksController = new BooksController();
        }
        catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load databases");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            exit(1);
        }

        BooksController finalBooksController = booksController;
        Button sellBooksButton = new Button("Sell books");
        sellBooksButton.setOnAction(e -> {
            SellBooksStage sellBooksStage = new SellBooksStage(finalBooksController, loginController, billController);
            sellBooksStage.show();
        });

        Button logoutButton = new Button("Log out");
        logoutButton.setOnAction(e -> loginController.logout());

        logoutHBox.getChildren().addAll(sellBooksButton, logoutButton);
        border.setBottom(logoutHBox);
    }
}
