package test.integration;
import controllers.AuthorsController;
import controllers.BooksController;
import enums.Gender;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Author;
import models.Book;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import services.FileHandlingService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestAuthorsControllers {

    public AuthorsController authorsController;
    public FileHandlingService fileHandlingService;

    @TempDir
    static File tempDir;

    String DATABASE;

    @BeforeEach
    public void setUp()  {
        try {
            fileHandlingService = new FileHandlingService();

            DATABASE = tempDir.getAbsolutePath() + "/authorsDatabase.dat";

            authorsController = new AuthorsController(fileHandlingService, DATABASE);
        }
        catch (IOException ex) {
            fail("Failed to set up databases: " + ex.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        fileHandlingService.deleteFile(DATABASE);
    }

    // the class was already tested fully isolated in unit testing, so start by testing the integration
    // with the real FileHandlingService
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
    void testRemoveAuthorNoBooks() {

    }

    private static Stream<Arguments> provideStubsAndImplementations() {
        BooksController booksController = null;
        BooksController stubBooksController = mock(BooksController.class);
        ObservableList<Book> books = FXCollections.observableArrayList();
        FileHandlingService fileHandlingService = new FileHandlingService();
        String DATABASE = tempDir.getAbsolutePath() + "/booksDatabase.dat";

        when(stubBooksController.getBooks()).thenReturn(books);

        try {
            booksController = new BooksController(fileHandlingService, DATABASE);
        }
        catch (IOException ex) {
            fail("Failed to load databases: " + ex.getMessage());
        }

        return Stream.of(
                Arguments.of(stubBooksController),
                Arguments.of(booksController)
        );
    }

    // test with a stub first, then replace with the real implementation
    @ParameterizedTest
    @MethodSource("provideStubsAndImplementations")
    void testUpdateAuthorNoBooks(BooksController booksController) {
        try {
            authorsController.addAuthor("John", "Doe", Gender.MALE);

            Author author = authorsController.getAuthors().get(0);
            authorsController.updateAuthor(author, "Jane", "Doe", Gender.FEMALE, booksController);

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
