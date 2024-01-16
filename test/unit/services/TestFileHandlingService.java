package test.unit.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import src.services.FileHandlingService;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestFileHandlingService {
    @TempDir
    static File tempDir;

    @Test
    @Order(1)
    void test_writeObjectFromFile() {
        String objectToWrite = "Hello World!";
        String path = tempDir.getAbsolutePath() + "/test.dat";
        FileHandlingService fileHandlingService = new FileHandlingService();
        try {
            fileHandlingService.writeObjectToFile(path, objectToWrite);

            File file = new File(path);
            assertTrue(file.exists());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Order(2)
    void test_readObjectFromFile() {
        String path = tempDir.getAbsolutePath() + "/test.dat";
        FileHandlingService fileHandlingService = new FileHandlingService();
        try {
            String objectRead = (String)fileHandlingService.readObjectFromFile(path);
            assertEquals("Hello World!", objectRead);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    void test_deleteFile() {
        String path = tempDir.getAbsolutePath() + "/test.dat";
        FileHandlingService fileHandlingService = new FileHandlingService();
        try {
            fileHandlingService.deleteFile(path);
            File file = new File(path);
            assertFalse(file.exists());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Order(4)
    void test_writeFileContents() {
        String contents = "Hello World!";
        String path = tempDir.getAbsolutePath() + "/test.txt";
        FileHandlingService fileHandlingService = new FileHandlingService();
        try {
            fileHandlingService.writeFileContents(path, contents);
            File file = new File(path);
            assertTrue(file.exists());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Order(5)
    void test_readFileContents() {
        String path = tempDir.getAbsolutePath() + "/test.txt";
        FileHandlingService fileHandlingService = new FileHandlingService();
        try {
            String contents = fileHandlingService.readFileContents(path);
            assertEquals("Hello World!\n", contents);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Order(6)
    void test_listDirectory() {
        String path = tempDir.getAbsolutePath();
        FileHandlingService fileHandlingService = new FileHandlingService();
        try {
            String[] contents = fileHandlingService.listDirectory(path);
            assertEquals(1, contents.length);
            assertEquals("test.txt", contents[0]);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void test_ensureDirectoryNotExists() {
        String path = tempDir.getAbsolutePath() + "/test";
        FileHandlingService fileHandlingService = new FileHandlingService();
        try {
            boolean result = fileHandlingService.ensureDirectory(path);
            assertTrue(result);

            File file = new File(path);
            assertTrue(file.exists());
            assertTrue(file.isDirectory());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void test_ensureDirectoryExistsAsFile() {
        String path = tempDir.getAbsolutePath() + "/test";
        FileHandlingService fileHandlingService = new FileHandlingService();
        try {
            File file = new File(path);
            boolean result = file.delete();
            assertTrue(result);
            result = file.createNewFile();
            assertTrue(result);

            assertTrue(file.exists());
            assertFalse(file.isDirectory());

            result = fileHandlingService.ensureDirectory(path);
            assertTrue(result);

            assertTrue(file.exists());
            assertTrue(file.isDirectory());

            result = fileHandlingService.ensureDirectory(path);
            assertTrue(result);
            assertTrue(file.exists());
            assertTrue(file.isDirectory());

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
