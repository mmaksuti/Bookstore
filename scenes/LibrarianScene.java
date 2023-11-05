package scenes;

import controllers.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import main.UnauthenticatedException;
import stages.SellBooksStage;

public class LibrarianScene extends Scene {
    public LibrarianScene() {
        super(new BorderPane(), 300, 200);

        BorderPane border = (BorderPane) getRoot();

        Label welcomeLabel;
        try {
            welcomeLabel = new Label();
            welcomeLabel.textProperty().bind(LoginController.getWelcomeMessage());
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

        Button sellBooksButton = new Button("Sell books");
        sellBooksButton.setOnAction(e -> {
            SellBooksStage sellBooksStage = new SellBooksStage();
            sellBooksStage.show();
        });

        Button logoutButton = new Button("Log out");
        logoutButton.setOnAction(e -> LoginController.logout());

        logoutHBox.getChildren().addAll(sellBooksButton, logoutButton);
        border.setBottom(logoutHBox);
    }
}
