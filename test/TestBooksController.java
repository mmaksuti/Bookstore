package test;

import controllers.BooksController;
import main.Author;
import main.Gender;
import main.Book;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestBooksController {
    public BooksController booksController;
    public static final String TEST_BOOKS_DATABASE = "test/testBooksDatabase.dat";

    // set up the database files
    @BeforeEach
    public void setUp() {
        try {
            booksController = new BooksController();
            booksController.setDatabase(TEST_BOOKS_DATABASE);
        } catch (IOException ex) {
            fail("Failed to set up databases: " + ex.getMessage());
        }
    }
    // clear everything after each test so each test starts from scratch
    @AfterEach
    public void tearDown() {
        File booksDatabase = new File(TEST_BOOKS_DATABASE);

        if (booksDatabase.exists()) {
            boolean deleted = booksDatabase.delete();
            if (!deleted) {
                fail("Failed to delete books database");
            }
        }
    }

    @Test
    void testAddBook() {
        try {
            booksController.addBook("Title", new Author("John", "Doe", Gender.MALE), "123-4-789-09123-0", 20.0, "Description", true, new ArrayList<>(), 10);
        } catch (IOException ex) {
            fail("Failed to add a valid book: " + ex.getMessage());
        }
        assertEquals(1, booksController.books.size());
        assertThrows(IllegalArgumentException.class, () ->
                booksController.addBook("Title", new Author("Jane", "Doe", Gender.FEMALE), "123-4-789-09123-0", 15.0, "Description", false, new ArrayList<>(), 5)
        );

        assertThrows(IllegalArgumentException.class, () ->
                booksController.addBook("Invalid ISBN", new Author("John", "Doe", Gender.MALE), "invalid_isbn", 15.0, "Description", false, new ArrayList<>(), 5)
        );

        assertThrows(IllegalArgumentException.class, () ->
                booksController.addBook("Title", new Author("John", "Doe", Gender.MALE), "123-4-789-09123-088", 25.0, "Description", true, new ArrayList<>(), -5)
        );
    }
    @Test
    void testUpdateBook() {
        try {
            booksController.addBook("Title", new Author("John", "Doe", Gender.MALE), "123-4-729-29123-0", 20.0, "Description", true, new ArrayList<>(), 10);
            Book bookToUpdate = booksController.books.get(0);
            booksController.updateBook(bookToUpdate, "Updated Title", new Author("Jane", "Doe", Gender.FEMALE), "123-4-729-09123-0", 25.0, "Updated Description", false, new ArrayList<>(), 15);
            assertEquals("Updated Title", bookToUpdate.getTitle());
            assertEquals("Jane", bookToUpdate.getAuthor().getFirstName());
            assertEquals("Doe", bookToUpdate.getAuthor().getLastName());
            assertEquals("123-4-729-09123-0", bookToUpdate.getIsbn13());
            assertEquals(25.0, bookToUpdate.getPrice());
            assertEquals("Updated Description", bookToUpdate.getDescription());
            assertFalse(bookToUpdate.isPaperback());
            assertEquals(15, bookToUpdate.getQuantity());


            assertThrows(IllegalArgumentException.class, () ->
                    booksController.updateBook(bookToUpdate, "Another Title", new Author("John", "Doe", Gender.MALE), "193-4-729-0923-0", 15.0, "Another Description", true, new ArrayList<>(), 5)
            );
        } catch (IOException ex) {
            fail("Failed to update book: " + ex.getMessage());
        }
    }

    @Test
    void testRemoveBook() {
        try {
            booksController.addBook("Title", new Author("John", "Doe", Gender.MALE), "123-4-789-09123-0", 20.0, "Description", true, new ArrayList<>(), 10);
            Book bookToRemove = booksController.books.get(0);
            System.out.println("Before removal: " + booksController.books);
            booksController.removeBook(bookToRemove);
            System.out.println("After removal: " + booksController.books);

            assertEquals(0, booksController.books.size());

        } catch (IOException ex) {
            fail("Failed to remove book: " + ex.getMessage());
        }
    }
}
