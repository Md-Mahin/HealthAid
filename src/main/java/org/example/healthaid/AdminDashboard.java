package org.example.healthaid;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
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

public class AdminDashboard {
    private Scene scene;
    private final HelloApplication mainApp;
    private String userName;
    private String userRole;

    public AdminDashboard(HelloApplication mainApp) {
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
        header.setStyle("-fx-background-color: #1b7d61; -fx-padding: 15;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);
        header.setPrefHeight(70);

        Text title = new Text("Admin Dashboard");
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
                -fx-background-color: #a02817;
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

            Text nameLabel = new Text("Administrator: " + userName);
            nameLabel.setFont(Font.font("Jost", 18));
            nameLabel.setFill(Color.WHITE);

            Text roleLabel = new Text("Role: " + userRole);
            roleLabel.setFont(Font.font("Jost", 16));
            roleLabel.setFill(Color.web("#00f5ff"));

            userDetailsBox.getChildren().addAll(nameLabel, roleLabel);
            content.getChildren().add(userDetailsBox);
        }

        Text title = new Text("Hospital Administration");
        title.setFont(Font.font("Jost", 36));
        title.setFill(Color.WHITE);

        content.getChildren().addAll(
                title,
                createStatisticsSection(),
                createStaffAssignmentSection(),
                createUserManagementSection(),
                createRecentAssignmentsSection()
        );
        return content;
    }

    private VBox createStatisticsSection() {
        AdminStats stats = loadStats();

        VBox section = new VBox();
        section.setSpacing(0);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.6); -fx-background-radius: 10;");

        HBox statsContainer = new HBox();
        statsContainer.setSpacing(0);
        statsContainer.setAlignment(Pos.CENTER);

