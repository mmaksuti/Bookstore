package src.stages;
import src.controllers.AuthorsController;
import src.controllers.BooksController;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import src.models.Book;

import java.io.IOException;

public class ManageBooksStage extends Stage {
    public ManageBooksStage(AuthorsController authorsController, BooksController booksController) {
        setTitle("Manage books");

        TableView <Book> tableView = new TableView <>();
        tableView.setItems(booksController.getBooks());
        
        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setMinWidth(150);
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setMinWidth(150);
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, String> isbn13Column = new TableColumn<>("ISBN13");
        isbn13Column.setMinWidth(150);
        isbn13Column.setCellValueFactory(new PropertyValueFactory<>("isbn13"));

        TableColumn<Book, String> priceColumn = new TableColumn<>("Price");
        priceColumn.setMinWidth(60);
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        TableColumn<Book, String> paperbackColumn = new TableColumn<>("Paperback");
        paperbackColumn.setMinWidth(60);
        paperbackColumn.setCellValueFactory(new PropertyValueFactory<>("paperback"));

        TableColumn<Book, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setMinWidth(60);
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setCellFactory(column -> {
            return new TableCell<>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(null);
                    if (!empty) {
                        setText(item.toString());

                        if (item <= 5) {
                            setStyle("-fx-text-fill: red;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            };
        });

        tableView.getColumns().addAll(titleColumn, authorColumn, isbn13Column, priceColumn, paperbackColumn, quantityColumn);

        Button editButton = new Button("Edit book");
        editButton.setOnAction(e -> {
            Book book = tableView.getSelectionModel().getSelectedItem();
            if (book != null) {
                EditBookStage editBookStage = new EditBookStage(authorsController, booksController, book);
                editBookStage.show();
            }
        });

        Button deleteButton = new Button("Remove book");
        deleteButton.setOnAction(e -> {
            Book book = tableView.getSelectionModel().getSelectedItem();
            if (book != null) {
                try {
                    booksController.removeBook(book);
                }
                catch (IOException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to remove book");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                }
            }
        });

        Button addButton = new Button("Add book");
        addButton.setOnAction(e -> {
            NewBookStage newBookStage = new NewBookStage(authorsController, booksController);
            newBookStage.show();
        });

        BorderPane pane = new BorderPane();
        pane.setTop(tableView);

        HBox buttons = new HBox();
        buttons.getChildren().addAll(editButton, deleteButton, addButton);
        buttons.setSpacing(10);
        buttons.setAlignment(Pos.CENTER);

        pane.setBottom(buttons);

        Scene scene = new Scene(pane, 657, 450);
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