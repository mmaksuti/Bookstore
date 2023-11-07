package main;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Bill {
    private final String textBill;
    private final String username;
    private final String date;
    private final double totalPrice;
    private final int nBooks;

    public String getTextBill() {
        return textBill;
    }

    public String getUsername() {
        return username;
    }

    public String getDate() {
        return date;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public int getNBooks() {
        return nBooks;
    }

    public Bill(String username, Map<Book, Integer> booksSold, double totalPrice) {
        this.username = username;
        this.totalPrice = totalPrice;
        this.nBooks = booksSold.size();

        date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        StringBuilder text = new StringBuilder();
        text.append("Items sold:\n");
        for (Book book : booksSold.keySet()) {
            text.append("\t- ").append(book.getTitle()).append(" x").append(booksSold.get(book)).append("\n");
        }

        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);
        totalPrice = Double.parseDouble(df.format(totalPrice));

        text.append("Total: ").append(totalPrice).append(" lek");
        
        textBill = text.toString();
    }

    @Override
    public String toString() {
        return textBill;
    }
}