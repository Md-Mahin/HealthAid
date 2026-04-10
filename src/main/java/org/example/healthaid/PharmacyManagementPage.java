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

public class PharmacyManagementPage {
    private final Scene scene;
    private final HelloApplication mainApp;
    private final ModuleRequestService requestService = new ModuleRequestService();

    public PharmacyManagementPage(HelloApplication mainApp) {
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

        Text title = new Text("Pharmacy Management System");
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

        Text title = new Text("Hospital Pharmacy Operations");
        title.setFont(Font.font("Jost", 36));
        title.setFill(Color.WHITE);

        if (mainApp.isDoctorLoggedIn()) {
            content.getChildren().addAll(title, createDoctorRestrictionSection());
            return content;
        }

        ModuleRequestSectionFactory.SectionBundle requestsSection = ModuleRequestSectionFactory.createSection(
                mainApp, requestService, ModuleRequestService.ModuleType.PHARMACY, "Pharmacy Requests"
        );

        VBox formSection = createFulfillPrescriptionSection(requestsSection.refresh());
        VBox inventorySection = createMedicationInventorySection();
        content.getChildren().addAll(title, formSection, requestsSection.section(), inventorySection);
        return content;
    }

    private VBox createDoctorRestrictionSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        Text message = new Text("Doctors do not have access to the pharmacy request module.");
        message.setFill(Color.WHITE);
        message.setFont(Font.font("Jost", 16));
        section.getChildren().add(message);
        return section;
    }

    private VBox createFulfillPrescriptionSection(Runnable refreshRequests) {
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

        Text sectionTitle = new Text("Request Medication");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        Label patientLabel = new Label("Patient:");
        patientLabel.setTextFill(Color.WHITE);
        TextField patientField = createField(mainApp.getCurrentUserName());
        patientField.setEditable(false);

        Label medicationLabel = new Label("Medication Name:");
        medicationLabel.setTextFill(Color.WHITE);
        ComboBox<String> medicationCombo = createCombo();
        medicationCombo.getItems().addAll(
                "Amoxicillin 500mg", "Metformin 1000mg", "Lisinopril 10mg", "Aspirin 100mg",
                "Ibuprofen 400mg", "Paracetamol 500mg", "Insulin Glargine"
        );

        Label quantityLabel = new Label("Quantity:");
        quantityLabel.setTextFill(Color.WHITE);
        TextField quantityField = createField("e.g., 30 tablets");

        Label instructionsLabel = new Label("Dosage Instructions:");
        instructionsLabel.setTextFill(Color.WHITE);
        TextField instructionsField = createField("e.g., Take twice daily with food");

        Button requestButton = new Button("Request Service");
        requestButton.setStyle("-fx-font-size: 12; -fx-padding: 10 20 10 20; -fx-background-color: #00f5ff; -fx-text-fill: #0a192f;");
        requestButton.setOnAction(e -> {
            if (mainApp.getCurrentUserId() == null || medicationCombo.getValue() == null || quantityField.getText().isBlank() || instructionsField.getText().isBlank()) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            String details = "Medication: " + medicationCombo.getValue() + " | Quantity: " + quantityField.getText().trim() +
                    " | Instructions: " + instructionsField.getText().trim();
            boolean created = requestService.createRequest(
                    ModuleRequestService.ModuleType.PHARMACY,
                    mainApp.getCurrentUserId(),
                    new ModuleRequestService.RequestPayload(details, LocalDate.now().toString(), medicationCombo.getValue(), quantityField.getText().trim(), 0)
            );

            if (created) {
                showAlert("Request Submitted", "Your request is pending for approval");
                medicationCombo.getSelectionModel().clearSelection();
                quantityField.clear();
                instructionsField.clear();
                refreshRequests.run();
            } else {
                showAlert("Error", "Failed to create pharmacy request.");
            }
        });

        HBox buttonBox = new HBox(requestButton);
        buttonBox.setAlignment(Pos.CENTER);

        section.getChildren().addAll(
                sectionTitle,
                patientLabel, patientField,
                medicationLabel, medicationCombo,
                quantityLabel, quantityField,
                instructionsLabel, instructionsField,
                buttonBox
        );
        return section;
    }

    private VBox createMedicationInventorySection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));

        Text sectionTitle = new Text("Medication Stock Levels");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        VBox inventoryList = new VBox(8);
        inventoryList.getChildren().addAll(
                createInventoryItem("Amoxicillin 500mg", "500/500", "Normal", "#00ff00"),
                createInventoryItem("Metformin 1000mg", "1200/1500", "Normal", "#00ff00"),
                createInventoryItem("Lisinopril 10mg", "80/100", "Warning", "#ffeb3b"),
                createInventoryItem("Insulin Glargine", "25/50", "Critical", "#ff6b6b")
        );

        section.getChildren().addAll(sectionTitle, inventoryList);
        return section;
    }

    private HBox createInventoryItem(String name, String stock, String status, String color) {
        HBox item = new HBox(15);
        item.setStyle("-fx-background-color: rgba(29, 184, 145, 0.3); -fx-background-radius: 8; -fx-padding: 12;");
        item.setAlignment(Pos.CENTER_LEFT);

        Text nameText = new Text(name);
        nameText.setFill(Color.WHITE);
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Text stockText = new Text(stock);
        stockText.setFill(Color.web("rgba(255,255,255,0.8)"));
        Text statusText = new Text(status);
        statusText.setFill(Color.web(color));

        item.getChildren().addAll(nameText, spacer, stockText, statusText);
        return item;
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
