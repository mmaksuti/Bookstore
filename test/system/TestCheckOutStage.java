package test.system;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.framework.junit5.ApplicationTest;
import src.controllers.BillController;
import src.controllers.BooksController;
import src.controllers.LoginController;
import src.enums.Gender;
import src.models.Author;
import src.models.Bill;
import src.models.Book;
import src.models.User;
import src.services.FileHandlingService;
import src.stages.CheckOutStage;
import src.stages.ManageUsersStage;
import src.enums.AccessLevel;

import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class TestCheckOutStage extends ApplicationTest {
    BooksController booksController;
    LoginController loginController;
    BillController billController;
    FileHandlingService fileHandlingService = new FileHandlingService();

    @TempDir
    File tempDir;

    @Override
    public void start(Stage stage) {
        CheckOutStage checkOutStage = null;
        try {
            String usersDatabase = tempDir.getAbsolutePath() + "/usersDatabase.dat";
            String booksDatabase = tempDir.getAbsolutePath() + "/booksDatabase.dat";
            String billsPath = tempDir.getAbsolutePath() + "/bills";
            String session = tempDir.getAbsolutePath() + "/session.dat";

            billController = new BillController(fileHandlingService, billsPath);

            booksController = new BooksController(fileHandlingService, booksDatabase);
            booksController.addBook("Book title", new Author("John", "Doe", Gender.MALE), "123-1234567890", 1000, "Book description", false, null, 10);
            booksController.addBook("Book2 title", new Author("Jane", "Doe", Gender.FEMALE), "123-1234567891", 2000, "Book2 description", true, null, 20);

            loginController = new LoginController(fileHandlingService, billController, usersDatabase, session);
            loginController.login("admin", "admin");

            ArrayList<Book> booksToSell = new ArrayList<Book>();
            booksToSell.add(booksController.getBooks().get(0));
            booksToSell.add(booksController.getBooks().get(1));

            checkOutStage = new CheckOutStage(booksToSell, booksController, loginController, billController);
        }
        catch (IOException ex) {
            fail("Failed to load database: " + ex.getMessage());
        }

        stage.setScene(checkOutStage.getScene());
        stage.show();
    }

    @AfterEach
    void tearDown() {
        fileHandlingService.deleteFile(tempDir.getAbsolutePath() + "/bills");
    }

    @Test
    void testCheckOut() {
        Set<TextField> textFields = lookup(".text-field").queryAll();
        Iterator<TextField> iterator = textFields.iterator();

        TextField quantityField1 = iterator.next();
        TextField quantityField2 = iterator.next();

        clickOn(quantityField1);
        write("7");
        clickOn(quantityField2);
        write("20");

        clickOn("Check out");
        clickOn("OK");

        Book book1 = booksController.getBooks().get(0);
        Book book2 = booksController.getBooks().get(1);

        assertEquals(3, book1.getQuantity());
        assertEquals(0, book2.getQuantity());

        assertEquals(1, billController.loadBills().length);
    }

    @Test
    void testCheckOutWithInvalidQuantity() {
        Set<TextField> textFields = lookup(".text-field").queryAll();
        Iterator<TextField> iterator = textFields.iterator();

        TextField quantityField1 = iterator.next();
        TextField quantityField2 = iterator.next();

        clickOn(quantityField1);
        write("7");
        clickOn(quantityField2);
        write("100");

        clickOn("Check out");
        clickOn("OK");

        Book book1 = booksController.getBooks().get(0);
        Book book2 = booksController.getBooks().get(1);

        assertEquals(3, book1.getQuantity());
        assertEquals(0, book2.getQuantity());

        assertEquals(1, billController.loadBills().length);
    }
}
