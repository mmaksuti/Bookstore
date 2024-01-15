package test.system;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import src.controllers.AuthorsController;
import src.controllers.BooksController;
import src.enums.Gender;
import src.models.Author;
import src.services.FileHandlingService;
import src.stages.EditAuthorStage;
import javafx.scene.control.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import static org.junit.jupiter.api.Assertions.*;


public class TestEditAuthorStage extends ApplicationTest {
    AuthorsController authorsController=null;
    BooksController booksController=null;
    FileHandlingService fileHandlingService = new FileHandlingService();
    @TempDir
    File tempDir;

    @Override
    public void start(Stage stage) {
        EditAuthorStage editAuthorStage = null;
        try {
            String authorsDatabase = tempDir.getAbsolutePath() + "/authorsDatabase.dat";
            String booksDatabase = tempDir.getAbsolutePath() + "/booksDatabase.dat";

            authorsController = new AuthorsController(fileHandlingService, authorsDatabase);
            booksController = new BooksController(fileHandlingService, booksDatabase);

            Author author = new Author("John", "Doe", Gender.MALE);
            authorsController.addAuthor(author.getFirstName(), author.getLastName(), author.getGender());

            editAuthorStage = new EditAuthorStage(authorsController, booksController, author);
        } catch (IOException ex) {
            fail("Failed to load databases: " + ex.getMessage());
        }

        stage.setScene(editAuthorStage.getScene());
        stage.show();
    }

    @Override
    public void stop() throws TimeoutException {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
    }

    @Test
    public void testFieldsAreCorrect() {
        Set<TextField> textFields = lookup(".text-field").queryAllAs(TextField.class);
        Iterator<TextField> iterator = textFields.iterator();

        Author author = authorsController.getAuthors().get(0);

        System.out.println("Author: " + author);

        try {
            assertEquals(author.getFirstName(), iterator.next().getText());
            assertEquals(author.getLastName(), iterator.next().getText());

            ComboBox<Gender> genderComboBox = lookup(".combo-box").query();
            assertEquals(author.getGender(), genderComboBox.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    void testEditAuthor() {
        Set<TextField> textFields = lookup(".text-field").queryAllAs(TextField.class);
        Iterator<TextField> iterator = textFields.iterator();

        Platform.runLater(() -> {
            TextField tf = iterator.next();
            tf.clear();
            clickOn(tf).write("John");

            tf = iterator.next();
            tf.clear();
            clickOn(tf).write("Doe");

            ComboBox<Gender> genderComboBox = lookup(".combo-box").queryComboBox();
            genderComboBox.getSelectionModel().select(Gender.FEMALE);

            clickOn(".button");
        });

        WaitForAsyncUtils.waitForFxEvents();

        Author author = authorsController.getAuthors().get(0);
        assertEquals("John", author.getFirstName());
        assertEquals("Doe", author.getLastName());
        assertEquals(Gender.MALE, author.getGender());
    }
}