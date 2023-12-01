package stages;
import controllers.LoginController;
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
import main.User;

public class ManageUsersStage extends Stage {
    public ManageUsersStage(LoginController loginController) {
        setTitle("User list");

        TableView <User> tableView = new TableView <>();
        tableView.setItems(loginController.users);
        
        TableColumn<User, String> firstNameColumn = new TableColumn<>("First name");
        firstNameColumn.setMinWidth(100);
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<User, String> lastNameColumn = new TableColumn<>("Last name");
        lastNameColumn.setMinWidth(100);
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<User, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setMinWidth(100);
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<User, String> accessLevelColumn = new TableColumn<>("Role");
        accessLevelColumn.setMinWidth(110);
        accessLevelColumn.setCellValueFactory(new PropertyValueFactory<>("accessLevel"));

        tableView.getColumns().addAll(firstNameColumn, lastNameColumn, usernameColumn, accessLevelColumn);

        Button editButton = new Button("Edit user");
        editButton.setOnAction(e -> {
            User user = tableView.getSelectionModel().getSelectedItem();
            if (user != null) {
                EditUserStage editUserStage = new EditUserStage(user, loginController);
                editUserStage.show();
            }
        });

        Button deleteButton = new Button("Remove user");
        deleteButton.setOnAction(e -> {
            User user = tableView.getSelectionModel().getSelectedItem();
            if (user != null) {
                try {
                    loginController.removeUser(user);
                } catch (Exception e1) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, e1.getMessage(), ButtonType.OK);
                    alert.showAndWait();
                }
            }
        });

        Button addButton = new Button("Add user");
        addButton.setOnAction(e -> {
            NewUserStage newUserStage = new NewUserStage(loginController);
            newUserStage.show();
        });

        BorderPane pane = new BorderPane();
        pane.setTop(tableView);

        HBox buttons = new HBox();
        buttons.getChildren().addAll(editButton, deleteButton, addButton);
        buttons.setSpacing(10);
        buttons.setAlignment(Pos.CENTER);

        pane.setBottom(buttons);

        Scene scene = new Scene(pane, 410, 450);
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