package test.system;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.framework.junit5.ApplicationTest;
import src.controllers.AuthorsController;
import src.controllers.BooksController;
import src.enums.Gender;
import src.enums.Genre;
import src.models.Author;
import src.models.Book;
import src.services.FileHandlingService;
import src.stages.NewBookStage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestNewBookStage extends ApplicationTest {
    AuthorsController authorsController = null;
    BooksController booksController = null;
    FileHandlingService fileHandlingService = new FileHandlingService();

    @TempDir
    File tempDir;

    @Override
    public void start(Stage stage) {
        try {
            String authorsDatabase = tempDir.getAbsolutePath() + "/authorsDatabase.dat";
            String booksDatabase = tempDir.getAbsolutePath() + "/booksDatabase.dat";

            authorsController = new AuthorsController(fileHandlingService, authorsDatabase);
            authorsController.addAuthor("John", "Doe", Gender.MALE);

            booksController = new BooksController(fileHandlingService, booksDatabase);

            NewBookStage newBookStage = new NewBookStage(authorsController, booksController);
            stage.setScene(newBookStage.getScene());
            stage.show();
        } catch (IOException ex) {
            Assertions.fail("Failed to load databases: " + ex.getMessage());
        }
    }

    @Test
    public void testAddBook() {
        ArrayList<CheckBox> genreCheckboxes = new ArrayList<>();

        interact(() -> {
            for (Genre g : Genre.values()) {
                CheckBox checkBox = lookup(g.toString()).query();
                genreCheckboxes.add(checkBox);
            }
        });

        Set<TextField> textFields = lookup(".text-field").queryAllAs(TextField.class);
        Iterator<TextField> iterator = textFields.iterator();

        clickOn(iterator.next()).write("Sample Book");

        clickOn(".combo-box");
        type(javafx.scene.input.KeyCode.DOWN);
        type(javafx.scene.input.KeyCode.ENTER);

        clickOn(iterator.next()).write("123-1234567890");
        clickOn(iterator.next()).write("19.99");

        TextArea descriptionTextArea = lookup(".text-area").query();
        clickOn(descriptionTextArea).write("This is a sample book description.");

        genreCheckboxes.forEach(checkbox -> interact(() -> checkbox.setSelected(true)));

        clickOn(iterator.next()).write("10");

        clickOn(".button");

        Set<Label> labels = lookup(".label").queryAllAs(Label.class);
        Label lastLabel = null;
        for (Label label : labels) {
            lastLabel = label;
        }

        assertNotNull(lastLabel, "No label found");
        assertEquals("Book added successfully", lastLabel.getText());

        Book addedBook = booksController.getBooks().get(0);
        Author author = authorsController.getAuthors().get(0);

        assertEquals("Sample Book", addedBook.getTitle());
        assertEquals(author, addedBook.getAuthor());
        assertEquals("123-1234567890", addedBook.getIsbn13());
        assertEquals(19.99, addedBook.getPrice());
        assertEquals("This is a sample book description.", addedBook.getDescription());
        assertEquals(10, addedBook.getQuantity());
        assertEquals(Genre.values().length, addedBook.getGenres().size());
    }

    @Test
    void testAddBookInvalidFields() {
        clickOn(".button");

        Set<Label> labels = lookup(".label").queryAllAs(Label.class);
        Label lastLabel = null;
        for (Label label : labels) {
            lastLabel = label;
        }

        assertNotNull(lastLabel, "No label found");
        assertEquals("Please fill in all fields", lastLabel.getText());
    }
}
