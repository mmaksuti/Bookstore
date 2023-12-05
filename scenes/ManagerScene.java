package scenes;

import controllers.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import exceptions.UnauthenticatedException;
import main.Author;
import main.Bill;
import stages.ManageAuthorsStage;
import stages.ManageBooksStage;
import stages.LibrariansStatisticsStage;
import stages.SalesStatisticsStage;
import stages.SellBooksStage;

import java.io.IOException;

import static java.lang.System.exit;

public class ManagerScene extends Scene implements UserScene {
    MenuBar menuBar = new MenuBar();
    Menu manage = new Menu("Manage");
    MenuItem manageBooks = new MenuItem("Books");
    MenuItem manageAuthors = new MenuItem("Authors");

    Menu statistics = new Menu("Statistics");
    MenuItem salesStatistics = new MenuItem("Sales");
    MenuItem librariansStats = new MenuItem("Librarians");

    public String getName() {
        return "Manager";
    }

    public ManagerScene(LoginController loginController, BillController billController, AuthorsController authorsController, BooksController booksController, LibrarianController librarianController) {
        super(new BorderPane(), 300, 200);

        BorderPane border = (BorderPane) getRoot();

//        AuthorsController authorsController = null;
//        BooksController booksController = null;
//        LibrarianController librarianController = null;
//        try {
//            authorsController = new AuthorsController();
//            booksController = new BooksController();
//            librarianController = new LibrarianController(loginController, billController);
//        }
//        catch (IOException ex) {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Error");
//            alert.setHeaderText("Failed to load databases");
//            alert.setContentText(ex.getMessage());
//            alert.showAndWait();
//            exit(1);
//        }

//        AuthorsController finalAuthorsController = authorsController;
//        BooksController finalBooksController = booksController;
//        LibrarianController finalLibrarianController = librarianController;
        manageBooks.setOnAction(e -> {
            ManageBooksStage manageBooksStage = new ManageBooksStage(authorsController, booksController);
            manageBooksStage.show();
        });

        manageAuthors.setOnAction(e -> {
            ManageAuthorsStage manageAuthorsStage = new ManageAuthorsStage(authorsController, booksController);
            manageAuthorsStage.show();
        });

        salesStatistics.setOnAction(e -> {
            SalesStatisticsStage salesStatisticsStage = new SalesStatisticsStage();
            salesStatisticsStage.show();
        });

        librariansStats.setOnAction(e -> {
            LibrariansStatisticsStage librariansStatsStage = new LibrariansStatisticsStage(librarianController);
            librariansStatsStage.show();
        });

        menuBar.getMenus().addAll(manage, statistics);
        manage.getItems().addAll(manageBooks, manageAuthors);
        statistics.getItems().addAll(salesStatistics, librariansStats);

        border.setTop(menuBar);

        Label welcomeLabel;
        try {
            welcomeLabel = new Label();
            welcomeLabel.textProperty().bind(loginController.getWelcomeMessage());
        }
        catch (UnauthenticatedException e) {
            // should never happen
            welcomeLabel = new Label("Unauthenticated");
        }
    
        welcomeLabel.setStyle("-fx-font-size: 15px; -fx-text-alignment: center;");
        border.setCenter(welcomeLabel);

        HBox logoutHBox = new HBox();
        logoutHBox.setAlignment(Pos.CENTER);
        logoutHBox.setPadding(new Insets(10));
        logoutHBox.setSpacing(10);

        Button sellBooksButton = new Button("Sell books");
        sellBooksButton.setOnAction(e -> {
            SellBooksStage sellBooksStage = new SellBooksStage(booksController, loginController, billController);
            sellBooksStage.show();
        });

        Button logoutButton = new Button("Log out");
        logoutButton.setOnAction(e -> loginController.logout());


        logoutHBox.getChildren().addAll(sellBooksButton, logoutButton);
        border.setBottom(logoutHBox);
    }
}
