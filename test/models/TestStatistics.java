package test.models;

import models.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.FileHandlingService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestStatistics {
    FileHandlingService mockFileHandlingService;

    @BeforeEach
    void setUp() {
        mockFileHandlingService = mock(FileHandlingService.class);
    }

    @Test
    void test_toStringOnlyInRangeBills() {
        String[] billFileNames = {
                "01-01-2020.username.1.100.txt",
                "01-02-2020.username.3.100.txt"
        };
        when(mockFileHandlingService.listDirectory("bills")).thenReturn(billFileNames);
        when(mockFileHandlingService.ensureDirectory("bills")).thenReturn(true);

        Statistics statistics = new Statistics(mockFileHandlingService, LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 1));
        String expectedStatistics =
                "Date: 01-01-2020\n" +
                "User: username\n" +
                "Books sold: 1\n" +
                "Money earned: 100.0\n\n" +

                "Date: 01-02-2020\n" +
                "User: username\n" +
                "Books sold: 3\n" +
                "Money earned: 100.0\n\n" +

                "Total money earned: 200.0";

        assertEquals(expectedStatistics, statistics.toString());
    }

    @Test
    void test_toStringMixedBills() {
        String[] billFileNames = {
                "01-01-2020.username.1.100.txt",
                "01-03-2020.username.2.150.txt",
                "01-02-2020.username.3.100.txt",
                "01-03-2020.username2.1.100.txt"
        };
        when(mockFileHandlingService.listDirectory("bills")).thenReturn(billFileNames);
        when(mockFileHandlingService.ensureDirectory("bills")).thenReturn(true);

        Statistics statistics = new Statistics(mockFileHandlingService, LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 1));
        String expectedStatistics =
                "Date: 01-01-2020\n" +
                "User: username\n" +
                "Books sold: 1\n" +
                "Money earned: 100.0\n\n" +

                "Date: 01-02-2020\n" +
                "User: username\n" +
                "Books sold: 3\n" +
                "Money earned: 100.0\n\n" +

                "Total money earned: 200.0";

        assertEquals(expectedStatistics, statistics.toString());
    }

    @Test
    void test_toStringNoBillsInRange() {
        String[] billFileNames = {
                "01-01-2020.username.1.100.txt",
                "01-03-2020.username.2.150.txt",
                "01-07-2020.username2.1.200.txt",
                "01-06-2020.username2.1.100.txt"
        };
        when(mockFileHandlingService.listDirectory("bills")).thenReturn(billFileNames);
        when(mockFileHandlingService.ensureDirectory("bills")).thenReturn(true);

        Statistics statistics = new Statistics(mockFileHandlingService, LocalDate.of(2020, 4, 1), LocalDate.of(2020, 5, 1));
        assertEquals("No bills\nTotal money earned: 0", statistics.toString());
    }

    @Test
    void test_toStringInvalidBills() {
        String[] billFileNames = {
                "invalidbill.txt",
                "invalidbill",
                "01-01-2020.txt",
                "01-01-2020.username.txt",
                "01-01-2020.username.notanumber.txt",
        };

        when(mockFileHandlingService.listDirectory("bills")).thenReturn(billFileNames);
        when(mockFileHandlingService.ensureDirectory("bills")).thenReturn(true);

        Statistics statistics = new Statistics(mockFileHandlingService, LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 1));
        assertEquals("No bills\nTotal money earned: 0", statistics.toString());
    }

    @Test
    void test_toStringNoBills() {
        String[] billFileNames = {};
        when(mockFileHandlingService.listDirectory("bills")).thenReturn(billFileNames);
        when(mockFileHandlingService.ensureDirectory("bills")).thenReturn(true);

        Statistics statistics = new Statistics(mockFileHandlingService, LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 1));
        assertEquals("No bills\nTotal money earned: 0", statistics.toString());

        when(mockFileHandlingService.listDirectory("bills")).thenReturn(null);
        statistics = new Statistics(mockFileHandlingService, LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 1));
        assertEquals("No bills\nTotal money earned: 0", statistics.toString());
    }

    @Test
    void test_toStringNotADirectory() {
        when(mockFileHandlingService.ensureDirectory("bills")).thenReturn(false);

        IllegalStateException exc = assertThrows(IllegalStateException.class, () -> {
            Statistics statistics = new Statistics(mockFileHandlingService, LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 1));
        });
        assertEquals("bills not a directory", exc.getMessage());
    }
}
