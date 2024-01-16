package test.integration;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import src.controllers.AuthorsController;
import src.controllers.BillController;
import src.controllers.BooksController;
import src.controllers.LoginController;
import src.enums.AccessLevel;
import src.enums.Gender;
import src.exceptions.LastAdministratorException;
import src.exceptions.UnauthenticatedException;
import src.models.Author;
import src.models.User;
import src.services.FileHandlingService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        try {
            fileHandlingService.writeFileContents(DATABASE, "invalid database");
            loginController = new LoginController(fileHandlingService, stubBillController, DATABASE, SESSION);
            assertEquals(1, loginController.getUsers().size());
        }
        catch (IOException ex) {
            fail("Failed to set up databases: " + ex.getMessage());
        }
    }

    @Test
    void testConstructorWithUsers() {
        try {
            ArrayList<User> users = new ArrayList<>();

            User user1 = new User("John", "Doe", "johndoe", "password", "johndoe@gmail.com", "+355671234567", 1000, LocalDate.of(1990, 1, 1), AccessLevel.LIBRARIAN);
            User user2 = new User("Jane", "Doe", "janedoe", "password", "janedoe@gmail.com", "+355671234569", 1000, LocalDate.of(1990, 1, 1), AccessLevel.LIBRARIAN);
            users.add(user1);
            users.add(user2);

            fileHandlingService.writeObjectToFile(DATABASE, users);
            loginController = new LoginController(fileHandlingService, stubBillController, DATABASE, SESSION);
            assertEquals(2, loginController.getUsers().size());

            user1 = loginController.getUsers().get(0);
            user2 = loginController.getUsers().get(1);
            assertEquals("John", user1.getFirstName());
            assertEquals("Doe", user1.getLastName());
            assertEquals("johndoe", user1.getUsername());
            assertEquals("+355671234567", user1.getPhone());

            assertEquals("Jane", user2.getFirstName());
            assertEquals("Doe", user2.getLastName());
            assertEquals("janedoe", user2.getUsername());
            assertEquals("+355671234569", user2.getPhone());
        }
        catch (IOException ex) {
            fail("Failed to set up databases: " + ex.getMessage());
        }
    }

    @Test
    void testAddUser() {
        try {
            loginController.addUser("John", "Doe", "johndoe", "password", "johndoe@gmail.com", "+355671234567", 1000, LocalDate.of(1990, 1, 1), AccessLevel.LIBRARIAN);
            ArrayList<User> users = (ArrayList<User>)fileHandlingService.readObjectFromFile(DATABASE);
            assertEquals(2, users.size());

            User user = users.get(1);
            assertEquals("John", user.getFirstName());
            assertEquals("Doe", user.getLastName());
            assertEquals("johndoe", user.getUsername());
            assertEquals("+355671234567", user.getPhone());
        }
        catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }
        catch (ClassNotFoundException ex) {
            fail("Failed to load database: " + ex.getMessage());
        }
    }

    @Test
    void testUpdateUser() {
        try {
            loginController.login("admin", "admin");

            loginController.addUser("John", "Doe", "johndoe", "password", "johndoe@gmail.com", "+355671234567", 1000, LocalDate.of(1990, 1, 1), AccessLevel.LIBRARIAN);
            User user = loginController.getUsers().get(1);
            loginController.updateUser(user, "Jane", "Doe", "janedoe", "password", "janedoe@gmail.com", "+355671234569", 1000, LocalDate.of(1990, 1, 1), AccessLevel.LIBRARIAN);

            ArrayList<User> users = (ArrayList<User>)fileHandlingService.readObjectFromFile(DATABASE);
            assertEquals(2, users.size());

            user = users.get(1);
            assertEquals("Jane", user.getFirstName());
            assertEquals("Doe", user.getLastName());
            assertEquals("janedoe", user.getUsername());
            assertEquals("+355671234569", user.getPhone());
        }
        catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }
        catch (ClassNotFoundException ex) {
            fail("Failed to load database: " + ex.getMessage());
        }
        catch (UnauthenticatedException ignored) {
        }
    }

    @Test
    void testRemoveUser() {
        try {
            loginController.addUser("John", "Doe", "johndoe", "password", "johndoe@gmail.com", "+355671234567", 1000, LocalDate.of(1990, 1, 1), AccessLevel.LIBRARIAN);
            User user = loginController.getUsers().get(1);
            loginController.removeUser(user);

            ArrayList<User> users = (ArrayList<User>)fileHandlingService.readObjectFromFile(DATABASE);
            assertEquals(1, users.size());
        }
        catch (IOException ex) {
            fail("Failed to remove author: " + ex.getMessage());
        }
        catch (ClassNotFoundException ex) {
            fail("Failed to load database: " + ex.getMessage());
        }
        catch (LastAdministratorException ignored) {
        }
    }
}
