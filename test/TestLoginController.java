package test;

import controllers.BillController;
import controllers.LoginController;
import exceptions.UnauthenticatedException;
import main.AccessLevel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

public class TestLoginController {
    private LoginController loginController;

    @BeforeEach
    public void setUp() throws IOException {
        loginController = new LoginController(new BillController());
    }

    @ParameterizedTest
    @CsvSource({"validUsername,validPassword,true", "invalidUsername,invalidPassword,False"})
    public void testLogin(String username, String password, boolean expected) {
        boolean result = loginController.login(username, password);
        Assertions.assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource({"ExistingUser,true", "nonExistingUser,false"})
    public void testUserExist(String username, boolean expected) {
        boolean result = loginController.userExists(username);
        Assertions.assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource({"validUsername, validPassword, true", "invalidUsername, invalidPassword, false"})
    public void testGetLoggedUsername(String username, String password, boolean isAuthenticated) {
        if (isAuthenticated) {
            loginController.login(username, password);
            Assertions.assertDoesNotThrow(() -> loginController.getLoggedUsername());
        } else {
            Assertions.assertThrows(UnauthenticatedException.class, () -> loginController.getLoggedUsername());
        }
    }

    @Test
    public void TestLogOut() {
       //..
    }
    @Test
    public void testGetWelcomeMessageUnauthenticated() {
        Assertions.assertThrows(UnauthenticatedException.class, () -> loginController.getWelcomeMessage());
    }
    @Test
    public void testGetLoggedAccessLevel() throws UnauthenticatedException {
        //..
    }

    @Test
    public void testGetLoggedAccessLevelUnauthenticated() {
        Assertions.assertThrows(UnauthenticatedException.class, () -> loginController.getLoggedAccessLevel());
    }


}
