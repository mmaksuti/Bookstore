package stages;
import javafx.stage.Stage;
import main.Author;
import main.Book;
import main.Genre;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

import controllers.AuthorsController;
import controllers.BooksController;


public class EditBookStage extends Stage {
    public EditBookStage(AuthorsController authorsController, BooksController booksController, Book book) {
        setTitle("Edit book");

        //System.out.println(book);

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setSpacing(20);
        Scene scene = new Scene(vbox, 400, 600);
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        Label bookTitleLabell = new Label("Book title:");
        grid.add(bookTitleLabell, 0, 0);
        TextField bookTitleField = new TextField();
        grid.add(bookTitleField, 1, 0);
        bookTitleField.setText(book.getTitle());

        Label bookAuthorLabel = new Label("Book author:");
        ComboBox<Author> cbo = new ComboBox<>();
		cbo.setItems(authorsController.getAuthors());
        cbo.setValue(book.getAuthor());
        grid.add(bookAuthorLabel, 0, 1);
        grid.add(cbo, 1, 1);
        
        Label iSBN13Label = new Label("ISBN13:");
        grid.add(iSBN13Label, 0, 2);
        TextField iSBN13Field = new TextField();
        grid.add(iSBN13Field, 1, 2);
        iSBN13Field.setText(book.getIsbn13());

        Label priceLabel = new Label("Price:");
        grid.add(priceLabel, 0, 3);
        TextField priceField = new TextField();
        grid.add(priceField, 1, 3);
        priceField.setText(String.valueOf(book.getPrice()));

        Label descriptionLabel = new Label("Description:");
        grid.add(descriptionLabel, 0, 4);
        TextArea descriptionTA = new TextArea();
        descriptionTA.setPrefColumnCount(20);
		descriptionTA.setPrefRowCount(5);
		descriptionTA.setWrapText(true);
        grid.add(descriptionTA, 1, 4);
        descriptionTA.setText(book.getDescription());

        VBox paneForGenres = new VBox(10);
        paneForGenres.setPadding(new Insets(4));
        Label genreLabel = new Label("Genres:");
        grid.add(genreLabel, 0, 5);
        ArrayList<CheckBox> genreCheckboxes = new ArrayList<>();
		for(Genre g : Genre.values()) {
			genreCheckboxes.add(new CheckBox(g.toString()));
		}
		paneForGenres.getChildren().addAll(genreCheckboxes);
        grid.add(paneForGenres, 1, 5);

        for (Genre g : book.getGenres()) {
            for (CheckBox cb : genreCheckboxes) {
                if (cb.getText().equals(g.toString())) {
                    cb.setSelected(true);
                }
            }
        }

        Label quantityLabel = new Label("Quantity:");
        grid.add(quantityLabel, 0, 6);
        TextField quantityField = new TextField();
        grid.add(quantityField, 1, 6);
        quantityField.setText(String.valueOf(book.getQuantity()));

        Label versionLabel = new Label("Version");
		RadioButton rbPaperback = new RadioButton("Paperback");
		RadioButton rbEbook = new RadioButton("E-book");
		ToggleGroup group = new ToggleGroup();
		rbPaperback.setToggleGroup(group);
		rbEbook.setToggleGroup(group);
        grid.add(versionLabel, 0, 7);
        grid.add(rbPaperback, 1, 7);
        grid.add(rbEbook, 1, 8);
        if (book.isPaperback()) {
            rbPaperback.setSelected(true);
        } else {
            rbEbook.setSelected(true);
        }
        
        Button addBook = new Button("Save changes");
        Label status = new Label("");
        status.setTextFill(javafx.scene.paint.Color.RED);

        addBook.setOnAction(e -> {
            String title = bookTitleField.getText();
            Author author = cbo.getValue();
            String isbn13 = iSBN13Field.getText();
            String price = priceField.getText();
            String description = descriptionTA.getText();
            boolean isPaperback = rbPaperback.isSelected();
            ArrayList<Genre> genres = new ArrayList<>();
            for(CheckBox cb : genreCheckboxes) {
                if(cb.isSelected()) {
                    genres.add(Genre.valueOf(cb.getText()));
                }
            }

            String quantity = quantityField.getText();

            double priceValue;
            try {
                priceValue = Double.parseDouble(price);
            } catch (NumberFormatException ex) {
                status.setText("Price must be a number");
                return;
            }

            int quantityValue;
            try {
                quantityValue = Integer.parseInt(quantity);
            } catch (NumberFormatException ex) {
                status.setText("Quantity must be a number");
                return;
            }

            try {
                booksController.updateBook(book, title, author, isbn13, priceValue, description, isPaperback, genres, quantityValue);
            }
            catch (IllegalArgumentException ex) {
                status.setText(ex.getMessage());
                return;
            }
            catch (IOException ex) {
                status.setText("Failed to save book: " + ex.getMessage());
                return;
            }
            status.setText("Book modified successfully");
        });

        setScene(scene);
        vbox.getChildren().addAll(grid, addBook, status);

    }
}