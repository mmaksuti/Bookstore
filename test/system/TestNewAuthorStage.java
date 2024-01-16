package test.system;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.framework.junit5.ApplicationTest;
import src.controllers.AuthorsController;
import src.enums.Gender;
import src.models.Author;
import src.services.FileHandlingService;
import src.stages.NewAuthorStage;

import java.io.File;
import java.io.IOException;

import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestNewAuthorStage extends ApplicationTest {
    AuthorsController authorsController = null;
    FileHandlingService fileHandlingService = new FileHandlingService();

    @TempDir
    File tempDir;

    @Override
    public void start(Stage stage) {
        NewAuthorStage newAuthorStage = null;
        try {
            String authorsDatabase = tempDir.getAbsolutePath() + "/authorsDatabase.dat";

            authorsController = new AuthorsController(fileHandlingService, authorsDatabase);
            newAuthorStage = new NewAuthorStage(authorsController);
        } catch (IOException ex) {
            fail("Failed to load database: " + ex.getMessage());
        }

        stage.setScene(newAuthorStage.getScene());
        stage.show();
    }

    @Test
    public void testAddAuthor() {
        Set<TextField> textFields = lookup(".text-field").queryAllAs(TextField.class);
        Iterator<TextField> iterator = textFields.iterator();

        clickOn(iterator.next()).write("John");
        clickOn(iterator.next()).write("Doe");

        // Open the ComboBox dropdown
        clickOn(".combo-box");

        // Use keyboard events to navigate to and select the "MALE" item
        type(javafx.scene.input.KeyCode.DOWN);
        type(javafx.scene.input.KeyCode.ENTER);

        clickOn(".button");

        Set<Label> labels = lookup(".label").queryAllAs(Label.class);
        Label lastLabel = null;
        for (Label label : labels) {
            lastLabel = label;
        }

        assertNotNull(lastLabel, "No label found");
        assertEquals("Author added successfully", lastLabel.getText());

        ObservableList<Author> authorList = authorsController.getAuthors();
        Author addedAuthor = authorList.get(authorList.size() - 1);
        assertEquals("John", addedAuthor.getFirstName());
        assertEquals("Doe", addedAuthor.getLastName());
        assertEquals(Gender.MALE, addedAuthor.getGender());
    }

    @Test
    public void testAddAuthorInvalidFields() {
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
