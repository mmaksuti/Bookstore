package stages;
import javafx.stage.Stage;
import main.Author;
import main.Book;
import main.Gender;
import controllers.AuthorsController;
import controllers.BooksController;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class EditAuthorStage extends Stage {
    public EditAuthorStage(AuthorsController authorsController, Author author) {
        setTitle("Edit author");

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
        authorFirstNameField.setText(author.getFirstName());

        Label authorLastNameLabel = new Label("Last name");
        grid.add(authorLastNameLabel, 0, 1);
        TextField authorLastNameField = new TextField();
        grid.add(authorLastNameField, 1, 1);
        authorLastNameField.setText(author.getLastName());

        Label authorGenderLabel = new Label("Gender");
        grid.add(authorGenderLabel, 0, 2);
        ComboBox<Gender> authorGenderComboBox = new ComboBox<>();
        authorGenderComboBox.getItems().addAll(Gender.values());
        grid.add(authorGenderComboBox, 1, 2);
        authorGenderComboBox.setValue(author.getGender());
        
        Button addAuthor = new Button("Edit author");
        Label status = new Label("");
        status.setTextFill(javafx.scene.paint.Color.RED);

        addAuthor.setOnAction(e -> {
            String firstName = authorFirstNameField.getText();
            String lastName = authorLastNameField.getText();
            Gender gender = authorGenderComboBox.getValue();

            try {
                authorsController.updateAuthor(author, firstName, lastName, gender);
            }
            catch (IllegalArgumentException ex) {
                status.setText(ex.getMessage());
                return;
            }
            catch (IOException ex) {
                status.setText("Failed to save author: " + ex.getMessage());
                return;
            }

            status.setText("Author modified successfully");
        });

        setScene(scene);
        vbox.getChildren().addAll(grid, addAuthor, status);
    }
}