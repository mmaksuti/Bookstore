package test.integration;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import src.controllers.BooksController;
import src.enums.Gender;
import src.enums.Genre;
import src.models.Book;
import src.services.FileHandlingService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestBooksControllerFileHandlingService {

    BooksController booksController;
    FileHandlingService fileHandlingService;

    @TempDir
    static File tempDir;

    String DATABASE;

    @BeforeEach
    public void setUp() {
        try {
            fileHandlingService = new FileHandlingService();
            DATABASE = tempDir.getAbsolutePath() + "/booksDatabase.dat";

            booksController = new BooksController(fileHandlingService, DATABASE);
        } catch (IOException ex) {
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
            fileHandlingService.deleteFile(DATABASE); // Ensure database file doesn't exist
            booksController = new BooksController(fileHandlingService);
            assertEquals(0, booksController.getBooks().size());
        } catch (IOException ex) {
            fail("Failed to set up databases: " + ex.getMessage());
        }
    }


    @Test
    void testConstructorInvalidDatabase() {
        try {
            fileHandlingService.writeFileContents(DATABASE, "invalid database");
            booksController = new BooksController(fileHandlingService, DATABASE);
            assertEquals(0, booksController.getBooks().size());
            assertFalse(new File(DATABASE).exists());
        } catch (IOException ex) {
            fail("Failed to set up databases: " + ex.getMessage());
        }
    }

    @Test
    void testAddBook() {
        try {
            booksController.addBook("Test Book", new src.models.Author("John", "Doe", Gender.MALE),
                    "123-4-567-34567-4", 29.99, "A test book", true, new ArrayList<>(), 10);

            ArrayList<Book> books= (ArrayList<Book>)fileHandlingService.readObjectFromFile(DATABASE);
            assertEquals(1, books.size());

            Book addedBook = books.get(0);
            assertEquals("Test Book", addedBook.getTitle());
            assertEquals("John Doe", addedBook.getAuthor().toString());
            assertEquals("123-4-567-34567-4", addedBook.getIsbn13());
            assertEquals(29.99, addedBook.getPrice());
            assertEquals("A test book", addedBook.getDescription());
            assertTrue(addedBook.isPaperback());
            assertEquals(10, addedBook.getQuantity());
        } catch (IOException ex) {
            fail("Failed to add book: " + ex.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testRemoveBook() {
        try {
            booksController.addBook("Test Book", new src.models.Author("John", "Doe",Gender.MALE),
                    "123-4-567-34567-4", 29.99, "A test book", true, new ArrayList<>(), 10);

            ObservableList<Book> books = booksController.getBooks();
            assertEquals(1, books.size());

            Book book = books.get(0);
            booksController.removeBook(book);
        } catch (IOException ex) {
            fail("Failed to remove book: " + ex.getMessage());
        }
    }

    @Test
    void testUpdateBook() {
        try {
            booksController.addBook("Test Book", new src.models.Author("John", "Doe",Gender.MALE),
                    "123-4-567-34567-4", 29.99, "A test book", true, new ArrayList<>(), 10);

            ObservableList<Book> books = booksController.getBooks();
            assertEquals(1, books.size());

            Book book = books.get(0);
            booksController.updateBook(book, "Updated Book", new src.models.Author("Jane", "Doe",Gender.MALE),
                    "123-4-567-34567-4", 39.99, "An updated book", false, new ArrayList<>(), 20);

            Book updatedBook = booksController.getBooks().get(0);
            assertEquals("Updated Book", updatedBook.getTitle());
            assertEquals("Jane Doe", updatedBook.getAuthor().toString());
            assertEquals("123-4-567-34567-4", updatedBook.getIsbn13());
            assertEquals(39.99, updatedBook.getPrice());
            assertEquals("An updated book", updatedBook.getDescription());
            assertFalse(updatedBook.isPaperback());
            assertEquals(20, updatedBook.getQuantity());
        } catch (IOException ex) {
            fail("Failed to update book: " + ex.getMessage());
        }
    }
}
