package test;

import java.io.*;
import java.util.ArrayList;

import controllers.AuthorsController;
import controllers.BooksController;

import controllers.FileDatabaseController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.Author;
import main.Book;
import main.Gender;
import main.UserConfirmation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import test.mocks.MockDatabaseController;

import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mockito.*;

public class TestAuthorsController {
    public AuthorsController authorsController;
    public BooksController booksController;
    public MockDatabaseController mockDbController;

    public ObservableList<Book> books = FXCollections.observableArrayList();

//    public static final String TEST_AUTHORS_DATABASE = "test/testAuthorsDatabase.dat";
//    public static final String TEST_BOOKS_DATABASE = "test/testBooksDatabase.dat";

    // set up the database files
    @BeforeEach
    public void setUp()  {
        try {
            mockDbController = new MockDatabaseController();
            mockDbController.setCannedDatabase(new ArrayList<Author>());
            authorsController = new AuthorsController(mockDbController);
            booksController = Mockito.mock(BooksController.class);
            //Mockito.when(booksController.getBooks()).thenReturn(books);
        }
        catch (IOException ex) {
            fail("Failed to set up databases: " + ex.getMessage());
        }
    }

    @Test
    void test_addAuthor() {
        String firstName = "John";
        String lastName = "Doe";
        Gender gender = Gender.MALE;

        try {
            authorsController.addAuthor(firstName, lastName, gender);
        } catch (IOException ex) {
            fail("Failed to add author: " + ex.getMessage());
        }

        assertEquals(1, authorsController.getAuthors().size());

        Author author = authorsController.getAuthors().get(0);
        assertEquals(firstName, author.getFirstName());
        assertEquals(lastName, author.getLastName());
        assertEquals(gender, author.getGender());

        firstName = "Jane";
        gender = Gender.FEMALE;

        try {
            authorsController.addAuthor(firstName, lastName, gender);
        }
        catch (IOException ex) {
            fail("Failed to add author: " + ex.getMessage());
        }

        assertEquals(2, authorsController.getAuthors().size());

        author = authorsController.getAuthors().get(1);
        assertEquals(firstName, author.getFirstName());
        assertEquals(lastName, author.getLastName());
        assertEquals(gender, author.getGender());
    }

