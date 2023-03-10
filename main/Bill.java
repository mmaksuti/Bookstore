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
    private String textBill;
    private String username;
    private String date;
    private double totalPrice;
    private int nBooks;

    public Bill(String username, Map<Book, Integer> booksSold, double totalPrice) {
        this.username = username;
        this.totalPrice = totalPrice;
        this.nBooks = booksSold.size();

        date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        StringBuilder text = new StringBuilder();
        text.append("Items sold:\n");
        for (Book book : booksSold.keySet()) {
            text.append("\t- " + book.getTitle() + " x" + booksSold.get(book) + "\n");
        }

        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);
        totalPrice = Double.parseDouble(df.format(totalPrice));

        text.append("Total: " + totalPrice + " lek");
        
        textBill = text.toString();
    }

    public void writeToFile() {
        try {
            String[] fileList = new File("bills/").list();
            int i = 0;
            for (String file : fileList) {
                if (file.startsWith(date + "." + username)) {
                    i++;
                }
            }

            File file = new File("bills/" + date + "." + username + "." + nBooks + "." + (int)totalPrice + "," + Math.round(100*(totalPrice - (int)totalPrice)) + "." + i + ".txt");
            
            PrintWriter writer = new PrintWriter(file);
            writer.println(textBill);
            writer.close();
        }
        catch (IOException e) {
            System.out.println("IOException");
        }
    }

    @Override
    public String toString() {
        return textBill;
    }
}