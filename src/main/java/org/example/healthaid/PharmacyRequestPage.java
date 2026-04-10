package org.example.healthaid;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.*;
import java.sql.*;

public class PharmacyRequestPage {
    private Scene scene;
    private HelloApplication mainApp;
    private int patientId;
    private String patientName;

    public PharmacyRequestPage(HelloApplication mainApp, int patientId, String patientName) {
        this.mainApp = mainApp;
        this.patientId = patientId;
        this.patientName = patientName;
        this.scene = createScene();
    }

    public Scene getScene() {
        return scene;
    }

    private Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1db891;");

        HBox header = createHeader();
        root.setTop(header);
        root.setCenter(LayoutUtils.createScrollablePage(createMainContent()));
        return new Scene(root);
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #157a52; -fx-padding: 15;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);
        header.setPrefHeight(70);

        Text title = new Text("💊 Request Medications");
        title.setFont(Font.font("Jost", 28));
        title.setFill(Color.WHITE);

        header.getChildren().add(title);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().add(spacer);

        Button backButton = new Button("← Back");
        backButton.setStyle("""
                -fx-font-size: 14;
                -fx-padding: 8 15 8 15;
                -fx-background-color: #0d3a24;
                -fx-text-fill: white;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """);
        backButton.setOnAction(e -> mainApp.navigateToHome());

        header.getChildren().add(backButton);
        return header;
    }

    private VBox createMainContent() {
        VBox content = new VBox();
        content.setStyle("-fx-background-color: #1db891;");
        content.setSpacing(25);
        content.setPadding(new Insets(30, 40, 40, 40));
        content.setAlignment(Pos.TOP_CENTER);

        VBox patientInfo = createPatientInfoBox();
        content.getChildren().add(patientInfo);

        VBox formBox = createRequestForm();
        content.getChildren().add(formBox);

        return content;
    }

    private VBox createPatientInfoBox() {
        VBox box = new VBox();
        box.setSpacing(10);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: rgba(0, 245, 255, 0.1); -fx-background-radius: 10;");
        box.setAlignment(Pos.CENTER);

        Text patientLabel = new Text("Patient: " + patientName);
        patientLabel.setFont(Font.font("Jost", 16));
        patientLabel.setFill(Color.WHITE);

        box.getChildren().add(patientLabel);
        return box;
    }

    private VBox createRequestForm() {
        VBox formBox = new VBox();
        formBox.setSpacing(20);
        formBox.setPadding(new Insets(20));
        formBox.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");

        Text medicationLabel = new Text("Medications Needed:");
        medicationLabel.setFont(Font.font("Jost", 14));
        medicationLabel.setFill(Color.WHITE);

        TextArea medicationArea = new TextArea();
        medicationArea.setPrefHeight(150);
        medicationArea.setWrapText(true);
        medicationArea.setPromptText("List all medications needed, dosage, and frequency.\nExample:\n- Aspirin 500mg - twice daily\n- Amoxicillin 250mg - three times daily\n- Vitamin D 1000IU - once daily");
        medicationArea.setStyle("-fx-font-size: 12; -fx-control-inner-background: rgba(255,255,255,0.9); -fx-padding: 10;");

        Text notesLabel = new Text("Additional Notes:");
        notesLabel.setFont(Font.font("Jost", 14));
        notesLabel.setFill(Color.WHITE);

        TextArea notesArea = new TextArea();
        notesArea.setPrefHeight(80);
        notesArea.setWrapText(true);
        notesArea.setPromptText("Any allergies or special instructions...");
        notesArea.setStyle("-fx-font-size: 12; -fx-control-inner-background: rgba(255,255,255,0.9); -fx-padding: 10;");

        Button submitButton = new Button("Submit Pharmacy Request");
        submitButton.setStyle("""
                -fx-font-size: 14;
                -fx-padding: 12 30 12 30;
                -fx-background-color: #00f5ff;
                -fx-text-fill: #0d3a24;
                -fx-font-weight: bold;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """);

        Label feedbackLabel = new Label();

        submitButton.setOnAction(e -> {
            if (medicationArea.getText().isEmpty()) {
                feedbackLabel.setText("⚠️ Please enter medications");
                feedbackLabel.setTextFill(Color.web("#ffeb3b"));
            } else {
                boolean success = submitPharmacyRequest(medicationArea.getText(), notesArea.getText());
                if (success) {
                    feedbackLabel.setText("✅ Request submitted successfully!");
                    feedbackLabel.setTextFill(Color.web("#00ff00"));
                    submitButton.setDisable(true);
                } else {
                    feedbackLabel.setText("❌ Failed to submit request");
                    feedbackLabel.setTextFill(Color.web("#ff0000"));
                }
            }
        });

        feedbackLabel.setFont(Font.font("Jost", 12));

        formBox.getChildren().addAll(
                medicationLabel, medicationArea,
                notesLabel, notesArea,
                submitButton, feedbackLabel
        );

        return formBox;
    }

    private boolean submitPharmacyRequest(String medications, String notes) {
        String sql = "INSERT INTO pharmacy_requests (patient_id, medications, status) VALUES (?, ?, 'Pending')";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            pstmt.setString(2, medications + " | Notes: " + notes);

            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
