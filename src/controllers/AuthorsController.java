package src.controllers;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import src.models.Author;
import src.models.Book;
import src.enums.Gender;
import src.interfaces.UserConfirmation;
import src.services.FileHandlingService;

public class AuthorsController {
    public String DATABASE = "authorsDatabase.dat";
    private ObservableList <Author> authors;
    private FileHandlingService fileHandlingService;

    public AuthorsController(FileHandlingService fileHandlingService) throws IOException {
        this.fileHandlingService = fileHandlingService;
        readFromFile(DATABASE);
    }

    public AuthorsController(FileHandlingService fileHandlingService, String database) throws IOException {
        this.fileHandlingService = fileHandlingService;
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

        ObservableList<Book> books = booksController.getBooks();

        if (nameChanged && books != null && !books.isEmpty()) {
            booksController.getBooks().set(0, books.get(0)); // trigger update on the books ObservableList

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

        if (removeAll) {
            booksController.writeToFile(booksController.getDATABASE());
        }
    }

    public void readFromFile(String file) throws IOException, IllegalStateException {
        try {
            ArrayList<Author> arrayList = (ArrayList<Author>)fileHandlingService.readObjectFromFile(file);
            authors = FXCollections.observableArrayList(arrayList);
        }
        catch (FileNotFoundException e) {
            System.out.println("No database saved");
            authors = FXCollections.observableArrayList();
        }
        catch (Exception e) {
            System.out.println("Invalid database");
            boolean deleted = fileHandlingService.deleteFile(file);
            if (!deleted) {
                throw new IllegalStateException("Failed to delete corrupted database");
            }
            authors = FXCollections.observableArrayList();
        }
    }

    public void writeToFile(String file) throws IOException {
        fileHandlingService.writeObjectToFile(file, new ArrayList<Author>(authors));
    }
}
