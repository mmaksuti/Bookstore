package controllers;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.Author;
import main.Book;
import main.Gender;
import main.UserConfirmation;

public class AuthorsController {
    public String DATABASE = "authorsDatabase.dat";

    public ObservableList <Author> authors;

    public DatabaseController dbController;

    public AuthorsController(DatabaseController dbController) throws IOException {
        this.dbController = dbController;
        readFromFile(DATABASE);
    }

    public AuthorsController(DatabaseController dbController, String database) throws IOException {
        this.dbController = dbController;
        DATABASE = database;
        readFromFile(DATABASE);
    }

    public ObservableList<Author> getAuthors() {
        return authors;
    }

    public void updateAuthor(Author author, String firstName, String lastName, Gender gender, BooksController booksController) throws IOException {
        if (firstName.isBlank() || lastName.isBlank() || gender == null) {
            throw new IllegalArgumentException("Please fill in all fields");
        }

        String oldFirstName = author.getFirstName();
        String oldLastName = author.getLastName();
        boolean nameChanged = !firstName.equals(oldFirstName) || !lastName.equals(oldLastName);
        if (nameChanged && authorExists(firstName, lastName)) {
            throw new IllegalArgumentException("Author already exists");
        }

        author.setFirstName(firstName);
        author.setLastName(lastName);
        author.setGender(gender);

        int index = authors.indexOf(author);
        authors.set(index, author);
        writeToFile(DATABASE);

        if (nameChanged) {
            ObservableList<Book> books = booksController.getBooks();

            if (!books.isEmpty()) {
                booksController.updateBook(books.get(0)); // trigger update on the books ObservableList
            }

            for (Book book : books) {
                if (book.getAuthor().getFirstName().equals(oldFirstName) && book.getAuthor().getLastName().equals(oldLastName)) {
                    // if the author name wasn't updated (if book has a different author reference), update it
                    book.setAuthor(author);
                    booksController.updateBook(book);
                }
            }
        }
    }

    public boolean authorExists(String firstName, String lastName) {
        for (Author author : authors) {
            if (author.getFirstName().equals(firstName) && author.getLastName().equals(lastName)) {
                return true;
            }
        }
        return false;
    }

    public void addAuthor(String firstName, String lastName, Gender gender) throws IOException {
        if (firstName.isBlank() || lastName.isBlank() || gender == null) {
            throw new IllegalArgumentException("Please fill in all fields");
        }

        if (authorExists(firstName, lastName)) {
            throw new IllegalArgumentException("Author already exists");
        }

        Author newAuthor = new Author(firstName, lastName, gender);
        authors.add(newAuthor);
        writeToFile(DATABASE);
    }

    public void removeAuthor(BooksController booksController, Author author, UserConfirmation confirmation) throws IOException {
        if (author == null) {
            return;
        }

        boolean removeAll = false;
        boolean firstTime = true;

        ObservableList<Book> books = booksController.getBooks();
        Iterator<Book> iter = books.iterator();
        while (iter.hasNext()) {
            Book book = iter.next();

            if (book.getAuthor().getFirstName().equals(author.getFirstName()) && book.getAuthor().getLastName().equals(author.getLastName())) {
                if (firstTime) {
                    removeAll = confirmation.confirm("Author has books", "Are you sure you want to delete them all?");
                    if (removeAll) {
                        iter.remove();
                    }
                    firstTime = false;
                }
                else if (removeAll) {
                    iter.remove();
                }
            }
        }

        if (firstTime || removeAll) {
            authors.remove(author);
            writeToFile(DATABASE);
        }
    }

    private void readFromFile(String file) throws IOException, IllegalStateException {
        try {
            ArrayList<Author> arrayList = (ArrayList<Author>)dbController.readFromFile(file);
            authors = FXCollections.observableArrayList(arrayList);
        }
        catch (FileNotFoundException e) {
            System.out.println("No database saved");
            authors = FXCollections.observableArrayList();
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

    private void writeToFile(String file) throws IOException {
        dbController.writeToFile(file, new ArrayList<Author>(authors));
    }
}
