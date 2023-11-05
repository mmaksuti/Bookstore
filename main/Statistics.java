package main;
import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Statistics {
    private String stringStatistics;
    private double totalMoney;

    private static boolean isWithinRange(LocalDate testDate, LocalDate startDate, LocalDate endDate) {
        return !(testDate.isBefore(startDate) || testDate.isAfter(endDate));
    }

    public Statistics(LocalDate from, LocalDate to) {
        stringStatistics = "";
        totalMoney = 0;

        File file = new File("bills/");
        if (file.exists()) {
            if (!file.isDirectory()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    System.out.println("Failed to delete the file.");
                }
                stringStatistics = "No bills\nTotal money earned: 0";
                return;
            }
            String[] fileList = file.list();
            assert fileList != null;
            if (fileList.length == 0) {
                stringStatistics = "No bills\nTotal money earned: 0";
                return;
            }

            StringBuilder stringStatisticsBuilder = new StringBuilder();
            for (String fileName : fileList) {
                if (!fileName.endsWith(".txt")) {
                    continue;
                }

                try {
                    String[] parts = fileName.split("\\.");
                    LocalDate date = LocalDate.parse(parts[0], DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    if (isWithinRange(date, from, to)) {
                        StringBuilder sb = new StringBuilder();

                        sb.append("Date: ").append(parts[0]).append("\n");
                        sb.append("User: ").append(parts[1]).append("\n");
                        sb.append("Books sold: ").append(parts[2]).append("\n");

                        double parsed = 0;
                        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                        try {
                            parsed = format.parse(parts[3]).doubleValue();
                            totalMoney += parsed;
                        } catch (ParseException e) {
                            totalMoney += 0;
                        }

                        sb.append("Money earned: ").append(parsed).append("\n\n");

                        stringStatisticsBuilder.append(sb.toString());
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            String stringStatistics = stringStatisticsBuilder.toString();

        }
        else {
            stringStatistics = "No bills\nTotal money earned: 0";
        }
    }

    @Override
    public String toString() {
        return stringStatistics;
    }
}