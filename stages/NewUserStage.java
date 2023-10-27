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

import controllers.LoginController;

public class NewUserStage extends Stage {
    private static boolean validEmail(String email) {
        return email.matches("\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+");
    }

    private static boolean validPhoneNumber(String phone) {
        String nospaces = phone.replaceAll(" ", "");
        return nospaces.matches("\\+3556[7-9]\\d{7}");
    }

    private static boolean validUsername(String username) {
        return username.matches("[a-zA-Z0-9_]+");
    }

    public NewUserStage() {
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

            if (firstName.isBlank() || lastName.isBlank() || username.isBlank() || password.isBlank() || email.isBlank() || phone.isBlank() || salaryString.isBlank() || datePicker.getValue() == null || roleComboBox.getValue() == null) {
                status.setText("Please fill in all fields");
                return;
            }

            if (!validUsername(username)) {
                status.setText("Username must contain only letters, numbers and underscores");
                return;
            }
            
            if (LoginController.userExists(username)) {
                status.setText("Username already exists");
                return;
            } 
            
            if (password.length() < 5) {
                status.setText("Password must be at least 5 characters");
                return;
            }

            if (!validEmail(email)) {
                status.setText("Invalid email");
                return;
            }

            if (!validPhoneNumber(phone)) {
                status.setText("Invalid phone number");
                return;
            }

            int salaryInt;
            try {
                salaryInt = Integer.parseInt(salaryString);
            } catch (NumberFormatException ex) {
                status.setText("Salary must be a number");
                return;
            }

            LocalDate birthday = datePicker.getValue();
            if (birthday.isAfter(LocalDate.now().minusYears(18))) {
                status.setText("User must be at least 18 years old");
                return;
            }

            AccessLevel role = roleComboBox.getValue();
            User user = new User(firstName, lastName, username, password, email, phone, salaryInt, birthday, role);
            LoginController.addUser(user);

            status.setText("User created successfully");
        });

        //Label accessLevelLabel = new Label("Access Level:");
        //grid.add(accessLevelLabel, 0, 3);

        setScene(scene);
        vbox.getChildren().addAll(grid, addUser, status);

    }
}