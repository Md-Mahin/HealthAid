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
import javafx.scene.control.Button;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserProfilePage {
    private Scene scene;
    private HelloApplication mainApp;
    private String userName;
    private String userRole;
    private String userPhone;
    private String userFullName;

    public UserProfilePage(HelloApplication mainApp) {
        this.mainApp = mainApp;
        this.scene = createScene();
    }

    public void setUserDetails(String name, String role) {
        this.userName = name;
        this.userRole = role;
        this.userFullName = name;
        this.userPhone = name; // Will be updated from DB
        // Fetch full user details from database
        fetchUserDetailsFromDB();
        this.scene = createScene();
    }

    private void fetchUserDetailsFromDB() {
        String sql = "SELECT full_name, phone, role FROM users WHERE full_name = ? OR phone = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userName);
            pstmt.setString(2, userName);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                this.userFullName = rs.getString("full_name");
                this.userPhone = rs.getString("phone");
                this.userRole = rs.getString("role");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        header.setStyle("-fx-background-color: #1db891; -fx-padding: 15;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);
        header.setPrefHeight(70);

        Text title = new Text("User Profile");
        title.setFont(Font.font("Jost", 28));
        title.setFill(Color.WHITE);

        header.getChildren().add(title);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().add(spacer);

        Button backButton = new Button("Back Home");
        backButton.setStyle("""
                -fx-font-size: 12;
                -fx-padding: 8 15 8 15;
                -fx-background-color: #00d1d1;
                -fx-text-fill: #0a192f;
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

        Text pageTitle = new Text("Your Profile");
        pageTitle.setFont(Font.font("Jost", 40));
        pageTitle.setFill(Color.WHITE);

        VBox profileDetailsBox = createProfileDetailsBox();
        VBox roleSection = createRoleSpecificSection();
        VBox accountInfoBox = createAccountInfoBox();
        VBox actionButtonsBox = createActionButtonsBox();

        content.getChildren().addAll(pageTitle, profileDetailsBox, roleSection, accountInfoBox, actionButtonsBox);
        return content;
    }

    private VBox createProfileDetailsBox() {
        VBox box = new VBox();
        box.setSpacing(15);
        box.setPadding(new Insets(25));
        box.setStyle("-fx-background-color: rgba(0, 245, 255, 0.1); -fx-background-radius: 10;");

        Text sectionTitle = new Text("Personal Information");
        sectionTitle.setFont(Font.font("Jost", 24));
        sectionTitle.setFill(Color.WHITE);

        // Full Name
        HBox nameBox = createInfoRow("Full Name:", userFullName);

        // Phone Number
        HBox phoneBox = createInfoRow("Phone Number:", userPhone);

        // Role
        HBox roleBox = createInfoRow("Role:", userRole);

        // Member Since
        HBox memberBox = createInfoRow("Member Since:", "2024");

        box.getChildren().addAll(sectionTitle, nameBox, phoneBox, roleBox, memberBox);
        return box;
    }

    private HBox createInfoRow(String label, String value) {
        HBox row = new HBox();
        row.setSpacing(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 0, 10, 0));

        Text labelText = new Text(label);
        labelText.setFont(Font.font("Jost", 16));
        labelText.setFill(Color.web("#00f5ff"));

        Text valueText = new Text(value);
        valueText.setFont(Font.font("Jost", 16));
        valueText.setFill(Color.WHITE);

        row.getChildren().addAll(labelText, valueText);
        return row;
    }

    private VBox createAccountInfoBox() {
        VBox box = new VBox();
        box.setSpacing(15);
        box.setPadding(new Insets(25));
        box.setStyle("-fx-background-color: rgba(29, 184, 145, 0.3); -fx-background-radius: 10;");

        Text sectionTitle = new Text("Account Information");
        sectionTitle.setFont(Font.font("Jost", 24));
        sectionTitle.setFill(Color.WHITE);

        HBox statusBox = createStatusRow("Account Status:", "✓ Active", Color.web("#00ff00"));
        HBox verificationBox = createStatusRow("Email Verified:", "✓ Yes", Color.web("#00ff00"));
        HBox securityBox = createStatusRow("Two-Factor Auth:", "✗ Not Enabled", Color.web("#ffaa00"));

        box.getChildren().addAll(sectionTitle, statusBox, verificationBox, securityBox);
        return box;
    }

    private VBox createRoleSpecificSection() {
        VBox box = new VBox();
        box.setSpacing(15);
        box.setPadding(new Insets(25));
        box.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10;");

        Text sectionTitle = new Text("Role-Specific Work Dashboard");
        sectionTitle.setFont(Font.font("Jost", 24));
        sectionTitle.setFill(Color.WHITE);

        box.getChildren().add(sectionTitle);

        switch (userRole == null ? "" : userRole.toUpperCase()) {
            case "DOCTOR" -> {
                box.getChildren().addAll(
                        createInfoRow("Upcoming Appointments:", "- 10:30am Patient A\n- 12:30pm Patient B"),
                        createInfoRow("Cabin Visits:", "- Cabin 3 at 11:00am\n- Cabin 5 at 03:00pm"),
                        createInfoRow("Operation Schedule:", "- 02:00pm Knee Surgery\n- 05:00pm Appendectomy")
                );
            }
            case "PATIENT" -> {
                box.getChildren().addAll(
                        createInfoRow("Medical History:", "Diabetes Type 2, Hypertension"),
                        createInfoRow("Previous Details:", "Admitted Mar 2024 - recovered\nCheckups quarterly"),
                        createInfoRow("Prescriptions:", "Metformin 500mg, Amlodipine 5mg"),
                        createInfoRow("Test Results:", "Blood test: normal, X-ray: clear")
                );
            }
            case "NURSE" -> {
                box.getChildren().addAll(
                        createInfoRow("Current Shift:", "Morning, 08:00am - 04:00pm"),
                        createInfoRow("Assigned Wards:", "Ward B, Ward D"),
                        createInfoRow("Care Tasks:", "- IV management\n- Vital signs check every 2 hours")
                );
            }
            case "ADMIN", "STAFF" -> {
                box.getChildren().addAll(
                        createInfoRow("Daily Tasks:", "- Staff coordination\n- Schedule updates"),
                        createInfoRow("Department Goals:", "- Optimize resource usage\n- Ensure compliance"),
                        createInfoRow("Notifications:", "- Meeting at 11:00am\n- Inventory audit tomorrow")
                );
            }
            default -> {
                box.getChildren().addAll(
                        createInfoRow("Summary:", "No role-specific data available."));
            }
        }

        return box;
    }

    private HBox createStatusRow(String label, String value, Color statusColor) {
        HBox row = new HBox();
        row.setSpacing(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 0, 10, 0));

        Text labelText = new Text(label);
        labelText.setFont(Font.font("Jost", 16));
        labelText.setFill(Color.WHITE);

        Text valueText = new Text(value);
        valueText.setFont(Font.font("Jost", 16));
        valueText.setFill(statusColor);

        row.getChildren().addAll(labelText, valueText);
        return row;
    }

    private VBox createActionButtonsBox() {
        VBox box = new VBox();
        box.setSpacing(15);
        box.setPadding(new Insets(25));
        box.setStyle("-fx-background-color: rgba(27, 110, 85, 0.5); -fx-background-radius: 10;");
        box.setAlignment(Pos.CENTER);

        Text sectionTitle = new Text("Actions");
        sectionTitle.setFont(Font.font("Jost", 20));
        sectionTitle.setFill(Color.WHITE);

        HBox buttonsBox = new HBox();
        buttonsBox.setSpacing(20);
        buttonsBox.setAlignment(Pos.CENTER);

        Button editButton = new Button("Edit Profile");
        editButton.setStyle("""
                -fx-font-size: 14;
                -fx-padding: 12 30 12 30;
                -fx-background-color: #00d1d1;
                -fx-text-fill: #0a192f;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """);
        editButton.setOnAction(e -> showAlert("Coming soon!", "Edit Profile feature will be available soon."));

        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setStyle("""
                -fx-font-size: 14;
                -fx-padding: 12 30 12 30;
                -fx-background-color: #1b6f55;
                -fx-text-fill: white;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """);
        changePasswordButton.setOnAction(e -> showAlert("Coming soon!", "Change Password feature will be available soon."));

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("""
                -fx-font-size: 14;
                -fx-padding: 12 30 12 30;
                -fx-background-color: #c23b22;
                -fx-text-fill: white;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """);
        logoutButton.setOnAction(e -> mainApp.navigateToHome());

        buttonsBox.getChildren().addAll(editButton, changePasswordButton, logoutButton);
        box.getChildren().addAll(sectionTitle, buttonsBox);
        return box;
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
