package test.system;

import org.junit.jupiter.api.io.TempDir;
import src.controllers.*;
import src.exceptions.UnauthenticatedException;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.stage.Stage;
import javafx.scene.control.*;

import src.scenes.LoginScene;
import src.services.FileHandlingService;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestLoginScene extends ApplicationTest {
    LoginController loginController = null;
    FileHandlingService fileHandlingService = new FileHandlingService();

    @TempDir
    File tempDir;

    @Override
    public void start(Stage stage) {
        try {
            String usersDatabase = tempDir.getAbsolutePath() + "/usersDatabase.dat";
            String session = tempDir.getAbsolutePath() + "/session.dat";

            loginController = new LoginController(fileHandlingService, null, usersDatabase, session);
        }
        catch (IOException ex) {
            fail("Failed to load database: " + ex.getMessage());
        }

        LoginScene login = new LoginScene(loginController, null, null, null, null, null);

        stage.setScene(login);
        stage.show();
    }

    @Test
    public void testLoginWrongCredentials() {
        Set<TextField> textFields = lookup(".text-field").queryAllAs(TextField.class);
        Iterator<TextField> iterator = textFields.iterator();

        clickOn(iterator.next()).write("admin");
        clickOn(iterator.next()).write("wrong");

        clickOn(".button");

        assertThrows(UnauthenticatedException.class, () -> loginController.getLoggedUsername());

        Set<Label> labels = lookup(".label").queryAllAs(Label.class);
        Iterator<Label> labelIterator = labels.iterator();
        Label statusLabel = labelIterator.next();
        statusLabel = labelIterator.next();
        statusLabel = labelIterator.next();
        assertEquals("Invalid credentials", statusLabel.getText());
    }

    @Test
    public void testLoginCorrectCredentials() {
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
