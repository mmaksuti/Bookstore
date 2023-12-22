package test.controllers;

import controllers.BooksController;
import services.FileHandlingService;
import models.Author;
import models.Book;
import enums.Gender;
import enums.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestBooksController {

    private BooksController booksController;
    private FileHandlingService mockFileHandlingService;

    public String DATABASE = "database";

    @BeforeEach
    public void setUp() {
        try {
            mockFileHandlingService = mock(FileHandlingService.class);
            when(mockFileHandlingService.readObjectFromFile(DATABASE)).thenReturn(new ArrayList<Book>());

            booksController = new BooksController(mockFileHandlingService, DATABASE);
        } catch (IOException ex) {
            fail("Failed to set up databases: " + ex.getMessage());
        }
        catch (ClassNotFoundException ignored) {
        }
    }

    @Test
    void testFirstConstructor() {
        ArrayList<Book> books = new ArrayList<>();
        try {
            when(mockFileHandlingService.readObjectFromFile(any(String.class))).thenReturn(books);
            booksController = new BooksController(mockFileHandlingService);

            // it was called once in the setup and once here
            verify(mockFileHandlingService, times(2)).readObjectFromFile(any(String.class));
        }
        catch (IOException|ClassNotFoundException ignored) {
        }

        assertEquals(books, booksController.getBooks());
    }

    @Test
    void testAddBook() {
        Author author = new Author("John", "Doe", Gender.MALE);
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
    void testAddBookThrowsFillInAllFieldsException() {
        Author author = new Author("John", "Doe", Gender.MALE);
        String isbn = "678-7-345-45678-8";
        double price = 25.0;
        String description = "Test Book";
        boolean isPaperback = true;
        ArrayList<Genre> genres = new ArrayList<>();
        int quantity = 10;

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> booksController.addBook("", author, isbn, price, description, isPaperback, genres, quantity));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> booksController.addBook("Book", null, isbn, price, description, isPaperback, genres, quantity));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> booksController.addBook("Book", author, "", price, description, isPaperback, genres, quantity));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> booksController.addBook("Book", author, isbn, price, "", isPaperback, genres, quantity));
        assertEquals("Please fill in all fields", exc.getMessage());

        assertEquals(0, booksController.getBooks().size());
    }

    @Test
    void testAddBookThrowsBookExistsException() {
        Author author = new Author("John", "Doe",Gender.MALE);
        String isbn = "678-7-345-45678-8";
        double price = 25.0;
        String description = "Book";
        boolean isPaperback = true;
        ArrayList<Genre> genres = new ArrayList<>();
        int quantity = 5;
        String validIsbn13 = "123-4-567-12345-6";

        try {
            booksController.addBook("Book", author, isbn, price, description, isPaperback, genres, quantity);
        } catch (IOException ex) {
            fail("Failed to add book: " + ex.getMessage());
        }

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> booksController.addBook("Book", author, isbn, price, description, isPaperback, genres, quantity));
        assertEquals("A book with the same ISBN13 already exists", exc.getMessage());

        assertEquals(1, booksController.getBooks().size());
    }

    @Test
    void testAddBookInvalidBook() {
        Author author = new Author("John", "Doe",Gender.MALE);
        String isbn = "678-7-345-45678-8";
        double price = 25.0;
        String description = "Book";
        boolean isPaperback = true;
        ArrayList<Genre> genres = new ArrayList<>();
        int quantity = 5;
        String validIsbn13 = "123-4-567-12345-6";

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> booksController.addBook("Book", author, "364784393", price, description, isPaperback, genres, quantity));
        assertEquals("Invalid ISBN13", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> booksController.addBook("Book", author, isbn, -1.0, description, isPaperback, genres, quantity));
        assertEquals("Price must be positive", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> booksController.addBook("Book", author, isbn, 0, description, isPaperback, genres, quantity));
        assertEquals("Price must be positive", exc.getMessage());


        exc = assertThrows(IllegalArgumentException.class,
                () -> booksController.addBook("Book", author, isbn, price, description, isPaperback, genres, -1));
        assertEquals("Quantity cannot be negative", exc.getMessage());

    }
    @Test
    void testUpdateBook() {
        Author author = new Author("John", "Doe", Gender.MALE);
        String isbn = "678-7345456788";
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
        Author author = new Author("John", "Doe", Gender.MALE);
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
                () -> booksController.updateBook(book, "", author, "678-7-345-45678-8", price, description, isPaperback, genres, quantity));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> booksController.updateBook(book, "Test Book", null, "678-7-345-45678-8", price, description, isPaperback, genres, quantity));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> booksController.updateBook(book, "Test Book", author, "", price, description, isPaperback, genres, quantity));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> booksController.updateBook(book, "Test Book", author, "678-7-345-45678-8", price, "", isPaperback, genres, quantity));
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
        Author author = new Author("John", "Doe", Gender.MALE);
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
    void testUpdateBookInvalidBook() {
        Author author = new Author("John", "Doe", Gender.MALE);
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
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> booksController.updateBook(book, "Test Book", author, isbn, -1.0, description, isPaperback, genres, quantity));
        assertEquals("Price must be positive", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> booksController.updateBook(book, "Test Book", author, isbn, 0, description, isPaperback, genres, quantity));
        assertEquals("Price must be positive", exc.getMessage());

        assertEquals(price, book.getPrice());

        exc = assertThrows(IllegalArgumentException.class,
                () -> booksController.updateBook(book, "Test Book", author, isbn, price, description, isPaperback, genres, -1));
        assertEquals("Quantity cannot be negative", exc.getMessage());

        assertEquals(quantity, book.getQuantity());

        exc = assertThrows(IllegalArgumentException.class,
                () -> booksController.updateBook(book, "Test Book", author, "364784393", price, description, isPaperback, genres, quantity));
        assertEquals("Invalid ISBN13", exc.getMessage());

        assertEquals("Test Book", book.getTitle());
        assertEquals(isbn, book.getIsbn13());
    }

    @Test
    void testUpdateBookThrowsBookExistsException() {
        Author author = new Author("John", "Doe", Gender.MALE);
        String isbn = "678-7-345-45678-8";
        double price = 25.0;
        String description = "Test Book";
        boolean isPaperback = true;
        ArrayList<Genre> genres = new ArrayList<>();
        int quantity = 10;

        try {
            booksController.addBook("Test Book", author, isbn, price, description, isPaperback, genres, quantity);
            booksController.addBook("Test Book 2", author, "123-4-567-12345-6", price, description, isPaperback, genres, quantity);
        } catch (IOException ex) {
            fail("Failed to add book: " + ex.getMessage());
        }

        Book book1 = booksController.getBooks().get(0);
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> booksController.updateBook(book1, "Test Book", author, "123-4-567-12345-6", price, description, isPaperback, genres, quantity));
        assertEquals("A book with the same ISBN13 already exists", exc.getMessage());

        assertEquals(isbn, book1.getIsbn13());

        try {
            booksController.updateBook(book1, "Test Book", author, "123-4-567-12345-7", price, description, isPaperback, genres, quantity);
        } catch (IOException e) {
            fail("Failed to update book: " + e.getMessage());
        }

        assertEquals("123-4-567-12345-7", book1.getIsbn13());

    }

    @Test
    void testRemoveBook() {
        Author author = new Author("John", "Doe", Gender.MALE);
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

    @Test
    void testReadFromFile() {
        ArrayList<Book> books = new ArrayList<>();
        Book book = new Book("", "Book title", "", 0, new Author("John", "Doe", Gender.MALE), null, 0, false);
        books.add(book);

        try {
            when(mockFileHandlingService.readObjectFromFile(DATABASE)).thenReturn(books);
            booksController.readFromFile(DATABASE);
            verify(mockFileHandlingService, times(2)).readObjectFromFile(DATABASE);
            assertEquals(books, booksController.getBooks());

            when(mockFileHandlingService.readObjectFromFile(DATABASE)).thenThrow(new FileNotFoundException());
            booksController.readFromFile(DATABASE);
            assertEquals(0, booksController.getBooks().size());

            reset(mockFileHandlingService);
            when(mockFileHandlingService.readObjectFromFile(DATABASE)).thenReturn(books);
            when(mockFileHandlingService.readObjectFromFile(DATABASE)).thenThrow(new ClassNotFoundException());
            when(mockFileHandlingService.deleteFile(DATABASE)).thenReturn(false);
            IllegalStateException exc = assertThrows(IllegalStateException.class, () -> booksController.readFromFile(DATABASE));
            assertEquals("Failed to delete corrupted database", exc.getMessage());
            verify(mockFileHandlingService, times(1)).deleteFile(DATABASE);

            when(mockFileHandlingService.deleteFile(DATABASE)).thenReturn(true);
            booksController.readFromFile(DATABASE);
            assertEquals(0, booksController.getBooks().size());
        }
        catch (IOException|ClassNotFoundException ignored) {
        }
    }
}
