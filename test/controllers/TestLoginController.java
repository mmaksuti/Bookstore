package test.controllers;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

import controllers.BillController;
import controllers.LoginController;

import models.Author;
import models.Book;
import models.Librarian;
import models.User;
import services.FileHandlingService;
import exceptions.LastAdministratorException;
import exceptions.UnauthenticatedException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestLoginController {
    public LoginController loginController;

    public BillController mockBillController;
    public FileHandlingService mockFileHandlingService;

    public ObservableList<Book> books = FXCollections.observableArrayList();

    String firstName;
    String lastName;
    String username;
    String password;
    String email;
    String phone;
    int salary = 50000;
    LocalDate birthday = LocalDate.of(1990, 12, 22);
    AccessLevel role = AccessLevel.ADMINISTRATOR;

    String SESSION = "session";
    String DATABASE = "database";

    // set up the database files
    @BeforeEach
    public void setUp()  {
        try {
            mockFileHandlingService = mock(FileHandlingService.class);
            mockBillController = mock(BillController.class);
            when(mockFileHandlingService.readObjectFromFile(DATABASE)).thenReturn(new ArrayList<User>());

            loginController = new LoginController(mockFileHandlingService, mockBillController, DATABASE, SESSION);

            firstName = "John";
            lastName = "Doe";
            username = "johndoe";
            password = "password";
            email = "johndoe@gmail.com";
            phone = "+355681234567";
            salary = 50000;
            birthday = LocalDate.of(1990, 12, 22);
            role = AccessLevel.ADMINISTRATOR;
        }
        catch (IOException ex) {
            fail("Failed to set up databases: " + ex.getMessage());
        }
        catch (ClassNotFoundException ignored) {
        }
    }

    @Test
    void test_firstConstructor() {
        ArrayList<User> users = new ArrayList<>();
        try {
            when(mockFileHandlingService.readObjectFromFile(any(String.class))).thenReturn(users);
            loginController = new LoginController(mockFileHandlingService, mockBillController);

            // it was called once in the setup and once here
            verify(mockFileHandlingService, times(2)).readObjectFromFile(any(String.class));
        }
        catch (IOException|ClassNotFoundException ignored) {
        }

        assertEquals(users, loginController.getUsers());
    }

    @Test
    void test_addUser() {
        try {
            loginController.addUser(firstName, lastName, username, password, email, phone, salary, birthday, role);
            verify(mockFileHandlingService, times(1)).writeObjectToFile(any(String.class), eq(loginController.getUsers()));
        } catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        assertEquals(1, loginController.getUsers().size());

        User user = loginController.getUsers().get(0);
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(email, user.getEmail());
        assertEquals(phone, user.getPhone());
        assertEquals(salary, user.getSalary());
        assertEquals(birthday, user.getBirthday());
        assertEquals(role, user.getAccessLevel());

        firstName = "Jane";
        username = "janedoe";
        try {
            loginController.addUser(firstName, lastName, username, password, email, phone, salary, birthday, role);
            verify(mockFileHandlingService, times(1)).writeObjectToFile(any(String.class), eq(loginController.getUsers()));
        }
        catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        assertEquals(2, loginController.getUsers().size());

        user = loginController.getUsers().get(1);
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(email, user.getEmail());
        assertEquals(phone, user.getPhone());
        assertEquals(salary, user.getSalary());
        assertEquals(birthday, user.getBirthday());
        assertEquals(role, user.getAccessLevel());
    }

    @Test
    void test_addUserThrowsFillInAllValuesException() {
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser("", lastName, username, password, email, phone, salary, birthday, role));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, "", username, password, email, phone, salary, birthday, role));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, "", password, email, phone, salary, birthday, role));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, username, "", email, phone, salary, birthday, role));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, username, password, "", phone, salary, birthday, role));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, username, password, email, "", salary, birthday, role));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, username, password, email, phone, salary, null, role));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, username, password, email, phone, salary, birthday, null));
        assertEquals("Please fill in all fields", exc.getMessage());

        assertEquals(0, loginController.getUsers().size());
    }

    @Test
    void test_addUserThrowsUsernameExistsException() {
        try {
            loginController.addUser(firstName, lastName, username, password, email, phone, salary, birthday, role);
        } catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser("Jane", lastName, username, password, "janedoe@gmail.com", phone, salary, birthday, role));
        assertEquals("Username already exists", exc.getMessage());
        assertEquals(1, loginController.getUsers().size());
    }

    @Test
    void test_addUserInvalidUsername() {
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, "johndoe+", password, email, phone, salary, birthday, role));
        assertEquals("Username must contain only letters, numbers and underscores", exc.getMessage());
    }

    @Test
    void test_addUserInvalidPassword() {
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, username, "123", email, phone, salary, birthday, role));
        assertEquals("Password must be at least 5 characters", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, username, "1234", email, phone, salary, birthday, role));
        assertEquals("Password must be at least 5 characters", exc.getMessage());

        assertEquals(0, loginController.getUsers().size());

        try {
            loginController.addUser(firstName, lastName, username, "12345", email, phone, salary, birthday, role);
        }
        catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }
        assertEquals(1, loginController.getUsers().size());
    }

    @Test
    void test_addUserInvalidEmail() {
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, username, password, "johndoe", phone, salary, birthday, role));
        assertEquals("Invalid email", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, username, password, "johndoe@", phone, salary, birthday, role));
        assertEquals("Invalid email", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, username, password, "johndoe@gmail", phone, salary, birthday, role));
        assertEquals("Invalid email", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, username, password, "johndoe@gmail.", phone, salary, birthday, role));
        assertEquals("Invalid email", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, username, password, "johndoe@gmail.c", phone, salary, birthday, role));
        assertEquals("Invalid email", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, username, password, "@gmail.com", phone, salary, birthday, role));
        assertEquals("Invalid email", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, username, password, "gmail.com", phone, salary, birthday, role));
        assertEquals("Invalid email", exc.getMessage());

        assertEquals(0, loginController.getUsers().size());

        try {
            loginController.addUser(firstName, lastName, username, password, "johndoe@gmail.co", phone, salary, birthday, role);
        }
        catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }
        assertEquals(1, loginController.getUsers().size());
    }

    @Test
    void test_addUserInvalidPhoneNumber() {
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, username, password, email, "+355", salary, birthday, role));
        assertEquals("Invalid phone number", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, username, password, email, "+35567123456", salary, birthday, role));
        assertEquals("Invalid phone number", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, username, password, email, "671234567", salary, birthday, role));
        assertEquals("Invalid phone number", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, username, password, email, "355671234567", salary, birthday, role));
        assertEquals("Invalid phone number", exc.getMessage());

        assertEquals(0, loginController.getUsers().size());

        try {
            loginController.addUser(firstName, lastName, username, password, email, "+355681234567", salary, birthday, role);
        }
        catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }
        assertEquals(1, loginController.getUsers().size());
    }

    @Test
    void test_addUserInvalidAge() {
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, username, password, email, phone, salary, LocalDate.now().minusYears(17), role));
        assertEquals("User must be at least 18 years old", exc.getMessage());

        assertEquals(0, loginController.getUsers().size());

        try {
            loginController.addUser(firstName, lastName, username, password, email, phone, salary, LocalDate.now().minusYears(18), role);
        }
        catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        assertEquals(1, loginController.getUsers().size());
    }

    @Test
    void test_addUserInvalidSalary() {
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, username, password, email, phone, -1, birthday, role));
        assertEquals("Salary must be positive", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.addUser(firstName, lastName, username, password, email, phone, 0, birthday, role));
        assertEquals("Salary must be positive", exc.getMessage());

        assertEquals(0, loginController.getUsers().size());

        try {
            loginController.addUser(firstName, lastName, username, password, email, phone, 1, birthday, role);
        }
        catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        assertEquals(1, loginController.getUsers().size());
    }

    @Test
    void test_updateUser() {
        try {
            loginController.addUser(firstName, lastName, username, password, email, phone, salary, birthday, AccessLevel.ADMINISTRATOR);
        } catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        User user = loginController.getUsers().get(0);
        try {
            loginController.login(username, password);
            loginController.updateUser(user, "Jane", lastName, "janedoe", password, "janedoe@gmail.com", phone, salary, birthday, role);
            verify(mockFileHandlingService, times(2)).writeObjectToFile(any(String.class), eq(loginController.getUsers()));
        } catch (IOException e) {
            fail("Failed to update user: " + e.getMessage());
        }
        catch (UnauthenticatedException exc) {
            fail("Failed to update user, unauthenticated");
        }

        assertEquals(1, loginController.getUsers().size());

        assertEquals("Jane", user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals("janedoe", user.getUsername());
        assertEquals("janedoe@gmail.com", user.getEmail());
    }

    @Test
    void test_updateUserThrowsFillInAllValuesException() {
        try {
            loginController.addUser(firstName, lastName, username, password, email, phone, salary, birthday, role);
        } catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        User user = loginController.getUsers().get(0);

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, "", lastName, username, password, email, phone, salary, birthday, role));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, "", username, password, email, phone, salary, birthday, role));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, "", password, email, phone, salary, birthday, role));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, username, "", email, phone, salary, birthday, role));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, username, password, "", phone, salary, birthday, role));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, username, password, email, "", salary, birthday, role));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, username, password, email, phone, salary, null, role));
        assertEquals("Please fill in all fields", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, username, password, email, phone, salary, birthday, null));
        assertEquals("Please fill in all fields", exc.getMessage());

        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(email, user.getEmail());
        assertEquals(phone, user.getPhone());
        assertEquals(salary, user.getSalary());
        assertEquals(birthday, user.getBirthday());
        assertEquals(role, user.getAccessLevel());
    }

    @Test
    void test_updateUserThrowsUsernameExistsException() {
        try {
            loginController.addUser("John", "Doe", "johndoe", password, email, phone, salary, birthday, role);
        } catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        try {
            loginController.addUser("Jane", "Doe", "janedoe", password, email, phone, salary, birthday, role);
        } catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        User user = loginController.getUsers().get(1);

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, "Jane", "Doe", "johndoe", password, email, phone, salary, birthday, role));
        assertEquals("Username already exists", exc.getMessage());
    }

    @Test
    void test_updateUserInvalidUsername() {
        try {
            loginController.addUser(firstName, lastName, username, password, email, phone, salary, birthday, role);
        } catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        User user = loginController.getUsers().get(0);

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, "johndoe+", password, email, phone, salary, birthday, role));
        assertEquals("Username must contain only letters, numbers and underscores", exc.getMessage());
    }

    @Test
    void test_updateUserInvalidPassword() {
        try {
            loginController.addUser(firstName, lastName, username, password, email, phone, salary, birthday, role);
        }
        catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        User user = loginController.getUsers().get(0);

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, username, "123", email, phone, salary, birthday, role));
        assertEquals("Password must be at least 5 characters", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, username, "1234", email, phone, salary, birthday, role));
        assertEquals("Password must be at least 5 characters", exc.getMessage());

        assertEquals(password, user.getPassword());

        try {
            loginController.updateUser(user, firstName, lastName, username, "12345", email, phone, salary, birthday, role);
        }
        catch (IOException ex) {
            fail("Failed to update user: " + ex.getMessage());
        }
        catch (UnauthenticatedException ignored) {
        }

        assertEquals(1, loginController.getUsers().size());
    }

    @Test
    void test_updateUserInvalidEmail() {
        try {
            loginController.addUser(firstName, lastName, username, password, email, phone, salary, birthday, role);
        }
        catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        User user = loginController.getUsers().get(0);

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, username, password, "johndoe", phone, salary, birthday, role));
        assertEquals("Invalid email", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, username, password, "johndoe@", phone, salary, birthday, role));
        assertEquals("Invalid email", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, username, password, "johndoe@gmail", phone, salary, birthday, role));
        assertEquals("Invalid email", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, username, password, "johndoe@gmail.", phone, salary, birthday, role));
        assertEquals("Invalid email", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, username, password, "johndoe@gmail.c", phone, salary, birthday, role));
        assertEquals("Invalid email", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, username, password, "@gmail.com", phone, salary, birthday, role));
        assertEquals("Invalid email", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, username, password, "gmail.com", phone, salary, birthday, role));
        assertEquals("Invalid email", exc.getMessage());

        assertEquals(email, user.getEmail());

        try {
            loginController.updateUser(user, firstName, lastName, username, password, "johndoe@gmail.com", phone, salary, birthday, role);
        }
        catch (IOException ex) {
            fail("Failed to update user: " + ex.getMessage());
        }
        catch (UnauthenticatedException ignored) {
        }

        assertEquals(1, loginController.getUsers().size());
    }

    @Test
    void test_updateUserInvalidPhoneNumber() {
        try {
            loginController.addUser(firstName, lastName, username, password, email, phone, salary, birthday, role);
        }
        catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        User user = loginController.getUsers().get(0);

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, username, password, email, "+355", salary, birthday, role));
        assertEquals("Invalid phone number", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, username, password, email, "+35567123456", salary, birthday, role));
        assertEquals("Invalid phone number", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, username, password, email, "671234567", salary, birthday, role));
        assertEquals("Invalid phone number", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, username, password, email, "355671234567", salary, birthday, role));
        assertEquals("Invalid phone number", exc.getMessage());

        assertEquals(phone, user.getPhone());

        try {
            loginController.updateUser(user, firstName, lastName, username, password, email, "+355681234567", salary, birthday, role);
        }
        catch (IOException ex) {
            fail("Failed to update user: " + ex.getMessage());
        }
        catch (UnauthenticatedException ignored) {
        }

        assertEquals(1, loginController.getUsers().size());
    }

    @Test
    void test_updateUserInvalidAge() {
        try {
            loginController.addUser(firstName, lastName, username, password, email, phone, salary, birthday, role);
        }
        catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        User user = loginController.getUsers().get(0);

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, username, password, email, phone, salary, LocalDate.now().minusYears(17), role));
        assertEquals("User must be at least 18 years old", exc.getMessage());

        assertEquals(birthday, user.getBirthday());

        try {
            loginController.updateUser(user, firstName, lastName, username, password, email, phone, salary, LocalDate.now().minusYears(18), role);
        }
        catch (IOException ex) {
            fail("Failed to update user: " + ex.getMessage());
        }
        catch (UnauthenticatedException ignored) {
        }

        assertEquals(1, loginController.getUsers().size());
    }

    @Test
    void test_updateUserInvalidSalary() {
        try {
            loginController.addUser(firstName, lastName, username, password, email, phone, salary, birthday, role);
        }
        catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        User user = loginController.getUsers().get(0);

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, username, password, email, phone, -1, birthday, role));
        assertEquals("Salary must be positive", exc.getMessage());

        exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, firstName, lastName, username, password, email, phone, 0, birthday, role));
        assertEquals("Salary must be positive", exc.getMessage());

        assertEquals(salary, user.getSalary());

        try {
            loginController.updateUser(user, firstName, lastName, username, password, email, phone, 1, birthday, role);
        }
        catch (IOException ex) {
            fail("Failed to update user: " + ex.getMessage());
        }
        catch (UnauthenticatedException ignored) {
        }

        assertEquals(1, loginController.getUsers().size());
    }

    @Test
    void test_updateUserThrowsCannotDemoteLastAdministratorException() {
        try {
            loginController.addUser("John", "Doe", "johndoe", password, email, phone, salary, birthday, AccessLevel.LIBRARIAN);
            loginController.addUser("Jane", "Doe", "janedoe", password, email, phone, salary, birthday, AccessLevel.ADMINISTRATOR);
            loginController.login("janedoe", password);
        } catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        User user = loginController.getUsers().get(1);
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> loginController.updateUser(user, "Jane", "Doe", "janedoe", password, email, phone, salary, birthday, AccessLevel.LIBRARIAN));
        assertEquals("Cannot demote last administrator", exc.getMessage());

        try {
            loginController.updateUser(loginController.getUsers().get(0), "John", "Doe", "johndoe", password, email, phone, salary, birthday, AccessLevel.ADMINISTRATOR);
            loginController.updateUser(user, "Jane", "Doe", "janedoe", password, email, phone, salary, birthday, AccessLevel.LIBRARIAN);
        }
        catch (IOException ex) {
            fail("Failed to update user: " + ex.getMessage());
        }
        catch (UnauthenticatedException ignored) {
        }
    }

    @Test
    void test_login() {
        try {
            loginController.addUser(firstName, lastName, username, password, email, phone, salary, birthday, role);
        }
        catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        boolean authenticated = loginController.login(username, password);
        assertTrue(authenticated);

        // should always return true until logout() is called
        assertTrue(loginController.login("wrongusername", "wrongpassword"));

        try {
            assertEquals(username, loginController.getLoggedUsername());
        }
        catch (UnauthenticatedException exc) {
            fail("Unauthenticated exception thrown when user is authenticated");
        }
    }

    @Test
    void test_loginWrongPassword() {
        try {
            loginController.addUser(firstName, lastName, username, password, email, phone, salary, birthday, role);
        }
        catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        boolean authenticated = loginController.login(username, "wrongpassword");
        assertFalse(authenticated);

        assertThrows(UnauthenticatedException.class, () -> loginController.getLoggedUsername());
    }

    @Test
    void test_logout() {
        assertTrue(loginController.logout());

        try {
            loginController.addUser(firstName, lastName, username, password, email, phone, salary, birthday, role);
            assertTrue(loginController.login(username, password));
        }
        catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        when(mockFileHandlingService.deleteFile(any(String.class))).thenReturn(false);
        assertFalse(loginController.logout());

        try {
            assertEquals(username, loginController.getLoggedUsername());
        }
        catch (UnauthenticatedException exc) {
            fail("Unauthenticated exception thrown when user is authenticated");
        }

        when(mockFileHandlingService.deleteFile(any(String.class))).thenReturn(true);
        assertTrue(loginController.logout());

        assertThrows(UnauthenticatedException.class, () -> loginController.getLoggedUsername());
        assertFalse(loginController.login("wrongusername", "wrongpassword"));
    }

    @Test
    void test_loginWithSavedSession() {
        try {
            loginController.addUser(firstName, lastName, username, password, email, phone, salary, birthday, role);
        }
        catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        try {
            String session = username + "\n" + password;
            when(mockFileHandlingService.readFileContents(SESSION)).thenReturn(session);
        }
        catch (IOException ignored) {
        }

        assertTrue(loginController.loginWithSavedSession());

        try {
            assertEquals(username, loginController.getLoggedUsername());
        }
        catch (UnauthenticatedException exc) {
            fail("Unauthenticated exception thrown when user is authenticated");
        }

        try {
            String session = "wrongusername\nwrongpassword";
            when(mockFileHandlingService.readFileContents(SESSION)).thenReturn(session);
        }
        catch (IOException ignored) {
        }

        assertTrue(loginController.loginWithSavedSession());
    }

    @Test
    void test_loginWithSavedSessionInvalidSession() {
        try {
            String session = "invalidsession";
            when(mockFileHandlingService.readFileContents(SESSION)).thenReturn(session);
        }
        catch (IOException ignored) {
        }

        when(mockFileHandlingService.deleteFile(any(String.class))).thenReturn(true);

        assertFalse(loginController.loginWithSavedSession());
        assertThrows(UnauthenticatedException.class, () -> loginController.getLoggedUsername());

        when(mockFileHandlingService.deleteFile(any(String.class))).thenReturn(false);
        assertFalse(loginController.loginWithSavedSession());
        assertThrows(UnauthenticatedException.class, () -> loginController.getLoggedUsername());

        verify(mockFileHandlingService, times(2)).deleteFile(SESSION);

        try {
            when(mockFileHandlingService.readFileContents(SESSION)).thenThrow(new IOException());
        }
        catch (IOException ignored) {
        }

        assertFalse(loginController.loginWithSavedSession());
        assertThrows(UnauthenticatedException.class, () -> loginController.getLoggedUsername());
    }

    @Test
    void test_loginWithSavedSessionNoSession() {
        try {
            when(mockFileHandlingService.readFileContents(SESSION)).thenThrow(new FileNotFoundException());
        }
        catch (IOException ignored) {
        }

        assertFalse(loginController.loginWithSavedSession());
        assertThrows(UnauthenticatedException.class, () -> loginController.getLoggedUsername());
    }

    @Test
    void test_removeUser() {
        try {
            loginController.addUser("John", "Doe", "johndoe", password, email, phone, salary, birthday, AccessLevel.MANAGER);
            loginController.addUser("Jane", "Doe", "janedoe", password, email, phone, salary, birthday, AccessLevel.ADMINISTRATOR);
            loginController.addUser("Jane", "Smith", "janesmith", password, email, phone, salary, birthday, AccessLevel.ADMINISTRATOR);
            loginController.addUser("John", "Smith", "johnsmith", password, email, phone, salary, birthday, AccessLevel.LIBRARIAN);
        }
        catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        User manager = loginController.getUsers().get(0);
        try {
            loginController.removeUser(manager);
            verify(mockFileHandlingService, times(1)).writeObjectToFile(any(String.class), eq(loginController.getUsers()));
            verify(mockBillController, never()).deleteBills(any(Librarian.class));
        }
        catch (IOException ex) {
            fail("Failed to remove user: " + ex.getMessage());
        }
        catch (LastAdministratorException ignored) {
            fail("Last administrator exception thrown when removing manager");
        }

        assertEquals(3, loginController.getUsers().size());

        User administrator = loginController.getUsers().get(0);
        try {
            loginController.removeUser(administrator);
            verify(mockFileHandlingService, times(1)).writeObjectToFile(any(String.class), eq(loginController.getUsers()));
            verify(mockBillController, never()).deleteBills(any(Librarian.class));
        }
        catch (IOException ex) {
            fail("Failed to remove user: " + ex.getMessage());
        }
        catch (LastAdministratorException ignored) {
            fail("Last administrator exception thrown when another administrator exists");
        }
        assertEquals(2, loginController.getUsers().size());

        User administrator2 = loginController.getUsers().get(0);
        assertThrows(LastAdministratorException.class, () -> loginController.removeUser(administrator2));
        assertEquals(2, loginController.getUsers().size());

        User librarian = loginController.getUsers().get(1);
        try {
            loginController.removeUser(librarian);
            verify(mockFileHandlingService, times(1)).writeObjectToFile(any(String.class), eq(loginController.getUsers()));
            verify(mockBillController, times(1)).deleteBills(any(Librarian.class));
        }
        catch (IOException ex) {
            fail("Failed to remove user: " + ex.getMessage());
        }
        catch (LastAdministratorException ignored) {
            fail("Last administrator exception thrown when removing librarian");
        }

        assertEquals(1, loginController.getUsers().size());
    }

    @Test
    void test_saveSession() {
        try {
            loginController.saveSession(username, password);
            verify(mockFileHandlingService, times(1)).writeFileContents(eq(SESSION), any(String.class));
        }
        catch (IOException ignored) {
        }
    }

    @Test
    void test_getLoggedAccessLevel() {
        try {
            loginController.addUser("John", "Doe", "johndoe", password, email, phone, salary, birthday, AccessLevel.MANAGER);
            loginController.addUser("Jane", "Doe", "janedoe", password, email, phone, salary, birthday, AccessLevel.ADMINISTRATOR);
            loginController.addUser("John", "Smith", "johnsmith", password, email, phone, salary, birthday, AccessLevel.LIBRARIAN);
        }
        catch (IOException ex) {
            fail("Failed to add user: " + ex.getMessage());
        }

        when(mockFileHandlingService.deleteFile(any(String.class))).thenReturn(true);

        try {
            assertTrue(loginController.login("johndoe", password));
            assertEquals(AccessLevel.MANAGER, loginController.getLoggedAccessLevel());
            assertTrue(loginController.logout());

            assertTrue(loginController.login("janedoe", password));
            assertEquals(AccessLevel.ADMINISTRATOR, loginController.getLoggedAccessLevel());
            assertTrue(loginController.logout());

            assertTrue(loginController.login("johnsmith", password));
            assertEquals(AccessLevel.LIBRARIAN, loginController.getLoggedAccessLevel());
            assertTrue(loginController.logout());
        }
        catch (UnauthenticatedException exc) {
            fail("Unauthenticated exception thrown when user is authenticated");
        }

        assertThrows(UnauthenticatedException.class, () -> loginController.getLoggedAccessLevel());
    }

    @Test
    void test_readFromFile() {
        ArrayList<User> users = new ArrayList<>();
        User user = new User(firstName, lastName, username, password, email, phone, salary, birthday, role);
        users.add(user);

        try {
            when(mockFileHandlingService.readObjectFromFile(DATABASE)).thenReturn(users);
            loginController.readFromFile(DATABASE);
            verify(mockFileHandlingService, times(2)).readObjectFromFile(DATABASE);
            assertEquals(users, loginController.getUsers());

            when(mockFileHandlingService.readObjectFromFile(DATABASE)).thenThrow(new FileNotFoundException());
            loginController.readFromFile(DATABASE);
            assertEquals(0, loginController.getUsers().size());

            reset(mockFileHandlingService);
            when(mockFileHandlingService.readObjectFromFile(DATABASE)).thenReturn(users);
            when(mockFileHandlingService.readObjectFromFile(DATABASE)).thenThrow(new ClassNotFoundException());
            when(mockFileHandlingService.deleteFile(DATABASE)).thenReturn(false);
            IllegalStateException exc = assertThrows(IllegalStateException.class, () -> loginController.readFromFile(DATABASE));
            assertEquals("Failed to delete corrupted database", exc.getMessage());
            verify(mockFileHandlingService, times(1)).deleteFile(DATABASE);

            when(mockFileHandlingService.deleteFile(DATABASE)).thenReturn(true);
            loginController.readFromFile(DATABASE);
            assertEquals(0, loginController.getUsers().size());
        }
        catch (IOException|ClassNotFoundException ignored) {
        }
    }

    @Test
    void test_writeToFile() {
        try {
            loginController.writeToFile(DATABASE);
            verify(mockFileHandlingService, times(1)).writeObjectToFile(eq(DATABASE), any(ArrayList.class));
        }
        catch (IOException ignored) {
        }
    }
}
