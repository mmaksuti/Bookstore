package controllers;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.*;
import exceptions.LastAdministratorException;
import exceptions.UnauthenticatedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class LoginController {
    private String DATABASE = "usersDatabase.dat";
    private String SESSION = "login.txt";
    private ObservableList <User> users;
    
    private boolean authenticated = false;
    private User currentUser;
    
    private final SimpleStringProperty welcomeMessage = new SimpleStringProperty();
    
    private BillController billController;
    private DatabaseController dbController;

    private static boolean validEmail(String email) {
        return email.matches("\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+");
    }

    private static boolean validPhoneNumber(String phone) {
        String nospaces = phone.replaceAll(" ", "");
        return nospaces.matches("\\+3556[7-9]\\d{7}");
    }

    private static boolean validUsername(String username) {
        return username.matches("[a-zA-Z0-9_]+");
    }

    public LoginController(DatabaseController dbController, BillController billController) throws IOException {
        this.dbController = dbController;
        this.billController = billController;
        readFromFile(DATABASE);
    }

    public LoginController(DatabaseController dbController, BillController billController, String database, String sessionFile) throws IOException {
        this.dbController = dbController;
        this.billController = billController;

        DATABASE = database;
        SESSION = sessionFile;
        readFromFile(DATABASE);
    }

    public ObservableList<User> getUsers() {
        return users;
    }

    public boolean userExists(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
    
    public String getLoggedUsername() throws UnauthenticatedException {
        if (authenticated) {
            return currentUser.getUsername();
        }
        throw new UnauthenticatedException();
    }
    
    public SimpleStringProperty getWelcomeMessage() throws UnauthenticatedException {
        if (authenticated) {
            updateCurrentUser();
            
            welcomeMessage.set("Welcome " + currentUser.getFirstName() + " " + currentUser.getLastName() + "\nLogged in as " + getLoggedAccessLevel());
            return welcomeMessage;
        }
        throw new UnauthenticatedException();
    }
    
    public AccessLevel getLoggedAccessLevel() throws UnauthenticatedException {
        if (authenticated) {
            return currentUser.getAccessLevel();
        }
        throw new UnauthenticatedException();
    }

    private void updateCurrentUser() {
        for (User user : users) {
            if (user.getUsername().equals(currentUser.getUsername())) {
                currentUser = user;
            }
        }
    }
    
    public boolean login(String username, String password) {
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
    
    public void logout() {
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
    
    public boolean loginWithSavedSession() {
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
                if (!deleted) {
                    System.err.println("File deletion failed.");
                }
                scanner.close();
                return false;
            }

            username = scanner.nextLine();
            if (!scanner.hasNextLine()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    System.err.println("File deletion failed.");
                }
                scanner.close();
                return false;
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
    
    public void updateUser(User user, String firstName, String lastName, String username, String password, String email, String phone, int salary, LocalDate birthday, AccessLevel role) throws IOException, UnauthenticatedException {
        if (firstName.isBlank() || lastName.isBlank() || username.isBlank() || password.isBlank() || email.isBlank() || phone.isBlank() || salary == 0 || birthday == null || role == null) {
            throw new IllegalArgumentException("Please fill in all fields");
        }

        if (!validUsername(username)) {
            throw new IllegalArgumentException("Username must contain only letters, numbers and underscores");
        }

        if (password.length() < 5) {
            throw new IllegalArgumentException("Password must be at least 5 characters");
        }

        if (!validEmail(email)) {
            throw new IllegalArgumentException("Invalid email");
        }

        if (!validPhoneNumber(phone)) {
            throw new IllegalArgumentException("Invalid phone number");
        }

        if (birthday.isAfter(LocalDate.now().minusYears(18))) {
            throw new IllegalArgumentException("User must be at least 18 years old");
        }

        String oldUsername = user.getUsername();
        boolean usernameChanged = !username.equals(oldUsername);
        if (usernameChanged && userExists(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        try {
            assertCanDemote(user, role);
        }
        catch (LastAdministratorException exc) {
            throw new IllegalArgumentException("Cannot demote last administator");
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhone(phone);
        user.setSalary(salary);
        user.setBirthday(birthday);
        user.setAccessLevel(role);

        getWelcomeMessage();

        int index = users.indexOf(user);
        users.set(index, user);
        writeToFile(DATABASE);
    }
    
    
    public void addUser(String firstName, String lastName, String username, String password, String email, String phone, int salary, LocalDate birthday, AccessLevel role) throws IOException {
        if (firstName.isBlank() || lastName.isBlank() || username.isBlank() || password.isBlank() || email.isBlank() || phone.isBlank() || salary == 0 || birthday == null || role == null) {
            throw new IllegalArgumentException("Please fill in all fields");
        }

        if (!validUsername(username)) {
            throw new IllegalArgumentException("Username must contain only letters, numbers and underscores");
        }

        if (userExists(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (password.length() < 5) {
            throw new IllegalArgumentException("Password must be at least 5 characters");
        }

        if (!validEmail(email)) {
            throw new IllegalArgumentException("Invalid email");
        }

        if (!validPhoneNumber(phone)) {
            throw new IllegalArgumentException("Invalid phone number");
        }

        if (birthday.isAfter(LocalDate.now().minusYears(18))) {
            throw new IllegalArgumentException("User must be at least 18 years old");
        }

        User user = new User(firstName, lastName, username, password, email, phone, salary, birthday, role);
        users.add(user);
        writeToFile(DATABASE);
    }
    
    private int adminCount() {
        int nAdmins = 0;
        for (User u : users) {
            if (u.getAccessLevel() == AccessLevel.ADMINISTRATOR) {
                nAdmins++;
            }
        }
        return nAdmins;
    }

    public void assertCanDemote(User user, AccessLevel role) throws LastAdministratorException {
        if (user.getAccessLevel() == AccessLevel.ADMINISTRATOR && role != AccessLevel.ADMINISTRATOR) {
            int nAdmins = adminCount();
            
            if (nAdmins == 1) {
                throw new LastAdministratorException();
            }
        }
    }
    
    public void removeUser(User user) throws LastAdministratorException, IOException, IllegalStateException {
        if (user.getAccessLevel() == AccessLevel.LIBRARIAN) {
            Librarian lib = new Librarian(user, billController);
            billController.deleteBills(lib);
        }
        else if (user.getAccessLevel() == AccessLevel.ADMINISTRATOR) {
            int nAdmins = adminCount();
            
            if (nAdmins == 1) {
                throw new LastAdministratorException();
            }
        }
        
        users.remove(user);
        writeToFile(DATABASE);
    }
    
    private void readFromFile(String file) throws IOException, IllegalStateException {
        try {
            ArrayList<User> arrayList = (ArrayList<User>)dbController.readFromFile(file);
            users = FXCollections.observableArrayList(arrayList);
        }
        catch (FileNotFoundException e) {
            System.out.println("No database saved");
            users = FXCollections.observableArrayList();
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
        dbController.writeToFile(file, new ArrayList<User>(users));
    }
    
    public void saveSession(String username, String password) throws IOException {
        File file = new File(SESSION);
        PrintWriter writer = new PrintWriter(file);
        writer.println(username);
        writer.println(password);
        writer.close();
    }
}
