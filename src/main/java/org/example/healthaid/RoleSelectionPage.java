package org.example.healthaid;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.Button;

public class RoleSelectionPage {
    private Scene scene;
    private RoleCallback roleCallback;

    public interface RoleCallback {
        void onRoleSelected(String role);
        void onBackToHome();
    }

    public RoleSelectionPage(RoleCallback roleCallback) {
        this.roleCallback = roleCallback;
        this.scene = createRoleSelectionScene();
    }

    private Scene createRoleSelectionScene() {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #1db891;");
        root.setPadding(new Insets(40));
        root.setSpacing(40);
        root.setAlignment(Pos.TOP_CENTER);

        // Back button
        HBox backBox = new HBox();
        backBox.setAlignment(Pos.TOP_LEFT);

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

        backButton.setOnMouseEntered(e -> 
            backButton.setStyle("""
                -fx-font-size: 14;
                -fx-padding: 8 15 8 15;
                -fx-background-color: #157a52;
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
                -fx-background-color: #1b6e55;
                -fx-text-fill: white;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """)
        );

        backButton.setOnAction(e -> roleCallback.onBackToHome());
        backBox.getChildren().add(backButton);

        // Title
        Text title = new Text("Select Your Role");
        title.setFont(Font.font("Jost", 48));
        title.setFill(Color.WHITE);

        // Subtitle
        Text subtitle = new Text("Choose your user role to access personalized features");
        subtitle.setFont(Font.font("Jost", 16));
        subtitle.setFill(Color.web("rgba(255,255,255,0.9)"));

        // Role cards container
        HBox rolesContainer = new HBox();
        rolesContainer.setSpacing(30);
        rolesContainer.setAlignment(Pos.CENTER);
        rolesContainer.setPadding(new Insets(40, 0, 0, 0));

        // Patient role
        VBox patientCard = createRoleCard("👤 Patient", "Access your appointments,\nmedical history, and\nprescriptions", "#157a52");
        patientCard.setOnMouseClicked(e -> roleCallback.onRoleSelected("PATIENT"));

        // Doctor role
        VBox doctorCard = createRoleCard("👨‍⚕️ Doctor", "Manage patients,\nview medical records,\nand prescriptions", "#0f4c2f");
        doctorCard.setOnMouseClicked(e -> roleCallback.onRoleSelected("DOCTOR"));

        // Nurse role
        VBox nurseCard = createRoleCard("👩‍⚕️ Nurse", "Monitor patients,\ntrack vital signs, and\nmedications", "#1b6e55");
        nurseCard.setOnMouseClicked(e -> roleCallback.onRoleSelected("NURSE"));

        // Hospital Staff role
        VBox staffCard = createRoleCard("👔 Staff", "Manage schedules,\nfacility resources, and\noperations", "#0d3a24");
        staffCard.setOnMouseClicked(e -> roleCallback.onRoleSelected("STAFF"));

        // Admin role
        VBox adminCard = createRoleCard("⚙️ Admin", "System management,\nuser accounts, and\nreports", "#c23b22");
        adminCard.setOnMouseClicked(e -> roleCallback.onRoleSelected("ADMIN"));

        rolesContainer.getChildren().addAll(patientCard, doctorCard, nurseCard, staffCard, adminCard);

        root.getChildren().addAll(backBox, title, subtitle, rolesContainer);

        Scene scene = new Scene(root);
        return scene;
    }

    private VBox createRoleCard(String roleTitle, String description, String bgColor) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 15; -fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 15; -fx-cursor: hand;");
        card.setPadding(new Insets(30));
        card.setSpacing(15);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(160);
        card.setPrefHeight(220);

        // Hover effect
        card.setOnMouseEntered(e -> 
            card.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 15; -fx-border-color: #00f5ff; -fx-border-width: 2; -fx-border-radius: 15; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);")
        );

        card.setOnMouseExited(e -> 
            card.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 15; -fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 15; -fx-cursor: hand;")
        );

        Text title = new Text(roleTitle);
        title.setFont(Font.font("Jost", 18));
        title.setFill(Color.WHITE);

        Text desc = new Text(description);
        desc.setFont(Font.font("Jost", 12));
        desc.setWrappingWidth(120);
        desc.setFill(Color.web("rgba(255,255,255,0.9)"));

        card.getChildren().addAll(title, desc);
        return card;
    }

    public Scene getScene() {
        return scene;
    }
}
