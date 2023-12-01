import controllers.BillController;
import controllers.LoginController;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import exceptions.UnauthenticatedException;
import scenes.AdministratorScene;
import scenes.LibrarianScene;
import scenes.LoginScene;
import scenes.ManagerScene;

import java.io.IOException;

import static java.lang.System.exit;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BillController billController = new BillController();
        LoginController loginController = null;

        try {
            loginController = new LoginController(billController);
        }
        catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load databases");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            exit(1);
        }

        if (!loginController.loginWithSavedSession()) {
            primaryStage.setTitle("Login");
            
            LoginScene login = new LoginScene(loginController, billController);
            primaryStage.setScene(login);
        }
        else {
            try {
                switch (loginController.getLoggedAccessLevel()) {
                    case ADMINISTRATOR:
                        AdministratorScene admin = new AdministratorScene(loginController, billController);
                        primaryStage.setTitle("Administrator");
                        primaryStage.setScene(admin);
                        break;
                    case MANAGER:
                        System.out.println("Manager");
                        ManagerScene manager = new ManagerScene(loginController, billController);
                        primaryStage.setTitle("Manager");
                        primaryStage.setScene(manager);

                        break;
                    case LIBRARIAN:
                        System.out.println("Librarian");
                        LibrarianScene librarian = new LibrarianScene(loginController, billController);
                        primaryStage.setTitle("Librarian");
                        primaryStage.setScene(librarian);

                        break;
                }
            }
            catch (UnauthenticatedException e) {
                // should never happen
            }
        }

        primaryStage.show();
    }
}
