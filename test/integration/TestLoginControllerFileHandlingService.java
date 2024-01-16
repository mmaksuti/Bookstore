package test.integration;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import src.controllers.BillController;
import src.controllers.BooksController;
import src.controllers.LoginController;
import src.enums.AccessLevel;
import src.exceptions.LastAdministratorException;
import src.exceptions.UnauthenticatedException;
import src.models.User;
import src.services.FileHandlingService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class TestLoginControllerFileHandlingService {
    private FileHandlingService fileHandlingService;
    private BillController stubBillController;
    private LoginController loginController;

    ObservableList<User> users;

    @TempDir
    static File tempDir;

    String DATABASE;
    String SESSION;

    @BeforeEach
    void setUp() throws IOException {
        fileHandlingService = new FileHandlingService();
        stubBillController = mock(BillController.class);

        DATABASE = tempDir.getAbsolutePath() + "/authorsDatabase.dat";
        SESSION = tempDir.getAbsolutePath() + "/session.dat";

        loginController = new LoginController(fileHandlingService, stubBillController, DATABASE, SESSION);
        users = FXCollections.observableArrayList();
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

    @Test
    void testLoginWithSavedSession() {
        String session = "admin\nadmin";
        try {
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
    }

    @Test
    void testLoginWithSavedSessionInvalidSession() {
        String session = "invalidsession";
        try {
            fileHandlingService.writeFileContents(SESSION, session);
        } catch (IOException e) {
            fail("Failed to write to file: " + e.getMessage());
        }

        assertFalse(loginController.loginWithSavedSession());
        assertThrows(UnauthenticatedException.class, () -> loginController.getLoggedUsername());
        assertFalse((new File(SESSION)).exists());
    }

    @Test
    void testConstructorNoDatabase() {
        try {
            fileHandlingService.deleteFile(DATABASE);
            assertFalse(new File(DATABASE).exists());
            loginController = new LoginController(fileHandlingService, stubBillController, DATABASE, SESSION);
            assertEquals(1, loginController.getUsers().size(), "Expected a user list with the default user in the absence of a database.");
        } catch (IOException ex) {
            fail("Failed to set up databases: " + ex.getMessage());
        }
    }

    @Test
    void testConstructorInvalidDatabase() {
    }

    @Test
    void testConstructorWithUsers() {
    }

    @Test
    void testAddUser() {
    }

    @Test
    void testUpdateUser() {
    }

    @Test
    void testRemoveUser() {
    }
//    @Test
//    void testAddUserAndLogin() throws IOException, UnauthenticatedException {
//        loginController.addUser("John", "Doe", "john_doe", "password123", "john.doe@email.com", "+355687328237", 2000, LocalDate.of(1990, 1, 1), AccessLevel.LIBRARIAN);
//        assertTrue(loginController.login("john_doe", "password123"));
//        assertEquals("john_doe", loginController.getLoggedUsername());
//        assertEquals(AccessLevel.LIBRARIAN, loginController.getLoggedAccessLevel());
//    }

//    @Test
//    void testRemoveUser() throws IOException, UnauthenticatedException, LastAdministratorException {
//        loginController.login("admin", "admin");
//        User userToRemove = loginController.getUsers().stream().filter(user -> user.getUsername().equals("john_doe")).findFirst().orElse(null);
//        assertNotNull(userToRemove);
//
//        loginController.removeUser(userToRemove);
//        assertFalse(loginController.userExists("john_doe"));
//    }

}
