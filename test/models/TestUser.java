package test.models;

import src.models.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUser {
    @Test
    public void testToString() {
        User user = new User("John", "Doe", "johndoe", "password", "johndoe@gmail.com", "", 1000, null, null);
        assertEquals("John Doe (johndoe)", user.toString());
    }
}
