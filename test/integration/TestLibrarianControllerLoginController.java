package test.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.controllers.BillController;
import src.controllers.LibrarianController;
import src.controllers.LoginController;
import src.enums.AccessLevel;
import src.enums.Gender;
import src.exceptions.UnauthenticatedException;
import src.models.User;
import src.services.FileHandlingService;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
public class TestLibrarianControllerLoginController {
    private LoginController loginController;
    private LibrarianController librarianController;
    private FileHandlingService stubFileHandlingService;
    private BillController billController;

    @BeforeEach
    public void setUp() throws IOException {
        stubFileHandlingService = new FileHandlingService();

        billController = new BillController(stubFileHandlingService);
        loginController = new LoginController(stubFileHandlingService, billController);
        librarianController = new LibrarianController(loginController, billController);
    }

    @Test
    public void testLoginAndLibrarianControllerIntegration() throws IOException, UnauthenticatedException {
        // Add a librarian user
        loginController.addUser("Librarian", "User", "librarian", "password", "librarian@example.com",
                "+355671234567", 1000, LocalDate.of(1980, 1, 1), AccessLevel.LIBRARIAN);

        assertTrue(loginController.login("librarian", "password"));
        String loggedUsername = loginController.getLoggedUsername();
        assertEquals("librarian", loggedUsername);

        assertEquals(1, librarianController.getLibrarians().size());
        assertEquals("Librarian User (librarian)", librarianController.getLibrarians().get(0).toString());
    }

    @Test
    public void testLogoutAndLibrarianControllerIntegration() throws IOException, UnauthenticatedException {
        if (!loginController.userExists("librarian")) {
            loginController.addUser("Librarian", "User", "librarian", "password", "librarian@example.com",
                    "+355671234567", 1000, LocalDate.of(1980, 1, 1), AccessLevel.LIBRARIAN);
        }
        assertTrue(loginController.login("librarian", "password"));

        assertFalse(loginController.logout());
        assertEquals(1, librarianController.getLibrarians().size());
    }
    @Test
    public void testLoginWithSavedSessionAndLibrarianController() throws IOException, UnauthenticatedException {
        if (!loginController.userExists("librarian")) {
            loginController.addUser("Librarian", "User", "librarian", "password", "librarian@example.com",
                    "+355671234567", 1000, LocalDate.of(1980, 1, 1), AccessLevel.LIBRARIAN);
        }

        loginController.saveSession("librarian", "password");

        assertTrue(loginController.logout());

        assertTrue(loginController.loginWithSavedSession());

        assertEquals(1, librarianController.getLibrarians().size());
    }

}
