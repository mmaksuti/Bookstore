package test.system;

import javafx.collections.ObservableList;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.framework.junit5.ApplicationTest;
import src.controllers.LoginController;
import src.models.User;
import src.services.FileHandlingService;
import src.stages.NewUserStage;
import src.enums.AccessLevel;

import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestNewUserStage extends ApplicationTest {
    LoginController loginController = null;
    FileHandlingService fileHandlingService = new FileHandlingService();

    @TempDir
    File tempDir;

    @Override
    public void start(Stage stage) {
        NewUserStage newUserStage = null;
        try {
            String usersDatabase = tempDir.getAbsolutePath() + "/usersDatabase.dat";
            String session = tempDir.getAbsolutePath() + "/session.dat";

            loginController = new LoginController(fileHandlingService, null, usersDatabase, session);
            newUserStage = new NewUserStage(loginController);
        }
        catch (IOException ex) {
            fail("Failed to load database: " + ex.getMessage());
        }

        stage.setScene(newUserStage.getScene());
        stage.show();
    }

    @Test
    public void testAddUser() {
        Set<TextField> textFields = lookup(".text-field").queryAllAs(TextField.class);
        Iterator<TextField> iterator = textFields.iterator();

        clickOn(iterator.next()).write("John");
        clickOn(iterator.next()).write("Doe");
        clickOn(iterator.next()).write("johndoe");
        clickOn(iterator.next()).write("password");
        clickOn(iterator.next()).write("johndoe@gmail.com");
        clickOn(iterator.next()).write("+355681234567");
        clickOn(iterator.next()).write("3000");

        LocalDate birthday = LocalDate.of(1990, 1, 1);
        DatePicker datePicker = lookup(".date-picker").queryAs(DatePicker.class);
        interact(() -> datePicker.setValue(birthday));

        AccessLevel accessLevel = AccessLevel.LIBRARIAN;
        clickOn(".combo-box").clickOn(accessLevel.toString());

        clickOn(".button");

        Set<Label> labels = lookup(".label").queryAllAs(Label.class);
        Label lastLabel = null;
        for (Label label : labels) {
            lastLabel = label;
        }
        if (lastLabel == null) {
            fail("No label found");
        }
        assertEquals("User added successfully", lastLabel.getText());

        ObservableList<User> users = loginController.getUsers();
        User user = users.get(1);
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("johndoe", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("johndoe@gmail.com", user.getEmail());
        assertEquals("+355681234567", user.getPhone());
        assertEquals(3000, user.getSalary());
        assertEquals(birthday, user.getBirthday());
        assertEquals(accessLevel, user.getAccessLevel());
    }

    @Test
    public void testAddUserInvalidFields() {
        clickOn(".button");

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
