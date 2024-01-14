package src.controllers;
import java.time.LocalDate;
import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import src.enums.*;
import src.exceptions.LastAdministratorException;
import src.exceptions.UnauthenticatedException;
import src.models.Librarian;
import src.models.User;
import src.services.FileHandlingService;

import java.io.FileNotFoundException;
import java.io.IOException;

public class LoginController {
    private String DATABASE = "usersDatabase.dat";
    private String SESSION = "login.txt";
    private ObservableList <User> users;
    
    private boolean authenticated = false;
    private User currentUser;
    
    private final SimpleStringProperty welcomeMessage = new SimpleStringProperty();
    
    private BillController billController;
    private FileHandlingService fileHandlingService;

    private boolean validEmail(String email) {
        return email.matches("\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+");
    }

    private boolean validPhoneNumber(String phone) {
        String nospaces = phone.replaceAll(" ", "");
        return nospaces.matches("\\+3556[7-9]\\d{7}");
    }

    private boolean validUsername(String username) {
        return username.matches("[a-zA-Z0-9_]+");
    }

    public LoginController(FileHandlingService fileHandlingService, BillController billController) throws IOException {
        this.fileHandlingService = fileHandlingService;
        this.billController = billController;
        readFromFile(DATABASE);
    }

    public LoginController(FileHandlingService fileHandlingService, BillController billController, String database, String sessionFile) throws IOException {
        this.fileHandlingService = fileHandlingService;
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
            }
        }
        return authenticated;
    }
    
    public boolean logout() {
        if (!authenticated) {
            // should never happen
            System.out.println("Already logged out");
            return true;
        }

        if (fileHandlingService.deleteFile(SESSION)) {
            System.out.println("Session deleted successfully");
        } else {
            System.err.println("Failed to delete session");
            return false;
        }

        authenticated = false;
        currentUser = null;
        return true;
    }
    
    public boolean loginWithSavedSession() {
        if (authenticated) {
            System.out.println("Already logged in");
            return true;
        }

        String username;
        String password;
        try {
            String session = fileHandlingService.readFileContents(SESSION);
            String[] lines = session.split("\n");
            if (lines.length != 2) {
                boolean deleted = fileHandlingService.deleteFile(SESSION);
                if (!deleted) {
                    System.err.println("Failed to delete corrupted session");
                }
                return false;
            }

            username = lines[0];
            password = lines[1];

            login(username, password);
            return authenticated;
        }
        catch (FileNotFoundException e) {
            System.out.println("No login saved");
            return false;
        }
        catch (IOException e) {
            System.err.println("Failed to read session");
            return false;
        }
    }
    
    public void updateUser(User user, String firstName, String lastName, String username, String password, String email, String phone, int salary, LocalDate birthday, AccessLevel role) throws IOException, UnauthenticatedException {
        if (firstName.isBlank() || lastName.isBlank() || username.isBlank() || password.isBlank() || email.isBlank() || phone.isBlank() || birthday == null || role == null) {
            throw new IllegalArgumentException("Please fill in all fields");
        }

        if (salary <= 0) {
            throw new IllegalArgumentException("Salary must be positive");
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
            throw new IllegalArgumentException("Cannot demote last administrator");
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
        if (firstName.isBlank() || lastName.isBlank() || username.isBlank() || password.isBlank() || email.isBlank() || phone.isBlank() || birthday == null || role == null) {
            throw new IllegalArgumentException("Please fill in all fields");
        }

        if (salary <= 0) {
            throw new IllegalArgumentException("Salary must be positive");
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
    
    public void removeUser(User user) throws LastAdministratorException, IOException {
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
    
    public void readFromFile(String file) throws IOException, IllegalStateException {
        try {
            ArrayList<User> arrayList = (ArrayList<User>)fileHandlingService.readObjectFromFile(file);
            users = FXCollections.observableArrayList(arrayList);
        }
        catch (FileNotFoundException e) {
            System.out.println("No database saved");
            users = FXCollections.observableArrayList();

            // default admin
            addUser("Administrator", "Administrator", "admin", "admin", "admin@gmail.com", "+355671234567", 1000, LocalDate.of(1999, 1, 1), AccessLevel.ADMINISTRATOR);
        }
        catch (Exception e) {
            System.out.println("Invalid database");
            boolean deleted = fileHandlingService.deleteFile(file);
            if (!deleted) {
                throw new IllegalStateException("Failed to delete corrupted database");
            }
            users = FXCollections.observableArrayList();

            // default admin
            addUser("Administrator", "Administrator", "admin", "admin", "admin@gmail.com", "+355671234567", 1000, LocalDate.of(1999, 1, 1), AccessLevel.ADMINISTRATOR);
        }
    }

    public void writeToFile(String file) throws IOException {
        fileHandlingService.writeObjectToFile(file, new ArrayList<User>(users));
    }
    
    public void saveSession(String username, String password) throws IOException {
        String session = username + "\n" + password + "\n";
        fileHandlingService.writeFileContents(SESSION, session);
    }
}
