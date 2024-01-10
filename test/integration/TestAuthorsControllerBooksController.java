package test.integration;
import controllers.AuthorsController;
import controllers.BooksController;
import enums.Gender;
import interfaces.UserConfirmation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Author;
import models.Book;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import services.FileHandlingService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestAuthorsControllerBooksController {
    AuthorsController authorsController;
    BooksController booksController;
    FileHandlingService stubFileHandlingService;

    ArrayList<Book> books;
    ArrayList<Author> authors;

    String authorsDATABASE = "authorsDatabase.dat";
    String booksDATABASE = "booksDatabase.dat";
    
    @BeforeEach
    public void setUp()  {
        try {
            stubFileHandlingService = mock(FileHandlingService.class);

            books = new ArrayList<>();
            authors = new ArrayList<>();

            when(stubFileHandlingService.readObjectFromFile(authorsDATABASE)).thenReturn(authors);
            when(stubFileHandlingService.readObjectFromFile(booksDATABASE)).thenReturn(books);

            authorsController = new AuthorsController(stubFileHandlingService, authorsDATABASE);
            booksController = new BooksController(stubFileHandlingService, booksDATABASE);
        }
        catch (IOException ex) {
            fail("Failed to set up databases: " + ex.getMessage());
        }
        catch (ClassNotFoundException ignored) {
        }
    }
    
    @Test
    public void testRemoveAuthorConfirmIsFalse() {
        String firstName = "John";
        String lastName = "Doe";
        Gender gender = Gender.MALE;

        try {
            authorsController.addAuthor("John", "Doe", Gender.MALE);
            authorsController.addAuthor("Jane", "Doe", Gender.FEMALE);
            authorsController.addAuthor("Jane", "Smith", Gender.FEMALE);
        } catch (IOException ignored) {
        }

        Author author = authorsController.getAuthors().get(0);
        Author author2 = authorsController.getAuthors().get(1);
        Author author3 = authorsController.getAuthors().get(2);

        try {
            booksController.addBook("book1", author2, "123-1234567890", 1.0, "Description", true, new ArrayList<>(), 1);
            booksController.addBook("book2", author3, "123-1234567891", 1.0, "Description", true, new ArrayList<>(), 1);
            booksController.addBook("book3", author, "123-1234567892", 1.0, "Description", true, new ArrayList<>(), 1);
            booksController.addBook("book4", author, "123-1234567893", 1.0, "Description", true, new ArrayList<>(), 1);
        }
        catch (IOException ignored) {
        }

        UserConfirmation mockedConfirmation = (header, message) -> {
            return false;
        };

        try {
            authorsController.removeAuthor(booksController, author, mockedConfirmation);

            assertEquals(4, booksController.getBooks().size());
            verify(stubFileHandlingService, times(3)).writeObjectToFile(eq(authorsDATABASE), any(ArrayList.class));
            verify(stubFileHandlingService, times(4)).writeObjectToFile(eq(booksDATABASE), any(ArrayList.class));
        }
        catch (IOException ignored) {
        }
    }

    @Test
    public void testRemoveAuthorConfirmIsTrue() {
        String firstName = "John";
        String lastName = "Doe";
        Gender gender = Gender.MALE;

        try {
            authorsController.addAuthor("John", "Doe", Gender.MALE);
            authorsController.addAuthor("Jane", "Doe", Gender.FEMALE);
            authorsController.addAuthor("Jane", "Smith", Gender.FEMALE);
        } catch (IOException ignored) {
        }

        Author author = authorsController.getAuthors().get(0);
        Author author2 = authorsController.getAuthors().get(1);
        Author author3 = authorsController.getAuthors().get(2);

        try {
            booksController.addBook("book1", author2, "123-1234567890", 1.0, "Description", true, new ArrayList<>(), 1);
            booksController.addBook("book2", author3, "123-1234567891", 1.0, "Description", true, new ArrayList<>(), 1);
            booksController.addBook("book3", author, "123-1234567892", 1.0, "Description", true, new ArrayList<>(), 1);
            booksController.addBook("book4", author, "123-1234567893", 1.0, "Description", true, new ArrayList<>(), 1);
        }
        catch (IOException ignored) {
        }

        UserConfirmation mockedConfirmation = (header, message) -> {
            return true;
        };

        try {
            authorsController.removeAuthor(booksController, author, mockedConfirmation);
            assertEquals(2, booksController.getBooks().size());

            // 3 additions, 1 deletion
            verify(stubFileHandlingService, times(3+1)).writeObjectToFile(eq(authorsDATABASE), any(ArrayList.class));
            // 4 additions, 1 (common) deletion
            verify(stubFileHandlingService, times(4+1)).writeObjectToFile(eq(booksDATABASE), any(ArrayList.class));
        }
        catch (IOException ignored) {
        }
    }

    @Test
    void testUpdateAuthor() {
        try {
            authorsController.addAuthor("John", "Doe", Gender.MALE);
            authorsController.addAuthor("Jane", "Doe", Gender.FEMALE);
        } catch (IOException ex) {
            fail("Failed to add author: " + ex.getMessage());
        }

        Author author1_copy = new Author("John", "Doe", Gender.MALE);
        Author author2_copy = new Author("Jane", "Doe", Gender.FEMALE);

        Author author1 = authorsController.getAuthors().get(0);
        Author author2 = authorsController.getAuthors().get(1);

        try {
            booksController.addBook("book1", author1_copy, "123-1234567890", 1.0, "Description", true, new ArrayList<>(), 1);
            booksController.addBook("book2", author2_copy, "123-1234567891", 1.0, "Description", true, new ArrayList<>(), 1);

            Book book1 = booksController.getBooks().get(0);
            Book book2 = booksController.getBooks().get(1);

            authorsController.updateAuthor(author1, "John", "Smith", Gender.MALE, booksController);

            assertEquals(author1, book1.getAuthor());
            assertEquals(author2_copy, book2.getAuthor());

            verify(stubFileHandlingService, times(2+1)).writeObjectToFile(eq(authorsDATABASE), any(ArrayList.class));
            verify(stubFileHandlingService, times(2+1)).writeObjectToFile(eq(booksDATABASE), any(ArrayList.class));
        }
        catch (IOException ignored) {
        }
    }
}
