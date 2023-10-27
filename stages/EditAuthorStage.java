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

public class EditAuthorStage extends Stage {
    public EditAuthorStage(Author author) {
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

            if (firstName.isBlank() || lastName.isBlank() || gender == null) {
                status.setText("Please fill in all fields");
                return;
            }

            String oldFirstName = author.getFirstName();
            String oldLastName = author.getLastName();
            boolean nameChanged = !firstName.equals(oldFirstName) || !lastName.equals(oldLastName);
            if (nameChanged && AuthorsController.authorExists(firstName, lastName)) {
                status.setText("Author already exists");
                return;
            }

            author.setFirstName(firstName);
            author.setLastName(lastName);
            author.setGender(gender);
            AuthorsController.updateAuthor(author);

            if (nameChanged) {
                ObservableList<Book> books = BooksController.books;
                BooksController.updateBook(books.get(0)); // trigger update on the books ObservableList

                for (Book book : books) {
                    if (book.getAuthor().getFirstName().equals(oldFirstName) && book.getAuthor().getLastName().equals(oldLastName)) {
                        // if the author name wasn't updated (if book has a different author reference), update it
                        book.setAuthor(author);
                        BooksController.updateBook(book);
                    }
                }
            }

            status.setText("Author modified successfully");
        });

        setScene(scene);
        vbox.getChildren().addAll(grid, addAuthor, status);
    }
}