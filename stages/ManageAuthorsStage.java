package stages;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import controllers.AuthorsController;
import controllers.BooksController;
import javafx.collections.ObservableList;
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
import main.Author;
import main.Book;

public class ManageAuthorsStage extends Stage {
    public ManageAuthorsStage() {
        setTitle("Author list");

        TableView <Author> tableView = new TableView <>();
        tableView.setItems(AuthorsController.authors);
        
        TableColumn<Author, String> firstName = new TableColumn<>("First Name");
        firstName.setMinWidth(100);
        firstName.setCellValueFactory(new PropertyValueFactory<Author, String>("firstName"));

        TableColumn<Author, String> lastName = new TableColumn<>("Last Name");
        lastName.setMinWidth(100);
        lastName.setCellValueFactory(new PropertyValueFactory<Author, String>("lastName"));

        TableColumn<Author, String> gender = new TableColumn<>("Gender");
        gender.setMinWidth(100);
        gender.setCellValueFactory(new PropertyValueFactory<Author, String>("gender"));

       
        tableView.getColumns().addAll(firstName, lastName, gender);

        Button editButton = new Button("Edit author");
        editButton.setOnAction(e -> {
            Author author = tableView.getSelectionModel().getSelectedItem();
            if (author != null) {
                EditAuthorStage editAuthorStage = new EditAuthorStage(author);
                editAuthorStage.show();
            }
        });

        Button deleteButton = new Button("Remove author");
        deleteButton.setOnAction(e -> {
            ObservableList<Book> books = BooksController.books;
            Author author = tableView.getSelectionModel().getSelectedItem();
            if (author == null) {
                return;
            }

            AtomicBoolean removeAll = new AtomicBoolean(false);
            AtomicBoolean firstTime = new AtomicBoolean(true);
            for (Book book : books) {
                System.out.println(book);
                if (book.getAuthor().getFirstName().equals(author.getFirstName()) && book.getAuthor().getLastName().equals(author.getLastName())) {
                    if (firstTime.get()) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Warning");
                        alert.setHeaderText("Author has books");
                        alert.setContentText("Are you sure you want to delete them all?");
                        ButtonType buttonTypeYes = new ButtonType("Yes");
                        ButtonType buttonTypeNo = new ButtonType("No");
                        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
                        alert.showAndWait().ifPresent(type -> {
                            if (type == buttonTypeYes) {
                                removeAll.set(true);
                                BooksController.removeBook(book);
                            }

                            firstTime.set(false);
                        });
                    }
                    else if (removeAll.get()) {
                        BooksController.removeBook(book);
                    }
                }
            }
            if (firstTime.get() || removeAll.get()) {
                AuthorsController.removeAuthor(author);
            }
        });

        Button addButton = new Button("Add author");
        addButton.setOnAction(e -> {
            NewAuthorStage newAuthorStage = new NewAuthorStage();
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