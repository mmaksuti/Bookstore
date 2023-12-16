package test;

import controllers.BooksController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.Author;
import main.Book;
import main.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.mocks.MockDatabaseController;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestBooksController {

    private BooksController booksController;
    private MockDatabaseController mockDbController;
    private ObservableList<Book> books;

    @BeforeEach
    public void setUp() {
        try {
            mockDbController = new MockDatabaseController(new ArrayList<Book>());
            booksController = new BooksController(mockDbController);
            books = FXCollections.observableArrayList();
        } catch (IOException ex) {
            fail("Failed to set up databases: " + ex.getMessage());
        }
    }

    @Test
    void testAddBook() {
        Author author = new Author("John", "Doe");
        String isbn = "678-7-345-45678-8";
        double price = 25.0;
        String description = "Test Book";
        boolean isPaperback = true;
        ArrayList<Genre> genres = new ArrayList<>();
        int quantity = 10;

        try {
            booksController.addBook("Test Book", author, isbn, price, description, isPaperback, genres, quantity);
        } catch (IOException ex) {
            fail("Failed to add book: " + ex.getMessage());
        }

        assertEquals(1, booksController.getBooks().size());

        Book book = booksController.getBooks().get(0);
        assertEquals("Test Book", book.getTitle());
        assertEquals(author, book.getAuthor());
        assertEquals(isbn, book.getIsbn13());
        assertEquals(price, book.getPrice());
        assertEquals(description, book.getDescription());
        assertEquals(isPaperback, book.isPaperback());
        assertEquals(genres, book.getGenres());
        assertEquals(quantity, book.getQuantity());
    }

    @Test
    void testAddBookThrowsFillInAllValuesException() {
        Author author = new Author("John", "Doe");
        double price = 25.0;
        String description = "Test Book";
        boolean isPaperback = true;
        ArrayList<Genre> genres = new ArrayList<>();
        int quantity = 10;

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> booksController.addBook("", author, "", price, description, isPaperback, genres, quantity));
        assertEquals("Please fill in all fields", exc.getMessage());

        assertEquals(0, booksController.getBooks().size());
    }

    @Test
    void testAddBookThrowsBookExistsException() {
        Author author = new Author("John", "Doe");
        String isbn = "678-7-345-45678-8";
        double price = 25.0;
        String description = "Test Book";
        boolean isPaperback = true;
        ArrayList<Genre> genres = new ArrayList<>();
        int quantity = 10;

        try {
            booksController.addBook("Test Book", author, isbn, price, description, isPaperback, genres, quantity);
        } catch (IOException ex) {
            fail("Failed to add book: " + ex.getMessage());
        }

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> booksController.addBook("Duplicate ISBN Book", author, isbn, price, description, isPaperback, genres, quantity));
        assertEquals("A book with the same ISBN13 already exists", exc.getMessage());
        assertEquals(1, booksController.getBooks().size());
    }

    @Test
    void testUpdateBook() {
        Author author = new Author("John", "Doe");
        String isbn = "678-7-345-45678-8";
        double price = 25.0;
        String description = "Test Book";
        boolean isPaperback = true;
        ArrayList<Genre> genres = new ArrayList<>();
        int quantity = 10;

        try {
            booksController.addBook("Test Book", author, isbn, price, description, isPaperback, genres, quantity);
        } catch (IOException ex) {
            fail("Failed to add book: " + ex.getMessage());
        }

        Book book = booksController.getBooks().get(0);
        try {
            booksController.updateBook(book, "Updated Book", author, isbn, 30.0, "Updated Description", false, genres, 15);
        } catch (IOException e) {
            fail("Failed to update book: " + e.getMessage());
        }

        assertEquals(1, booksController.getBooks().size());

        assertEquals("Updated Book", book.getTitle());
        assertEquals(author, book.getAuthor());
        assertEquals(isbn, book.getIsbn13());
        assertEquals(30.0, book.getPrice());
        assertEquals("Updated Description", book.getDescription());
        assertFalse(book.isPaperback());
        assertEquals(genres, book.getGenres());
        assertEquals(15, book.getQuantity());
    }

    @Test
    void testUpdateBookThrowsFillInAllValuesException() {
        Author author = new Author("John", "Doe");
        double price = 25.0;
        String description = "Test Book";
        boolean isPaperback = true;
        ArrayList<Genre> genres = new ArrayList<>();
        int quantity = 10;

        try {
            booksController.addBook("Test Book", author, "678-7-345-45678-8", price, description, isPaperback, genres, quantity);
        } catch (IOException ex) {
            fail("Failed to add book: " + ex.getMessage());
        }

        Book book = booksController.getBooks().get(0);

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> booksController.updateBook(book, "", null, "", -1.0, "", true, new ArrayList<>(), -5));
        assertEquals("Please fill in all fields", exc.getMessage());

        assertEquals("Test Book", book.getTitle());
        assertEquals(author, book.getAuthor());
        assertEquals("678-7-345-45678-8", book.getIsbn13());
        assertEquals(price, book.getPrice());
        assertEquals(description, book.getDescription());
        assertTrue(book.isPaperback());
        assertEquals(genres, book.getGenres());
        assertEquals(quantity, book.getQuantity());
    }

    @Test
    void testUpdateBookThrowsISBNExistsException() {
        Author author = new Author("John", "Doe");
        String isbn = "678-7-345-45678-8";
        double price = 25.0;
        String description = "Test Book";
        boolean isPaperback = true;
        ArrayList<Genre> genres = new ArrayList<>();
        int quantity = 10;

        try {
            booksController.addBook("Test Book", author, isbn, price, description, isPaperback, genres, quantity);
        } catch (IOException ex) {
            fail("Failed to add book: " + ex.getMessage());
        }

        Book existingBook = booksController.getBooks().get(0);

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> booksController.addBook("Duplicate ISBN Book", author, isbn, price, description, isPaperback, genres, quantity));
        assertEquals("A book with the same ISBN13 already exists", exc.getMessage());
        assertEquals(1, booksController.getBooks().size());
    }

    @Test
    void testRemoveBook() {
        Author author = new Author("John", "Doe");
        String isbn = "678-7-345-45678-8";
        double price = 25.0;
        String description = "Test Book";
        boolean isPaperback = true;
        ArrayList<Genre> genres = new ArrayList<>();
        int quantity = 10;

        try {
            booksController.addBook("Test Book", author, isbn, price, description, isPaperback, genres, quantity);
        } catch (IOException ex) {
            fail("Failed to add book: " + ex.getMessage());
        }

        Book book = booksController.getBooks().get(0);
        try {
            booksController.removeBook(book);
        } catch (IOException ex) {
            fail("Failed to remove book: " + ex.getMessage());
        }

        assertEquals(0, booksController.getBooks().size());
    }




}
