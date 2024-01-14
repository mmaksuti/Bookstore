package test.system;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.framework.junit5.ApplicationTest;
import src.controllers.LoginController;
import src.models.User;
import src.services.FileHandlingService;
import src.stages.EditUserStage;
import src.stages.ManageUsersStage;
import src.stages.NewUserStage;
import src.enums.AccessLevel;

import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestManageUsersStage extends ApplicationTest {
    LoginController loginController = null;
    FileHandlingService fileHandlingService = new FileHandlingService();

    @TempDir
    File tempDir;

    @Override
    public void start(Stage stage) {
        ManageUsersStage manageUsersStage = null;
        try {
            String usersDatabase = tempDir.getAbsolutePath() + "/usersDatabase.dat";
            String session = tempDir.getAbsolutePath() + "/session.dat";

            loginController = new LoginController(fileHandlingService, null, usersDatabase, session);

            User user = loginController.getUsers().get(0);
            manageUsersStage = new ManageUsersStage(loginController);
        }
        catch (IOException ex) {
            fail("Failed to load database: " + ex.getMessage());
        }

        stage.setScene(manageUsersStage.getScene());
        stage.show();
    }

    @Test
    void testTableViewListener() {
        TableView<User> tableView = lookup(".table-view").query();
        ObservableList<User> users = tableView.getItems();
        assertEquals(loginController.getUsers(), users);
    }

    @Test
    void testRemoveUserOnlyOneAdmin() {
        TableView<User> tableView = lookup(".table-view").query();
        TableRow<User> row = lookup(".table-row-cell").query();
        clickOn(row);

        Set<Button> buttons = lookup(".button").queryAllAs(Button.class);
        Iterator<Button> buttonIterator = buttons.iterator();
        buttonIterator.next();

        clickOn(buttonIterator.next());

        Node dialogPane = lookup(".dialog-pane").query();
        assertEquals("Cannot delete the last administrator", ((DialogPane) dialogPane).getContentText());
    }

    @Test
    void testRemoveUser() {
        try {
            loginController.addUser("John", "Doe", "johndoe", "password", "johndoe@gmail.com", "+355671234567", 3000, LocalDate.of(1999, 12, 12), AccessLevel.MANAGER);
        }
        catch (Exception ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        TableView<User> tableView = lookup(".table-view").query();
        ObservableList<User> users = tableView.getItems();

        User user = users.get(1);

        Set<TableRow> rows = lookup(".table-row-cell").queryAllAs(TableRow.class);
        Iterator<TableRow> iterator = rows.iterator();
        iterator.next();

        clickOn(iterator.next());

        Set<Button> buttons = lookup(".button").queryAllAs(Button.class);
        Iterator<Button> buttonIterator = buttons.iterator();
        buttonIterator.next();

        clickOn(buttonIterator.next());

        assertFalse(users.contains(user));
    }
}
