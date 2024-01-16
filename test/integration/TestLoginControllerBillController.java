package test.integration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.controllers.BillController;
import src.controllers.LoginController;
import src.enums.AccessLevel;
import src.exceptions.UnauthenticatedException;
import src.models.Bill;
import src.services.FileHandlingService;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class TestLoginControllerBillController {
    private FileHandlingService stubFileHandlingService;
    private BillController billController;
    private LoginController loginController;

    @BeforeEach
    public void setUp() throws IOException {
        stubFileHandlingService = mock(FileHandlingService.class);
        billController = new BillController(stubFileHandlingService);
        loginController = new LoginController(stubFileHandlingService, billController);
    }

    @Test
    public void testRemoveUserIsLibrarian() {
    }

//    @Test
//    public void testReadingFromEmptyDatabase() {
//        assertDoesNotThrow(() -> loginController.readFromFile("emptyDatabase.dat"));
//        assertEquals(1, loginController.getUsers().size());
//    }
//    @Test
//    public void testAddingNewUser() {
//        try {
//            String username = "john_doe";
//
//            // Check if the user already exists
//            if (!loginController.userExists(username)) {
//                assertDoesNotThrow(() -> loginController.addUser("John", "Doe", username, "password", "john@example.com", "+123456789", 1000, LocalDate.of(1990, 1, 1), AccessLevel.LIBRARIAN));
//            }
//
//            assertTrue(loginController.userExists(username));
//        } catch (Exception e) {
//            e.printStackTrace(); // Print the exception details
//            fail("Unexpected exception thrown: " + e.getClass().getSimpleName() + ": " + e.getMessage());
//        }
//    }
//
//
//    @Test
//    public void testLoggingInAndGettingUserInfo() throws UnauthenticatedException, IOException {
//        String username = "john_doe";
//        String password = "password";
//
//        // Check if the user already exists
//        if (!loginController.userExists(username)) {
//            loginController.addUser("John", "Doe", username, password, "john@example.com", "+355673456789", 1000, LocalDate.of(1990, 1, 1), AccessLevel.LIBRARIAN);
//        }
//
//        assertTrue(loginController.login(username, password));
//        assertEquals(username, loginController.getLoggedUsername());
//        assertEquals(AccessLevel.LIBRARIAN, loginController.getLoggedAccessLevel());
//    }
//
//
//    @Test
//    public void testUpdatingUserInfo() {
//        String initialUsername = "john__doe";
//        String updatedUsername = "jane_doe";
//
//        assertDoesNotThrow(() -> loginController.addUser("John", "Doe", initialUsername, "password", "john@example.com", "+355674567789", 1000, LocalDate.of(1990, 1, 1), AccessLevel.LIBRARIAN));
//        assertTrue(loginController.login(initialUsername, "password"));
//
//        assertDoesNotThrow(() -> loginController.updateUser(
//                loginController.getUsers().get(0),
//                "Jane",
//                "Doe",
//                updatedUsername,  // Use a valid username here
//                "newpassword",
//                "jane@example.com",
//                "+355676546732",
//                1200,
//                LocalDate.of(1985, 1, 1),
//                AccessLevel.ADMINISTRATOR));
//
//        assertTrue(loginController.userExists(updatedUsername));
//    }
//
//
//    @Test
//    public void testSavingAndLoadingSession() throws IOException {
//        String username = "john_doe";
//        String password = "password";
//
//        // Check if the user already exists
//        if (!loginController.userExists(username)) {
//            loginController.addUser("John", "Doe", username, password, "john@example.com", "+35567456789", 1000, LocalDate.of(1990, 1, 1), AccessLevel.LIBRARIAN);
//        }
//
//        assertTrue(loginController.login(username, password));
//        assertDoesNotThrow(() -> loginController.saveSession(username, password));
//
//        loginController.logout();
//        assertFalse(loginController.loginWithSavedSession());
//    }


}
