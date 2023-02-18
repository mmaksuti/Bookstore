package stages;
import java.util.ArrayList;

import controllers.BooksController;
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
import main.Book;

public class SellBooksStage extends Stage {
    public SellBooksStage() {
        setTitle("Sell books");

        TableView <Book> tableView = new TableView <>();
        tableView.setItems(BooksController.books);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setMinWidth(150);
        titleColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("title"));

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setMinWidth(150);
        authorColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("author"));

        TableColumn<Book, String> isbn13Column = new TableColumn<>("ISBN13");
        isbn13Column.setMinWidth(150);
        isbn13Column.setCellValueFactory(new PropertyValueFactory<Book, String>("isbn13"));

        TableColumn<Book, String> priceColumn = new TableColumn<>("Price");
        priceColumn.setMinWidth(60);
        priceColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("price"));

        TableColumn<Book, String> paperbackColumn = new TableColumn<>("Paperback");
        paperbackColumn.setMinWidth(60);
        paperbackColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("paperback"));

        TableColumn<Book, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setMinWidth(60);
        quantityColumn.setCellValueFactory(new PropertyValueFactory<Book, Integer>("quantity"));
        quantityColumn.setCellFactory(column -> {
            return new TableCell<Book, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(null);
                    if (!empty) { 
                        setText(item.toString());

                        if (item <= 5) {
                            setStyle("-fx-text-fill: red;");
                        }
                        else {
                            setStyle("");
                        }
                    }
                }
            };
        });

        tableView.getColumns().addAll(titleColumn, authorColumn, isbn13Column, priceColumn, paperbackColumn, quantityColumn);

        Button sellButton = new Button("Sell");
        sellButton.setOnAction(e -> {
            ObservableList <Book> booksToSell = tableView.getSelectionModel().getSelectedItems();

            if (booksToSell.size() > 0) {
                ArrayList <Book> booksToSellArray = new ArrayList <>(booksToSell);
                CheckOutStage checkOutStage = new CheckOutStage(booksToSellArray);
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
        
            while (source != null && !(source instanceof TableRow)) {
                source = source.getParent();
            }
        
            if (source == null || (source instanceof TableRow && ((TableRow) source).isEmpty())) {
                tableView.getSelectionModel().clearSelection();
            }
        });
    }
}