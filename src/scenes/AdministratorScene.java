package src.scenes;

import src.controllers.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import src.exceptions.UnauthenticatedException;
import src.services.FileHandlingService;
import src.stages.ManageAuthorsStage;
import src.stages.ManageBooksStage;
import src.stages.LibrariansStatisticsStage;
import src.stages.ManageUsersStage;
import src.stages.SalesStatisticsStage;
import src.stages.SellBooksStage;

public class AdministratorScene extends Scene implements UserScene {
    MenuBar menuBar = new MenuBar();
    Menu manage = new Menu("Manage");
    MenuItem manageUsers = new MenuItem("Users");
    MenuItem manageBooks = new MenuItem("Books");
    MenuItem manageAuthors = new MenuItem("Authors");
    
    Menu statistics = new Menu("Statistics");
    MenuItem salesStatistics = new MenuItem("Sales");
    MenuItem librariansStats = new MenuItem("Librarians");

    public String getName() {
        return "Administrator";
    }

    public AdministratorScene(LoginController loginController, BillController billController, AuthorsController authorsController, BooksController booksController, LibrarianController librarianController, FileHandlingService fileHandlingService) {
        super(new BorderPane(), 300, 200);

        BorderPane border = (BorderPane) getRoot();

        manageUsers.setOnAction(e -> {
            ManageUsersStage listUsersStage = new ManageUsersStage(loginController);
            listUsersStage.show();
        });

        manageBooks.setOnAction(e -> {
            ManageBooksStage listBooksStage = new ManageBooksStage(authorsController, booksController);
            listBooksStage.show();
        });

        manageAuthors.setOnAction(e -> {
            ManageAuthorsStage listAuthorsStage = new ManageAuthorsStage(authorsController, booksController);
            listAuthorsStage.show();
        });
        
        salesStatistics.setOnAction(e -> {
            SalesStatisticsStage salesStatisticsStage = new SalesStatisticsStage(fileHandlingService);
            salesStatisticsStage.show();
        });

        librariansStats.setOnAction(e -> {
            LibrariansStatisticsStage librariansStatsStage = new LibrariansStatisticsStage(librarianController);
            librariansStatsStage.show();
        });
        
        menuBar.getMenus().addAll(manage, statistics);
        manage.getItems().addAll(manageUsers, manageBooks, manageAuthors);
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
        logoutButton.setOnAction(e -> {
            loginController.logout();
            System.exit(0);
        });

        logoutHBox.getChildren().addAll(sellBooksButton, logoutButton);
        border.setBottom(logoutHBox);
    }
}
