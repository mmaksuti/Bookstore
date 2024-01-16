package test.integration;
import src.controllers.AuthorsController;
import src.controllers.BooksController;
import src.enums.Gender;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import src.models.Author;
import src.models.Book;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import src.services.FileHandlingService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestAuthorsControllerFileHandlingService {

    AuthorsController authorsController;
    FileHandlingService fileHandlingService;
    BooksController stubBooksController;
    ObservableList<Book> books;

    @TempDir
    static File tempDir;

    String DATABASE;

    @BeforeEach
    public void setUp()  {
        try {
            fileHandlingService = new FileHandlingService();

            DATABASE = tempDir.getAbsolutePath() + "/authorsDatabase.dat";

            authorsController = new AuthorsController(fileHandlingService, DATABASE);
            stubBooksController = mock(BooksController.class);
            books = FXCollections.observableArrayList();
        }
        catch (IOException ex) {
            fail("Failed to set up databases: " + ex.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        fileHandlingService.deleteFile(DATABASE);
    }

    @Test
    void testConstructorNoDatabase() {
        try {
            assertFalse(new File(DATABASE).exists());
            authorsController = new AuthorsController(fileHandlingService, DATABASE);
            assertEquals(0, authorsController.getAuthors().size());
        }
        catch (IOException ex) {
            fail("Failed to set up databases: " + ex.getMessage());
        }
    }

    @Test
    void testConstructorNoAuthors() {
        try {
            ArrayList<Author> authors = new ArrayList<>();
            fileHandlingService.writeObjectToFile(DATABASE, authors);
            authorsController = new AuthorsController(fileHandlingService, DATABASE);
            assertEquals(0, authorsController.getAuthors().size());
        }
        catch (IOException ex) {
            fail("Failed to set up databases: " + ex.getMessage());
        }
    }

    @Test
    void testConstructorWithAuthors() {
        try {
            ArrayList<Author> authors = new ArrayList<>();

            Author author1 = new Author("John", "Doe", Gender.MALE);
            Author author2 = new Author("Jane", "Doe", Gender.MALE);
            authors.add(author1);
            authors.add(author2);

            fileHandlingService.writeObjectToFile(DATABASE, authors);
            authorsController = new AuthorsController(fileHandlingService, DATABASE);
            assertEquals(2, authorsController.getAuthors().size());
            author1 = authorsController.getAuthors().get(0);
            author2 = authorsController.getAuthors().get(1);
            assertEquals("John", author1.getFirstName());
            assertEquals("Doe", author1.getLastName());
            assertEquals("Jane", author2.getFirstName());
            assertEquals("Doe", author2.getLastName());
        }
        catch (IOException ex) {
            fail("Failed to set up databases: " + ex.getMessage());
        }
    }

    @Test
    void testConstructorInvalidDatabase() {
        try {
            fileHandlingService.writeFileContents(DATABASE, "invalid database");
            authorsController = new AuthorsController(fileHandlingService, DATABASE);
            assertEquals(0, authorsController.getAuthors().size());
            assertFalse(new File(DATABASE).exists());
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

    @Test
    void testRemoveAuthor() {
        try {
            authorsController.addAuthor("John", "Doe", Gender.MALE);
            when(stubBooksController.getBooks()).thenReturn(books);

            Author author =  authorsController.getAuthors().get(0);
            authorsController.removeAuthor(stubBooksController, author, null);

            ArrayList<Author> authors = (ArrayList<Author>)fileHandlingService.readObjectFromFile(DATABASE);
            assertEquals(0, authors.size());
        }
        catch (IOException ex) {
            fail("Failed to remove author: " + ex.getMessage());
        }
        catch (ClassNotFoundException ex) {
            fail("Failed to load database: " + ex.getMessage());
        }
    }

    void testUpdateAuthor() {
        try {
            authorsController.addAuthor("John", "Doe", Gender.MALE);

            Author author = authorsController.getAuthors().get(0);
            authorsController.updateAuthor(author, "Jane", "Doe", Gender.FEMALE, stubBooksController);

            ArrayList<Author> authors = (ArrayList<Author>)fileHandlingService.readObjectFromFile(DATABASE);
            assertEquals(1, authors.size());

            Author updatedAuthor = authors.get(0);
            assertEquals("Jane", updatedAuthor.getFirstName());
            assertEquals("Doe", updatedAuthor.getLastName());
            assertEquals(Gender.FEMALE, updatedAuthor.getGender());
        } catch (IOException ex) {
            fail("Failed to update author: " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            fail("Failed to load database: " + ex.getMessage());
        }
    }
}
