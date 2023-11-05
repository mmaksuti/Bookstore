package controllers;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.AccessLevel;
import main.LastAdministratorException;
import main.Librarian;
import main.UnauthenticatedException;
import main.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

public class LoginController {
    private static final String DATABASE = "usersDatabase.dat";
    private static final String SESSION = "login.txt";
    public static ObservableList <User> users;
    
    private static boolean authenticated = false;
    private static User currentUser;
    
    private static final SimpleStringProperty welcomeMessage = new SimpleStringProperty();
    
    public static boolean userExists(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
    
    public static String getLoggedUsername() throws UnauthenticatedException {
        if (authenticated) {
            return currentUser.getUsername();
        }
        throw new UnauthenticatedException();
    }
    
    public static SimpleStringProperty getWelcomeMessage() throws UnauthenticatedException {
        if (authenticated) {
            updateCurrentUser();
            
            welcomeMessage.set("Welcome " + currentUser.getFirstName() + " " + currentUser.getLastName() + "\nLogged in as " + LoginController.getLoggedAccessLevel());
            return welcomeMessage;
        }
        throw new UnauthenticatedException();
    }
    
    public static AccessLevel getLoggedAccessLevel() throws UnauthenticatedException {
        if (authenticated) {
            return currentUser.getAccessLevel();
        }
        throw new UnauthenticatedException();
    }

    private static void updateCurrentUser() {
        for (User user : users) {
            if (user.getUsername().equals(currentUser.getUsername())) {
                currentUser = user;
            }
        }
    }
    
    public static boolean login(String username, String password) {
        if (authenticated) {
            System.out.println("Already logged in");
            return true;
        }
        
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                currentUser = user;
                authenticated = true;
                //System.out.println("Successfully logged in as " + username + " with access level " + user.getAccessLevel());
            }
        }
        return authenticated;
    }
    
    public static void logout() {
        if (!authenticated) {
            // should never happen
            System.out.println("Already logged out");
            return;
        }

        File file = new File(SESSION);
        if (file.delete()) {
            System.out.println("File deleted successfully.");
        } else {
            System.err.println("File deletion failed.");
        }
        System.exit(0);
    }
    
    public static boolean loginWithSavedSession() {
        if (authenticated) {
            System.out.println("Already logged in");
            return true;
        }
        
        File file = new File(SESSION);
        String username;
        String password;
        try {
            Scanner scanner = new Scanner(file);
            if (!scanner.hasNextLine()) {
                boolean deleted = file.delete();
                scanner.close();
                return deleted;
            }

            username = scanner.nextLine();
            if (!scanner.hasNextLine()) {
                boolean deleted = file.delete();
                scanner.close();
                return deleted;
            }


            password = scanner.nextLine();
            scanner.close();
            

            
            login(username, password);
            return authenticated;
        }
        catch (FileNotFoundException e) {
            System.out.println("No login saved");
            return false;
        }
    }
    
    static {
        users = FXCollections.observableArrayList();
        readFromFile();
    }
    
    public static void updateUser(User user) {
        int index = users.indexOf(user);
        users.set(index, user);
        writeToFile();
    }
    
    
    public static void addUser(User user) {
        users.add(user);
        writeToFile();
    }
    
    private static int adminCount() {
        int nAdmins = 0;
        for (User u : users) {
            if (u.getAccessLevel() == AccessLevel.ADMINISTRATOR) {
                nAdmins++;
            }
        }
        return nAdmins;
    }

    public static void canDemote(User user, AccessLevel role) throws LastAdministratorException {
        if (user.getAccessLevel() == AccessLevel.ADMINISTRATOR && role != AccessLevel.ADMINISTRATOR) {
            int nAdmins = adminCount();
            
            if (nAdmins == 1) {
                throw new LastAdministratorException();
            }
        }
    }
    
    public static void removeUser(User user) throws LastAdministratorException {
        if (user.getAccessLevel() == AccessLevel.LIBRARIAN) {
            Librarian lib = new Librarian(user);
            lib.deleteBills();
        }
        else if (user.getAccessLevel() == AccessLevel.ADMINISTRATOR) {
            int nAdmins = adminCount();
            
            if (nAdmins == 1) {
                throw new LastAdministratorException();
            }
        }
        
        users.remove(user);
        writeToFile();
    }
    
    private static void readFromFile() {
        try {
            FileInputStream fis = new FileInputStream(LoginController.DATABASE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<User> arrayList = (ArrayList<User>) ois.readObject();
            users = FXCollections.observableArrayList(arrayList);
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
    
    private static void writeToFile() {
        try {
            FileOutputStream fos = new FileOutputStream(LoginController.DATABASE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            ArrayList<User> arrayList = new ArrayList<>(users);
            oos.writeObject(arrayList);
            oos.close();
        }
        catch (IOException e) {
            System.out.println("IOException");
        }
    }
    
    public static void saveSession(String username, String password) {
        try {
            File file = new File(SESSION);
            PrintWriter writer = new PrintWriter(file);
            writer.println(username);
            writer.println(password);
            writer.close();
        }
        catch (IOException e) {
            System.out.println("IOException");
        }
    }
}
