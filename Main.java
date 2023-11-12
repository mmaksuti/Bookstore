import controllers.LoginController;
import javafx.application.Application;
import javafx.stage.Stage;
import exceptions.UnauthenticatedException;
import scenes.AdministratorScene;
import scenes.LibrarianScene;
import scenes.LoginScene;
import scenes.ManagerScene;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        if (!LoginController.loginWithSavedSession()) {
            primaryStage.setTitle("Login");
            
            LoginScene login = new LoginScene();
            primaryStage.setScene(login);
        }
        else {
            try {
                switch (LoginController.getLoggedAccessLevel()) {
                    case ADMINISTRATOR:
                        AdministratorScene admin = new AdministratorScene();
                        primaryStage.setTitle("Administrator");
                        primaryStage.setScene(admin);
                        break;
                    case MANAGER:
                        System.out.println("Manager");
                        ManagerScene manager = new ManagerScene();
                        primaryStage.setTitle("Manager");
                        primaryStage.setScene(manager);

                        break;
                    case LIBRARIAN:
                        System.out.println("Librarian");
                        LibrarianScene librarian = new LibrarianScene();
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
