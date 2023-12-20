package scenes;

import controllers.*;
import javafx.scene.Scene;
import exceptions.UnauthenticatedException;

public class SceneSelector {
    public static Scene getSceneByAccessLevel(LoginController loginController, BillController billController, AuthorsController authorsController, BooksController booksController, LibrarianController librarianController, DatabaseController dbController) throws UnauthenticatedException {
        return switch (loginController.getLoggedAccessLevel()) {
            case ADMINISTRATOR ->
                    new AdministratorScene(loginController, billController, authorsController, booksController, librarianController, dbController);
            case MANAGER ->
                    new ManagerScene(loginController, billController, authorsController, booksController, librarianController, dbController);
            case LIBRARIAN -> new LibrarianScene(loginController, billController, booksController);
        };
    }
}