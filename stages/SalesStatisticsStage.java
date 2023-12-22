package stages;

import services.FileHandlingService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Statistics;

public class SalesStatisticsStage extends Stage {
    public SalesStatisticsStage(FileHandlingService fileHandlingService) {
        setTitle("Sales statistics");

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setSpacing(20);
        Scene scene = new Scene(vbox, 300, 200);
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        Label fromLabel = new Label("From: ");
        grid.add(fromLabel, 0, 0);

        DatePicker startDatePicker = new DatePicker();
        grid.add(startDatePicker, 1, 0);

        Label toLabel = new Label("To: ");
        grid.add(toLabel, 0, 1);
        
        DatePicker endDatePicker = new DatePicker();
        grid.add(endDatePicker, 1, 1);

        Button getStatistics = new Button("Get statistics");
        getStatistics.setOnAction(e -> {
            Statistics statistics = new Statistics(fileHandlingService, startDatePicker.getValue(), endDatePicker.getValue());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Statistics");
            alert.setHeaderText("Statistics");
            alert.setContentText(statistics.toString());
            alert.showAndWait();
        });

        setScene(scene);
        vbox.getChildren().addAll(grid, getStatistics);
    }
}