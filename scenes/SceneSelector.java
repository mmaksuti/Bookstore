package scenes;

import controllers.*;
import javafx.scene.Scene;
import exceptions.UnauthenticatedException;
import services.FileHandlingService;

public class SceneSelector {
    public static Scene getSceneByAccessLevel(LoginController loginController, BillController billController, AuthorsController authorsController, BooksController booksController, LibrarianController librarianController, FileHandlingService fileHandlingService) throws UnauthenticatedException {
        return switch (loginController.getLoggedAccessLevel()) {
            case ADMINISTRATOR ->
                    new AdministratorScene(loginController, billController, authorsController, booksController, librarianController, fileHandlingService);
            case MANAGER ->
                    new ManagerScene(loginController, billController, authorsController, booksController, librarianController, fileHandlingService);
            case LIBRARIAN -> new LibrarianScene(loginController, billController, booksController);
        };
    }
}