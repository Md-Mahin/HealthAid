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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

public class EmergencyCommunicationCenter {
    private Scene scene;
    private HelloApplication mainApp;

    public EmergencyCommunicationCenter(HelloApplication mainApp) {
        this.mainApp = mainApp;
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
        header.setStyle("-fx-background-color: #c23b22; -fx-padding: 15;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);
        header.setPrefHeight(70);

        Text title = new Text("🚨 EMERGENCY COMMUNICATION CENTER");
        title.setFont(Font.font("Times New Roman", 28));
        title.setFill(Color.WHITE);

        header.getChildren().add(title);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().add(spacer);

        Button backButton = new Button("← Back");
        backButton.setStyle("""
                -fx-font-size: 14;
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
        content.setSpacing(30);
        content.setPadding(new Insets(40));
        content.setAlignment(Pos.TOP_CENTER);

        Text warning = new Text("⚠️ CRITICAL: All emergency messages are routed to appropriate departments automatically");
        warning.setFont(Font.font("Times New Roman", 16));
        warning.setFill(Color.web("#ffeb3b"));

        VBox emergencyChannelsSection = createEmergencyChannelsSection();
        VBox sendEmergencySection = createSendEmergencySection();
        VBox recentEmergenciesSection = createRecentEmergenciesSection();

        content.getChildren().addAll(warning, emergencyChannelsSection, sendEmergencySection, recentEmergenciesSection);
        return content;
    }

    private VBox createEmergencyChannelsSection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(25));

        Text sectionTitle = new Text("Emergency Routing Channels");
        sectionTitle.setFont(Font.font("Segoe UI", 22));
        sectionTitle.setFill(Color.WHITE);

        HBox channelsContainer = new HBox();
        channelsContainer.setSpacing(15);
        channelsContainer.setAlignment(Pos.CENTER);

        VBox doctorChannel = createChannelCard("👨‍⚕️ Doctor Alert", "Medical Emergencies\nCritical Patient States", "#157a52");
        VBox nurseChannel = createChannelCard("👩‍⚕️ Nurse Alert", "Patient Monitoring\nVital Signs Critical", "#1b6e55");
        VBox operationsChannel = createChannelCard("⏱️ OR Alert", "Emergency Surgery\nOR Activation", "#0f4c2f");
        VBox adminChannel = createChannelCard("⚙️ Admin Alert", "System Issues\nFacility Problems", "#0d3a24");

        channelsContainer.getChildren().addAll(doctorChannel, nurseChannel, operationsChannel, adminChannel);
        section.getChildren().addAll(sectionTitle, channelsContainer);
        return section;
    }

    private VBox createChannelCard(String title, String description, String bgColor) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 8; -fx-padding: 15; -fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 8;");
        card.setSpacing(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(160);

        Text titleText = new Text(title);
        titleText.setFont(Font.font("Segoe UI", 14));
        titleText.setFill(Color.WHITE);

        Text descText = new Text(description);
        descText.setFont(Font.font("Segoe UI", 11));
        descText.setWrappingWidth(140);
        descText.setFill(Color.web("rgba(255,255,255,0.85)"));

        card.getChildren().addAll(titleText, descText);
        return card;
    }

    private VBox createSendEmergencySection() {
        VBox section = new VBox();
        section.setSpacing(20);
        section.setStyle("-fx-background-color: rgba(194, 59, 34, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(25));
        section.setMaxWidth(800);

        Text sectionTitle = new Text("Send Emergency Alert");
        sectionTitle.setFont(Font.font("Segoe UI", 22));
        sectionTitle.setFill(Color.WHITE);

        // Emergency type
        Label typeLabel = new Label("Emergency Type:");
        typeLabel.setTextFill(Color.WHITE);
        typeLabel.setFont(Font.font("Segoe UI", 12));

        ComboBox<String> emergencyTypeCombo = new ComboBox<>();
        emergencyTypeCombo.getItems().addAll(
            "Medical Emergency - Cardiac",
            "Medical Emergency - Respiratory",
            "Trauma/Injury",
            "Allergic Reaction",
            "Unconscious Patient",
            "Severe Bleeding",
            "Sepsis Alert",
            "Code Blue",
            "OR Emergency",
            "Facility Problem"
        );
        emergencyTypeCombo.setPrefWidth(300);
        emergencyTypeCombo.setPrefHeight(40);
        emergencyTypeCombo.setStyle("""
                -fx-font-size: 12;
                -fx-padding: 10;
                -fx-background-color: rgba(255,255,255,0.9);
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                """);

        // Recipient department
        Label deptLabel = new Label("Route To:");
        deptLabel.setTextFill(Color.WHITE);
        deptLabel.setFont(Font.font("Segoe UI", 12));

        ComboBox<String> deptCombo = new ComboBox<>();
        deptCombo.getItems().addAll(
            "All Departments (Broadcast)",
            "Doctors Only",
            "Nurses & Medical Staff",
            "Operating Room",
            "ICU / Intensive Care",
            "Emergency Department",
            "Administration"
        );
        deptCombo.setPrefWidth(300);
        deptCombo.setPrefHeight(40);
        deptCombo.setStyle("""
                -fx-font-size: 12;
                -fx-padding: 10;
                -fx-background-color: rgba(255,255,255,0.9);
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                """);

        // Location
        Label locationLabel = new Label("Location/Department:");
        locationLabel.setTextFill(Color.WHITE);
        locationLabel.setFont(Font.font("Segoe UI", 12));

        ComboBox<String> locationCombo = new ComboBox<>();
        locationCombo.getItems().addAll(
            "Room 101", "Room 102", "Room 103", "Room 104",
            "ICU Ward A", "ICU Ward B",
            "Operating Room 1", "Operating Room 2",
            "Emergency Department",
            "Outpatient Clinic"
        );
        locationCombo.setPrefWidth(300);
        locationCombo.setPrefHeight(40);
        locationCombo.setStyle("""
                -fx-font-size: 12;
                -fx-padding: 10;
                -fx-background-color: rgba(255,255,255,0.9);
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                """);

        // Priority level
        Label priorityLabel = new Label("Priority Level:");
        priorityLabel.setTextFill(Color.WHITE);
        priorityLabel.setFont(Font.font("Segoe UI", 12));

        ComboBox<String> priorityCombo = new ComboBox<>();
        priorityCombo.getItems().addAll("🔴 Critical (Life-threatening)", "🟠 High", "🟡 Medium", "🟢 Low");
        priorityCombo.setPrefWidth(300);
        priorityCombo.setPrefHeight(40);
        priorityCombo.setStyle("""
                -fx-font-size: 12;
                -fx-padding: 10;
                -fx-background-color: rgba(255,255,255,0.9);
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                """);

        // Details
        Label detailsLabel = new Label("Details & Additional Info:");
        detailsLabel.setTextFill(Color.WHITE);
        detailsLabel.setFont(Font.font("Segoe UI", 12));

        TextArea detailsArea = new TextArea();
        detailsArea.setPromptText("Provide detailed information about the emergency situation...");
        detailsArea.setStyle("""
                -fx-font-size: 12;
                -fx-padding: 10;
                -fx-background-color: rgba(255,255,255,0.9);
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-control-inner-background: rgba(255,255,255,0.9);
                """);
        detailsArea.setPrefRowCount(6);
        detailsArea.setWrapText(true);

        // Send button
        Button sendButton = new Button("🚨 SEND EMERGENCY ALERT");
        sendButton.setStyle("""
                -fx-font-size: 14;
                -fx-font-weight: bold;
                -fx-padding: 14 40 14 40;
                -fx-background-color: #ffeb3b;
                -fx-text-fill: #c23b22;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """);

        sendButton.setOnMouseEntered(e ->
            sendButton.setStyle("""
                -fx-font-size: 14;
                -fx-font-weight: bold;
                -fx-padding: 14 40 14 40;
                -fx-background-color: #ffca00;
                -fx-text-fill: #c23b22;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """)
        );

        sendButton.setOnMouseExited(e ->
            sendButton.setStyle("""
                -fx-font-size: 14;
                -fx-font-weight: bold;
                -fx-padding: 14 40 14 40;
                -fx-background-color: #ffeb3b;
                -fx-text-fill: #c23b22;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """)
        );

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(sendButton);

        section.getChildren().addAll(
                sectionTitle,
                typeLabel, emergencyTypeCombo,
                deptLabel, deptCombo,
                locationLabel, locationCombo,
                priorityLabel, priorityCombo,
                detailsLabel, detailsArea,
                buttonBox
        );
        return section;
    }

    private VBox createRecentEmergenciesSection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(25));

        Text sectionTitle = new Text("Recent Emergency Alerts (Last 24 Hours)");
        sectionTitle.setFont(Font.font("Segoe UI", 18));
        sectionTitle.setFill(Color.WHITE);

        VBox alertsList = new VBox();
        alertsList.setSpacing(10);

        HBox alert1 = createAlertItem("🔴 Code Blue", "ICU Ward A - Room 105", "14:30 - Cardiac Arrest", "Dr. Sarah Johnson", "RESOLVED");
        HBox alert2 = createAlertItem("🟠 Trauma Alert", "Emergency Department", "13:15 - Motor Vehicle Accident", "Dr. Michael Chen", "ONGOING");
        HBox alert3 = createAlertItem("🟠 Sepsis Alert", "Room 102", "11:45 - Patient with Septic Shock", "Dr. Emily Davis", "RESOLVED");
        HBox alert4 = createAlertItem("🟡 Allergic Reaction", "Room 104", "09:30 - Anaphylactic Reaction", "Nurse Maria", "RESOLVED");

        alertsList.getChildren().addAll(alert1, alert2, alert3, alert4);
        section.getChildren().addAll(sectionTitle, alertsList);
        return section;
    }

