package stages;
import javafx.stage.Stage;
import main.Author;
import main.Gender;
import controllers.AuthorsController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class NewAuthorStage extends Stage {
    public NewAuthorStage() {
        setTitle("New author");

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setSpacing(20);
        Scene scene = new Scene(vbox, 300, 200);
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        Label authorFirstNameLabel = new Label("First name");
        grid.add(authorFirstNameLabel, 0, 0);
        TextField authorFirstNameField = new TextField();
        grid.add(authorFirstNameField, 1, 0);
     
        Label authorLastNameLabel = new Label("Last name");
        grid.add(authorLastNameLabel, 0, 1);
        TextField authorLastNameField = new TextField();
        grid.add(authorLastNameField, 1, 1);

        Label authorGenderLabel = new Label("Gender");
        grid.add(authorGenderLabel, 0, 2);
        ComboBox<Gender> authorGenderComboBox = new ComboBox<>();
        authorGenderComboBox.getItems().addAll(Gender.values());
        grid.add(authorGenderComboBox, 1, 2);

        Button addAuthor = new Button("Add author");
        Label status = new Label("");
        status.setTextFill(javafx.scene.paint.Color.RED);

        addAuthor.setOnAction(e -> {
            String firstName = authorFirstNameField.getText();
            String lastName = authorLastNameField.getText();
            Gender gender = authorGenderComboBox.getValue();

            if (firstName.isBlank() || lastName.isBlank() || gender == null) {
                status.setText("Please fill in all fields");
                return;
            }

            if (AuthorsController.authorExists(firstName, lastName)) {
                status.setText("Author already exists");
                return;
            }
            
            Author newAuthor = new Author(firstName, lastName, gender);
            AuthorsController.addAuthor(newAuthor);

            status.setText("Author added successfully");
        });

        setScene(scene);
        vbox.getChildren().addAll(grid, addAuthor, status);

    }


}