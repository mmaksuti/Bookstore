package test.system;

import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.framework.junit5.ApplicationTest;
import src.controllers.AuthorsController;
import src.controllers.BooksController;
import src.enums.Genre;
import src.models.Author;
import src.services.FileHandlingService;
import src.stages.NewBookStage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

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

        interact(() -> {
            clickOn(iterator.next()).write("Sample Book");

            ComboBox<Author> authorComboBox = lookup(".combo-box").query();
            authorComboBox.getSelectionModel().select(0);

            clickOn(iterator.next()).write("1234567890123");
            clickOn(iterator.next()).write("19.99");

            TextArea descriptionTextArea = lookup(".text-area").query();
            clickOn(descriptionTextArea).write("This is a sample book description.");

            genreCheckboxes.forEach(checkbox -> interact(() -> checkbox.setSelected(true)));

            clickOn(iterator.next()).write("10");
        });}}
