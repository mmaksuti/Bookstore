package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import main.AccessLevel;
import main.Librarian;
import main.User;

public class LibrarianController {
    public static ObservableList <Librarian> librarians;

    static {
        librarians = FXCollections.observableArrayList();
        for (User user : LoginController.users) {
            if (user.getAccessLevel() == AccessLevel.LIBRARIAN) {
                librarians.add(new Librarian(user));
            }
        }

        LoginController.users.addListener((ListChangeListener<User>)change -> {
            librarians.removeAll(librarians);
            for (User user : LoginController.users) {
                if (user.getAccessLevel() == AccessLevel.LIBRARIAN) {
                    librarians.add(new Librarian(user));
                }
            }
        });
    }
}
