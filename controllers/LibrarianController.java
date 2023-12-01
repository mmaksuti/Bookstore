package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import main.AccessLevel;
import main.Librarian;
import main.User;

public class LibrarianController {
    public ObservableList <Librarian> librarians;

    public LibrarianController(LoginController loginController, BillController billController) {
        librarians = FXCollections.observableArrayList();
        for (User user : loginController.users) {
            if (user.getAccessLevel() == AccessLevel.LIBRARIAN) {
                librarians.add(new Librarian(user, billController));
            }
        }

        loginController.users.addListener((ListChangeListener<User>)change -> {
            librarians.removeAll(librarians);
            for (User user : loginController.users) {
                if (user.getAccessLevel() == AccessLevel.LIBRARIAN) {
                    librarians.add(new Librarian(user, billController));
                }
            }
        });
    }
}
