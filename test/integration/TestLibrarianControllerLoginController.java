package test.integration;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import src.controllers.BillController;
import src.controllers.LibrarianController;
import src.controllers.LoginController;
import src.enums.AccessLevel;
import src.models.Librarian;
import src.models.User;
import src.services.FileHandlingService;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class TestLibrarianControllerLoginController {
    private LibrarianController librarianController;

    @Mock
    private FileHandlingService fileHandlingService;

    @BeforeEach
    void setUp() throws IOException, ClassNotFoundException {
        MockitoAnnotations.openMocks(this);

        when(fileHandlingService.readObjectFromFile("usersDatabase.dat"))
                .thenReturn(new ArrayList<>());

        BillController billController = new BillController(fileHandlingService);

        LoginController loginController = new LoginController(fileHandlingService, billController);
        loginController.getUsers().addAll(
                new User("admin", "Administrator", "admin", "admin", "admin@gmail.com",
                        "+355671234567", 1000, LocalDate.of(1999, 1, 1), AccessLevel.ADMINISTRATOR),
                new User("librarian1", "Librarian One", "librarian1", "password1", "librarian1@gmail.com",
                        "+355671234568", 800, LocalDate.of(1990, 5, 10), AccessLevel.LIBRARIAN),
                new User("librarian2", "Librarian Two", "librarian2", "password2", "librarian2@gmail.com",
                        "+355671234569", 900, LocalDate.of(1985, 7, 22), AccessLevel.LIBRARIAN)
        );
        librarianController = new LibrarianController(loginController, billController);
    }
    @Test
    void testGetLibrarians() {
        ObservableList<Librarian> librarians = librarianController.getLibrarians();

        assertEquals(2, librarians.size());
        assertEquals("librarian1", librarians.get(0).getUsername());
        assertEquals("librarian2", librarians.get(1).getUsername());
    }
}
