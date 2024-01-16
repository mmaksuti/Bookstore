package test.system;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.framework.junit5.ApplicationTest;
import src.controllers.*;
import src.models.*;
import src.services.FileHandlingService;
import src.stages.LibrariansStatisticsStage;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TestLibrariansStatisticsStage extends ApplicationTest {
    LibrarianController librarianController;
    LoginController loginController;
    BillController billController;
    FileHandlingService fileHandlingService = new FileHandlingService();

    @TempDir
    File tempDir;

    @Override
    public void start(Stage stage) {
        try {
            String userDatabase = tempDir.getAbsolutePath() + "/usersDatabase.dat";
            String session = tempDir.getAbsolutePath() + "/session.dat";
            String billsPath = tempDir.getAbsolutePath() + "/bills";

            billController = new BillController(fileHandlingService, billsPath);
            loginController = new LoginController(fileHandlingService, billController, userDatabase, session);
            librarianController = new LibrarianController(loginController, billController);

            LibrariansStatisticsStage librariansStatisticsStage = new LibrariansStatisticsStage(librarianController);
            stage.setScene(librariansStatisticsStage.getScene());
            stage.show();
        } catch (IOException ex) {
            Assertions.fail("Failed to load databases: " + ex.getMessage());
        }
    }

    @Test
    void testTableViewListener() {
        TableView<Librarian> tableView = lookup(".table-view").query();
        ObservableList<Librarian> librarians = tableView.getItems();
        assertEquals(librarianController.getLibrarians(), librarians);
    }
}
