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
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;

public class MedicalRecordsPage {
    private Scene scene;
    private HelloApplication mainApp;

    public MedicalRecordsPage(HelloApplication mainApp) {
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
        header.setStyle("-fx-background-color: #157a52; -fx-padding: 15;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);
        header.setPrefHeight(70);

        Text title = new Text("📋 Medical Records Management");
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

        Text title = new Text("Patient Medical Records System");
        title.setFont(Font.font("Jost", 36));
        title.setFill(Color.WHITE);

        VBox searchSection = createSearchSection();
        VBox recordsSection = createPatientRecordsSection();
        VBox addRecordSection = createAddRecordSection();

        content.getChildren().addAll(title, searchSection, recordsSection, addRecordSection);
        return content;
    }

    private VBox createSearchSection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));

        Text sectionTitle = new Text("Search Patient Records");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        HBox searchBox = new HBox();
        searchBox.setSpacing(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Enter Patient ID or Name...");
        searchField.setStyle("""
                -fx-font-size: 12;
                -fx-padding: 10;
                -fx-background-color: rgba(255,255,255,0.9);
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                """);
        searchField.setPrefWidth(300);
        searchField.setPrefHeight(40);

        Button searchButton = new Button("Search");
        searchButton.setStyle("""
                -fx-font-size: 12;
                -fx-padding: 10 20 10 20;
                -fx-background-color: #00f5ff;
                -fx-text-fill: #0a192f;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """);

        searchBox.getChildren().addAll(searchField, searchButton);
        section.getChildren().addAll(sectionTitle, searchBox);
        return section;
    }

    private VBox createPatientRecordsSection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));

        Text sectionTitle = new Text("Patient: John Smith (ID: P001)");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        VBox recordsList = new VBox();
        recordsList.setSpacing(10);

        HBox record1 = createRecordItem("Complete Blood Count", "November 15, 2024", "Dr. Sarah Johnson", "View");
        HBox record2 = createRecordItem("Chest X-Ray", "November 10, 2024", "Radiology Dept", "View");
        HBox record3 = createRecordItem("ECG - Electrocardiogram", "November 1, 2024", "Cardiology", "View");
        HBox record4 = createRecordItem("Endoscopy Report", "October 20, 2024", "Gastroenterology", "View");

        recordsList.getChildren().addAll(record1, record2, record3, record4);
        section.getChildren().addAll(sectionTitle, recordsList);
        return section;
    }

    private HBox createRecordItem(String recordType, String date, String department, String action) {
        HBox item = new HBox();
        item.setStyle("-fx-background-color: rgba(29, 184, 145, 0.3); -fx-background-radius: 8; -fx-padding: 12;");
        item.setSpacing(15);
        item.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox();
        info.setSpacing(3);
        Text typeText = new Text(recordType);
        typeText.setFont(Font.font("Jost", 12));
        typeText.setFill(Color.WHITE);
        Text dateText = new Text(date + " - " + department);
        dateText.setFont(Font.font("Jost", 11));
        dateText.setFill(Color.web("rgba(255,255,255,0.8)"));
        info.getChildren().addAll(typeText, dateText);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewButton = new Button(action);
        viewButton.setStyle("-fx-font-size: 11; -fx-padding: 6 12 6 12; -fx-background-color: #00f5ff; -fx-text-fill: #0a192f; -fx-border-radius: 3; -fx-background-radius: 3;");

        Button downloadButton = new Button("Download");
        downloadButton.setStyle("-fx-font-size: 11; -fx-padding: 6 12 6 12; -fx-background-color: #157a52; -fx-text-fill: white; -fx-border-radius: 3; -fx-background-radius: 3;");

        item.getChildren().addAll(info, spacer, viewButton, downloadButton);
        return item;
    }

    private VBox createAddRecordSection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));
        section.setMaxWidth(800);

        Text sectionTitle = new Text("Add New Medical Record");
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        Label typeLabel = new Label("Record Type:");
        typeLabel.setTextFill(Color.WHITE);
        typeLabel.setFont(Font.font("Jost", 12));

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Lab Results", "X-Ray Report", "CT Scan", "MRI Report", "Ultrasound", "Surgery Notes", "Prescription", "Vaccination Record");
        typeCombo.setStyle("""
                -fx-font-size: 12;
                -fx-padding: 10;
                -fx-background-color: rgba(255,255,255,0.9);
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                """);
        typeCombo.setPrefWidth(300);
        typeCombo.setPrefHeight(40);

        Label detailsLabel = new Label("Record Details:");
        detailsLabel.setTextFill(Color.WHITE);
        detailsLabel.setFont(Font.font("Jost", 12));

        TextArea detailsArea = new TextArea();
        detailsArea.setPromptText("Enter record details here...");
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

        Button saveButton = new Button("Save Medical Record");
        saveButton.setStyle("""
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
        buttonBox.getChildren().add(saveButton);

        section.getChildren().addAll(sectionTitle, typeLabel, typeCombo, detailsLabel, detailsArea, buttonBox);
        return section;
    }

    public Scene getScene() {
        return scene;
    }
}
