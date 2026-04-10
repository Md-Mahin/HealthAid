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

public class StaffSchedulingPage {
    private Scene scene;
    private HelloApplication mainApp;
    private boolean isAdminLoggedIn;

    public StaffSchedulingPage(HelloApplication mainApp) {
        this.mainApp = mainApp;
        this.isAdminLoggedIn = mainApp != null && mainApp.isAdminLoggedIn();
        this.scene = createScene();
        
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
        header.setStyle("-fx-background-color: #0d3a24; -fx-padding: 15;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);
        header.setPrefHeight(70);

        Text title = new Text("📅 Staff Scheduling & Management");
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
                -fx-background-color: #1b6e55;
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

        Text title = new Text("Staff Dashboard - Scheduling & Request Management");
        title.setFont(Font.font("Jost", 36));
        title.setFill(Color.WHITE);

        // Add Request Verification Section FIRST
       // VBox requestVerificationSection = createRequestVerificationSection();
        
       VBox weeklyScheduleSection = createWeeklyScheduleSection();
        VBox assignShiftSection = createAssignShiftSection();
        VBox staffAvailabilitySection = createStaffAvailabilitySection();

        content.getChildren().addAll(title,  weeklyScheduleSection, assignShiftSection, staffAvailabilitySection);
        return content;
    }

    private VBox createRequestVerificationSection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setStyle("-fx-background-color: rgba(244, 67, 54, 0.3); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));

        Text sectionTitle = new Text("🔍 Patient Request Verification");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        VBox requestsList = new VBox();
        requestsList.setSpacing(10);

        // Sample pending requests
        HBox req1 = createRequestItem("Appointment Request", "John Doe", "Pending", "March 20, 2024", "Dr. Johnson");
        HBox req2 = createRequestItem("Operation Request", "Jane Smith", "Pending", "Appendectomy", "Urgent");
        HBox req3 = createRequestItem("Pharmacy Request", "Mike Johnson", "Pending", "Medications", "Routine");
        HBox req4 = createRequestItem("Bed Request", "Sarah Brown", "Pending", "ICU Bed", "High Priority");

        requestsList.getChildren().addAll(req1, req2, req3, req4);
        section.getChildren().addAll(sectionTitle, requestsList);
        return section;
    }

    private HBox createRequestItem(String requestType, String patientName, String status, String details, String priority) {
        HBox item = new HBox();
        item.setStyle("-fx-background-color: rgba(29, 184, 145, 0.2); -fx-background-radius: 8; -fx-padding: 12;");
        item.setSpacing(15);
        item.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox();
        info.setSpacing(3);
        info.setMaxWidth(350);
        
        Text typeText = new Text(requestType + " - " + patientName);
        typeText.setFont(Font.font("Jost", 13));
        typeText.setFill(Color.WHITE);
        
        Text detailsText = new Text(details);
        detailsText.setFont(Font.font("Jost", 11));
        detailsText.setFill(Color.web("rgba(255,255,255,0.8)"));
        
        Text priorityText = new Text("Priority: " + priority);
        priorityText.setFont(Font.font("Jost", 10));
        priorityText.setFill(Color.web("#ffeb3b"));
        
        info.getChildren().addAll(typeText, detailsText, priorityText);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox buttonBox = new VBox();
        buttonBox.setSpacing(8);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button verifyBtn = new Button("✓ Verify");
        verifyBtn.setStyle("""
                -fx-font-size: 11;
                -fx-padding: 6 12 6 12;
                -fx-background-color: #00c853;
                -fx-text-fill: white;
                -fx-border-radius: 3;
                -fx-background-radius: 3;
                -fx-cursor: hand;
                """);
        verifyBtn.setOnAction(e -> {
            verifyBtn.setDisable(true);
            verifyBtn.setText("✓ Verified");
        });

        Button rejectBtn = new Button("✗ Reject");
        rejectBtn.setStyle("""
                -fx-font-size: 11;
                -fx-padding: 6 12 6 12;
                -fx-background-color: #d32f2f;
                -fx-text-fill: white;
                -fx-border-radius: 3;
                -fx-background-radius: 3;
                -fx-cursor: hand;
                """);
        
        buttonBox.getChildren().addAll(verifyBtn, rejectBtn);
        item.getChildren().addAll(info, spacer, buttonBox);
        return item;
    }

    private VBox createWeeklyScheduleSection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));

        Text sectionTitle = new Text("Weekly Schedule - Week of March 10, 2024");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        VBox scheduleList = new VBox();
        scheduleList.setSpacing(8);

        HBox mon = createDaySchedule("Monday, March 10", "Doctors: 3", "Nurses: 6", "Staff: 4", "✅ Complete");
        HBox tue = createDaySchedule("Tuesday, March 11", "Doctors: 2", "Nurses: 5", "Staff: 3", "⚠️ Need 1 Doctor");
        HBox wed = createDaySchedule("Wednesday, March 12", "Doctors: 3", "Nurses: 6", "Staff: 4", "✅ Complete");
        HBox thu = createDaySchedule("Thursday, March 13", "Doctors: 3", "Nurses: 6", "Staff: 4", "✅ Complete");
        HBox fri = createDaySchedule("Friday, March 14", "Doctors: 2", "Nurses: 4", "Staff: 3", "⚠️ Understaffed");

        scheduleList.getChildren().addAll(mon, tue, wed, thu, fri);
        section.getChildren().addAll(sectionTitle, scheduleList);
        return section;
    }

    private HBox createDaySchedule(String day, String doctors, String nurses, String staff, String status) {
        HBox item = new HBox();
        item.setStyle("-fx-background-color: rgba(29, 184, 145, 0.3); -fx-background-radius: 8; -fx-padding: 12;");
        item.setSpacing(20);
        item.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox();
        info.setSpacing(3);
        Text dayText = new Text(day);
        dayText.setFont(Font.font("Jost", 12));
        dayText.setFill(Color.WHITE);
        Text staffInfo = new Text(doctors + " | " + nurses + " | " + staff);
        staffInfo.setFont(Font.font("Jost", 11));
        staffInfo.setFill(Color.web("rgba(255,255,255,0.8)"));
        info.getChildren().addAll(dayText, staffInfo);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text statusText = new Text(status);
        statusText.setFont(Font.font("Jost", 11));
        statusText.setFill(status.contains("✅") ? Color.web("#00ff00") : Color.web("#ffeb3b"));

        item.getChildren().addAll(info, spacer, statusText);
        return item;
    }

    private VBox createAssignShiftSection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));
        section.setMaxWidth(800);
        if(!mainApp.isAdminLoggedIn()){
            return section;
        }

        Text sectionTitle = new Text("Assign Staff to Shift");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        Label staffLabel = new Label("Select Staff Member:");
        staffLabel.setTextFill(Color.WHITE);
        staffLabel.setFont(Font.font("Jost", 12));

        ComboBox<String> staffCombo = new ComboBox<>();
        staffCombo.getItems().addAll(
            "Dr. Sarah Johnson - Cardiology",
            "Dr. Michael Chen - Orthopedics",
            "Nurse Maria Garcia",
            "Nurse James Wilson",
            "Staff: Robert Smith",
            "Staff: Emily Rodriguez"
        );
        staffCombo.setStyle("""
                -fx-font-size: 12;
                -fx-padding: 10;
                -fx-background-color: rgba(255,255,255,0.9);
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                """);
        staffCombo.setPrefWidth(300);
        staffCombo.setPrefHeight(40);

        Label dateLabel = new Label("Select Date:");
        dateLabel.setTextFill(Color.WHITE);
        dateLabel.setFont(Font.font("Jost", 12));

        DatePicker datePicker = new DatePicker();
        datePicker.setStyle("""
                -fx-font-size: 12;
                -fx-padding: 10;
                -fx-background-color: rgba(255,255,255,0.9);
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                """);

        Label shiftLabel = new Label("Select Shift:");
        shiftLabel.setTextFill(Color.WHITE);
        shiftLabel.setFont(Font.font("Jost", 12));

        ComboBox<String> shiftCombo = new ComboBox<>();
        shiftCombo.getItems().addAll("Morning (6:00 AM - 2:00 PM)", "Afternoon (2:00 PM - 10:00 PM)", "Night (10:00 PM - 6:00 AM)");
        shiftCombo.setStyle("""
                -fx-font-size: 12;
                -fx-padding: 10;
                -fx-background-color: rgba(255,255,255,0.9);
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                """);
        shiftCombo.setPrefWidth(300);
        shiftCombo.setPrefHeight(40);

        Label departmentLabel = new Label("Department:");
        departmentLabel.setTextFill(Color.WHITE);
        departmentLabel.setFont(Font.font("Jost", 12));

        ComboBox<String> departmentCombo = new ComboBox<>();
        departmentCombo.getItems().addAll("Cardiology", "Orthopedics", "General Ward", "ICU", "Emergency Department", "Surgery");
        departmentCombo.setStyle("""
                -fx-font-size: 12;
                -fx-padding: 10;
                -fx-background-color: rgba(255,255,255,0.9);
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                """);
        departmentCombo.setPrefWidth(300);
        departmentCombo.setPrefHeight(40);

        Button assignButton = new Button("Assign to Shift");
        assignButton.setStyle("""
                -fx-font-size: 12;
                -fx-padding: 10 20 10 20;
                -fx-background-color: #00f5ff;
                -fx-text-fill: #0a192f;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """);

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(assignButton);

        section.getChildren().addAll(
                sectionTitle,
                staffLabel, staffCombo,
                dateLabel, datePicker,
                shiftLabel, shiftCombo,
                departmentLabel, departmentCombo,
                buttonBox
        );
        return section;
    }

    private VBox createStaffAvailabilitySection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));

        Text sectionTitle = new Text("Available Staff for Shifts");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        VBox staffList = new VBox();
        staffList.setSpacing(8);

        HBox staff1 = createStaffAvailabilityItem("Dr. Sarah Johnson", "Cardiology", "Available", "Morning & Afternoon");
        HBox staff2 = createStaffAvailabilityItem("Nurse Maria Garcia", "General Ward", "Available", "All Shifts");
        HBox staff3 = createStaffAvailabilityItem("Dr. Michael Chen", "Orthopedics", "On Leave", "Until March 15");
        HBox staff4 = createStaffAvailabilityItem("Nurse James Wilson", "ICU", "Available", "Night Shifts Only");

        staffList.getChildren().addAll(staff1, staff2, staff3, staff4);
        section.getChildren().addAll(sectionTitle, staffList);
        return section;
    }

    private HBox createStaffAvailabilityItem(String name, String department, String status, String availability) {
        HBox item = new HBox();
        item.setStyle("-fx-background-color: rgba(29, 184, 145, 0.3); -fx-background-radius: 8; -fx-padding: 12;");
        item.setSpacing(15);
        item.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox();
        info.setSpacing(3);
        Text nameText = new Text(name);
        nameText.setFont(Font.font("Jost", 12));
        nameText.setFill(Color.WHITE);
        Text deptText = new Text(department);
        deptText.setFont(Font.font("Jost", 11));
        deptText.setFill(Color.web("rgba(255,255,255,0.8)"));
        info.getChildren().addAll(nameText, deptText);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String statusColor = status.equals("Available") ? "#00ff00" : "#ffeb3b";
        Text statusText = new Text(status + " - " + availability);
        statusText.setFont(Font.font("Jost", 11));
        statusText.setFill(Color.web(statusColor));

        item.getChildren().addAll(info, spacer, statusText);
        return item;
    }

    public Scene getScene() {
        return scene;
    }
}
