package test.controllers;

import controllers.BillController;
import services.FileHandlingService;
import models.Bill;
import models.Librarian;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import java.io.IOException;

public class TestBillController {
    FileHandlingService mockFileHandlingService;
    BillController billController;

    @BeforeEach
    void setUp() {
        mockFileHandlingService = mock(FileHandlingService.class);
        billController = new BillController(mockFileHandlingService);
    }

    @Test
    void test_loadBills() {
        String[] cannedDirectoryContents = {};
        when(mockFileHandlingService.listDirectory("bills")).thenReturn(cannedDirectoryContents);
        when(mockFileHandlingService.ensureDirectory("bills")).thenReturn(true);

        String[] bills = billController.loadBills();
        assertEquals(0, bills.length);

        cannedDirectoryContents = new String[] {
                "21-12-2023.librarian.1.1000,0.0.txt",
                "21-12-2023.librarian.1.1000,0.1.txt"
        };
        when(mockFileHandlingService.listDirectory("bills")).thenReturn(cannedDirectoryContents);

        bills = billController.loadBills();
        assertArrayEquals(cannedDirectoryContents, bills);
    }

    @Test
    void test_loadBillsThrowsBillNotADirectoryException() {
        when(mockFileHandlingService.ensureDirectory("bills")).thenReturn(false);

        IllegalStateException exc = assertThrows(IllegalStateException.class, () -> billController.loadBills());
        assertEquals("bills not a directory", exc.getMessage());
    }

    @Test
    void test_deleteBillsNoBills() {
        when(mockFileHandlingService.ensureDirectory("bills")).thenReturn(true);

        Librarian librarian = mock(Librarian.class);
        when(librarian.getUsername()).thenReturn("librarian");

        String[] cannedDirectoryContents = {};
        when(mockFileHandlingService.listDirectory("bills")).thenReturn(cannedDirectoryContents);

        billController.deleteBills(librarian);
        assertEquals(0, billController.loadBills().length);
        verify(mockFileHandlingService, never()).deleteFile(any(String.class));
    }

    @Test
    void test_deleteBillsOnlyOtherBills() {
        Librarian librarian = mock(Librarian.class);
        when(librarian.getUsername()).thenReturn("librarian");

        String[] cannedDirectoryContents = new String[] {
                "21-12-2023.librarian2.1.1000,0.0.txt",
                "21-12-2023.librarian2.1.1000,0.1.txt"
        };

        when(mockFileHandlingService.ensureDirectory("bills")).thenReturn(true);
        when(mockFileHandlingService.listDirectory("bills")).thenReturn(cannedDirectoryContents);
        when(mockFileHandlingService.deleteFile(any(String.class))).thenReturn(true);

        billController.deleteBills(librarian);

        verify(mockFileHandlingService, never()).deleteFile(any(String.class));
    }

    @Test
    void test_deleteBillsOnlyCorrectBills() {
        Librarian librarian = mock(Librarian.class);
        when(librarian.getUsername()).thenReturn("librarian");

        String[] cannedDirectoryContents = new String[] {
                "21-12-2023.librarian.1.1000,0.0.txt",
                "21-12-2023.librarian.1.1000,0.1.txt"
        };

        when(mockFileHandlingService.ensureDirectory("bills")).thenReturn(true);
        when(mockFileHandlingService.deleteFile(any(String.class))).thenReturn(true);
        when(mockFileHandlingService.listDirectory("bills")).thenReturn(cannedDirectoryContents);

        billController.deleteBills(librarian);

        verify(mockFileHandlingService, times(2)).deleteFile(any(String.class));
        verify(mockFileHandlingService, times(1)).deleteFile("bills/" + cannedDirectoryContents[0]);
        verify(mockFileHandlingService, times(1)).deleteFile("bills/" + cannedDirectoryContents[1]);
    }