    @Test
    void test_addAuthorThrowsFillInAllValuesException() {
        String firstName = "John";
        String lastName = "Doe";
        Gender gender = Gender.MALE;

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> authorsController.addAuthor("", lastName, gender));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> authorsController.addAuthor(firstName, "", gender));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> authorsController.addAuthor(firstName, lastName, null));
        assertEquals("Please fill in all fields", exc.getMessage());

        assertEquals(0, authorsController.getAuthors().size());
    }

    @Test
    void test_addAuthorThrowsAuthorExistsException() {
        String firstName = "John";
        String lastName = "Doe";
        Gender gender = Gender.MALE;

        try {
            authorsController.addAuthor(firstName, lastName, gender);
        } catch (IOException ex) {
            fail("Failed to add author: " + ex.getMessage());
        }

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> authorsController.addAuthor(firstName, lastName, gender));
        assertEquals("Author already exists", exc.getMessage());
        assertEquals(1, authorsController.getAuthors().size());
    }

    @Test
    void test_updateAuthor() {
        String firstName = "John";
        String lastName = "Doe";
        Gender gender = Gender.MALE;

        try {
            authorsController.addAuthor(firstName, lastName, gender);
        } catch (IOException ex) {
            fail("Failed to add author: " + ex.getMessage());
        }

        Mockito.when(booksController.getBooks()).thenReturn(books);

        Author author = authorsController.getAuthors().get(0);
        try {
            authorsController.updateAuthor(author, "Jane", lastName, Gender.FEMALE, booksController);
        } catch (IOException e) {
            fail("Failed to update author: " + e.getMessage());
        }

        assertEquals(1, authorsController.getAuthors().size());

        assertEquals("Jane", author.getFirstName());
        assertEquals(lastName, author.getLastName());
        assertEquals(Gender.FEMALE, author.getGender());
    }

    @Test
    void test_updateAuthorThrowsFillInAllValuesException() {
        String firstName = "John";
        String lastName = "Doe";
        Gender gender = Gender.MALE;

        try {
            authorsController.addAuthor(firstName, lastName, gender);
        } catch (IOException ex) {
            fail("Failed to add author: " + ex.getMessage());
        }

        Mockito.when(booksController.getBooks()).thenReturn(books);

        Author author = authorsController.getAuthors().get(0);

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> authorsController.updateAuthor(author, "", lastName, gender, booksController));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> authorsController.updateAuthor(author, firstName, "", gender, booksController));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> authorsController.updateAuthor(author, firstName, lastName, null, booksController));
        assertEquals("Please fill in all fields", exc.getMessage());

        assertEquals("John", author.getFirstName());
        assertEquals(lastName, author.getLastName());
        assertEquals(Gender.MALE, author.getGender());
    }

    @Test
    void test_updateAuthorThrowsAuthorExistsException() {
        try {
            authorsController.addAuthor("John", "Doe", Gender.MALE);
        } catch (IOException ex) {
            fail("Failed to add author: " + ex.getMessage());
        }

        try {
            authorsController.addAuthor("Jane", "Doe", Gender.FEMALE);
        } catch (IOException ex) {
            fail("Failed to add author: " + ex.getMessage());
        }

        Author author = authorsController.getAuthors().get(1);

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> authorsController.updateAuthor(author, "John", "Doe", Gender.FEMALE, booksController));
        assertEquals("Author already exists", exc.getMessage());
    }

    @Test
    void test_removeAuthorNoBooks() {
        String firstName = "John";
        String lastName = "Doe";
        Gender gender = Gender.MALE;

        try {
            authorsController.addAuthor(firstName, lastName, gender);
        } catch (IOException ex) {
            fail("Failed to add author: " + ex.getMessage());
        }

        Mockito.when(booksController.getBooks()).thenReturn(books);

        Author author = authorsController.getAuthors().get(0);
        try {
            authorsController.removeAuthor(booksController, author, null);
        }
        catch (IOException ex) {
            fail("Failed to remove author: " + ex.getMessage());
        }

        assertEquals(0, authorsController.getAuthors().size());
    }

    @Test
    void test_removeAuthorWithBooksConfirmIsFalse() {
        String firstName = "John";
        String lastName = "Doe";
        Gender gender = Gender.MALE;

        try {
            authorsController.addAuthor(firstName, lastName, gender);
        } catch (IOException ex) {
            fail("Failed to add author: " + ex.getMessage());
        }


        Author author = authorsController.getAuthors().get(0);

        Book testBook = new Book("", "", "", 1.0, author, new ArrayList<>(), 1, true);
        books.add(testBook);
        Mockito.when(booksController.getBooks()).thenReturn(books);

        UserConfirmation mockedConfirmation = (header, message) -> {
            return false;
        };

        try {
            authorsController.removeAuthor(booksController, author, mockedConfirmation);
        }
        catch (IOException ex) {
            fail("Failed to remove author: " + ex.getMessage());
        }

        assertEquals(1, authorsController.getAuthors().size());
    }

    @Test
    void test_removeAuthorWithBooksConfirmIsTrue() {
        String firstName = "John";
        String lastName = "Doe";
        Gender gender = Gender.MALE;

        try {
            authorsController.addAuthor(firstName, lastName, gender);
        } catch (IOException ex) {
            fail("Failed to add author: " + ex.getMessage());
        }


        Author author = authorsController.getAuthors().get(0);

        Book testBook = new Book("", "", "", 1.0, author, new ArrayList<>(), 1, true);
        books.add(testBook);
        Mockito.when(booksController.getBooks()).thenReturn(books);

        UserConfirmation mockedConfirmation = (header, message) -> {
            return true;
        };

        try {
            authorsController.removeAuthor(booksController, author, mockedConfirmation);
        }
        catch (IOException ex) {
            fail("Failed to remove author: " + ex.getMessage());
        }

        assertEquals(0, authorsController.getAuthors().size());
    }

    // etc.
}
