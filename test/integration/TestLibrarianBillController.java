package test.integration;
import src.controllers.BillController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.models.Librarian;
import src.services.FileHandlingService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestLibrarianBillController {
    FileHandlingService stubFileHandlingService;
    String BILLS = "bills";

    @BeforeEach
    void setUp() {
        stubFileHandlingService = mock(FileHandlingService.class);
        when(stubFileHandlingService.ensureDirectory(BILLS)).thenReturn(true);
    }

    @Test
    void testGetNumberOfBillsNoBIlls() {
        String[] billFileNames = {
        };
        when(stubFileHandlingService.listDirectory(BILLS)).thenReturn(billFileNames);

        BillController billController = new BillController(stubFileHandlingService);
        Librarian librarian = new Librarian("firstName", "lastName", "username", "password", "email", "phone", 100, null, billController);
        assertEquals(0, librarian.getNumberOfBills());
    }

    @Test
    void testGetNumberOfBillsHasBills() {
        String[] billFileNames = {
                "01-01-2020.username.0.100.txt",
                "01-03-2020.username2.0.100.txt",
                "01-03-2020.username.0.150.txt",
                "invalidbill",
                "01-02-2020.username.0.170.txt",
                "01-02-2020.username.0.notanumber.txt"
        };
        when(stubFileHandlingService.listDirectory(BILLS)).thenReturn(billFileNames);

        BillController billController = new BillController(stubFileHandlingService);
        Librarian librarian = new Librarian("firstName", "lastName", "username", "password", "email", "phone", 100, null, billController);
        assertEquals(4, librarian.getNumberOfBills());
    }

    @Test
    void testGetTotalMoneyNoBills() {
        String[] billFileNames = {
        };
        when(stubFileHandlingService.listDirectory(BILLS)).thenReturn(billFileNames);

        BillController billController = new BillController(stubFileHandlingService);
        Librarian librarian = new Librarian("firstName", "lastName", "username", "password", "email", "phone", 100, null, billController);
        assertEquals(0, librarian.getTotalMoney());
    }

    @Test
    void testGetTotalMoneyHasBills() {
        String[] billFileNames = {
                "01-01-2020.username.0.100.txt",
                "01-03-2020.username2.0.100.txt",
                "01-03-2020.username.0.150.txt",
                "invalidbill",
                "01-02-2020.username.0.170.txt",
                "01-02-2020.username.0.notanumber.txt"
        };
        when(stubFileHandlingService.listDirectory(BILLS)).thenReturn(billFileNames);

        BillController billController = new BillController(stubFileHandlingService);
        Librarian librarian = new Librarian("firstName", "lastName", "username", "password", "email", "phone", 100, null, billController);
        assertEquals(420, librarian.getTotalMoney());
    }
}
