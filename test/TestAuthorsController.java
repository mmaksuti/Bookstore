package test;

import java.io.*;

import controllers.AuthorsController;
import controllers.BooksController;

import main.Author;
import main.Gender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class TestAuthorsController {
    public static AuthorsController authorsController;
    public static BooksController booksController;
    public static final String TEST_AUTHORS_DATABASE = "test/testAuthorsDatabase.dat";
    public static final String TEST_BOOKS_DATABASE = "test/testBooksDatabase.dat";

    // set up the database files
    @BeforeAll
    public static void setUp()  {
        try {
            authorsController = new AuthorsController(TEST_AUTHORS_DATABASE);
            //authorsController.setDatabase(TEST_AUTHORS_DATABASE);

            booksController = new BooksController();
            booksController.setDatabase(TEST_BOOKS_DATABASE);
        }
        catch (IOException ex) {
            fail("Failed to set up databases: " + ex.getMessage());
        }
    }

    // clear everything so each test starts from scratch
    @AfterEach
    public void tearDown() {
        File authorsDatabase = new File(TEST_AUTHORS_DATABASE);

        if (authorsDatabase.exists()) {
            boolean deleted = authorsDatabase.delete();
            if (!deleted) {
                fail("Failed to delete authors database");
            }
        }

        File booksDatabase = new File(TEST_BOOKS_DATABASE);
        if (booksDatabase.exists()) {
            boolean deleted = booksDatabase.delete();
            if (!deleted) {
                fail("Failed to delete books database");
            }
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

        Author author = authorsController.authors.get(0);
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

        author = authorsController.authors.get(1);
        assertEquals(firstName, author.getFirstName());
        assertEquals(lastName, author.getLastName());
        assertEquals(gender, author.getGender());
    }

    @Test
    void test_addAuthorThrowsAuthorExistsException() {
        // ...
    }

    @Test
    void test_removeAuthorNoBooks() {
        // ...
    }

    @Test
    void test_removeAuthorWithBooks() {
        // ...
    }

    // etc.
}
