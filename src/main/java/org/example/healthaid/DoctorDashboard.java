package org.example.healthaid;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class DoctorDashboard {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Scene scene;
    private final HelloApplication mainApp;
    private String userName;
    private String userRole;

    public DoctorDashboard(HelloApplication mainApp) {
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
        header.setStyle("-fx-background-color: #0f4c2f; -fx-padding: 15;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);
        header.setPrefHeight(70);

        Text title = new Text("Doctor Portal");
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

            Text nameLabel = new Text("Doctor: " + userName);
            nameLabel.setFont(Font.font("Jost", 18));
            nameLabel.setFill(Color.WHITE);

            Text roleLabel = new Text("Role: " + userRole);
            roleLabel.setFont(Font.font("Jost", 16));
            roleLabel.setFill(Color.web("#00f5ff"));

            userDetailsBox.getChildren().addAll(nameLabel, roleLabel);
            content.getChildren().add(userDetailsBox);
        }

        Text title = new Text("Doctor Dashboard");
        title.setFont(Font.font("Jost", 36));
        title.setFill(Color.WHITE);

        content.getChildren().addAll(
                title,
                createQuickStatsSection(),
                createTodayAppointmentsSection(),
                createPatientsListSection()
        );
        return content;
    }

    private VBox createQuickStatsSection() {
        DoctorStats stats = loadDoctorStats();

        VBox section = new VBox();
        section.setSpacing(0);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.6); -fx-background-radius: 10;");

        HBox statsContainer = new HBox();
        statsContainer.setSpacing(0);
        statsContainer.setAlignment(Pos.CENTER);

        statsContainer.getChildren().addAll(
                createStatCard("Total Patients", String.valueOf(stats.totalPatients()), "#157a52"),
                createStatCard("Today's Appointments", String.valueOf(stats.todayAppointments()), "#1b6e55"),
                createStatCard("Upcoming Appointments", String.valueOf(stats.upcomingAppointments()), "#0f4c2f"),
                createStatCard("Past Appointments", String.valueOf(stats.pastAppointments()), "#0d3a24")
        );

        section.getChildren().add(statsContainer);
        return section;
    }

    private VBox createStatCard(String label, String value, String bgColor) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: " + bgColor + ";");
        card.setPadding(new Insets(20));
        card.setSpacing(8);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(card, Priority.ALWAYS);

        Text val = new Text(value);
        val.setFont(Font.font("Jost", 32));
        val.setFill(Color.WHITE);

        Text lbl = new Text(label);
        lbl.setFont(Font.font("Jost", 13));
        lbl.setFill(Color.web("rgba(255,255,255,0.8)"));

        card.getChildren().addAll(val, lbl);
        return card;
    }

    private VBox createTodayAppointmentsSection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));

        Text sectionTitle = new Text("📅 Today's Appointments");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        VBox appointmentsList = new VBox();
        appointmentsList.setSpacing(10);

        List<DoctorAppointment> appointments = loadAppointments("""
                SELECT ar.reason,
                       ar.appointment_date,
                       ar.status,
                       u.full_name AS patient_name
                FROM appointment_requests ar
                LEFT JOIN users u ON ar.patient_id = u.id
                WHERE ar.doctor_id = ?
                  AND date(ar.appointment_date) = date('now')
                ORDER BY ar.appointment_date
                """);

        if (appointments.isEmpty()) {
            Text emptyText = new Text("No appointments scheduled for today.");
            emptyText.setFont(Font.font("Jost", 13));
            emptyText.setFill(Color.web("rgba(255,255,255,0.8)"));
            appointmentsList.getChildren().add(emptyText);
        } else {
            for (DoctorAppointment appointment : appointments) {
                appointmentsList.getChildren().add(createAppointmentItem(
                        appointment.patientName(),
                        appointment.reason(),
                        formatTime(appointment.appointmentDateTime()),
                        appointment.status()
                ));
            }
        }

        section.getChildren().addAll(sectionTitle, appointmentsList);
        return section;
    }

    private HBox createAppointmentItem(String patient, String reason, String time, String status) {
        HBox item = new HBox();
        item.setStyle("-fx-background-color: rgba(29, 184, 145, 0.3); -fx-background-radius: 8; -fx-padding: 12;");
        item.setSpacing(15);
        item.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox();
        info.setSpacing(3);

        Text patientName = new Text(patient);
        patientName.setFont(Font.font("Jost", 13));
        patientName.setFill(Color.WHITE);

        Text appointmentReason = new Text(reason);
        appointmentReason.setFont(Font.font("Jost", 11));
        appointmentReason.setFill(Color.web("rgba(255,255,255,0.7)"));

        info.getChildren().addAll(patientName, appointmentReason);

        Text timeText = new Text(time);
        timeText.setFont(Font.font("Jost", 12));
        timeText.setFill(Color.web("#00f5ff"));

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text statusText = new Text(status);
        statusText.setFont(Font.font("Jost", 11));
        statusText.setFill(Color.web(statusColor(status)));

        Button viewButton = new Button("View Records");
        viewButton.setStyle("-fx-font-size: 11; -fx-padding: 6 12 6 12; -fx-background-color: #00f5ff; -fx-text-fill: #0a192f; -fx-border-radius: 3; -fx-background-radius: 3;");
        viewButton.setOnAction(e -> mainApp.navigateToMedicalRecords());

        item.getChildren().addAll(info, timeText, spacer, statusText, viewButton);
        return item;
    }

    private VBox createPatientsListSection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));

        Text sectionTitle = new Text("👥 My Patients");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        VBox patientsList = new VBox();
        patientsList.setSpacing(8);

        List<DoctorPatient> patients = loadDoctorPatients();
        if (patients.isEmpty()) {
            Text emptyText = new Text("No appointment patients found for this doctor.");
            emptyText.setFont(Font.font("Jost", 12));
            emptyText.setFill(Color.web("rgba(255,255,255,0.8)"));
            patientsList.getChildren().add(emptyText);
        } else {
            for (DoctorPatient patient : patients) {
                Text patientText = new Text("• " + patient.patientName() + " - " + patient.dateLabel() + " - " + patient.timeLabel() + " - " + patient.status());
                patientText.setFont(Font.font("Jost", 12));
                patientText.setFill(Color.web("rgba(255,255,255,0.9)"));
                patientsList.getChildren().add(patientText);
            }
        }

        section.getChildren().addAll(sectionTitle, patientsList);
        return section;
    }

    private List<DoctorPatient> loadDoctorPatients() {
        List<DoctorPatient> patients = new ArrayList<>();
        List<DoctorAppointment> appointments = loadAppointments("""
                SELECT ar.reason,
                       ar.appointment_date,
                       ar.status,
                       u.full_name AS patient_name
                FROM appointment_requests ar
                LEFT JOIN users u ON ar.patient_id = u.id
                WHERE ar.doctor_id = ?
                  AND UPPER(COALESCE(ar.status, '')) <> 'REJECTED'
                ORDER BY ar.appointment_date
                """);

        for (DoctorAppointment appointment : appointments) {
            LocalDate date = parseDate(appointment.appointmentDateTime());
            String bucket = appointmentBucket(date);
            patients.add(new DoctorPatient(
                    appointment.patientName(),
                    bucket,
                    formatDate(appointment.appointmentDateTime()),
                    appointment.status()
            ));
        }
        return patients;
    }

    private List<DoctorAppointment> loadAppointments(String sql) {
        List<DoctorAppointment> appointments = new ArrayList<>();
        Integer doctorId = mainApp.getCurrentUserId();
        if (doctorId == null) {
            return appointments;
        }

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(new DoctorAppointment(
                            safeText(rs.getString("patient_name"), "Unknown Patient"),
                            safeText(rs.getString("reason"), "General Visit"),
                            rs.getString("appointment_date"),
                            safeText(rs.getString("status"), "PENDING").toUpperCase()
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return appointments;
    }

    private DoctorStats loadDoctorStats() {
        Integer doctorId = mainApp.getCurrentUserId();
        if (doctorId == null) {
            return new DoctorStats(0, 0, 0, 0);
        }

        String sql = """
                SELECT COUNT(DISTINCT ar.patient_id) AS total_patients,
                       SUM(CASE WHEN date(ar.appointment_date) = date('now') THEN 1 ELSE 0 END) AS today_appointments,
                       SUM(CASE WHEN date(ar.appointment_date) > date('now') THEN 1 ELSE 0 END) AS upcoming_appointments,
                       SUM(CASE WHEN date(ar.appointment_date) < date('now') THEN 1 ELSE 0 END) AS past_appointments
                FROM appointment_requests ar
                WHERE ar.doctor_id = ?
                """;

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new DoctorStats(
                            rs.getInt("total_patients"),
                            rs.getInt("today_appointments"),
                            rs.getInt("upcoming_appointments"),
                            rs.getInt("past_appointments")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new DoctorStats(0, 0, 0, 0);
    }

    private String formatDate(String appointmentDateTime) {
        if (appointmentDateTime == null || appointmentDateTime.isBlank() || appointmentDateTime.length() < 10) {
            return "Date not set";
        }
        return appointmentDateTime.substring(0, 10);
    }

    private String formatTime(String appointmentDateTime) {
        if (appointmentDateTime == null || appointmentDateTime.isBlank()) {
            return "TBD";
        }
        if (appointmentDateTime.length() >= 16) {
            return appointmentDateTime.substring(11);
        }
        return "TBD";
    }

    private LocalDate parseDate(String appointmentDateTime) {
        if (appointmentDateTime == null || appointmentDateTime.isBlank() || appointmentDateTime.length() < 10) {
            return null;
        }

        try {
            return LocalDate.parse(appointmentDateTime.substring(0, 10), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private String appointmentBucket(LocalDate appointmentDate) {
        if (appointmentDate == null) {
            return "Scheduled";
        }

        LocalDate today = LocalDate.now();
        if (appointmentDate.isBefore(today)) {
            return "Previous";
        }
        if (appointmentDate.isEqual(today)) {
            return "Current";
        }
        return "Future";
    }

    private String statusColor(String status) {
        if ("APPROVED".equalsIgnoreCase(status)) {
            return "#00ff00";
        }
        if ("REJECTED".equalsIgnoreCase(status)) {
            return "#ff6b6b";
        }
        return "#ffeb3b";
    }

    private String safeText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private record DoctorAppointment(String patientName, String reason, String appointmentDateTime, String status) {
    }

    private record DoctorPatient(String patientName, String timeLabel, String dateLabel, String status) {
    }

    private record DoctorStats(int totalPatients, int todayAppointments, int upcomingAppointments, int pastAppointments) {
    }
}
