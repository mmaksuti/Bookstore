package test.unit.models;

import src.enums.Gender;
import src.models.Author;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAuthor {
    @Test
    public void testToString() {
        Author author = new Author("John", "Doe", Gender.MALE);
        assertEquals("John Doe", author.toString());
    }
}
