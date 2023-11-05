package main;
import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;

public class Librarian extends User {
    private int numberOfBills;
    private double totalMoney;

    public Librarian(String firstName, String lastName, String username, String password, String email, String phone,
                     int salary, LocalDate birthday) {
        super(firstName, lastName, username, password, email, phone, salary, birthday, AccessLevel.LIBRARIAN);
    }

    public Librarian(User user) throws IllegalArgumentException {
        super(user.getFirstName(), user.getLastName(), user.getUsername(), user.getPassword(), user.getEmail(), user.getPhone(), user.getSalary(), user.getBirthday(), AccessLevel.LIBRARIAN);

        if (user.getAccessLevel() != AccessLevel.LIBRARIAN) {
            throw new IllegalArgumentException("User is not a librarian");
        }
    }

    public void deleteBills() {
        File file = new File("bills");
        if (file.exists()) {
            if (!file.isDirectory()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    System.out.println("Failed to delete the file.");
                }
                return;
            }

            String[] fileList = file.list();
            assert fileList != null;
            for (String fileName : fileList) {
                String[] parts = fileName.split("\\.");
                if (parts.length > 1 && parts[1].equals(username)) {
                    File billFile = new File("bills/" + fileName);
                    boolean deleted = billFile.delete();
                    if (!deleted) {
                        System.out.println("Failed to delete bill file: " + fileName);
                    }
                }
            }
        }
    }



    public int getNumberOfBills() {
        numberOfBills = 0;

        File file = new File("bills");
        if (file.exists()) {
            if (!file.isDirectory()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    System.out.println("Failed to delete the file.");
                }
                return numberOfBills;
            }

            String[] fileList = file.list();
            if (fileList != null) {
                int i = 0;
                for (String fileName : fileList) {
                    String[] parts = fileName.split("\\.");
                    if (parts.length > 1 && parts[1].equals(username)) {
                        i++;
                    }
                }
                numberOfBills = i;
                return numberOfBills;
            }
        }

        return numberOfBills;
    }


    public double getTotalMoney() {
        totalMoney = 0;

        File file = new File("bills");
        if (file.exists()) {
            if (!file.isDirectory()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    System.out.println("Failed to delete the file.");
                }
                return totalMoney;
            }

            String[] fileList = file.list();
            assert fileList != null;
            for (String filename : fileList) {
                String[] parts = filename.split("\\.");
                if (parts.length > 3 && parts[1].equals(username)) {
                    double parsed;
                    NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                    try {
                        parsed = format.parse(parts[3]).doubleValue();
                        totalMoney += parsed;
                    } catch (ParseException e) {
                        totalMoney += 0;
                    }
                }
            }
        }
        return totalMoney;
    }

}