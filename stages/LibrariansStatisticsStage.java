package stages;
import controllers.LibrarianController;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import main.Librarian;

public class LibrariansStatisticsStage extends Stage {
    public LibrariansStatisticsStage() {
        setTitle("Librarian list");

        TableView <Librarian> tableView = new TableView <>();
        tableView.setItems(LibrarianController.librarians);
        
        TableColumn<Librarian, String> firstNameColumn = new TableColumn<>("First name");
        firstNameColumn.setMinWidth(100);
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Librarian, String> lastNameColumn = new TableColumn<>("Last name");
        lastNameColumn.setMinWidth(100);
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Librarian, String> totalBillsColumn = new TableColumn<>("Total bills");
        totalBillsColumn.setMinWidth(100);
        totalBillsColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfBills"));

        TableColumn<Librarian, String> totalMoneyColumn = new TableColumn<>("Total Money");
        totalMoneyColumn.setMinWidth(100);
        totalMoneyColumn.setCellValueFactory(new PropertyValueFactory<>("totalMoney"));

        tableView.getColumns().addAll(firstNameColumn, lastNameColumn, totalBillsColumn, totalMoneyColumn);

        Pane pane = new Pane();
        pane.getChildren().add(tableView);
        
        Scene scene = new Scene(pane, 400, 400);
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