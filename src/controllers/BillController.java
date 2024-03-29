package src.controllers;

import src.models.Bill;
import src.models.Librarian;
import src.services.FileHandlingService;

import java.io.IOException;

public class BillController {
    private String BILLS = "bills";
    private FileHandlingService fileHandlingService;

    public BillController(FileHandlingService fileHandlingService) {
        this.fileHandlingService = fileHandlingService;
    }

    public BillController(FileHandlingService fileHandlingService, String billsPath) {
        this.fileHandlingService = fileHandlingService;
        this.BILLS = billsPath;
    }

    public String[] loadBills() {
        if (!fileHandlingService.ensureDirectory(BILLS)) {
            throw new IllegalStateException("bills not a directory");
        }

        return fileHandlingService.listDirectory(BILLS);
    }

    public void deleteBills(Librarian librarian) throws IllegalStateException {
        String[] fileList = loadBills();
        for (String fileName : fileList) {
            String[] parts = fileName.split("\\.");
            if (parts.length > 1 && parts[1].equals(librarian.getUsername())) {
                boolean deleted = fileHandlingService.deleteFile("bills/" + fileName);
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

        fileHandlingService.writeFileContents(builder.toString(), bill.toString());
    }
}