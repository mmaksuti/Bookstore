package src.stages;
import java.io.IOException;

import src.controllers.AuthorsController;
import src.controllers.BooksController;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import src.models.Author;
import src.interfaces.UserConfirmation;

public class ManageAuthorsStage extends Stage {
    public ManageAuthorsStage(AuthorsController authorsController, BooksController booksController) {
        setTitle("Author list");

        TableView <Author> tableView = new TableView <>();
        tableView.setItems(authorsController.getAuthors());
        
        TableColumn<Author, String> firstName = new TableColumn<>("First Name");
        firstName.setMinWidth(100);
        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Author, String> lastName = new TableColumn<>("Last Name");
        lastName.setMinWidth(100);
        lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Author, String> gender = new TableColumn<>("Gender");
        gender.setMinWidth(100);
        gender.setCellValueFactory(new PropertyValueFactory<>("gender"));

        tableView.getColumns().addAll(firstName, lastName, gender);

        Button editButton = new Button("Edit author");
        editButton.setOnAction(e -> {
            Author author = tableView.getSelectionModel().getSelectedItem();
            if (author != null) {
                EditAuthorStage editAuthorStage = new EditAuthorStage(authorsController, booksController, author);
                editAuthorStage.show();
            }
        });

        Button deleteButton = new Button("Remove author");
        deleteButton.setOnAction(e -> {
            Author author = tableView.getSelectionModel().getSelectedItem();
            if (author == null) {
                return;
            }

            try {
                UserConfirmation confirm = (header, message) -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Warning");
                    alert.setHeaderText(header);
                    alert.setContentText(message);
                    ButtonType buttonTypeYes = new ButtonType("Yes");
                    ButtonType buttonTypeNo = new ButtonType("No");
                    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
                    return alert.showAndWait().filter(type -> type == buttonTypeYes).isPresent();
                };
                authorsController.removeAuthor(booksController, author, confirm);
            }

            catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to remove author");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        Button addButton = new Button("Add author");
        addButton.setOnAction(e -> {
            NewAuthorStage newAuthorStage = new NewAuthorStage(authorsController);
            newAuthorStage.show();
        });

        BorderPane pane = new BorderPane();
        pane.setTop(tableView);

        HBox buttons = new HBox();
        buttons.getChildren().addAll(editButton, deleteButton, addButton);
        buttons.setSpacing(10);
        buttons.setAlignment(Pos.CENTER);

        pane.setBottom(buttons);

        Scene scene = new Scene(pane, 300, 450);
        setScene(scene);

        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> {
            Node source = evt.getPickResult().getIntersectedNode();

            //noinspection ConstantValue
            while (source != null && !(source instanceof TableRow)) {
                source = source.getParent();
            }
        
            if (source == null || ((TableRow<?>) source).isEmpty()) {
                tableView.getSelectionModel().clearSelection();
            }
        });
    }
}