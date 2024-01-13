package src;

import src.controllers.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import src.exceptions.UnauthenticatedException;

import src.scenes.*;
import src.services.FileHandlingService;

import java.io.IOException;

import static java.lang.System.exit;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BillController billController = null;
        LoginController loginController = null;
        AuthorsController authorsController = null;
        BooksController booksController = null;
        LibrarianController librarianController = null;
        FileHandlingService fileHandlingService = new FileHandlingService();

        try {
            billController = new BillController(fileHandlingService);
            loginController = new LoginController(fileHandlingService, billController);
            authorsController = new AuthorsController(fileHandlingService);
            booksController = new BooksController(fileHandlingService);
            librarianController = new LibrarianController(loginController, billController);
        }
        catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load databases");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            exit(1);
        }

        if (!loginController.loginWithSavedSession()) {
            primaryStage.setTitle("Login");
            
            LoginScene login = new LoginScene(loginController, billController, authorsController, booksController, librarianController, fileHandlingService);
            primaryStage.setScene(login);
        }
        else {
            try {
                UserScene scene = (UserScene)SceneSelector.getSceneByAccessLevel(loginController, billController, authorsController, booksController, librarianController, fileHandlingService);
                primaryStage.setTitle(scene.getName());
                primaryStage.setScene((Scene)scene);
            }
            catch (UnauthenticatedException e) {
                // should never happen
            }
        }

        primaryStage.show();
    }
}
