package controllers;

import main.Bill;
import main.Librarian;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class BillController {
    private final String BILLS = "bills";
    private DatabaseController dbController;

    /*public BillController(DatabaseController dbController) {
        this.dbController = dbController;
    }*/

    public String[] loadBills() {
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

    public void deleteBills(Librarian librarian) throws IllegalStateException {
        String[] fileList = loadBills();
        for (String fileName : fileList) {
            String[] parts = fileName.split("\\.");
            if (parts.length > 1 && parts[1].equals(librarian.getUsername())) {
                File billFile = new File("bills/" + fileName);
                boolean deleted = billFile.delete();
                if (!deleted) {
                    throw new IllegalStateException("Failed to delete bill file: " + fileName);
                }
            }
        }
    }
    public void saveBill(Bill bill) throws IOException {
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
