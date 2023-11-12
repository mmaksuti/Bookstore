package scenes;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import exceptions.UnauthenticatedException;
import controllers.LoginController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;

public class LoginScene extends Scene {
    TextField usernameField = new TextField();
    PasswordField passwordField = new PasswordField();
    Label statusLabel = new Label("");

    public LoginScene() {
        super(new VBox(), 300, 200);
        
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
            if (LoginController.login(username, password)) {
                LoginController.saveSession(username, password);
                
                try {
                    Stage primaryStage = (Stage) LoginScene.this.getWindow();
                    switch (LoginController.getLoggedAccessLevel()) {
                        case ADMINISTRATOR:
                            AdministratorScene admin = new AdministratorScene();
                            primaryStage.setTitle("Administrator");
                            primaryStage.setScene(admin);
                            
                            break;
                        case MANAGER:
                            ManagerScene manager = new ManagerScene();
                            primaryStage.setTitle("Manager");
                            primaryStage.setScene(manager);
    
                            break;
                        case LIBRARIAN:
                            LibrarianScene librarian = new LibrarianScene();
                            primaryStage.setTitle("Librarian");
                            primaryStage.setScene(librarian);
                            
                            break;
                    }
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
