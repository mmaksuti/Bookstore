package scenes;

import controllers.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import main.UnauthenticatedException;
import stages.ManageAuthorsStage;
import stages.ManageBooksStage;
import stages.LibrariansStatisticsStage;
import stages.SalesStatisticsStage;
import stages.SellBooksStage;

public class ManagerScene extends Scene {
    MenuBar menuBar = new MenuBar();
    Menu manage = new Menu("Manage");
    MenuItem manageBooks = new MenuItem("Books");
    MenuItem manageAuthors = new MenuItem("Authors");

    Menu statistics = new Menu("Statistics");
    MenuItem salesStatistics = new MenuItem("Sales");
    MenuItem librariansStats = new MenuItem("Librarians");
    
    public ManagerScene() {
        super(new BorderPane(), 300, 200);

        BorderPane border = (BorderPane) getRoot();

        manageBooks.setOnAction(e -> {
            ManageBooksStage manageBooksStage = new ManageBooksStage();
            manageBooksStage.show();
        });

        manageAuthors.setOnAction(e -> {
            ManageAuthorsStage manageAuthorsStage = new ManageAuthorsStage();
            manageAuthorsStage.show();
        });

        salesStatistics.setOnAction(e -> {
            SalesStatisticsStage salesStatisticsStage = new SalesStatisticsStage();
            salesStatisticsStage.show();
        });

        librariansStats.setOnAction(e -> {
            LibrariansStatisticsStage librariansStatsStage = new LibrariansStatisticsStage();
            librariansStatsStage.show();
        });

        menuBar.getMenus().addAll(manage, statistics);
        manage.getItems().addAll(manageBooks, manageAuthors);
        statistics.getItems().addAll(salesStatistics, librariansStats);

        border.setTop(menuBar);

        Label welcomeLabel;
        try {
            welcomeLabel = new Label();
            welcomeLabel.textProperty().bind(LoginController.getWelcomeMessage());
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
            SellBooksStage sellBooksStage = new SellBooksStage();
            sellBooksStage.show();
        });

        Button logoutButton = new Button("Log out");
        logoutButton.setOnAction(e -> LoginController.logout());


        logoutHBox.getChildren().addAll(sellBooksButton, logoutButton);
        border.setBottom(logoutHBox);
    }
}
