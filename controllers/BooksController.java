package controllers;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.Book;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BooksController {
    public static final String DATABASE = "booksDatabase.dat";

    public static ObservableList <Book> books;

    public static boolean bookExists(String isbn13) {
        for (Book book : books) {
            if (book.getIsbn13().equals(isbn13)) {
                return true;
            }
        }
        return false;
    }

    static {
        books = FXCollections.observableArrayList();
        readFromFile(DATABASE);
    }

    public static void updateBook(Book book) {
        int index = books.indexOf(book);
        books.set(index, book);
        writeToFile(DATABASE);
    }

    public static void addBook(Book book) {
        books.add(book);
        writeToFile(DATABASE);
    }

    public static void removeBook(Book book) {
        books.remove(book);
        writeToFile(DATABASE);
    }

    public static void readFromFile(String file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<Book> arrayList = (ArrayList<Book>) ois.readObject();
            books = FXCollections.observableArrayList(arrayList);
            ois.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("No database saved");
        }
        catch (IOException e) {
            System.out.println("IOException " + e.getMessage());
        }
        catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException");
        }
    }

    public static void writeToFile(String file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            ArrayList<Book> arrayList = new ArrayList<Book>(books);
            oos.writeObject(arrayList);
            oos.close();
        }
        catch (IOException e) {
            System.out.println("IOException");
        }
    }
}
