package test.integration;
import src.models.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import src.services.FileHandlingService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TestStatisticsFileHandlingService {
    Statistics statistics;
    FileHandlingService fileHandlingService = new FileHandlingService();

    @TempDir
    static File tempDir;

    String BILLS = tempDir.getAbsolutePath() + "/bills";

    @AfterEach
    public void tearDown() {
        File dir = new File(BILLS);
        if (dir.isDirectory()) {
            File[] fileList = dir.listFiles();
            if (fileList != null) {
                for (File file : fileList) {
                    boolean deleted = file.delete();
                    assertTrue(deleted);
                }
            }
        }
        boolean deleted = dir.delete();
        assertTrue(deleted);
    }

    @Test
    void testConstructorDirectoryDoesntExist() {
        File dir = new File(BILLS);
        assertFalse(dir.exists());
        Statistics statistics = new Statistics(fileHandlingService, LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 1), BILLS);
        assertTrue(dir.exists());
    }

    @Test
    void testConstructorDirectoryIsFile() {
        File dir = new File(BILLS);
        try {
            boolean created = dir.createNewFile();
            assertTrue(created);
        }
        catch (IOException ex) {
            fail("Failed to create file: " + ex.getMessage());
        }

        assertTrue(dir.exists());
        assertFalse(dir.isDirectory());
        Statistics statistics = new Statistics(fileHandlingService, LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 1), BILLS);
        assertTrue(dir.isDirectory());
    }

    @Test
    void testConstructorDirectoryExists() {
        File dir = new File(BILLS);
        try {
            boolean created = dir.mkdir();
            assertTrue(created);
        }
        catch (Exception ex) {
            fail("Failed to create directory: " + ex.getMessage());
        }

        String[] billFileNames = {
                "01-01-2020.username.1.100.txt",
                "01-03-2020.username.2.150.txt",
                "01-02-2020.username.3.100.txt",
                "01-03-2020.username2.1.100.txt"
        };

        for (String fileName : billFileNames) {
            File file = new File(BILLS + "/" + fileName);
            try {
                boolean created = file.createNewFile();
                assertTrue(created);
            }
            catch (IOException ex) {
                fail("Failed to create file: " + ex.getMessage());
            }
        }

        Statistics statistics = new Statistics(fileHandlingService, LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 1), BILLS);
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
}
