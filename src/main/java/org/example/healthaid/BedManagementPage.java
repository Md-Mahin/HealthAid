package org.example.healthaid;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.time.LocalDate;

public class BedManagementPage {
    private final Scene scene;
    private final HelloApplication mainApp;
    private final ModuleRequestService requestService = new ModuleRequestService();

    public BedManagementPage(HelloApplication mainApp) {
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
        header.setStyle("-fx-background-color: #0d3a24; -fx-padding: 15;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPrefHeight(70);

        Text title = new Text("Hospital Bed Management System");
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

        Text title = new Text("Hospital Bed Allocation & Occupancy");
        title.setFont(Font.font("Jost", 36));
        title.setFill(Color.WHITE);

        if (mainApp.isDoctorLoggedIn()) {
            content.getChildren().addAll(title, createDoctorRestrictionSection());
            return content;
        }

        ModuleRequestSectionFactory.SectionBundle requestsSection = ModuleRequestSectionFactory.createSection(
        mainApp, requestService, ModuleRequestService.ModuleType.BEDS, "Bed Requests"
);

VBox tableSection = requestsSection.section();
tableSection.setMaxHeight(300);
VBox.setVgrow(tableSection, Priority.NEVER);

VBox statsSection = createBedStatisticsSection();
VBox floorPlanSection = createBedFloorPlanSection();
VBox requestSection = createBedRequestSection(requestsSection.refresh());

content.getChildren().addAll(
        title,
        requestSection,
        tableSection,
        statsSection,
        floorPlanSection
);

        return content;
    }

    private VBox createDoctorRestrictionSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        Text message = new Text("Doctors do not have access to the bed request module.");
        message.setFill(Color.WHITE);
        message.setFont(Font.font("Jost", 16));
        section.getChildren().add(message);
        return section;
    }

    private VBox createBedStatisticsSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));

        Text sectionTitle = new Text("Bed Occupancy Statistics");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.getChildren().addAll(
                createStatBox("Total Beds", "312", "#00f5ff"),
                createStatBox("Occupied Beds", "244", "#ff6b6b"),
                createStatBox("Available Beds", "68", "#00ff00")
        );

        section.getChildren().addAll(sectionTitle, statsBox);
        return section;
    }

    private VBox createStatBox(String label, String value, String color) {
        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: rgba(29, 184, 145, 0.3); -fx-background-radius: 8;");
        box.setPadding(new Insets(15));
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(150);

        Text labelText = new Text(label);
        labelText.setFill(Color.web("rgba(255,255,255,0.8)"));
        Text valueText = new Text(value);
        valueText.setFill(Color.web(color));
        valueText.setFont(Font.font("Jost", 24));
        box.getChildren().addAll(labelText, valueText);
        return box;
    }

    private VBox createBedFloorPlanSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));

        Text sectionTitle = new Text("Floor Plan - Room Beds Status");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(createBedCard("101", "Occupied", "#ff6b6b", "John Anderson"), 0, 0);
        grid.add(createBedCard("102", "Available", "#00ff00", "-"), 1, 0);
        grid.add(createBedCard("201", "Available", "#00ff00", "-"), 0, 1);
        grid.add(createBedCard("202", "Occupied", "#ff6b6b", "Michael Brown"), 1, 1);

        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }

    private VBox createBedCard(String bedNumber, String status, String statusColor, String patientInfo) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: rgba(29, 184, 145, 0.3); -fx-background-radius: 8; -fx-border-color: " + statusColor + "; -fx-border-width: 2;");
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(120, 100);

        Text bedText = new Text("Bed " + bedNumber);
        bedText.setFill(Color.WHITE);
        Text statusText = new Text(status);
        statusText.setFill(Color.web(statusColor));
        Text patientText = new Text(patientInfo);
        patientText.setFill(Color.web("rgba(255,255,255,0.7)"));
        card.getChildren().addAll(bedText, statusText, patientText);
        return card;
    }

    private VBox createBedRequestSection(Runnable refreshRequests) {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));
        section.setMaxWidth(800);

        if (!mainApp.isPatientLoggedIn()) {
            section.setVisible(false);
            section.setManaged(false);
            return section;
        }

        section.setVisible(true);
        section.setManaged(true);

        Text sectionTitle = new Text("Request Bed Service");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        Label patientLabel = new Label("Patient:");
        patientLabel.setTextFill(Color.WHITE);
        TextField patientField = createField(mainApp.getCurrentUserName());
        patientField.setEditable(false);

        Label departmentLabel = new Label("Department:");
        departmentLabel.setTextFill(Color.WHITE);
        ComboBox<String> departmentCombo = createCombo();
        departmentCombo.getItems().addAll("General Ward", "ICU", "Cardiac Care Unit", "Orthopedic Ward", "Pediatric Ward");

        Label bedLabel = new Label("Preferred Bed:");
        bedLabel.setTextFill(Color.WHITE);
        ComboBox<String> bedCombo = createCombo();
        bedCombo.getItems().addAll("Room 102 - Bed A", "Room 201 - Bed A", "Room 302 - Bed B", "Room 304 - Bed A");

        Label doctorLabel = new Label("Assigned Doctor:");
        doctorLabel.setTextFill(Color.WHITE);
        ComboBox<String> doctorCombo = createCombo();
        doctorCombo.getItems().addAll("Dr. Sarah Johnson", "Dr. Michael Chen", "Dr. Robert Lee", "Dr. Emily Rodriguez");

        Button requestButton = new Button("Request Service");
        requestButton.setStyle("-fx-font-size: 12; -fx-padding: 10 20 10 20; -fx-background-color: #00f5ff; -fx-text-fill: #0a192f;");
        requestButton.setOnAction(e -> {
            if (mainApp.getCurrentUserId() == null || departmentCombo.getValue() == null || bedCombo.getValue() == null || doctorCombo.getValue() == null) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            String details = "Department: " + departmentCombo.getValue() + " | Preferred Bed: " + bedCombo.getValue() + " | Doctor: " + doctorCombo.getValue();
            boolean created = requestService.createRequest(
                    ModuleRequestService.ModuleType.BEDS,
                    mainApp.getCurrentUserId(),
                    new ModuleRequestService.RequestPayload(details, LocalDate.now().toString(), departmentCombo.getValue(), bedCombo.getValue() + " / " + doctorCombo.getValue(), 0)
            );

            if (created) {
                showAlert("Request Submitted", "Your request is pending for approval");
                departmentCombo.getSelectionModel().clearSelection();
                bedCombo.getSelectionModel().clearSelection();
                doctorCombo.getSelectionModel().clearSelection();
                refreshRequests.run();
            } else {
                showAlert("Error", "Failed to create bed request.");
            }
        });

        HBox buttonBox = new HBox(requestButton);
        buttonBox.setAlignment(Pos.CENTER);

        section.getChildren().addAll(
                sectionTitle,
                patientLabel, patientField,
                departmentLabel, departmentCombo,
                bedLabel, bedCombo,
                doctorLabel, doctorCombo,
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

    private ComboBox<String> createCombo() {
        ComboBox<String> combo = new ComboBox<>();
        combo.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-background-color: rgba(255,255,255,0.9);");
        combo.setPrefWidth(300);
        combo.setPrefHeight(40);
        return combo;
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
