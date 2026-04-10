package org.example.healthaid;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PatientDashboard {
    private Scene scene;
    private final HelloApplication mainApp;
    private String userName;
    private String userRole;

    public PatientDashboard(HelloApplication mainApp) {
        this.mainApp = mainApp;
        this.scene = createScene();
    }

    public void setUserDetails(String name, String role) {
        this.userName = name;
        this.userRole = role;
        this.scene = createScene();
    }

    public Scene getScene() {
        return scene;
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
        header.setStyle("-fx-background-color: #1db891; -fx-padding: 15;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);
        header.setPrefHeight(70);

        Text title = new Text("Patient Portal");
        title.setFont(Font.font("Jost", 28));
        title.setFill(Color.WHITE);
        header.getChildren().add(title);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().add(spacer);

        Button backButton = new Button("Back");
        backButton.setStyle("""
                -fx-font-size: 12;
                -fx-padding: 8 15 8 15;
                -fx-background-color: #c23b22;
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

        if (userName != null && userRole != null) {
            VBox userDetailsBox = new VBox();
            userDetailsBox.setSpacing(10);
            userDetailsBox.setPadding(new Insets(20));
            userDetailsBox.setStyle("-fx-background-color: rgba(0, 245, 255, 0.1); -fx-background-radius: 10;");
            userDetailsBox.setAlignment(Pos.CENTER);

            Label nameLabel = new Label("Name: " + userName);
            nameLabel.setFont(Font.font("Jost", 18));
            nameLabel.setTextFill(Color.WHITE);

            Label roleLabel = new Label("Role: " + userRole);
            roleLabel.setFont(Font.font("Jost", 16));
            roleLabel.setTextFill(Color.web("#00f5ff"));

            userDetailsBox.getChildren().addAll(nameLabel, roleLabel);
            content.getChildren().add(userDetailsBox);
        }

        Text title = new Text("Welcome to Your Patient Dashboard");
        title.setFont(Font.font("Jost", 36));
        title.setFill(Color.WHITE);

        content.getChildren().addAll(
                title,
                createPatientRequestsSection(),
                createAppointmentsSection(),
                createRequestStatusSection()
        );
        return content;
    }

    private VBox createPatientRequestsSection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setStyle("-fx-background-color: rgba(0, 150, 136, 0.9); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));

        Text sectionTitle = new Text("📝 Make a Request");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        HBox buttonsRow1 = new HBox(15);
        buttonsRow1.setAlignment(Pos.CENTER_LEFT);
        HBox buttonsRow2 = new HBox(15);
        buttonsRow2.setAlignment(Pos.CENTER_LEFT);

        Integer patientId = mainApp.getCurrentUserId();
        String patientName = userName == null ? "Current User" : userName;

        Button appointmentBtn = createRequestButton("📋 Appointment Request");
        appointmentBtn.setOnAction(e -> {
            if (patientId != null) {
                mainApp.navigateToAppointmentRequest(patientId, patientName);
            }
        });

        Button operationBtn = createRequestButton("🏥 Operation Request");
        operationBtn.setOnAction(e -> {
            if (patientId != null) {
                mainApp.navigateToOperations();
            }
        });

        Button pharmacyBtn = createRequestButton("💊 Pharmacy Request");
        pharmacyBtn.setOnAction(e -> {
            if (patientId != null) {
                mainApp.navigateToPharmacyManagement();
            }
        });

        Button billingBtn = createRequestButton("💳 Billing Request");
        billingBtn.setOnAction(e -> {
            if (patientId != null) {
                mainApp.navigateToBillingInsurance();
            }
        });

        Button bedBtn = createRequestButton("🛏️ Bed Request");
        bedBtn.setOnAction(e -> {
            if (patientId != null) {
                mainApp.navigateToBedManagement();
            }
        });

        buttonsRow1.getChildren().addAll(appointmentBtn, operationBtn, pharmacyBtn);
        buttonsRow2.getChildren().addAll(billingBtn, bedBtn);

        section.getChildren().addAll(sectionTitle, buttonsRow1, buttonsRow2);
        return section;
    }

    private Button createRequestButton(String text) {
        Button button = new Button(text);
        button.setStyle("""
                -fx-font-size: 12;
                -fx-padding: 10 15 10 15;
                -fx-background-color: #00d4ff;
                -fx-text-fill: #0d3a24;
                -fx-font-weight: bold;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """);
        return button;
    }

    private VBox createAppointmentsSection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));

        Text sectionTitle = new Text("📅 Your Appointments");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        VBox appointmentsList = new VBox();
        appointmentsList.setSpacing(10);

        List<PatientAppointment> appointments = loadAppointments();
        if (appointments.isEmpty()) {
            Text emptyText = new Text("No appointments found for the logged-in patient.");
            emptyText.setFont(Font.font("Jost", 13));
            emptyText.setFill(Color.web("rgba(255,255,255,0.8)"));
            appointmentsList.getChildren().add(emptyText);
        } else {
            for (PatientAppointment appointment : appointments) {
                appointmentsList.getChildren().add(createAppointmentItem(appointment));
            }
        }

        section.getChildren().addAll(sectionTitle, appointmentsList);
        return section;
    }

    private HBox createAppointmentItem(PatientAppointment appointment) {
        HBox item = new HBox();
        item.setStyle("-fx-background-color: rgba(29, 184, 145, 0.3); -fx-background-radius: 8; -fx-padding: 12;");
        item.setSpacing(15);
        item.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox();
        info.setSpacing(3);

        Text doctorName = new Text(appointment.doctorName());
        doctorName.setFont(Font.font("Jost", 13));
        doctorName.setFill(Color.WHITE);

        Text reason = new Text(appointment.reason());
        reason.setFont(Font.font("Jost", 11));
        reason.setFill(Color.web("rgba(255,255,255,0.7)"));

        Text dateTime = new Text(appointment.dateTime());
        dateTime.setFont(Font.font("Jost", 11));
        dateTime.setFill(Color.web("#00f5ff"));

        info.getChildren().addAll(doctorName, reason, dateTime);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text statusText = new Text(appointment.status());
        statusText.setFont(Font.font("Jost", 11));
        statusText.setFill(Color.web(statusColor(appointment.status())));

        //Button rescheduleBtn = new Button("Reschedule");
        //rescheduleBtn.setStyle("-fx-font-size: 11; -fx-padding: 6 12 6 12; -fx-background-color: #157a52; -fx-text-fill: white; -fx-border-radius: 3; -fx-background-radius: 3;");

        item.getChildren().addAll(info, spacer, statusText);
        return item;
    }

    private VBox createRequestStatusSection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));

        Text sectionTitle = new Text("📌 Recent Request Status");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        VBox statusList = new VBox();
        statusList.setSpacing(10);

        List<RequestSummary> summaries = loadRequestSummaries();
        if (summaries.isEmpty()) {
            Text emptyText = new Text("No recent requests found.");
            emptyText.setFont(Font.font("Jost", 13));
            emptyText.setFill(Color.web("rgba(255,255,255,0.8)"));
            statusList.getChildren().add(emptyText);
        } else {
            for (RequestSummary summary : summaries) {
                Text line = new Text("• " + summary.type() + " | " + summary.latestDate() + " | " + summary.latestStatus());
                line.setFont(Font.font("Jost", 12));
                line.setFill(Color.web("rgba(255,255,255,0.9)"));
                statusList.getChildren().add(line);
            }
        }

        section.getChildren().addAll(sectionTitle, statusList);
        return section;
    }

    private List<PatientAppointment> loadAppointments() {
        List<PatientAppointment> appointments = new ArrayList<>();
        Integer patientId = mainApp.getCurrentUserId();
        if (patientId == null) {
            return appointments;
        }

        String sql = """
                SELECT ar.appointment_date,
                       ar.reason,
                       ar.status,
                       ar.serial_number,
                       doctor.full_name AS doctor_name
                FROM appointment_requests ar
                LEFT JOIN users doctor ON doctor.id = ar.doctor_id
                WHERE ar.patient_id = ?
                ORDER BY ar.appointment_date DESC
                """;

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(new PatientAppointment(
                            safeText(rs.getString("doctor_name"), "Unknown Doctor"),
                            safeText(rs.getString("reason"), "General Visit"),
                            safeText(rs.getString("appointment_date"), "Date not set"),
                            safeText(rs.getString("status"), "PENDING").toUpperCase(),
                            rs.getObject("serial_number") == null ? "-" : String.valueOf(rs.getInt("serial_number"))
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return appointments;
    }

    private List<RequestSummary> loadRequestSummaries() {
        List<RequestSummary> summaries = new ArrayList<>();
        Integer patientId = mainApp.getCurrentUserId();
        if (patientId == null) {
            return summaries;
        }

        summaries.add(loadLatestRequestSummary("Appointment", """
                SELECT status, COALESCE(appointment_date, created_at) AS latest_date
                FROM appointment_requests
                WHERE patient_id = ?
                ORDER BY created_at DESC
                LIMIT 1
                """, patientId));
        summaries.add(loadLatestRequestSummary("Lab", """
                SELECT status, COALESCE(date, created_at) AS latest_date
                FROM lab_requests
                WHERE patient_id = ?
                ORDER BY created_at DESC
                LIMIT 1
                """, patientId));
        summaries.add(loadLatestRequestSummary("Operation", """
                SELECT status, COALESCE(date, created_at) AS latest_date
                FROM operation_requests
                WHERE patient_id = ?
                ORDER BY created_at DESC
                LIMIT 1
                """, patientId));
        summaries.add(loadLatestRequestSummary("Pharmacy", """
                SELECT status, COALESCE(date, created_at) AS latest_date
                FROM pharmacy_requests
                WHERE patient_id = ?
                ORDER BY created_at DESC
                LIMIT 1
                """, patientId));
        summaries.add(loadLatestRequestSummary("Billing", """
                SELECT status, COALESCE(date, created_at) AS latest_date
                FROM billing_requests
                WHERE patient_id = ?
                ORDER BY created_at DESC
                LIMIT 1
                """, patientId));
        summaries.add(loadLatestRequestSummary("Bed", """
                SELECT status, COALESCE(date, created_at) AS latest_date
                FROM bed_requests
                WHERE patient_id = ?
                ORDER BY created_at DESC
                LIMIT 1
                """, patientId));

        summaries.removeIf(summary -> summary == null);
        return summaries;
    }

    private RequestSummary loadLatestRequestSummary(String type, String sql, int patientId) {
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new RequestSummary(
                            type,
                            safeText(rs.getString("latest_date"), "N/A"),
                            safeText(rs.getString("status"), "PENDING").toUpperCase()
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String safeText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String statusColor(String status) {
        if ("APPROVED".equalsIgnoreCase(status) || "GIVEN".equalsIgnoreCase(status)) {
            return "#00ff00";
        }
        if ("REJECTED".equalsIgnoreCase(status)) {
            return "#ff6b6b";
        }
        return "#ffeb3b";
    }

    private record PatientAppointment(String doctorName, String reason, String dateTime, String status, String serialNumber) {
    }

    private record RequestSummary(String type, String latestDate, String latestStatus) {
    }
}
