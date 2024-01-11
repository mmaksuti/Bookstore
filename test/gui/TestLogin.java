package test.gui;

import controllers.*;
import exceptions.UnauthenticatedException;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.stage.Stage;
import javafx.scene.control.*;

import scenes.LoginScene;
import services.FileHandlingService;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestLogin extends ApplicationTest {
    BillController billController = null;
    LoginController loginController = null;
    AuthorsController authorsController = null;
    BooksController booksController = null;
    LibrarianController librarianController = null;
    FileHandlingService fileHandlingService = new FileHandlingService();

    @Override
    public void start(Stage stage) {
        try {
            billController = new BillController(fileHandlingService);
            loginController = new LoginController(fileHandlingService, billController);
            authorsController = new AuthorsController(fileHandlingService);
            booksController = new BooksController(fileHandlingService);
            librarianController = new LibrarianController(loginController, billController);
        }
        catch (IOException ex) {
        }

        LoginScene login = new LoginScene(loginController, billController, authorsController, booksController, librarianController, fileHandlingService);

        stage.setScene(login);
        stage.show();
    }

    @Test
    public void testLogin() {
        Set<TextField> textFields = lookup(".text-field").queryAllAs(TextField.class);
        Iterator<TextField> iterator = textFields.iterator();

        clickOn(iterator.next()).write("admin");
        clickOn(iterator.next()).write("admin");

        clickOn(".button");

        try {
            assertEquals("admin", loginController.getLoggedUsername());
        }
        catch (UnauthenticatedException ex) {
            fail("Failed to login: " + ex.getMessage());
        }
    }
}
