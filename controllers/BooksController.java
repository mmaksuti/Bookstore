package controllers;
import java.io.*;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.Author;
import main.Book;
import main.Genre;

public class BooksController {
    public String DATABASE = "booksDatabase.dat";

    public ObservableList <Book> books;

    public BooksController() throws IOException {
        readFromFile(DATABASE);
    }

    public void setDatabase(String database) {
        DATABASE = database;
    }

    public boolean bookExists(String isbn13) {
        for (Book book : books) {
            if (book.getIsbn13().equals(isbn13)) {
                return true;
            }
        }
        return false;
    }

    public void updateBook(Book book) throws IOException {
        int index = books.indexOf(book);
        books.set(index, book);
        writeToFile(DATABASE);
    }

    private static boolean validIsbn13(String isbn13) {
        return isbn13.matches("[0-9]{3}-[0-9]-[0-9]{3}-[0-9]{5}-[0-9]") || isbn13.matches("[0-9]{3}-[0-9]{10}");
    }

    public void updateBook(Book book, String title, Author author, String isbn13, double price, String description, boolean isPaperback, ArrayList<Genre> genres, int quantity) throws IOException {
        if (title.isBlank() || author == null || isbn13.isBlank() || description.isBlank()) {
            throw new IllegalArgumentException("Please fill in all fields");
        }

        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        if (!validIsbn13(isbn13)) {
            throw new IllegalArgumentException("Invalid ISBN13");
        }

        String oldIsbn13 = book.getIsbn13();
        boolean isbn13Changed = !isbn13.equals(oldIsbn13);
        if (isbn13Changed && bookExists(isbn13)) {
            throw new IllegalArgumentException("A book with the same ISBN13 already exists");
        }

        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn13(isbn13);
        book.setPrice(price);
        book.setDescription(description);
        book.setPaperback(isPaperback);
        book.setGenres(genres);
        book.setQuantity(quantity);

        int index = books.indexOf(book);
        books.set(index, book);
        writeToFile(DATABASE);
    }

    public void addBook(String title, Author author, String isbn13, double price, String description, boolean isPaperback, ArrayList<Genre> genres, int quantity) throws IOException {
        if (title.isBlank() || author == null || isbn13.isBlank() || price == 0 || description.isBlank()) {
            throw new IllegalArgumentException("Please fill in all fields");
        }

        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        if (bookExists(isbn13)) {
            throw new IllegalArgumentException("A book with the same ISBN13 already exists");
        }

        if (!validIsbn13(isbn13)) {
            throw new IllegalArgumentException("Invalid ISBN13");
        }

        Book newBook = new Book(isbn13, title, description, price, author, genres, quantity, isPaperback);
        books.add(newBook);
        writeToFile(DATABASE);
    }

    public void removeBook(Book book) throws IOException {
        books.remove(book);
        writeToFile(DATABASE);
    }

    public void readFromFile(String file) throws IOException, IllegalStateException {
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<Book> arrayList = (ArrayList<Book>) ois.readObject();
            books = FXCollections.observableArrayList(arrayList);
            ois.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("No database saved");
            books = FXCollections.observableArrayList();
        }
        catch (IOException e) {
            System.out.println("IOException " + e.getMessage());
        }
        catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException");
            File fob = new File(file);
            boolean deleted = fob.delete();
            if (!deleted) {
                throw new IllegalStateException("Failed to delete corrupted database");
            }
        }
    }

    public void writeToFile(String file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        ArrayList<Book> arrayList = new ArrayList<>(books);
        oos.writeObject(arrayList);
        oos.close();
    }
}
