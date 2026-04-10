package org.example.healthaid;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class OperationSchedulesPage {
    private final Scene scene;
    private final HelloApplication mainApp;
    private final ModuleRequestService requestService = new ModuleRequestService();

    public OperationSchedulesPage(HelloApplication mainApp) {
        this.mainApp = mainApp;
        this.scene = createScene();
    }

    private Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1db891;");
        root.setTop(createHeader());
        root.setCenter(LayoutUtils.createScrollablePage(createMainContent()));
        return new Scene(root);
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setStyle("-fx-background-color: #0f4c2f; -fx-padding: 15;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPrefHeight(70);

        Text title = new Text("Operation Schedules");
        title.setFont(Font.font("Jost", 28));
        title.setFill(Color.WHITE);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-font-size: 14; -fx-padding: 8 15 8 15; -fx-background-color: #1b6e55; -fx-text-fill: white;");
        backButton.setOnAction(e -> mainApp.navigateToHome());

        header.getChildren().addAll(title, spacer, backButton);
        return header;
    }

    private VBox createMainContent() {
        VBox content = new VBox(25);
        content.setStyle("-fx-background-color: #1db891;");
        content.setPadding(new Insets(30, 40, 40, 40));
        content.setAlignment(Pos.TOP_CENTER);

        Text title = new Text("Your Operation Schedules");
        title.setFont(Font.font("Jost", 40));
        title.setFill(Color.WHITE);

        ModuleRequestSectionFactory.SectionBundle requestsSection = ModuleRequestSectionFactory.createSection(
                mainApp, requestService, ModuleRequestService.ModuleType.OPERATIONS, "Operation Requests"
        );

        VBox checklistSection = createPreOperationChecklistSection();
        VBox requestSection = createScheduleOperationSection(requestsSection.refresh());

        content.getChildren().addAll(title, requestsSection.section(), requestSection, checklistSection);
        return content;
    }

    private VBox createPreOperationChecklistSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(25));

        Text sectionTitle = new Text("Pre-Operation Checklist");
        sectionTitle.setFont(Font.font("Jost", 22));
        sectionTitle.setFill(Color.WHITE);

        VBox checklistBox = new VBox(10);
        String[] checklist = {
                "Complete pre-operation blood tests",
                "Get medical clearance from your doctor",
                "Stop medications as advised",
                "Arrange pickup after surgery",
                "Arrive 2 hours before scheduled time"
        };
        for (String item : checklist) {
            Text text = new Text(item);
            text.setFill(Color.WHITE);
            checklistBox.getChildren().add(text);
        }

        section.getChildren().addAll(sectionTitle, checklistBox);
        return section;
    }

    private VBox createScheduleOperationSection(Runnable refreshRequests) {
        VBox section = new VBox(20);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(25));
        section.setMaxWidth(800);

        if (!mainApp.isPatientLoggedIn()) {
            section.setVisible(false);
            section.setManaged(false);
            return section;
        }

        section.setVisible(true);
        section.setManaged(true);

        Text sectionTitle = new Text("Book / Request Operation");
        sectionTitle.setFont(Font.font("Jost", 22));
        sectionTitle.setFill(Color.WHITE);

        Label typeLabel = new Label("Operation Type:");
        typeLabel.setTextFill(Color.WHITE);
        TextField typeField = createField("Enter type of operation needed");

        Label dateLabel = new Label("Preferred Date:");
        dateLabel.setTextFill(Color.WHITE);
        DatePicker datePicker = new DatePicker();
        datePicker.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-background-color: rgba(255,255,255,0.9);");

        Label reasonLabel = new Label("Reason / Medical Condition:");
        reasonLabel.setTextFill(Color.WHITE);
        TextField reasonField = createField("Brief description of medical condition");

        Button requestButton = new Button("Book");
        requestButton.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 12 30 12 30; -fx-background-color: #00f5ff; -fx-text-fill: #0a192f;");
        requestButton.setOnAction(e -> {
            if (mainApp.getCurrentUserId() == null || typeField.getText().isBlank() || datePicker.getValue() == null || reasonField.getText().isBlank()) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            String details = "Operation: " + typeField.getText().trim() + " | Reason: " + reasonField.getText().trim();
            boolean created = requestService.createRequest(
                    ModuleRequestService.ModuleType.OPERATIONS,
                    mainApp.getCurrentUserId(),
                    new ModuleRequestService.RequestPayload(details, datePicker.getValue().toString(), typeField.getText().trim(), reasonField.getText().trim(), 0)
            );

            if (created) {
                showAlert("Request Submitted", "Your request is pending for approval");
                typeField.clear();
                datePicker.setValue(null);
                reasonField.clear();
                refreshRequests.run();
            } else {
                showAlert("Error", "Failed to create operation request.");
            }
        });

        HBox buttonBox = new HBox(requestButton);
        buttonBox.setAlignment(Pos.CENTER);

        section.getChildren().addAll(
                sectionTitle,
                typeLabel, typeField,
                dateLabel, datePicker,
                reasonLabel, reasonField,
                buttonBox
        );
        return section;
    }

    private TextField createField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-background-color: rgba(255,255,255,0.9);");
        field.setPrefHeight(40);
        return field;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
}
