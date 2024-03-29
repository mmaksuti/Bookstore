package src.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import src.enums.AccessLevel;
import src.models.Librarian;
import src.models.User;

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