    private HBox createAlertItem(String type, String location, String details, String responder, String status) {
        HBox item = new HBox();
        item.setStyle("-fx-background-color: rgba(29, 184, 145, 0.3); -fx-background-radius: 8; -fx-padding: 12;");
        item.setSpacing(15);
        item.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox();
        info.setSpacing(3);
        
        Text typeText = new Text(type);
        typeText.setFont(Font.font("Segoe UI", 13));
        typeText.setFill(Color.WHITE);
        
        Text locText = new Text(location + " - " + details);
        locText.setFont(Font.font("Segoe UI", 11));
        locText.setFill(Color.web("rgba(255,255,255,0.8)"));
        
        Text responderText = new Text("Responded by: " + responder);
        responderText.setFont(Font.font("Segoe UI", 10));
        responderText.setFill(Color.web("#00f5ff"));
        
        info.getChildren().addAll(typeText, locText, responderText);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String statusColor = status.equals("RESOLVED") ? "#00ff00" : status.equals("ONGOING") ? "#ffeb3b" : "#ff6b6b";
        Text statusText = new Text(status);
        statusText.setFont(Font.font("Segoe UI", 11));
        statusText.setFill(Color.web(statusColor));

        item.getChildren().addAll(info, spacer, statusText);
        return item;
    }

    public Scene getScene() {
        return scene;
    }
}
