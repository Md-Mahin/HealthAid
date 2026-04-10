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
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;

public class EmergencyMessagesPage {
    private Scene scene;
    private HelloApplication mainApp;

    public EmergencyMessagesPage(HelloApplication mainApp) {
        this.mainApp = mainApp;
        this.scene = createScene();
    }

    private Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1db891;");

        // Header
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

        Text title = new Text("🚨 Emergency Messages");
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
                -fx-background-color: #a02817;
                -fx-text-fill: white;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """);

        backButton.setOnMouseEntered(e -> 
            backButton.setStyle("""
                -fx-font-size: 14;
                -fx-padding: 8 15 8 15;
                -fx-background-color: #7a1f12;
                -fx-text-fill: white;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """)
        );

        backButton.setOnMouseExited(e -> 
            backButton.setStyle("""
                -fx-font-size: 14;
                -fx-padding: 8 15 8 15;
                -fx-background-color: #a02817;
                -fx-text-fill: white;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """)
        );

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

        // Title
        Text title = new Text("Emergency Communication Center");
        title.setFont(Font.font("Jost", 40));
        title.setFill(Color.WHITE);

        // Emergency contacts section
        VBox contactsSection = createEmergencyContactsSection();

        // Send emergency message section
       // VBox messageSection = createMessageSection();

        content.getChildren().addAll(title, contactsSection);
        return content;
    }

    private VBox createEmergencyContactsSection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(25));

        Text sectionTitle = new Text("Quick Emergency Contacts");
        sectionTitle.setFont(Font.font("Jost", 22));
        sectionTitle.setFill(Color.WHITE);

        VBox contactsList = new VBox();
        contactsList.setSpacing(10);

        // Contact 1
        HBox contact1 = createContactCard("Ambulance Service", "Emergency Medical Transport", "📞 +1-911");
        // Contact 2
        HBox contact2 = createContactCard("Hospital Main Line", "General Hospital Information", "📞 +1-555-0100");
        // Contact 3
        HBox contact3 = createContactCard("Poison Control", "24/7 Poison Emergency", "📞 +1-800-222-1222");
        // Contact 4
        HBox contact4 = createContactCard("Mental Health Crisis", "Mental Health Emergency Support", "📞 +1-988");

        contactsList.getChildren().addAll(contact1, contact2, contact3, contact4);

        section.getChildren().addAll(sectionTitle, contactsList);
        return section;
    }

    private HBox createContactCard(String name, String description, String phone) {
        HBox card = new HBox();
        card.setStyle("""
                -fx-background-color: rgba(29, 184, 145, 0.3);
                -fx-background-radius: 8;
                -fx-padding: 15;
                -fx-border-color: rgba(0, 245, 255, 0.3);
                -fx-border-radius: 8;
                """);
        card.setSpacing(20);
        card.setAlignment(Pos.CENTER_LEFT);

        VBox textBox = new VBox();
        textBox.setSpacing(5);

        Text contactName = new Text(name);
        contactName.setFont(Font.font("Jost", 16));
        contactName.setFill(Color.WHITE);

        Text contactDesc = new Text(description);
        contactDesc.setFont(Font.font("Jost", 12));
        contactDesc.setFill(Color.web("rgba(255,255,255,0.8)"));

        textBox.getChildren().addAll(contactName, contactDesc);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text phoneText = new Text(phone);
        phoneText.setFont(Font.font("Jost", 14));
        phoneText.setFill(Color.web("#00f5ff"));

        card.getChildren().addAll(textBox, spacer, phoneText);
        return card;
    }

//    private VBox createMessageSection() {
//        VBox section = new VBox();
//        section.setSpacing(20);
//        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
//        section.setPadding(new Insets(25));
//        section.setMaxWidth(800);
//
//        Text sectionTitle = new Text("Send Emergency Message");
//        sectionTitle.setFont(Font.font("Jost", 22));
//        sectionTitle.setFill(Color.WHITE);
//
//        // Recipient field
//        Label recipientLabel = new Label("Send to:");
//        recipientLabel.setTextFill(Color.WHITE);
//        recipientLabel.setFont(Font.font("Jost", 12));
//
//        TextField recipientField = new TextField();
//        recipientField.setPromptText("Select recipient or enter contact");
//        recipientField.setStyle("""
//                -fx-font-size: 12;
//                -fx-padding: 10;
//                -fx-background-color: rgba(255,255,255,0.9);
//                -fx-border-radius: 5;
//                -fx-background-radius: 5;
//                """);
//        recipientField.setPrefHeight(40);
//
//        // Message field
//        Label messageLabel = new Label("Message:");
//        messageLabel.setTextFill(Color.WHITE);
//        messageLabel.setFont(Font.font("Jost", 12));
//
//        TextArea messageArea = new TextArea();
//        messageArea.setPromptText("Type your emergency message here...");
//        messageArea.setStyle("""
//                -fx-font-size: 12;
//                -fx-padding: 10;
//                -fx-background-color: rgba(255,255,255,0.9);
//                -fx-border-radius: 5;
//                -fx-background-radius: 5;
//                -fx-control-inner-background: rgba(255,255,255,0.9);
//                """);
//        messageArea.setPrefRowCount(6);
//        messageArea.setWrapText(true);
//
//        // Send button
//        Button sendButton = new Button("Send Emergency Message");
//        sendButton.setStyle("""
//                -fx-font-size: 14;
//                -fx-font-weight: bold;
//                -fx-padding: 12 30 12 30;
//                -fx-background-color: #c23b22;
//                -fx-text-fill: white;
//                -fx-border-radius: 5;
//                -fx-background-radius: 5;
//                -fx-cursor: hand;
//                """);
//
//        sendButton.setOnMouseEntered(e ->
//            sendButton.setStyle("""
//                -fx-font-size: 14;
//                -fx-font-weight: bold;
//                -fx-padding: 12 30 12 30;
//                -fx-background-color: #a02817;
//                -fx-text-fill: white;
//                -fx-border-radius: 5;
//                -fx-background-radius: 5;
//                -fx-cursor: hand;
//                """)
//        );
//
//        sendButton.setOnMouseExited(e ->
//            sendButton.setStyle("""
//                -fx-font-size: 14;
//                -fx-font-weight: bold;
//                -fx-padding: 12 30 12 30;
//                -fx-background-color: #c23b22;
//                -fx-text-fill: white;
//                -fx-border-radius: 5;
//                -fx-background-radius: 5;
//                -fx-cursor: hand;
//                """)
//        );
//
//        HBox buttonBox = new HBox();
//        buttonBox.setAlignment(Pos.CENTER);
//        buttonBox.getChildren().add(sendButton);
//
//        section.getChildren().addAll(sectionTitle, recipientLabel, recipientField,
//                                     messageLabel, messageArea, buttonBox);
//        return section;
//    }

    public Scene getScene() {
        return scene;
    }
}
