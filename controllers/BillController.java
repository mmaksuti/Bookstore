package controllers;

import main.Bill;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class BillController {
    private static final String BILLS = "bills";

    public static String[] loadBills() {
        File file = new File(BILLS);
        if (!file.isDirectory()) {
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    throw new IllegalStateException("File exists at bills directory");
                }
            }
            boolean created = file.mkdir();
            if (!created) {
                throw new IllegalStateException("Failed to create bills directory");
            }
        }
        return file.list();
    }

    public static void saveBill(Bill bill) throws IOException {
        String[] fileList = loadBills();
        int i = 0;
        for (String file : fileList) {
            if (file.startsWith(bill.getDate() + "." + bill.getUsername())) {
                i++;
            }
        }

        StringBuilder builder = new StringBuilder();
        builder.append(BILLS)
                .append("/")
                .append(bill.getDate())
                .append(".")
                .append(bill.getUsername())
                .append(".")
                .append(bill.getNBooks())
                .append(".")
                .append((int)bill.getTotalPrice())
                .append(",")
                .append(Math.round(100*(bill.getTotalPrice() - (int)bill.getTotalPrice())))
                .append(".")
                .append(i)
                .append(".txt");

        File file = new File(builder.toString());

        PrintWriter writer = new PrintWriter(file);
        writer.println(bill.getTextBill());
        writer.close();
    }
}
