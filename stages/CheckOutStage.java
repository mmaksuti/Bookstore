package stages;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import controllers.BooksController;
import controllers.LoginController;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Bill;
import main.Book;
import main.UnauthenticatedException;

public class CheckOutStage extends Stage {
    public CheckOutStage(ArrayList <Book> booksToSell) {
        setTitle("Check out");
        
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.setSpacing(10);

        ColumnConstraints c1 = new ColumnConstraints(190);
        ColumnConstraints c2 = new ColumnConstraints(190);

        SimpleStringProperty totalString = new SimpleStringProperty("");
        AtomicReference <Double> totalPrice = new AtomicReference <>(0.0);
        
        Map<Book, Integer> quantities = new HashMap<Book, Integer>();
        
        for (Book book : booksToSell) {      
            quantities.put(book, 0);

            GridPane grid = new GridPane();
            grid.getColumnConstraints().addAll(c1, c2);
            
            Label bookTitleLabel = new Label(book.getTitle());
            bookTitleLabel.setStyle("-fx-font-family: \"Arial\"; -fx-font-size: 15px; -fx-font-weight: bold;");
            grid.add(bookTitleLabel, 0, 0);
            
            Label quantityLabel = new Label("Quantity:");
            grid.add(quantityLabel, 0, 1);

            TextField quantityField = new TextField();
            grid.add(quantityField, 1, 1);

            quantityField.setOnKeyTyped(e -> {
                if (!quantityField.getText().matches("[0-9]*")) {
                    quantityField.setText(quantityField.getText().replaceAll("[^0-9]", ""));
                }

                int quantity;
                
                try {
                    quantity = Integer.parseInt(quantityField.getText());
                }
                catch (NumberFormatException ex) {
                    quantity = 0;
                }
                
                if (quantity > book.getQuantity()) {
                    quantityField.setText(String.valueOf(book.getQuantity()));
                    quantity = book.getQuantity();
                }

                double price = book.getPrice();
                totalPrice.set(totalPrice.get() - price * quantities.get(book) + price * quantity);

                DecimalFormat df = new DecimalFormat("#.###");
                df.setRoundingMode(RoundingMode.CEILING);
                double tp = Double.parseDouble(df.format(totalPrice.get()));

                quantities.put(book, quantity);

                totalString.set("Total: " + tp + " lek");
            });
            vBox.getChildren().add(grid);
        }

        Label totalLabel = new Label("");
        totalLabel.textProperty().bind(totalString);
        vBox.getChildren().add(totalLabel);

        Button checkOutButton = new Button("Check out");
        vBox.getChildren().add(checkOutButton);
        checkOutButton.setOnAction(e -> {
            if (totalPrice.get() == 0) {
                return;
            }

            for (Book book : booksToSell) {
                int quantity = quantities.get(book);

                book.setQuantity(book.getQuantity() - quantity);
                BooksController.updateBook(book);
            }

            Bill bill;
            try {
                bill = new Bill(LoginController.getLoggedUsername(), quantities, totalPrice.get());
                System.out.print("Bill: \n" + bill);
            }
            catch (UnauthenticatedException exc) {
                // should never happen
                close();
                return;
            }

            quantities.clear();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Bill");
            alert.setHeaderText("Your bill");
            alert.setContentText(bill.toString());
            alert.showAndWait();

            bill.writeToFile();

            close();
        });

        Scene scene = new Scene(vBox, 400, booksToSell.size() * 65 + 50);
        setScene(scene);
    }
}