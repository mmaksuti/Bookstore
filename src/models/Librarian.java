package src.models;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;
import src.controllers.BillController;
import src.enums.AccessLevel;

public class Librarian extends User {
    BillController billController;

    public Librarian(String firstName, String lastName, String username, String password, String email, String phone,
                     int salary, LocalDate birthday, BillController billController) {
        super(firstName, lastName, username, password, email, phone, salary, birthday, AccessLevel.LIBRARIAN);

        this.billController = billController;
    }

    public Librarian(User user, BillController billController) throws IllegalArgumentException {
        this(user.getFirstName(), user.getLastName(), user.getUsername(), user.getPassword(), user.getEmail(), user.getPhone(), user.getSalary(), user.getBirthday(), billController);

        if (user.getAccessLevel() != AccessLevel.LIBRARIAN) {
            throw new IllegalArgumentException("User is not a librarian");
        }
    }

    public int getNumberOfBills() {
        int numberOfBills = 0;

        String[] fileList = billController.loadBills();
        for (String fileName : fileList) {
            String[] parts = fileName.split("\\.");
            if (parts.length > 3 && parts[1].equals(username)) {
                numberOfBills++;
            }
        }
        return numberOfBills;
    }

    public double getTotalMoney() {
        double totalMoney = 0;

        String[] fileList = billController.loadBills();
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

        return totalMoney;
    }
}