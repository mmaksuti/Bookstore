package scenes;

import controllers.*;
import javafx.scene.Scene;
import exceptions.UnauthenticatedException;

public class SceneSelector {
    public static Scene getSceneByAccessLevel(LoginController loginController, BillController billController, AuthorsController authorsController, BooksController booksController, LibrarianController librarianController) throws UnauthenticatedException {
        return switch (loginController.getLoggedAccessLevel()) {
            case ADMINISTRATOR ->
                    new AdministratorScene(loginController, billController, authorsController, booksController, librarianController);
            case MANAGER ->
                    new ManagerScene(loginController, billController, authorsController, booksController, librarianController);
            case LIBRARIAN -> new LibrarianScene(loginController, billController, booksController);
        };
    }
}