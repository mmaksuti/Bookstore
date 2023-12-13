package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import main.AccessLevel;
import main.Librarian;
import main.User;

public class LibrarianController {
    private ObservableList <Librarian> librarians;

    public LibrarianController(LoginController loginController, BillController billController) {
        librarians = FXCollections.observableArrayList();
        for (User user : loginController.getUsers()) {
            if (user.getAccessLevel() == AccessLevel.LIBRARIAN) {
                librarians.add(new Librarian(user, billController));
            }
        }

        loginController.getUsers().addListener((ListChangeListener<User>)change -> {
            librarians.removeAll(librarians);
            for (User user : loginController.getUsers()) {
                if (user.getAccessLevel() == AccessLevel.LIBRARIAN) {
                    librarians.add(new Librarian(user, billController));
                }
            }
        });
    }
    
    public ObservableList<Librarian> getLibrarians() {
        return librarians;
    }
}