    @Test
    void test_deleteBillsMixedBills() {
        Librarian librarian = mock(Librarian.class);
        when(librarian.getUsername()).thenReturn("librarian");

        String[] cannedDirectoryContents = new String[] {
                "21-12-2023.librarian.1.1000,0.0.txt",
                "21-12-2023.librarian2.1.1000,0.1.txt"
        };

        when(mockFileHandlingService.ensureDirectory("bills")).thenReturn(true);
        when(mockFileHandlingService.deleteFile(any(String.class))).thenReturn(true);
        when(mockFileHandlingService.listDirectory("bills")).thenReturn(cannedDirectoryContents);

        billController.deleteBills(librarian);

        verify(mockFileHandlingService, times(1)).deleteFile(any(String.class));
        verify(mockFileHandlingService, times(1)).deleteFile("bills/" + cannedDirectoryContents[0]);
    }

    @Test
    void test_deleteBillsInvalidBills() {
        Librarian librarian = mock(Librarian.class);
        when(librarian.getUsername()).thenReturn("librarian");

        String[] cannedDirectoryContents = new String[] {
                "Invalid bill 1",
                "Invalid bill 2"
        };

        when(mockFileHandlingService.ensureDirectory("bills")).thenReturn(true);
        when(mockFileHandlingService.deleteFile(any(String.class))).thenReturn(true);
        when(mockFileHandlingService.listDirectory("bills")).thenReturn(cannedDirectoryContents);

        billController.deleteBills(librarian);

        verify(mockFileHandlingService, never()).deleteFile(any(String.class));
    }

    @Test
    void test_deleteBillsThrowsFailedToDeleteBillFileException() {
        Librarian librarian = mock(Librarian.class);
        when(librarian.getUsername()).thenReturn("librarian");

        String[] cannedDirectoryContents = new String[] {
                "21-12-2023.librarian.1.1000,0.0.txt",
                "21-12-2023.librarian2.1.1000,0.1.txt"
        };

        when(mockFileHandlingService.ensureDirectory("bills")).thenReturn(true);
        when(mockFileHandlingService.deleteFile(any(String.class))).thenReturn(false);
        when(mockFileHandlingService.listDirectory("bills")).thenReturn(cannedDirectoryContents);

        IllegalStateException exc = assertThrows(IllegalStateException.class, () -> billController.deleteBills(librarian));
        assertEquals("Failed to delete bill file: " + cannedDirectoryContents[0], exc.getMessage());

        verify(mockFileHandlingService, times(1)).deleteFile(any(String.class));
    }

    @Test
    void test_saveBillNoBills() {
        Bill bill = mock(Bill.class);
        when(bill.getDate()).thenReturn("21-12-2023");
        when(bill.getUsername()).thenReturn("librarian");
        when(bill.getNBooks()).thenReturn(2);
        when(bill.getTotalPrice()).thenReturn(1000.0);
        when(bill.getTextBill()).thenReturn("Bill text");

        String[] cannedDirectoryContents = {};

        when(mockFileHandlingService.ensureDirectory("bills")).thenReturn(true);
        when(mockFileHandlingService.listDirectory("bills")).thenReturn(cannedDirectoryContents);

        try {
            billController.saveBill(bill);

            verify(mockFileHandlingService, times(1)).writeFileContents("bills/21-12-2023.librarian.2.1000,0.0.txt", "Bill text");
        }
        catch (IOException ignored) {
        }
    }

    @Test
    void test_saveBillHasBills() {
        Bill bill = mock(Bill.class);
        when(bill.getDate()).thenReturn("21-12-2023");
        when(bill.getUsername()).thenReturn("librarian");
        when(bill.getNBooks()).thenReturn(2);
        when(bill.getTotalPrice()).thenReturn(1000.0);
        when(bill.getTextBill()).thenReturn("Bill text");

        String[] cannedDirectoryContents = new String[] {
                "21-12-2023.librarian2.1.1000,0.0.txt",
                "21-12-2023.librarian.1.1000,0.0.txt",
                "21-12-2023.librarian.1.1000,0.1.txt"
        };

        when(mockFileHandlingService.ensureDirectory("bills")).thenReturn(true);
        when(mockFileHandlingService.listDirectory("bills")).thenReturn(cannedDirectoryContents);

        try {
            billController.saveBill(bill);

            verify(mockFileHandlingService, times(1)).writeFileContents("bills/21-12-2023.librarian.2.1000,0.2.txt", "Bill text");
        }
        catch (IOException ignored) {
        }
    }
}
