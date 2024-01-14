package test.integration;
import src.controllers.AuthorsController;
import src.controllers.BillController;
import src.controllers.BooksController;
import src.controllers.LoginController;
import src.enums.Gender;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import src.exceptions.UnauthenticatedException;
import src.models.Author;
import src.models.Book;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import src.models.User;
import src.services.FileHandlingService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestLoginControllerFileHandlingService {
    LoginController loginController;
    BillController stubBillController;
    FileHandlingService fileHandlingService;
    ObservableList<User> users;

    @TempDir
    static File tempDir;

    String DATABASE;
    String SESSION;

    @BeforeEach
    public void setUp()  {
        try {
            fileHandlingService = new FileHandlingService();
            stubBillController = mock(BillController.class);

            DATABASE = tempDir.getAbsolutePath() + "/authorsDatabase.dat";
            SESSION = tempDir.getAbsolutePath() + "/session.dat";
            loginController = new LoginController(fileHandlingService, stubBillController, DATABASE, SESSION);
            users = FXCollections.observableArrayList();
        }
        catch (IOException ex) {
            fail("Failed to set up databases: " + ex.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        fileHandlingService.deleteFile(DATABASE);
        fileHandlingService.deleteFile(SESSION);
    }

    @Test
    void testLogout() {
        try {
            String session = "admin\nadmin";
            fileHandlingService.writeFileContents(SESSION, session);
        } catch (IOException e) {
            fail("Failed to write to file: " + e.getMessage());
        }

        try {
            assertTrue(loginController.loginWithSavedSession());
            assertEquals("admin", loginController.getLoggedUsername());
        }
        catch (UnauthenticatedException ex) {
            fail("Not logged in: " + ex.getMessage());
        }

        loginController.logout();
        assertFalse((new File(SESSION)).exists());
        assertThrows(UnauthenticatedException.class, () -> loginController.getLoggedUsername());
    }

    @Test
    void testSaveSession() {
        try {
            loginController.saveSession("username", "password");
            assertTrue((new File(SESSION)).exists());
            assertEquals("username\npassword\n", fileHandlingService.readFileContents(SESSION));
        }
        catch (IOException ex) {
            fail("Failed to write to file: " + ex.getMessage());
        }
    }
}
