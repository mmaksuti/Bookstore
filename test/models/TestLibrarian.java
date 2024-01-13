package test.models;

import src.controllers.BillController;
import src.enums.AccessLevel;
import src.models.Librarian;

import src.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestLibrarian {
    BillController mockBillController;

    @BeforeEach
    void setUp() {
        mockBillController = mock(BillController.class);
    }

    @Test
    void test_constructorThrowsNotALibrarianException() {
        User user = new User("firstName", "lastName", "username", "password", "email", "phone", 100, null, AccessLevel.ADMINISTRATOR);
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class, () -> new Librarian(user, mockBillController));
        assertEquals("User is not a librarian", exc.getMessage());

        User user2 = new User("firstName", "lastName", "username", "password", "email", "phone", 100, null, AccessLevel.MANAGER);
        exc = assertThrows(IllegalArgumentException.class, () -> new Librarian(user2, mockBillController));
        assertEquals("User is not a librarian", exc.getMessage());
    }

    @Test
    void test_getNumberOfBills() {
        String[] billFileNames = {
                "01-01-2020.username.0.100.txt",
                "01-03-2020.username2.0.100.txt",
                "01-03-2020.username.0.150.txt",
                "01-02-2020.username.0.100.txt",
                "invalidbill"
        };
        when(mockBillController.loadBills()).thenReturn(billFileNames);

        Librarian librarian = new Librarian("firstName", "lastName", "username", "password", "email", "phone", 100, null, mockBillController);
        assertEquals(3, librarian.getNumberOfBills());
    }

    @Test
    void test_getTotalMoney() {
        String[] billFileNames = {
                "01-01-2020.username.0.100.txt",
                "01-03-2020.username2.0.100.txt",
                "01-03-2020.username.0.150.txt",
                "invalidbill",
                "01-02-2020.username.0.170.txt",
                "01-02-2020.username.0.notanumber.txt"
        };
        when(mockBillController.loadBills()).thenReturn(billFileNames);

        Librarian librarian = new Librarian("firstName", "lastName", "username", "password", "email", "phone", 100, null, mockBillController);
        assertEquals(420, librarian.getTotalMoney());
    }
}
