package controllers;

import main.Bill;
import main.Librarian;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class BillController {
    private final String BILLS = "bills";
    private DatabaseController dbController;

    public BillController(DatabaseController dbController) {
        this.dbController = dbController;
    }

    public String[] loadBills() {
        if (!dbController.ensureDirectory(BILLS)) {
            throw new IllegalStateException("bills not a directory");
        }

        return dbController.listDirectory(BILLS);
    }

    public void deleteBills(Librarian librarian) throws IllegalStateException {
        String[] fileList = loadBills();
        for (String fileName : fileList) {
            String[] parts = fileName.split("\\.");
            if (parts.length > 1 && parts[1].equals(librarian.getUsername())) {
                boolean deleted = dbController.deleteFile("bills/" + fileName);
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
            if (file.startsWith(bill.getDate() + "." + bill.getUsername() + ".")) {
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

        dbController.writeFileContents(builder.toString(), bill.getTextBill());
    }
}