        statsContainer.getChildren().addAll(
                createStatCard("Total Patients", String.valueOf(stats.totalPatients()), "#157a52"),
                createStatCard("Total Staff", String.valueOf(stats.totalStaff()), "#1b6e55"),
                createStatCard("Appointments", String.valueOf(stats.totalAppointments()), "#0f4c2f"),
                createStatCard("Pending Requests", String.valueOf(stats.pendingRequests()), "#0d3a24")
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

    private VBox createStaffAssignmentSection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setStyle("-fx-background-color: rgba(0, 150, 136, 0.7); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));

        Text sectionTitle = new Text("🔑 Staff Assignment");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        HBox formRow = new HBox(20);
        formRow.setAlignment(Pos.CENTER_LEFT);

        Label staffLabel = new Label("Select Staff:");
        staffLabel.setTextFill(Color.WHITE);
        staffLabel.setFont(Font.font("Jost", 12));

        ComboBox<StaffOption> staffCombo = new ComboBox<>();
        staffCombo.getItems().addAll(loadAssignableStaff());
        staffCombo.setPromptText("Choose staff member");
        styleControl(staffCombo, 240);

        Label roleLabel = new Label("Role:");
        roleLabel.setTextFill(Color.WHITE);
        roleLabel.setFont(Font.font("Jost", 12));

        Label roleValueLabel = new Label("Select staff member");
        roleValueLabel.setTextFill(Color.web("#00f5ff"));
        roleValueLabel.setFont(Font.font("Jost", 12));

        Label dateLabel = new Label("Shift Date:");
        dateLabel.setTextFill(Color.WHITE);
        dateLabel.setFont(Font.font("Jost", 12));

        DatePicker datePicker = new DatePicker();
        datePicker.setPrefWidth(160);
        styleControl(datePicker, 160);

        Label shiftLabel = new Label("Shift:");
        shiftLabel.setTextFill(Color.WHITE);
        shiftLabel.setFont(Font.font("Jost", 12));

        ComboBox<String> shiftCombo = new ComboBox<>();
        shiftCombo.getItems().addAll("Morning (6:00 AM - 2:00 PM)", "Afternoon (2:00 PM - 10:00 PM)", "Night (10:00 PM - 6:00 AM)");
        shiftCombo.setPromptText("Choose shift");
        styleControl(shiftCombo, 260);

        formRow.getChildren().addAll(staffLabel, staffCombo, roleLabel, roleValueLabel, dateLabel, datePicker, shiftLabel, shiftCombo);

        staffCombo.setOnAction(event -> {
            StaffOption selected = staffCombo.getValue();
            if (selected == null) {
                roleValueLabel.setText("Select staff member");
                return;
            }
            roleValueLabel.setText(selected.role());
        });

        Button assignButton = new Button("Assign Staff");
        assignButton.setStyle("""
                -fx-font-size: 12;
                -fx-padding: 10 20 10 20;
                -fx-background-color: #00ff00;
                -fx-text-fill: #0a192f;
                -fx-font-weight: bold;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """);

        Label feedbackLabel = new Label();
        feedbackLabel.setFont(Font.font("Jost", 12));

        assignButton.setOnAction(e -> {
            if (staffCombo.getValue() == null || datePicker.getValue() == null || shiftCombo.getValue() == null) {
                feedbackLabel.setText("Please fill in all fields.");
                feedbackLabel.setTextFill(Color.web("#ffeb3b"));
                return;
            }

            StaffOption selectedStaff = staffCombo.getValue();
            boolean success = assignStaff(
                    selectedStaff,
                    datePicker.getValue().toString(),
                    shiftCombo.getValue()
            );

            if (success) {
                feedbackLabel.setText("Staff assigned successfully.");
                feedbackLabel.setTextFill(Color.web("#00ff00"));
                staffCombo.getItems().setAll(loadAssignableStaff());
                staffCombo.getSelectionModel().clearSelection();
                roleValueLabel.setText("Select staff member");
                datePicker.setValue(null);
                shiftCombo.getSelectionModel().clearSelection();
            } else {
                feedbackLabel.setText("Failed to assign staff.");
                feedbackLabel.setTextFill(Color.web("#ff6b6b"));
            }
        });

        HBox buttonBox = new HBox(10, assignButton, feedbackLabel);
        buttonBox.setAlignment(Pos.CENTER);

        section.getChildren().addAll(sectionTitle, formRow, buttonBox);
        return section;
    }

    private VBox createUserManagementSection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));

        Text sectionTitle = new Text("👥 User Management");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        VBox usersList = new VBox();
        usersList.setSpacing(8);

        List<UserRow> users = loadUsers();
        if (users.isEmpty()) {
            Text emptyText = new Text("No users found.");
            emptyText.setFont(Font.font("Jost", 13));
            emptyText.setFill(Color.web("rgba(255,255,255,0.8)"));
            usersList.getChildren().add(emptyText);
        } else {
            for (UserRow user : users) {
                usersList.getChildren().add(createUserItem(user));
            }
        }

        section.getChildren().addAll(sectionTitle, usersList);
        return section;
    }

    private VBox createRecentAssignmentsSection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));

        Text sectionTitle = new Text("🗂 Recent Staff Assignments");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        VBox list = new VBox();
        list.setSpacing(8);

        List<AssignmentRow> assignments = loadRecentAssignments();
        if (assignments.isEmpty()) {
            Text emptyText = new Text("No staff assignments found.");
            emptyText.setFont(Font.font("Jost", 13));
            emptyText.setFill(Color.web("rgba(255,255,255,0.8)"));
            list.getChildren().add(emptyText);
        } else {
            for (AssignmentRow assignment : assignments) {
                Text line = new Text("• " + assignment.staffName() + " | " + assignment.role() + " | " + assignment.shiftDate() + " | By: " + assignment.assignedBy());
                line.setFont(Font.font("Jost", 12));
                line.setFill(Color.web("rgba(255,255,255,0.9)"));
                list.getChildren().add(line);
            }
        }

        section.getChildren().addAll(sectionTitle, list);
        return section;
    }

    private void styleControl(Object control, double width) {
        if (control instanceof ComboBox<?> comboBox) {
            comboBox.setStyle("""
                    -fx-font-size: 12;
                    -fx-padding: 10;
                    -fx-background-color: rgba(255,255,255,0.9);
                    -fx-border-radius: 5;
                    -fx-background-radius: 5;
                    """);
            comboBox.setPrefWidth(width);
        } else if (control instanceof DatePicker datePicker) {
            datePicker.setStyle("""
                    -fx-font-size: 12;
                    -fx-padding: 10;
                    -fx-background-color: rgba(255,255,255,0.9);
                    -fx-border-radius: 5;
                    -fx-background-radius: 5;
                    """);
        }
    }

    private boolean assignStaff(StaffOption staff, String shiftDate, String shiftType) {
        Integer adminId = mainApp.getCurrentUserId();
        if (adminId == null) {
            return false;
        }

        String sql = """
                INSERT INTO staff_assignments (staff_id, staff_name, role, shift_date, assigned_by)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, staff.id());
            pstmt.setString(2, staff.name());
            pstmt.setString(3, staff.role());
            pstmt.setString(4, shiftDate + " - " + shiftType);
            pstmt.setInt(5, adminId);
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<StaffOption> loadAssignableStaff() {
        List<StaffOption> staff = new ArrayList<>();
        String sql = """
                SELECT id, full_name, role
                FROM users
                WHERE UPPER(role) IN ('DOCTOR', 'NURSE', 'STAFF', 'PHARMACIST', 'LAB_TECHNICIAN')
                ORDER BY full_name
                """;

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                staff.add(new StaffOption(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("role")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return staff;
    }

    private List<UserRow> loadUsers() {
        List<UserRow> users = new ArrayList<>();
        String sql = """
                SELECT id, full_name, phone, role
                FROM users
                ORDER BY full_name
                """;

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                users.add(new UserRow(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("role")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    private List<AssignmentRow> loadRecentAssignments() {
        List<AssignmentRow> assignments = new ArrayList<>();
        String sql = """
                SELECT sa.staff_name,
                       sa.role,
                       sa.shift_date,
                       admin.full_name AS assigned_by
                FROM staff_assignments sa
                LEFT JOIN users admin ON admin.id = sa.assigned_by
                ORDER BY sa.created_at DESC
                LIMIT 10
                """;

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                assignments.add(new AssignmentRow(
                        rs.getString("staff_name"),
                        rs.getString("role"),
                        rs.getString("shift_date"),
                        rs.getString("assigned_by")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return assignments;
    }

    private HBox createUserItem(UserRow user) {
        HBox item = new HBox();
        item.setStyle("-fx-background-color: rgba(29, 184, 145, 0.3); -fx-background-radius: 8; -fx-padding: 12;");
        item.setSpacing(15);
        item.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox();
        info.setSpacing(3);

        Text nameText = new Text(user.name());
        nameText.setFont(Font.font("Jost", 12));
        nameText.setFill(Color.WHITE);

        Text phoneText = new Text(user.phone());
        phoneText.setFont(Font.font("Jost", 11));
        phoneText.setFill(Color.web("rgba(255,255,255,0.7)"));

        info.getChildren().addAll(nameText, phoneText);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text roleText = new Text(user.role());
        roleText.setFont(Font.font("Jost", 11));
        roleText.setFill(Color.web("#00f5ff"));

        item.getChildren().addAll(info, spacer, roleText);
        return item;
    }

    private AdminStats loadStats() {
        int totalPatients = count("SELECT COUNT(*) AS count FROM users WHERE UPPER(role) = 'PATIENT'");
        int totalStaff = count("""
                SELECT COUNT(*) AS count
                FROM users
                WHERE UPPER(role) IN ('DOCTOR', 'NURSE', 'STAFF', 'PHARMACIST', 'LAB_TECHNICIAN', 'ADMIN')
                """);
        int totalAppointments = count("SELECT COUNT(*) AS count FROM appointment_requests");
        int pendingRequests = count("""
                SELECT
                    (
                        SELECT COUNT(*) FROM appointment_requests WHERE UPPER(COALESCE(status, '')) = 'PENDING'
                    ) +
                    (
                        SELECT COUNT(*) FROM operation_requests WHERE UPPER(COALESCE(status, '')) = 'PENDING'
                    ) +
                    (
                        SELECT COUNT(*) FROM pharmacy_requests WHERE UPPER(COALESCE(status, '')) = 'PENDING'
                    ) +
                    (
                        SELECT COUNT(*) FROM billing_requests WHERE UPPER(COALESCE(status, '')) = 'PENDING'
                    ) +
                    (
                        SELECT COUNT(*) FROM bed_requests WHERE UPPER(COALESCE(status, '')) = 'PENDING'
                    ) +
                    (
                        SELECT COUNT(*) FROM lab_requests WHERE UPPER(COALESCE(status, '')) = 'PENDING'
                    ) AS count
                """);

        return new AdminStats(totalPatients, totalStaff, totalAppointments, pendingRequests);
    }

    private int count(String sql) {
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private record AdminStats(int totalPatients, int totalStaff, int totalAppointments, int pendingRequests) {
    }

    private record StaffOption(int id, String name, String role) {
        @Override
        public String toString() {
            return name + " (" + role + ")";
        }
    }

    private record UserRow(int id, String name, String phone, String role) {
    }

    private record AssignmentRow(String staffName, String role, String shiftDate, String assignedBy) {
    }
}
