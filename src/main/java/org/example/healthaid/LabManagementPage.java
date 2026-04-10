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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.time.LocalDate;

public class LabManagementPage {
    private final Scene scene;
    private final HelloApplication mainApp;
    private final ModuleRequestService requestService = new ModuleRequestService();

    public LabManagementPage(HelloApplication mainApp) {
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
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #0d3a24; -fx-padding: 15;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);
        header.setPrefHeight(70);

        Text title = new Text("Laboratory Management System");
        title.setFont(Font.font("Jost", 28));
        title.setFill(Color.WHITE);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backButton = new Button("Back");
        backButton.setStyle("""
                -fx-font-size: 14;
                -fx-padding: 8 15 8 15;
                -fx-background-color: #1b6e55;
                -fx-text-fill: white;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """);
        backButton.setOnAction(e -> mainApp.navigateToHome());

        header.getChildren().addAll(title, spacer, backButton);
        return header;
    }

    private VBox createMainContent() {
        VBox content = new VBox(25);
        content.setStyle("-fx-background-color: #1db891");
        content.setPadding(new Insets(30, 40, 40, 40));
        content.setAlignment(Pos.TOP_CENTER);

        Text title = new Text("Hospital Laboratory Services");
        title.setFont(Font.font("Jost", 36));
        title.setFill(Color.WHITE);

        ModuleRequestSectionFactory.SectionBundle requestsSection = ModuleRequestSectionFactory.createSection(
                mainApp, requestService, ModuleRequestService.ModuleType.LAB, "Lab Service Requests"
        );

        VBox requestFormSection = createRequestTestSection(requestsSection.refresh());
        VBox inventorySection = createLabInventorySection();

        content.getChildren().addAll(title, requestFormSection, requestsSection.section(), inventorySection);
        return content;
    }

    private VBox createRequestTestSection(Runnable refreshRequests) {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: rgba(245, 249, 248, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));
        section.setMaxWidth(800);

        if (!mainApp.isPatientLoggedIn()) {
            section.setVisible(false);
            section.setManaged(false);
            return section;
        }

        section.setVisible(true);
        section.setManaged(true);

        Text sectionTitle = new Text("Request New Lab Test");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        Label patientLabel = new Label("Patient:");
        patientLabel.setTextFill(Color.WHITE);
        TextField patientField = createField(mainApp.getCurrentUserName());
        patientField.setEditable(false);

        Label testLabel = new Label("Select Test Type:");
        testLabel.setTextFill(Color.WHITE);
        ComboBox<String> testCombo = createCombo();
        testCombo.getItems().addAll(
                "Complete Blood Count (CBC)",
                "Lipid Panel Test",
                "Blood Culture Test",
                "Thyroid Function Test (TSH)",
                "Urinalysis",
                "Liver Function Test",
                "Kidney Function Test"
        );

        Label priorityLabel = new Label("Priority Level:");
        priorityLabel.setTextFill(Color.WHITE);
        ComboBox<String> priorityCombo = createCombo();
        priorityCombo.getItems().addAll("Routine", "Normal", "Urgent", "STAT");

        Label notesLabel = new Label("Special Notes/Instructions:");
        notesLabel.setTextFill(Color.WHITE);
        TextField notesField = createField("e.g., Fasting required");

        Button requestButton = new Button("Request Service");
        requestButton.setStyle("""
                -fx-font-size: 12;
                -fx-padding: 10 20 10 20;
                -fx-background-color: #00f5ff;
                -fx-text-fill: #0a192f;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """);
        requestButton.setOnAction(e -> {
            if (mainApp.getCurrentUserId() == null || testCombo.getValue() == null || priorityCombo.getValue() == null || notesField.getText().isBlank()) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            String details = "Test: " + testCombo.getValue() + " | Priority: " + priorityCombo.getValue() + " | Notes: " + notesField.getText().trim();
            boolean created = requestService.createRequest(
                    ModuleRequestService.ModuleType.LAB,
                    mainApp.getCurrentUserId(),
                    new ModuleRequestService.RequestPayload(details, LocalDate.now().toString(), details, "", 0)
            );

            if (created) {
                showAlert("Request Submitted", "Your request is pending for approval");
                testCombo.getSelectionModel().clearSelection();
                priorityCombo.getSelectionModel().clearSelection();
                notesField.clear();
                refreshRequests.run();
            } else {
                showAlert("Error", "Failed to create lab request.");
            }
        });

        HBox buttonBox = new HBox(requestButton);
        buttonBox.setAlignment(Pos.CENTER);

        section.getChildren().addAll(
                sectionTitle,
                patientLabel, patientField,
                testLabel, testCombo,
                priorityLabel, priorityCombo,
                notesLabel, notesField,
                buttonBox
        );
        return section;
    }

    private VBox createLabInventorySection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));

        Text sectionTitle = new Text("Lab Reagent & Equipment Inventory");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        VBox inventoryList = new VBox(8);
        inventoryList.getChildren().addAll(
                createInventoryItem("CBC Reagent Kit", "85/100 units", "Normal", "#00ff00"),
                createInventoryItem("Lipid Panel Strips", "45/50 units", "Critical", "#ff6b6b"),
                createInventoryItem("Blood Culture Media", "120/150 units", "Normal", "#00ff00"),
                createInventoryItem("TSH Testing Kits", "25/30 units", "Warning", "#ffeb3b")
        );

        section.getChildren().addAll(sectionTitle, inventoryList);
        return section;
    }

    private HBox createInventoryItem(String name, String quantity, String status, String statusColor) {
        HBox item = new HBox(15);
        item.setStyle("-fx-background-color: rgba(29, 184, 145, 0.3); -fx-background-radius: 8; -fx-padding: 12;");
        item.setAlignment(Pos.CENTER_LEFT);

        Text nameText = new Text(name);
        nameText.setFont(Font.font("Jost", 12));
        nameText.setFill(Color.WHITE);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text quantityText = new Text(quantity);
        quantityText.setFill(Color.web("rgba(255,255,255,0.8)"));

        Text statusText = new Text(status);
        statusText.setFill(Color.web(statusColor));

        item.getChildren().addAll(nameText, spacer, quantityText, statusText);
        return item;
    }

    private TextField createField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setStyle("""
                -fx-font-size: 12;
                -fx-padding: 10;
                -fx-background-color: rgba(255,255,255,0.9);
                -fx-background-radius: 5;
                """);
        field.setPrefHeight(40);
        return field;
    }

    private ComboBox<String> createCombo() {
        ComboBox<String> combo = new ComboBox<>();
        combo.setStyle("""
                -fx-font-size: 12;
                -fx-padding: 10;
                -fx-background-color: rgba(255,255,255,0.9);
                -fx-background-radius: 5;
                """);
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
