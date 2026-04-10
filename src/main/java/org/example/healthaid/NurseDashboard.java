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
import java.util.ArrayList;
import java.util.List;

public class NurseDashboard {
    private Scene scene;
    private final HelloApplication mainApp;
    private String userName;
    private String userRole;

    public NurseDashboard(HelloApplication mainApp) {
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
        header.setStyle("-fx-background-color: #1b6e55; -fx-padding: 15;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);
        header.setPrefHeight(70);

        Text title = new Text("Nurse Portal");
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

            Text nameLabel = new Text("Nurse: " + userName);
            nameLabel.setFont(Font.font("Jost", 18));
            nameLabel.setFill(Color.WHITE);

            Text roleLabel = new Text("Role: " + userRole);
            roleLabel.setFont(Font.font("Jost", 16));
            roleLabel.setFill(Color.web("#00f5ff"));

            userDetailsBox.getChildren().addAll(nameLabel, roleLabel);
            content.getChildren().add(userDetailsBox);
        }

        Text title = new Text("Nurse Dashboard");
        title.setFont(Font.font("Jost", 36));
        title.setFill(Color.WHITE);

        content.getChildren().addAll(
                title,
                createShiftAssignmentsSection(),
                createTodayAppointmentQueueSection()
        );
        return content;
    }

    private VBox createShiftAssignmentsSection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));

        Text sectionTitle = new Text("📋 Shift Assignments");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        VBox assignmentsList = new VBox();
        assignmentsList.setSpacing(10);

        List<ShiftAssignment> assignments = loadShiftAssignments();
        if (assignments.isEmpty()) {
            Text emptyText = new Text("No shift assignments found.");
            emptyText.setFont(Font.font("Jost", 13));
            emptyText.setFill(Color.web("rgba(255,255,255,0.8)"));
            assignmentsList.getChildren().add(emptyText);
        } else {
            for (ShiftAssignment assignment : assignments) {
                Text line = new Text("• " + assignment.shiftDate() + " - " + assignment.role() + " - " + assignment.shiftInfo());
                line.setFont(Font.font("Jost", 12));
                line.setFill(Color.web("rgba(255,255,255,0.9)"));
                assignmentsList.getChildren().add(line);
            }
        }

        section.getChildren().addAll(sectionTitle, assignmentsList);
        return section;
    }

    private VBox createTodayAppointmentQueueSection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));

        Text sectionTitle = new Text("👥 Today's Appointment Queue");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        VBox queueList = new VBox();
        queueList.setSpacing(10);

        List<QueueItem> queueItems = loadTodayAppointmentQueue();
        if (queueItems.isEmpty()) {
            Text emptyText = new Text("No appointment queue items found for today.");
            emptyText.setFont(Font.font("Jost", 13));
            emptyText.setFill(Color.web("rgba(255,255,255,0.8)"));
            queueList.getChildren().add(emptyText);
        } else {
            for (QueueItem item : queueItems) {
                Text line = new Text("• " + item.patientName() + " | " + item.doctorName() + " | " + item.reason() + " | " + item.status());
                line.setFont(Font.font("Jost", 12));
                line.setFill(Color.web("rgba(255,255,255,0.9)"));
                queueList.getChildren().add(line);
            }
        }

        section.getChildren().addAll(sectionTitle, queueList);
        return section;
    }

    private List<ShiftAssignment> loadShiftAssignments() {
        List<ShiftAssignment> assignments = new ArrayList<>();
        Integer userId = mainApp.getCurrentUserId();
        if (userId == null) {
            return assignments;
        }

        String sql = """
                SELECT role, shift_date
                FROM staff_assignments
                WHERE staff_id = ?
                ORDER BY shift_date DESC
                """;

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    assignments.add(new ShiftAssignment(
                            safeText(rs.getString("shift_date"), "No date"),
                            safeText(rs.getString("role"), "NURSE"),
                            safeText(rs.getString("shift_date"), "Unspecified")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return assignments;
    }

    private List<QueueItem> loadTodayAppointmentQueue() {
        List<QueueItem> queue = new ArrayList<>();
        String sql = """
                SELECT ar.reason,
                       ar.status,
                       patient.full_name AS patient_name,
                       doctor.full_name AS doctor_name
                FROM appointment_requests ar
                LEFT JOIN users patient ON patient.id = ar.patient_id
                LEFT JOIN users doctor ON doctor.id = ar.doctor_id
                WHERE date(ar.appointment_date) = date('now')
                  AND UPPER(COALESCE(ar.status, '')) IN ('PENDING', 'APPROVED')
                ORDER BY ar.appointment_date
                """;

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                queue.add(new QueueItem(
                        safeText(rs.getString("patient_name"), "Unknown Patient"),
                        safeText(rs.getString("doctor_name"), "Unknown Doctor"),
                        safeText(rs.getString("reason"), "General Visit"),
                        safeText(rs.getString("status"), "PENDING").toUpperCase()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return queue;
    }

    private String safeText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private record ShiftAssignment(String shiftDate, String role, String shiftInfo) {
    }

    private record QueueItem(String patientName, String doctorName, String reason, String status) {
    }
}
