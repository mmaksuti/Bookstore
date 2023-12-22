package test.integration;
import controllers.AuthorsController;
import controllers.BooksController;
import enums.Gender;
import models.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import services.FileHandlingService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestAuthorsControllers {

    AuthorsController authorsController;
    FileHandlingService fileHandlingService;

    @TempDir
    static File tempDir;

    String DATABASE;

    @BeforeEach
    public void setUp()  {
        try {
            fileHandlingService = new FileHandlingService();
            //stubBooksController = mock(BooksController.class);

            DATABASE = tempDir.getAbsolutePath() + "/authorsDatabase.dat";

            authorsController = new AuthorsController(fileHandlingService, DATABASE);
        }
        catch (IOException ex) {
            fail("Failed to set up databases: " + ex.getMessage());
        }
    }
    
    @Test
    void testAddAuthor() {
        try {
            authorsController.addAuthor("John", "Doe", Gender.MALE);

            ArrayList<Author> authors = (ArrayList<Author>)fileHandlingService.readObjectFromFile(DATABASE);
            assertEquals(1, authors.size());

            Author author = authors.get(0);
            assertEquals("John", author.getFirstName());
            assertEquals("Doe", author.getLastName());
            assertEquals(Gender.MALE, author.getGender());
        }
        catch (IOException ex) {
            fail("Failed to add author: " + ex.getMessage());
        }
        catch (ClassNotFoundException ex) {
            fail("Failed to load database: " + ex.getMessage());
        }
    }
}
