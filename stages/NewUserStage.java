package stages;
import javafx.stage.Stage;
import main.AccessLevel;
import main.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.io.IOException;

import controllers.LoginController;

public class NewUserStage extends Stage {

    public NewUserStage(LoginController loginController) {
        setTitle("New user");

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setSpacing(20);
        Scene scene = new Scene(vbox, 350, 400);
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        Label firstNameLabel = new Label("First name:");
        grid.add(firstNameLabel, 0, 0);
        TextField firstNameField = new TextField();
        grid.add(firstNameField, 1, 0);

        Label lastNameLabel = new Label("Last name:");
        grid.add(lastNameLabel, 0, 1);
        TextField lastNameField = new TextField();
        grid.add(lastNameField, 1, 1);

        Label usernameLabel = new Label("Username:");
        grid.add(usernameLabel, 0, 2);
        TextField usernameField = new TextField();
        grid.add(usernameField, 1, 2);

        Label passwordLabel = new Label("Password:");
        grid.add(passwordLabel, 0, 3);
        PasswordField passwordField = new PasswordField();
        grid.add(passwordField, 1, 3);

        Label emaiLabel = new Label("Email:");
        grid.add(emaiLabel, 0, 4);
        TextField labelField = new TextField();
        grid.add(labelField, 1, 4);

        Label phoneNumber = new Label("Phone:");
        grid.add(phoneNumber, 0, 5);
        TextField phoneNumberField = new TextField();
        grid.add(phoneNumberField, 1, 5);

        Label salary = new Label("Salary:");
        grid.add(salary, 0, 6);
        TextField salaryField = new TextField();
        grid.add(salaryField, 1, 6);

        Label birthdayLabel = new Label("Birthday:");
        grid.add(birthdayLabel, 0, 7);
        DatePicker datePicker = new DatePicker();
        grid.add(datePicker, 1, 7);

        Label roleLabel = new Label("Role:");
        grid.add(roleLabel, 0, 8);
        ComboBox<AccessLevel> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll(AccessLevel.values());
        grid.add(roleComboBox, 1, 8);
        
        Button addUser = new Button("Add User");
        Label status = new Label("");
        status.setTextFill(javafx.scene.paint.Color.RED);

        addUser.setOnAction(e -> {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();
            String email = labelField.getText();
            String phone = phoneNumberField.getText();
            String salaryString = salaryField.getText();

            int salaryInt;
            try {
                salaryInt = Integer.parseInt(salaryString);
            } catch (NumberFormatException ex) {
                status.setText("Salary must be a number");
                return;
            }

            LocalDate birthday = datePicker.getValue();
            AccessLevel role = roleComboBox.getValue();
            try {
                loginController.addUser(firstName, lastName, username, password, email, phone, salaryInt, birthday, role);
            }
            catch (IllegalArgumentException ex) {
                status.setText(ex.getMessage());
                return;
            }
            catch (IOException ex) {
                status.setText("Failed to save user: " + ex.getMessage());
                return;
            }

            status.setText("User added successfully");
        });

        //Label accessLevelLabel = new Label("Access Level:");
        //grid.add(accessLevelLabel, 0, 3);

        setScene(scene);
        vbox.getChildren().addAll(grid, addUser, status);

    }
}