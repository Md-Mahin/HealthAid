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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;

public class HomePage {
    private Scene scene;
    private HelloApplication mainApp;

    public HomePage(HelloApplication mainApp) {
        this.mainApp = mainApp;
        this.scene = createHomeScene();
    }

    private Scene createHomeScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1db891;");

        // Top header
        HBox header = createHeader();
        root.setTop(header);

        root.setCenter(LayoutUtils.createScrollablePage(createMainContent()));

        Scene scene = new Scene(root);
        return scene;
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #1db891; -fx-padding: 15;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);
        header.setPrefHeight(70);

        try {
            Image logo = new Image("title_icon.png");
            ImageView logo_view = new ImageView(logo);
            logo_view.setFitWidth(50);
            logo_view.setFitHeight(50);
            logo_view.setPreserveRatio(true);

            Text home_logo = new Text("HealthAID");
            home_logo.setFont(Font.font("Jost", 28));
            home_logo.setFill(Color.WHITE);

            HBox logoSection = new HBox(10);
            logoSection.setAlignment(Pos.CENTER_LEFT);
            logoSection.getChildren().addAll(logo_view, home_logo);
            header.getChildren().add(logoSection);
        } catch (Exception e) {
            Text home_logo = new Text("HealthAID");
            home_logo.setFont(Font.font("Jost", 28));
            home_logo.setFill(Color.WHITE);
            header.getChildren().add(home_logo);
        }

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().add(spacer);

        try {
            Image home_menu = new Image("home_menu_icon.png");
            ImageView home_menu_view = new ImageView(home_menu);
            home_menu_view.setFitWidth(35);
            home_menu_view.setFitHeight(35);
            home_menu_view.setPreserveRatio(true);

            home_menu_view.setOnMousePressed(e -> {
                home_menu_view.setScaleX(0.9);
                home_menu_view.setScaleY(0.9);
            });

            home_menu_view.setOnMouseReleased(e -> {
                home_menu_view.setScaleX(1.0);
                home_menu_view.setScaleY(1.0);
            });

            home_menu_view.setOnMouseEntered(e -> home_menu_view.setOpacity(0.7));
            home_menu_view.setOnMouseExited(e -> home_menu_view.setOpacity(1.0));

            home_menu_view.setStyle("-fx-cursor: hand;");
            header.getChildren().add(home_menu_view);
        } catch (Exception e) {
            Button menuButton = new Button("☰");
            menuButton.setStyle("""
                    -fx-font-size: 20;
                    -fx-padding: 5 10 5 10;
                    -fx-background-color: transparent;
                    -fx-text-fill: white;
                    -fx-cursor: hand;
                    """);
            header.getChildren().add(menuButton);
        }

