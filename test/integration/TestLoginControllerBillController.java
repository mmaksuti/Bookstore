package test.integration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.controllers.BillController;
import src.controllers.LoginController;
import src.enums.AccessLevel;
import src.exceptions.LastAdministratorException;
import src.exceptions.UnauthenticatedException;
import src.models.Bill;
import src.models.User;
import src.services.FileHandlingService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TestLoginControllerBillController {
    private FileHandlingService stubFileHandlingService;
    private BillController billController;
    private LoginController loginController;

    @BeforeEach
    public void setUp() throws IOException {
        stubFileHandlingService = mock(FileHandlingService.class);

        ArrayList<User> users = new ArrayList<User>();
        try {
            when(stubFileHandlingService.readObjectFromFile("usersDatabase.dat")).thenReturn(users);
            when(stubFileHandlingService.ensureDirectory("bills")).thenReturn(true);
            when(stubFileHandlingService.deleteFile(any(String.class))).thenReturn(true);
        }
        catch (ClassNotFoundException ignored) {
        }

        billController = new BillController(stubFileHandlingService, "bills");
        loginController = new LoginController(stubFileHandlingService, billController, "usersDatabase.dat", "session");
    }

    @Test
    public void testRemoveUserIsLibrarian() {
        try {
            loginController.addUser("John", "Doe", "johndoe", "password", "johndoe@gmail.com", "+355671234567", 1000, LocalDate.of(1999, 1, 1), AccessLevel.LIBRARIAN);
            User user1 = loginController.getUsers().get(0);

            String[] billFileNames = {
                    "01-01-2020.johndoe.0.100.txt",
                    "01-03-2020.johndoe.0.100.txt",
                    "01-03-2020.janedoe.0.150.txt",
                    "01-02-2020.janedoe.0.100.txt",
            };
            when(stubFileHandlingService.listDirectory("bills")).thenReturn(billFileNames);
            loginController.removeUser(user1);

            verify(stubFileHandlingService, times(1)).deleteFile("bills/01-01-2020.johndoe.0.100.txt");
            verify(stubFileHandlingService, times(1)).deleteFile("bills/01-03-2020.johndoe.0.100.txt");
            verify(stubFileHandlingService, times(0)).deleteFile("bills/01-03-2020.janedoe.0.150.txt");
            verify(stubFileHandlingService, times(0)).deleteFile("bills/01-02-2020.janedoe.0.100.txt");
        }
        catch (IOException ex) {
            fail("Failed to add users: " + ex.getMessage());
        }
        catch (LastAdministratorException ignored) {
        }
    }

}
