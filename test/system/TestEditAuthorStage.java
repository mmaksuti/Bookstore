package test.system;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import src.controllers.AuthorsController;
import src.controllers.BooksController;
import src.enums.Gender;
import src.models.Author;
import src.services.FileHandlingService;
import src.stages.EditAuthorStage;

import javafx.scene.control.*;
import java.util.Iterator;

import java.util.Set;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

public class TestEditAuthorStage extends ApplicationTest {
    AuthorsController authorsController;
    BooksController booksController;
    FileHandlingService fileHandlingService = new FileHandlingService();

    private Author testAuthor;

    @Override
    public void start(Stage stage) {
        try {
            FileHandlingService fileHandlingService = new FileHandlingService();
            authorsController = new AuthorsController(fileHandlingService, "testAuthorsDatabase.dat");
            booksController = new BooksController(fileHandlingService, "testBooksDatabase.dat");

            testAuthor = new Author("John", "Doe", Gender.MALE);
            authorsController.getAuthors().add(testAuthor);

            EditAuthorStage editAuthorStage = new EditAuthorStage(authorsController, booksController, testAuthor);
            editAuthorStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

}