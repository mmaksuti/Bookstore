package controllers;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.Author;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AuthorsController {
    public static final String DATABASE = "authorsDatabase.dat";

    public static ObservableList <Author> authors;

    static {
        authors = FXCollections.observableArrayList();
        readFromFile(DATABASE);
    }

    public static void updateAuthor(Author author) {
        int index = authors.indexOf(author);
        authors.set(index, author);
        writeToFile(DATABASE);
    }

    public static boolean authorExists(String firstName, String lastName) {
        for (Author author : authors) {
            if (author.getFirstName().equals(firstName) && author.getLastName().equals(lastName)) {
                return true;
            }
        }
        return false;
    }

    public static void addAuthor(Author author) {
        authors.add(author);
        writeToFile(DATABASE);
    }

    public static void removeAuthor(Author author) {
        authors.remove(author);
        writeToFile(DATABASE);
    }

    private static void readFromFile(String file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<Author> arrayList = (ArrayList<Author>) ois.readObject();
            authors = FXCollections.observableArrayList(arrayList);
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

    private static void writeToFile(String file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            ArrayList<Author> arrayList = new ArrayList<Author>(authors);
            oos.writeObject(arrayList);
            oos.close();
        }
        catch (IOException e) {
            System.out.println("IOException");
        }
    }
}