        return header;
    }

    private VBox createMainContent() {
        VBox mainContent = new VBox();
        mainContent.setStyle("-fx-background-color: #1db891;");
        mainContent.setSpacing(30);
        mainContent.setPadding(new Insets(30, 40, 40, 40));
        mainContent.setAlignment(Pos.TOP_CENTER);

        // Welcome section
        VBox welcomeSection = createWelcomeSection();
        mainContent.getChildren().add(welcomeSection);

        // Quick Access Cards
        VBox quickAccessSection = createQuickAccessSection();
        mainContent.getChildren().add(quickAccessSection);

        // Information section
        VBox infoSection = createInfoSection();
        mainContent.getChildren().add(infoSection);

        return mainContent;
    }

    public VBox createWelcomeSection() {
//

        VBox welcomeBox = new VBox();
        welcomeBox.setSpacing(5);
        welcomeBox.setAlignment(Pos.CENTER);
        welcomeBox.setTranslateY(-15);


        Text title = new Text("Welcome to HealthAID");
        title.setFont(Font.font("Jost", 48));
        title.setFill(Color.WHITE);


        Text wlcmMsg = new Text(
                "Welcome to your personal health management assistant. " +
                        "Manage your appointments, access emergency services, " +
                        "and track your health schedules all in one place."
        );
        wlcmMsg.setFont(Font.font("Jost", 24));
        wlcmMsg.setWrappingWidth(500);
        wlcmMsg.setFill(Color.WHITE);


        ImageView doc_welc_view = null;
        try {
            Image doc_welc = new Image("doctor_welcome.png");
            doc_welc_view = new ImageView(doc_welc);
            doc_welc_view.setFitWidth(280);
            doc_welc_view.setFitHeight(280);
            doc_welc_view.setPreserveRatio(true);
        } catch (Exception e) {
            System.out.println("Doctor image not found");
        }


        HBox content = new HBox(40);
        content.setAlignment(Pos.CENTER); // align bottom
        content.setStyle("-fx-padding: 15;");


        VBox textAndButtons = new VBox(15);
        textAndButtons.setAlignment(Pos.CENTER);

// Add welcome message to VBox
        textAndButtons.getChildren().add(wlcmMsg);

// Only show buttons if user is not logged in
        if (!mainApp.isUserLoggedIn()) {
            HBox buttonBox = new HBox(20);
            buttonBox.setAlignment(Pos.BOTTOM_LEFT);

            // Login Button
            Button loginBtn = new Button("Login");
            loginBtn.setStyle("""
        -fx-font-size: 14;
        -fx-padding: 10 25 10 25;
        -fx-background-color: #00f5ff;
        -fx-text-fill: #0a192f;
        -fx-font-weight: bold;
        -fx-background-radius: 8;
        -fx-cursor: hand;
    """);
            loginBtn.setOnAction(e -> mainApp.navigateToLogin());
            // Hover effect
            loginBtn.setOnMouseEntered(e -> loginBtn.setStyle("""
        -fx-font-size: 14;
        -fx-padding: 10 25 10 25;
        -fx-background-color: #00d4d4;
        -fx-text-fill: #0a192f;
        -fx-font-weight: bold;
        -fx-background-radius: 8;
    """));
            loginBtn.setOnMouseExited(e -> loginBtn.setStyle("""
        -fx-font-size: 14;
        -fx-padding: 10 25 10 25;
        -fx-background-color: #00f5ff;
        -fx-text-fill: #0a192f;
        -fx-font-weight: bold;
        -fx-background-radius: 8;
    """));

            // Sign Up Button
            Button signupBtn = new Button("Sign Up");
            signupBtn.setStyle("""
        -fx-font-size: 14;
        -fx-padding: 10 25 10 25;
        -fx-background-color: transparent;
        -fx-border-color: #00f5ff;
        -fx-border-width: 2;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 8;
        -fx-border-radius: 8;
        -fx-cursor: hand;
    """);
            signupBtn.setOnAction(e -> mainApp.navigateToSignUp());
            // Hover effect
            signupBtn.setOnMouseEntered(ev -> signupBtn.setStyle("""
        -fx-font-size: 14;
        -fx-padding: 10 25 10 25;
        -fx-background-color: #00f5ff;
        -fx-text-fill: #0a192f;
        -fx-font-weight: bold;
        -fx-background-radius: 8;
    """));
            signupBtn.setOnMouseExited(ev -> signupBtn.setStyle("""
        -fx-font-size: 14;
        -fx-padding: 10 25 10 25;
        -fx-background-color: transparent;
        -fx-border-color: #00f5ff;
        -fx-border-width: 2;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 8;
        -fx-border-radius: 8;
    """));

            buttonBox.getChildren().addAll(loginBtn, signupBtn);
            textAndButtons.getChildren().add(buttonBox);
        }

// Add text + buttons to HBox
        content.getChildren().add(textAndButtons);

// Add image to HBox (if exists)
        if (doc_welc_view != null) {
            content.getChildren().add(doc_welc_view);
        }

// Card container
        VBox card = new VBox();
        card.setStyle("-fx-background-color: transparent; -fx-background-radius: 15;");
        card.getChildren().add(content);

// Add title + card to welcomeBox
        welcomeBox.getChildren().addAll(title, card);
        return welcomeBox;
    }

    private VBox createQuickAccessSection() {
        VBox section = new VBox();
        section.setSpacing(20);
        section.setPadding(new Insets(20));

        Text sectionTitle = new Text("Quick Access");
        sectionTitle.setFont(Font.font("Jost", 32));
        sectionTitle.setFill(Color.WHITE);

        // Cards container
        HBox cardsContainer = new HBox();
        cardsContainer.setSpacing(20);
        cardsContainer.setAlignment(Pos.CENTER);
        cardsContainer.setStyle("-fx-wrap-text: true;");

        // Appointments card
        VBox appointCard = createQuickCard("📅 Appointments", "Manage your medical\nappointments", "#157a52");
        appointCard.setOnMouseClicked(e -> mainApp.navigateToAppointments());

        // Emergency card
        VBox emergencyCard = createQuickCard("🚨 Emergency", "Quick access to \nemergency services", "#c23b22");
        emergencyCard.setOnMouseClicked(e -> mainApp.navigateToEmergency());

        // Operations card
        VBox opsCard = createQuickCard("⏱️ Operations", "View scheduled \noperations", "#157a52");
        opsCard.setOnMouseClicked(e -> mainApp.navigateToOperations());

        cardsContainer.getChildren().addAll(appointCard, emergencyCard, opsCard);

        section.getChildren().addAll(sectionTitle, cardsContainer);
        return section;
    }

    private VBox createQuickCard(String title, String description, String bgColor) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 10;");
        card.setPadding(new Insets(25));
        card.setSpacing(15);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(200);
        card.setPrefHeight(150);
        card.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 10; -fx-border-color: rgba(255,255,255,0.3); -fx-border-radius: 10; -fx-cursor: hand;");

        // Hover effect
        card.setOnMouseEntered(e -> 
            card.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 10; -fx-border-color: #00f5ff; -fx-border-radius: 10; -fx-border-width: 2; -fx-cursor: hand;")
        );

        card.setOnMouseExited(e -> 
            card.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 10; -fx-border-color: rgba(255,255,255,0.3); -fx-border-radius: 10; -fx-cursor: hand;")
        );

        Text cardTitle = new Text(title);
        cardTitle.setFont(Font.font("Jost", 18));
        cardTitle.setFill(Color.WHITE);

        Text cardDesc = new Text(description);
        cardDesc.setFont(Font.font("Jost", 12));
        cardDesc.setWrappingWidth(150);
        cardDesc.setFill(Color.web("rgba(255,255,255,0.9)"));

        card.getChildren().addAll(cardTitle, cardDesc);
        return card;
    }

    private VBox createInfoSection() {
        VBox section = new VBox();
        section.setSpacing(20);
        section.setPadding(new Insets(20, 0, 40, 0));

        Text title = new Text("About HealthAID");
        title.setFont(Font.font("Jost", 28));
        title.setFill(Color.WHITE);

        VBox infoBox = new VBox();
        infoBox.setStyle("-fx-background-color: rgba(27, 110, 85, 0.6); -fx-background-radius: 10;");
        infoBox.setPadding(new Insets(25));
        infoBox.setSpacing(15);

        Text info1 = new Text("🏥 Complete Health Management");
        info1.setFont(Font.font("Jost", 14));
        info1.setFill(Color.WHITE);

        Text info2 = new Text("Track all your medical appointments, emergency contacts, and surgical schedules in one secure platform.");
        info2.setFont(Font.font("Jost", 12));
        info2.setWrappingWidth(600);
        info2.setFill(Color.web("rgba(255,255,255,0.9)"));

        infoBox.getChildren().addAll(info1, info2);

        section.getChildren().addAll(title, infoBox);
        return section;
    }

    public Scene getScene() {
        return scene;
    }
}
