package test.unit.models;

import src.models.Bill;
import src.models.Book;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestBill {
    @Test
    void test_toString() {
        Map<Book, Integer> booksSold = new LinkedHashMap<>(); // LinkedHashMap to preserve insertion order
        booksSold.put(new Book("", "Book title", "", 100, null, null, 0, false), 1);

        Bill bill = new Bill("username", booksSold, 100);
        String expectedBill = "Items sold:\n" +
                "\t- Book title x1\n" +
                "Total: 100.0 lek";

        assertEquals(expectedBill, bill.toString());

        booksSold.put(new Book("", "Book title 2", "", 100, null, null, 0, false), 2);
        bill = new Bill("username2", booksSold, 300);
        expectedBill = "Items sold:\n" +
                "\t- Book title x1\n" +
                "\t- Book title 2 x2\n" +
                "Total: 300.0 lek";

        assertEquals(expectedBill, bill.toString());
    }

    @Test
    void test_getters() {
        Map<Book, Integer> booksSold = new LinkedHashMap<>();
        booksSold.put(new Book("", "Book title", "", 200, null, null, 1, false), 1);

        Bill bill = new Bill("username", booksSold, 200);

        assertEquals("username", bill.getUsername());
        assertEquals(1, bill.getNBooks());
        assertEquals(200, bill.getTotalPrice());
        assertEquals("username", bill.getUsername());
        assertEquals(new SimpleDateFormat("dd-MM-yyyy").format(new Date()), bill.getDate());
    }
}
