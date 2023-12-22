package test;

import java.io.*;
import java.util.ArrayList;

import controllers.AuthorsController;
import controllers.BooksController;

import services.FileHandlingService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestAuthorsController {
    public AuthorsController authorsController;
    public BooksController mockBooksController;
    public FileHandlingService mockFileHandlingService;

    public ObservableList<Book> books = FXCollections.observableArrayList();

    public String DATABASE = "database";

    @BeforeEach
    public void setUp()  {
        try {
            mockFileHandlingService = mock(FileHandlingService.class);
            when(mockFileHandlingService.readObjectFromFile(DATABASE)).thenReturn(new ArrayList<Author>());
            mockBooksController = mock(BooksController.class);

            authorsController = new AuthorsController(mockFileHandlingService, DATABASE);
        }
        catch (IOException ex) {
            fail("Failed to set up databases: " + ex.getMessage());
        }
        catch (ClassNotFoundException ignored) {
        }
    }

    @Test
    void test_firstConstructor() {
        ArrayList<Author> authors = new ArrayList<>();
        try {
            when(mockFileHandlingService.readObjectFromFile(any(String.class))).thenReturn(authors);
            authorsController = new AuthorsController(mockFileHandlingService);

            // it was called once in the setup and once here
            verify(mockFileHandlingService, times(2)).readObjectFromFile(any(String.class));
        }
        catch (IOException|ClassNotFoundException ignored) {
        }

        assertEquals(authors, authorsController.getAuthors());
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

        when(mockBooksController.getBooks()).thenReturn(books);

        Author author = authorsController.getAuthors().get(0);
        try {
            authorsController.updateAuthor(author, "Jane", lastName, Gender.FEMALE, mockBooksController);
        } catch (IOException e) {
            fail("Failed to update author: " + e.getMessage());
        }

        assertEquals(1, authorsController.getAuthors().size());

        assertEquals("Jane", author.getFirstName());
        assertEquals(lastName, author.getLastName());
        assertEquals(Gender.FEMALE, author.getGender());

        try {
            authorsController.updateAuthor(author, "Jane", "Smith", Gender.FEMALE, mockBooksController);
        } catch (IOException e) {
            fail("Failed to update author: " + e.getMessage());
        }

        assertEquals(1, authorsController.getAuthors().size());

        assertEquals("Jane", author.getFirstName());
        assertEquals("Smith", author.getLastName());
        assertEquals(Gender.FEMALE, author.getGender());

        try {
            authorsController.updateAuthor(author, "Jane", "Smith", Gender.MALE, mockBooksController);
        } catch (IOException e) {
            fail("Failed to update author: " + e.getMessage());
        }

        assertEquals(1, authorsController.getAuthors().size());

        assertEquals("Jane", author.getFirstName());
        assertEquals("Smith", author.getLastName());
        assertEquals(Gender.MALE, author.getGender());
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

        when(mockBooksController.getBooks()).thenReturn(books);

        Author author = authorsController.getAuthors().get(0);

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> authorsController.updateAuthor(author, "", lastName, gender, mockBooksController));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> authorsController.updateAuthor(author, firstName, "", gender, mockBooksController));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> authorsController.updateAuthor(author, firstName, lastName, null, mockBooksController));
        assertEquals("Please fill in all fields", exc.getMessage());

        assertEquals(firstName, author.getFirstName());
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
                () -> authorsController.updateAuthor(author, "John", "Doe", Gender.FEMALE, mockBooksController));
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

        when(mockBooksController.getBooks()).thenReturn(books);

        Author author = authorsController.getAuthors().get(0);
        try {
            authorsController.removeAuthor(mockBooksController, author, null);
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
        when(mockBooksController.getBooks()).thenReturn(books);

        UserConfirmation mockedConfirmation = (header, message) -> {
            return false;
        };

        try {
            authorsController.removeAuthor(mockBooksController, author, mockedConfirmation);
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
        when(mockBooksController.getBooks()).thenReturn(books);

        UserConfirmation mockedConfirmation = (header, message) -> {
            return true;
        };

        try {
            authorsController.removeAuthor(mockBooksController, author, mockedConfirmation);
        }
        catch (IOException ex) {
            fail("Failed to remove author: " + ex.getMessage());
        }

        assertEquals(0, authorsController.getAuthors().size());
    }
}
