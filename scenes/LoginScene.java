package scenes;

import controllers.BillController;
import controllers.LoginController;
import exceptions.UnauthenticatedException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Bill;

import java.io.IOException;

import static java.lang.System.exit;

public class LoginScene extends Scene {
    TextField usernameField = new TextField();
    PasswordField passwordField = new PasswordField();
    Label statusLabel = new Label("");

    private LoginController loginController;
    private BillController billController;

    public LoginScene(LoginController loginController, BillController billController) {
        super(new VBox(), 300, 200);

        this.loginController = loginController;
        this.billController = billController;

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

                    Stage primaryStage = (Stage) LoginScene.this.getWindow();
                    switch (loginController.getLoggedAccessLevel()) {
                        case ADMINISTRATOR:
                            AdministratorScene admin = new AdministratorScene(loginController, billController);
                            primaryStage.setTitle("Administrator");
                            primaryStage.setScene(admin);
                            
                            break;
                        case MANAGER:
                            ManagerScene manager = new ManagerScene(loginController, billController);
                            primaryStage.setTitle("Manager");
                            primaryStage.setScene(manager);
    
                            break;
                        case LIBRARIAN:
                            LibrarianScene librarian = new LibrarianScene(loginController, billController);
                            primaryStage.setTitle("Librarian");
                            primaryStage.setScene(librarian);
                            
                            break;
                    }
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
