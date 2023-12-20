package scenes;

import controllers.*;
import exceptions.UnauthenticatedException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginScene extends Scene {
    TextField usernameField = new TextField();
    PasswordField passwordField = new PasswordField();
    Label statusLabel = new Label("");

    private final LoginController loginController;
    private final BillController billController;
    private final AuthorsController authorsController;
    private final BooksController booksController;
    private final LibrarianController librarianController;
    private final DatabaseController dbController;

    public LoginScene(LoginController loginController, BillController billController, AuthorsController authorsController, BooksController booksController, LibrarianController librarianController, DatabaseController dbController) {
        super(new VBox(), 300, 200);

        this.loginController = loginController;
        this.billController = billController;
        this.booksController = booksController;
        this.authorsController = authorsController;
        this.librarianController = librarianController;
        this.dbController = dbController;

        VBox vbox = (VBox) this.getRoot();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        Label usernameLabel = new Label("Username:");
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);

        Label passwordLabel = new Label("Password:");
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);

        statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        
        Button loginButton = new Button("Login");
        grid.add(loginButton, 1, 2);

        usernameField.setOnAction(new LoginHandler());
        passwordField.setOnAction(new LoginHandler());
        loginButton.setOnAction(new LoginHandler());
        
        vbox.getChildren().addAll(grid, loginButton, statusLabel);
    }

    class LoginHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (loginController.login(username, password)) {
                try {
                    loginController.saveSession(username, password);

                    Stage primaryStage = (Stage)LoginScene.this.getWindow();

                    UserScene scene = (UserScene)SceneSelector.getSceneByAccessLevel(loginController, billController, authorsController, booksController, librarianController, dbController);
                    primaryStage.setTitle(scene.getName());
                    primaryStage.setScene((Scene)scene);
                }
                catch (IOException ex) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("Failed to save session");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                }
                catch (UnauthenticatedException ignored) {
                    // should never happen
                }
            }
            else {
                statusLabel.setText("Invalid credentials");
            }
        }
        
    }
}
