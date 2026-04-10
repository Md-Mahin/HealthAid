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
import java.util.ArrayList;
import java.util.List;

public class AppointmentRequestPage {
    private Scene scene;
    private HelloApplication mainApp;
    private int patientId;
    private String patientName;
    private final List<DoctorOption> doctors = new ArrayList<>();

    public AppointmentRequestPage(HelloApplication mainApp, int patientId, String patientName) {
        this.mainApp = mainApp;
        this.patientId = patientId;
        this.patientName = patientName;
        loadDoctors();
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

        Text title = new Text("📋 Request Appointment");
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

        // Patient Info
        VBox patientInfo = createPatientInfoBox();
        content.getChildren().add(patientInfo);

        // Request Form
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

        // Doctor Selection
        Text doctorLabel = new Text("Select Doctor:");
        doctorLabel.setFont(Font.font("Jost", 14));
        doctorLabel.setFill(Color.WHITE);

        ComboBox<DoctorOption> doctorCombo = new ComboBox<>();
        doctorCombo.getItems().addAll(doctors);
        doctorCombo.setPromptText(doctors.isEmpty() ? "No doctors available" : "Choose a doctor");
        doctorCombo.setStyle("-fx-font-size: 12; -fx-padding: 10;");

        // Date Selection
        Text dateLabel = new Text("Appointment Date:");
        dateLabel.setFont(Font.font("Jost", 14));
        dateLabel.setFill(Color.WHITE);

        DatePicker datePicker = new DatePicker();
        datePicker.setStyle("-fx-font-size: 12; -fx-padding: 10;");

        // Reason
        Text reasonLabel = new Text("Reason for Appointment:");
        reasonLabel.setFont(Font.font("Jost", 14));
        reasonLabel.setFill(Color.WHITE);

        TextArea reasonTextArea = new TextArea();
        reasonTextArea.setPrefHeight(100);
        reasonTextArea.setWrapText(true);
        reasonTextArea.setPromptText("Describe the reason for your appointment...");
        reasonTextArea.setStyle("-fx-font-size: 12; -fx-control-inner-background: rgba(255,255,255,0.9); -fx-padding: 10;");

        // Submit Button
        Button submitButton = new Button("Submit Request");
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
            if (doctorCombo.getValue() == null || datePicker.getValue() == null || reasonTextArea.getText().isEmpty()) {
                feedbackLabel.setText("⚠️ Please fill in all fields");
                feedbackLabel.setTextFill(Color.web("#ffeb3b"));
            } else {
                boolean success = submitAppointmentRequest(
                        doctorCombo.getValue(),
                        datePicker.getValue().toString(),
                        reasonTextArea.getText()
                );
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
                doctorLabel, doctorCombo,
                dateLabel, datePicker,
                reasonLabel, reasonTextArea,
                submitButton, feedbackLabel
        );

        return formBox;
    }

    private void loadDoctors() {
        doctors.clear();
        String sql = """
                SELECT id, full_name
                FROM users
                WHERE UPPER(role) = 'DOCTOR'
                ORDER BY full_name
                """;

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                doctors.add(new DoctorOption(rs.getInt("id"), rs.getString("full_name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean submitAppointmentRequest(DoctorOption doctor, String appointmentDate, String reason) {
        String sql = "INSERT INTO appointment_requests (patient_id, doctor_id, appointment_date, reason, status) VALUES (?, ?, ?, ?, 'Pending')";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            pstmt.setInt(2, doctor.id());
            pstmt.setString(3, appointmentDate);
            pstmt.setString(4, reason);

            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private record DoctorOption(int id, String name) {
        @Override
        public String toString() {
            return name;
        }
    }
}
