package test;

import controllers.BillController;
import controllers.LibrarianController;
import controllers.LoginController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.AccessLevel;
import main.Librarian;
import main.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TestLibrarianController {

    private LibrarianController librarianController;
    private LoginController mockLoginController;
    private BillController mockBillController;

    @BeforeEach
    public void setUp() {
        mockLoginController = Mockito.mock(LoginController.class);
        when(mockLoginController.getUsers()).thenReturn(FXCollections.observableArrayList());

        mockBillController = Mockito.mock(BillController.class);
        librarianController = new LibrarianController(mockLoginController, mockBillController);
    }

    @Test
    public void testGetLibrariansNoLibrarians() {
        ObservableList<Librarian> librarians = librarianController.getLibrarians();
        assertEquals(0, librarians.size());
    }

    @Test
    public void testGetLibrarians() {
        ObservableList<User> users = FXCollections.observableArrayList();
        users.add(new User("Admin1", "Lastname1", "admin1", "password", "admin1@example.com", "+123456789", 1000, LocalDate.of(1990, 1, 1), AccessLevel.ADMINISTRATOR));
        users.add(new User("Librarian1", "Lastname2", "librarian1", "password", "librarian1@example.com", "+123456789", 1000, LocalDate.of(1990, 1, 1), AccessLevel.LIBRARIAN));
        when(mockLoginController.getUsers()).thenReturn(users);

        LibrarianController controller = new LibrarianController(mockLoginController, mockBillController);

        ObservableList<Librarian> librarians = controller.getLibrarians();
        assertEquals(1, librarians.size());
        assertEquals("Librarian1", librarians.get(0).getFirstName());
        assertEquals("Lastname2", librarians.get(0).getLastName());
    }

//    @Test
//    public void testGetLibrarians() {
//        ObservableList<Librarian> librarians = librarianController.getLibrarians();
//        assertEquals(0, librarians.size());
//        Librarian librarian = mock(Librarian.class);
//        librarians.add(librarian);
//        assertEquals(1, librarians.size());
//    }

    @Test
    public void testListChangeListener() {
        ObservableList<User> users = FXCollections.observableArrayList();
        when(mockLoginController.getUsers()).thenReturn(users);

        LibrarianController controller = new LibrarianController(mockLoginController, mockBillController);

        users.add(new User("Admin1", "Lastname1", "admin1", "password", "admin1@example.com", "+123456789", 1000, LocalDate.of(1990, 1, 1), AccessLevel.ADMINISTRATOR));
        users.add(new User("Librarian1", "Lastname2", "librarian1", "password", "librarian1@example.com", "+123456789", 1000, LocalDate.of(1990, 1, 1), AccessLevel.LIBRARIAN));
        ObservableList<Librarian> librarians = controller.getLibrarians();
        assertEquals(1, librarians.size());
        assertEquals("Librarian1", librarians.get(0).getFirstName());
        assertEquals("Lastname2", librarians.get(0).getLastName());
    }
}
