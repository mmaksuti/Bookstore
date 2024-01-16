package test.system;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.framework.junit5.ApplicationTest;
import src.controllers.AuthorsController;
import src.controllers.BooksController;
import src.enums.Gender;
import src.models.Author;
import src.services.FileHandlingService;
import src.stages.EditAuthorStage;
import javafx.scene.control.*;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
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

            authorsController.addAuthor("John", "Doe", Gender.MALE);

            Author author = authorsController.getAuthors().get(0);
            editAuthorStage = new EditAuthorStage(authorsController, booksController, author);
        } catch (IOException ex) {
            fail("Failed to load databases: " + ex.getMessage());
        }

        stage.setScene(editAuthorStage.getScene());
        stage.show();
    }

    @Test
    public void testFieldsAreCorrect() {
        Set<TextField> textFields = lookup(".text-field").queryAllAs(TextField.class);
        Iterator<TextField> iterator = textFields.iterator();

        Author author = authorsController.getAuthors().get(0);

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

        TextField tf = iterator.next();
        tf.clear();
        clickOn(tf).write("Jane");

        tf = iterator.next();

        clickOn(".combo-box");
        type(javafx.scene.input.KeyCode.DOWN);
        type(javafx.scene.input.KeyCode.ENTER);

        clickOn(".button");

        Author author = authorsController.getAuthors().get(0);
        assertEquals("Jane", author.getFirstName());
        assertEquals("Doe", author.getLastName());
        assertEquals(Gender.FEMALE, author.getGender());
    }
}