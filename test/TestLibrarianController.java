package test;

import controllers.BillController;
import controllers.LibrarianController;
import controllers.LoginController;
import main.AccessLevel;
import main.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class TestLibrarianController {
    private BillController billController;
    private LoginController loginController;
    @BeforeEach
    public void setUp() throws IOException {
        billController = new BillController();
        loginController = new LoginController(billController);
    }

    @Test
    public void testInitialization() {
        LibrarianController librarianController = new LibrarianController(loginController, billController);

        assertNotNull(librarianController.getLibrarians());
        assertFalse(librarianController.getLibrarians().isEmpty());
    }

    @Test
    public void testAddingLibrarians() {
        User librarianUser = new User("John", "Doe", "johndoe", "password", "john@example.com", "123456789", 50000, null, AccessLevel.LIBRARIAN);
        loginController.getUsers().add(librarianUser);

        LibrarianController librarianController = new LibrarianController(loginController, billController);

        assertFalse(librarianController.getLibrarians().isEmpty());
        assertEquals(2, librarianController.getLibrarians().size());
        assertEquals(librarianUser.getUsername(), librarianController.getLibrarians().get(0).getUsername());
    }

    @Test
    public void testUpdatingLibrariansOnUserListChange() {
        LibrarianController librarianController = new LibrarianController(loginController, billController);
        User librarianUser = new User("John", "Doe", "john.doe", "password", "john@example.com", "123456789", 50000, null, AccessLevel.LIBRARIAN);
        loginController.getUsers().add(librarianUser);

        loginController.getUsers().remove(librarianUser);

        assertFalse(librarianController.getLibrarians().isEmpty());
    }

    @Test
    public void testRemovingLibrariansOnUserListChange() {
        // Arrange
        User librarianUser = new User("John", "Doe", "john.doe", "password", "john@example.com", "123456789", 50000, null, AccessLevel.LIBRARIAN);
        loginController.getUsers().add(librarianUser);

        LibrarianController librarianController = new LibrarianController(loginController, billController);
        loginController.getUsers().remove(librarianUser);

        assertFalse(librarianController.getLibrarians().isEmpty());
    }
}
