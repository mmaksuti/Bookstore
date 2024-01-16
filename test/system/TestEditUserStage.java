package test.system;

import javafx.collections.ObservableList;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.framework.junit5.ApplicationTest;
import src.controllers.LoginController;
import src.models.User;
import src.services.FileHandlingService;
import src.stages.EditUserStage;
import src.stages.NewUserStage;
import src.enums.AccessLevel;

import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestEditUserStage extends ApplicationTest {
    LoginController loginController = null;
    FileHandlingService fileHandlingService = new FileHandlingService();

    @TempDir
    File tempDir;

    @Override
    public void start(Stage stage) {
        EditUserStage editUserStage = null;
        try {
            String usersDatabase = tempDir.getAbsolutePath() + "/usersDatabase.dat";
            String session = tempDir.getAbsolutePath() + "/session.dat";

            loginController = new LoginController(fileHandlingService, null, usersDatabase, session);

            User user = loginController.getUsers().get(0);
            editUserStage = new EditUserStage(user, loginController);
        }
        catch (IOException ex) {
            fail("Failed to load database: " + ex.getMessage());
        }

        stage.setScene(editUserStage.getScene());
        stage.show();
    }

    @Test
    public void testFieldsAreCorrect() {
        Set<TextField> textFields = lookup(".text-field").queryAllAs(TextField.class);
        Iterator<TextField> iterator = textFields.iterator();

        User user = loginController.getUsers().get(0);

        assertEquals(user.getFirstName(), iterator.next().getText());
        assertEquals(user.getLastName(), iterator.next().getText());
        assertEquals(user.getUsername(), iterator.next().getText());
        assertEquals("", iterator.next().getText());
        assertEquals(user.getEmail(), iterator.next().getText());
        assertEquals(user.getPhone(), iterator.next().getText());
        assertEquals(user.getSalary(), Double.parseDouble(iterator.next().getText()));
    }

    @Test
    void testEditUser() {
        Set<TextField> textFields = lookup(".text-field").queryAllAs(TextField.class);
        Iterator<TextField> iterator = textFields.iterator();

        TextField tf = iterator.next();
        tf.clear();
        clickOn(tf).write("John");

        iterator.next();

        tf = iterator.next();
        tf.clear();
        clickOn(tf).write("johndoe");

        tf = iterator.next();
        tf.clear();
        clickOn(tf).write("password");

        clickOn("Save changes");

        User user = loginController.getUsers().get(0);
        assertEquals("John", user.getFirstName());
        assertEquals("johndoe", user.getUsername());
    }

    @Test
    void testEditUserInvalidFields() {
        clickOn("Save changes");

        Set<Label> labels = lookup(".label").queryAllAs(Label.class);
        Label lastLabel = null;
        for (Label label : labels) {
            lastLabel = label;
        }
        if (lastLabel == null) {
            fail("No label found");
        }

        assertEquals("Please fill in all fields", lastLabel.getText());
    }
}
