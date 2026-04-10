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

public class BillingInsuranceManagementPage {
    private final Scene scene;
    private final HelloApplication mainApp;
    private final ModuleRequestService requestService = new ModuleRequestService();

    public BillingInsuranceManagementPage(HelloApplication mainApp) {
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

        Text title = new Text("Billing & Insurance Management");
        title.setFont(Font.font("Jost", 28));
        title.setFill(Color.WHITE);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button newButton = new Button("Forget Password");
        newButton.setStyle("-fx-font-size : 16; -fx-padding : 20");
        newButton.setOnAction(e ->
                showAlert("Forgot Password", "Please contact admin to reset your password.")
        );
        Button backButton = new Button("Back");

        backButton.setStyle("-fx-font-size: 14; -fx-padding: 8 15 8 15; -fx-background-color: #1b6e55; -fx-text-fill: white;");
        backButton.setOnAction(e -> mainApp.navigateToHome());

        header.getChildren().addAll(title, spacer, newButton,backButton);
        return header;
    }

    private VBox createMainContent() {
        VBox content = new VBox(25);
        content.setStyle("-fx-background-color: #1db891;");
        content.setPadding(new Insets(30, 40, 40, 40));
        content.setAlignment(Pos.TOP_CENTER);

        Text title = new Text("Hospital Billing & Insurance Operations");
        title.setFont(Font.font("Jost", 36));
        title.setFill(Color.WHITE);

        if (mainApp.isDoctorLoggedIn()) {
            content.getChildren().addAll(title, createDoctorRestrictionSection());
            return content;
        }

        ModuleRequestSectionFactory.SectionBundle requestsSection = ModuleRequestSectionFactory.createSection(
                mainApp, requestService, ModuleRequestService.ModuleType.BILLING, "Billing Requests"
        );

        VBox createBillingSection = createCreateBillingSection(requestsSection.refresh());
        VBox summarySection = createInsuranceClaimsSection();

        content.getChildren().addAll(title, createBillingSection, requestsSection.section(), summarySection);
        return content;
    }

    private VBox createDoctorRestrictionSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        Text message = new Text("Doctors do not have access to the billing request module.");
        message.setFill(Color.WHITE);
        message.setFont(Font.font("Jost", 16));
        section.getChildren().add(message);
        return section;
    }

    private VBox createInsuranceClaimsSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));

        Text sectionTitle = new Text("Insurance Claims Summary");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        VBox claims = new VBox(8);
        claims.getChildren().addAll(
                createSummaryItem("Blue Cross Insurance", "Approved", "#00ff00"),
                createSummaryItem("Aetna Health Insurance", "Pending Review", "#ffeb3b"),
                createSummaryItem("United Healthcare", "Rejected", "#ff6b6b")
        );

        section.getChildren().addAll(sectionTitle, claims);
        return section;
    }

    private HBox createSummaryItem(String provider, String status, String color) {
        HBox item = new HBox(15);
        item.setStyle("-fx-background-color: rgba(29, 184, 145, 0.3); -fx-background-radius: 8; -fx-padding: 12;");
        Text providerText = new Text(provider);
        providerText.setFill(Color.WHITE);
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Text statusText = new Text(status);
        statusText.setFill(Color.web(color));
        item.getChildren().addAll(providerText, spacer, statusText);
        return item;
    }

    private VBox createCreateBillingSection(Runnable refreshRequests) {
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

        Text sectionTitle = new Text("Request Billing / Insurance Service");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        Label patientLabel = new Label("Patient:");
        patientLabel.setTextFill(Color.WHITE);
        TextField patientField = createField(mainApp.getCurrentUserName());
        patientField.setEditable(false);

        Label amountLabel = new Label("Estimated Amount:");
        amountLabel.setTextFill(Color.WHITE);
        TextField amountField = createField("e.g., 2500.00");

        Label insuranceLabel = new Label("Insurance Provider:");
        insuranceLabel.setTextFill(Color.WHITE);
        ComboBox<String> insuranceCombo = createCombo();
        insuranceCombo.getItems().addAll("Blue Cross Insurance", "Aetna Health Insurance", "United Healthcare", "Cigna Health", "No Insurance");

        Label servicesLabel = new Label("Services / Charges:");
        servicesLabel.setTextFill(Color.WHITE);
        TextField servicesField = createField("e.g., Room charge, surgery, medication");

        Button requestButton = new Button("Request Service");
        requestButton.setStyle("-fx-font-size: 12; -fx-padding: 10 20 10 20; -fx-background-color: #00f5ff; -fx-text-fill: #0a192f;");
        requestButton.setOnAction(e -> {
            if (mainApp.getCurrentUserId() == null || amountField.getText().isBlank() || insuranceCombo.getValue() == null || servicesField.getText().isBlank()) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountField.getText().trim());
            } catch (NumberFormatException ex) {
                showAlert("Error", "Amount must be numeric.");
                return;
            }

            String details = "Insurance: " + insuranceCombo.getValue() + " | Services: " + servicesField.getText().trim() + " | Amount: " + amount;
            boolean created = requestService.createRequest(
                    ModuleRequestService.ModuleType.BILLING,
                    mainApp.getCurrentUserId(),
                    new ModuleRequestService.RequestPayload(details, LocalDate.now().toString(), servicesField.getText().trim(), insuranceCombo.getValue(), amount)
            );

            if (created) {
                showAlert("Request Submitted", "Your request is pending for approval");
                amountField.clear();
                insuranceCombo.getSelectionModel().clearSelection();
                servicesField.clear();
                refreshRequests.run();
            } else {
                showAlert("Error", "Failed to create billing request.");
            }
        });

        HBox buttonBox = new HBox(requestButton);
        buttonBox.setAlignment(Pos.CENTER);

        section.getChildren().addAll(
                sectionTitle,
                patientLabel, patientField,
                amountLabel, amountField,
                insuranceLabel, insuranceCombo,
                servicesLabel, servicesField,
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
