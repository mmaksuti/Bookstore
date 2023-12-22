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
    void test_toString() {
        String[] billFileNames = {"01-01-2020.username.1.100.txt", "02-01-2020.username2.1.200.txt", "invalidbill"};
        when(mockFileHandlingService.listDirectory("bills")).thenReturn(billFileNames);
        when(mockFileHandlingService.ensureDirectory("bills")).thenReturn(true);

        Statistics statistics = new Statistics(mockFileHandlingService, LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 1));
        String expectedStatistics =
                "Date: 01-01-2020\n" +
                "User: username\n" +
                "Books sold: 1\n" +
                "Money earned: 100.0\n\n" +

                "Date: 02-01-2020\n" +
                "User: username2\n" +
                "Books sold: 1\n" +
                "Money earned: 200.0\n\n" +
                "Total money earned: 300.0";

        assertEquals(expectedStatistics, statistics.toString());
    }
}
