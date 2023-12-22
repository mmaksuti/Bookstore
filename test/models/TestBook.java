package test.models;

import enums.Gender;
import models.Author;
import models.Book;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestBook {
    @Test
    public void testToString() {
        Author author = new Author("John", "Doe", Gender.MALE);
        Book book = new Book("", "Book title", "", 1000, author, null,0, false);
        assertEquals("Book title by John Doe, 1000.0 leke", book.toString());
    }
}
