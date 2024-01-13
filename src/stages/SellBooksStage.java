package src.stages;
import java.util.ArrayList;

import src.controllers.BooksController;
import src.controllers.LoginController;
import src.controllers.BillController;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import src.models.Book;

public class SellBooksStage extends Stage {
    public SellBooksStage(BooksController booksController, LoginController loginController, BillController billController) {
        setTitle("Sell books");

        TableView <Book> tableView = new TableView <>();
        tableView.setItems(booksController.getBooks());
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
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
        quantityColumn.setCellFactory(column -> new TableCell<>() {
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
        });
        tableView.getColumns().addAll(titleColumn, authorColumn, isbn13Column, priceColumn, paperbackColumn, quantityColumn);
        Button sellButton = new Button("Sell");
        sellButton.setOnAction(e -> {
            ObservableList <Book> booksToSell = tableView.getSelectionModel().getSelectedItems();
            if (!booksToSell.isEmpty()) {
                ArrayList <Book> booksToSellArray = new ArrayList <>(booksToSell);
                CheckOutStage checkOutStage = new CheckOutStage(booksToSellArray, booksController, loginController, billController);
                checkOutStage.show();
            }
        });

        BorderPane pane = new BorderPane();
        pane.setTop(tableView);

        HBox buttons = new HBox();
        buttons.getChildren().addAll(sellButton);
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