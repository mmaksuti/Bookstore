package test.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.controllers.BillController;
import src.controllers.LoginController;
import src.enums.AccessLevel;
import src.exceptions.LastAdministratorException;
import src.exceptions.UnauthenticatedException;
import src.models.User;
import src.services.FileHandlingService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestLoginControllerFileHandlingService {

    private FileHandlingService fileHandlingService;
    private BillController billController;
    private LoginController loginController;

    private static final String TEST_DATABASE = "test_database.txt";
    private static final String TEST_SESSION_FILE = "test_session.txt";


    @BeforeEach
    void setUp() throws IOException {
        fileHandlingService = new FileHandlingService();
        billController = new BillController(fileHandlingService);
        loginController = new LoginController(fileHandlingService, billController);
    }

    @Test
    void testConstructorNoDatabase() {
        try {
            fileHandlingService.writeFileContents(TEST_DATABASE, "");

            LoginController loginController = new LoginController(fileHandlingService, billController, TEST_DATABASE, TEST_SESSION_FILE);

            assertFalse(loginController.getUsers().isEmpty(), "Expected an empty user list in the absence of a database.");
        } catch (IOException ex) {
            fail("Failed to set up databases: " + ex.getMessage());
        }
    }
    @Test
    void testConstructorInvalidSessionFile() {
        try {
            fileHandlingService.writeFileContents(TEST_SESSION_FILE, "invalid session data");
            LoginController loginController = new LoginController(fileHandlingService, billController, TEST_DATABASE, TEST_SESSION_FILE);

            assertFalse(loginController.getUsers().isEmpty());
        } catch (IOException ex) {
            fail("Failed to set up databases: " + ex.getMessage());
        }
    }

    @Test
    void testAddUserAndLogin() throws IOException, UnauthenticatedException {

        loginController.addUser("John", "Doe", "john_doe", "password123", "john.doe@email.com", "+355687328237", 2000, LocalDate.of(1990, 1, 1), AccessLevel.LIBRARIAN);
        assertTrue(loginController.login("john_doe", "password123"));
        assertEquals("john_doe", loginController.getLoggedUsername());
        assertEquals(AccessLevel.LIBRARIAN, loginController.getLoggedAccessLevel());
    }

    @Test
    void testRemoveUser() throws IOException, UnauthenticatedException, LastAdministratorException {
        loginController.login("admin", "admin");
        User userToRemove = loginController.getUsers().stream().filter(user -> user.getUsername().equals("john_doe")).findFirst().orElse(null);
        assertNotNull(userToRemove);

        loginController.removeUser(userToRemove);
        assertFalse(loginController.userExists("john_doe"));
    }


}
