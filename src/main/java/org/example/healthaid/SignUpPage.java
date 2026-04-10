package org.example.healthaid;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class SignUpPage {
    private Scene scene;
    private NavigationCallback navigationCallback;

    public interface NavigationCallback {
        void navigateToHome();
        void navigateToLogin();
    }

    public SignUpPage(NavigationCallback navigationCallback) {
        this.navigationCallback = navigationCallback;
        this.scene = createSignUpScene();
    }

    //savind data to database
    private boolean saveUser(String fullName, String phone, String password, String role) {

        String sql = "INSERT INTO users(full_name, phone, password, role) VALUES(?,?,?,?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fullName);
            pstmt.setString(2, phone);
            pstmt.setString(3, password);
            pstmt.setString(4, role);

            pstmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Scene createSignUpScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1db891;");

        // Header with back button
        HBox header = createHeader();
        root.setTop(header);

        // Create scrollable content area
        VBox contentBox = new VBox();
        contentBox.setStyle("-fx-background-color: #1db891;");
        contentBox.setSpacing(20);
        contentBox.setPadding(new Insets(20));
        contentBox.setAlignment(Pos.TOP_CENTER);

        // Title
        Text title = new Text("Sign Up");
        title.setFont(Font.font("Jost", 50));
        title.setFill(Color.WHITE);
        VBox titleBox = new VBox(title);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, 20, 0));
        contentBox.getChildren().add(titleBox);

        // Sign up form card
        VBox formCard = createFormCard();
        contentBox.getChildren().add(formCard);

        // Add spacing at bottom
        VBox spacer = new VBox();
        spacer.setPrefHeight(30);
        contentBox.getChildren().add(spacer);

        root.setCenter(LayoutUtils.createScrollablePage(contentBox));

        Scene scene = new Scene(root);
        return scene;
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(10));
        header.setAlignment(Pos.TOP_LEFT);

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

        backButton.setOnAction(e -> navigationCallback.navigateToHome());

        header.getChildren().add(backButton);
        return header;
    }

    private VBox createFormCard() {
        VBox card = new VBox();
        card.setStyle("""
                -fx-background-color: #1b6e55;
                -fx-background-radius: 15;
                -fx-padding: 40;
                """);
        card.setSpacing(15);
        card.setMaxWidth(450);
        card.setMinWidth(350);
        card.setAlignment(Pos.TOP_CENTER);

        // field where he sets he is a patient or a doctor or a staff or and admin
        Label roleLabel = new Label("Select Role");
        roleLabel.setFont(Font.font("Jost", 15));
        roleLabel.setTextFill(Color.WHITE);
        roleLabel.setAlignment(Pos.CENTER);
        roleLabel.setMaxWidth(Double.MAX_VALUE);

        roleLabel.setStyle("""
    -fx-font-weight: bold;
    -fx-padding: 5 0 10 0;
""");

// Radio buttons
        RadioButton patientRadio = new RadioButton("Patient");
        RadioButton doctorRadio = new RadioButton("Doctor");
        RadioButton staffRadio = new RadioButton("Staff");
        RadioButton adminRadio = new RadioButton("Admin");


        String radioStyle = """
    -fx-text-fill: white;
    -fx-font-size: 13;
""";

        patientRadio.setStyle(radioStyle);
        doctorRadio.setStyle(radioStyle);
        staffRadio.setStyle(radioStyle);
        adminRadio.setStyle(radioStyle);


        ToggleGroup roleGroup = new ToggleGroup();
        patientRadio.setToggleGroup(roleGroup);
        doctorRadio.setToggleGroup(roleGroup);
        staffRadio.setToggleGroup(roleGroup);
        adminRadio.setToggleGroup(roleGroup);

        staffRadio.setSelected(true);


        HBox roleOptions = new HBox(20);
        roleOptions.setAlignment(Pos.CENTER);
        roleOptions.getChildren().addAll(
                patientRadio,
                doctorRadio,
                staffRadio,
                adminRadio
        );


        VBox roleBox = new VBox(5);
        roleBox.setAlignment(Pos.CENTER);
        roleBox.getChildren().addAll(roleLabel, roleOptions);




        // Full Name field
        Label fullNameLabel = new Label("Full Name:");
        fullNameLabel.setTextFill(Color.WHITE);
        fullNameLabel.setFont(Font.font("Jost", 12));

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Enter your full name");
        fullNameField.setStyle(getInputFieldStyle());
        fullNameField.setPrefHeight(35);

        // Phone Number field
        Label phoneLabel = new Label("Phone Number:");
        phoneLabel.setTextFill(Color.WHITE);
        phoneLabel.setFont(Font.font("Jost", 12));

        TextField phoneField = new TextField();
        phoneField.setPromptText("Enter your phone number");
        phoneField.setStyle(getInputFieldStyle());
        phoneField.setPrefHeight(35);

        // Password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setTextFill(Color.WHITE);
        passwordLabel.setFont(Font.font("Jost", 12));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter a strong password");
        passwordField.setStyle(getInputFieldStyle());
        passwordField.setPrefHeight(35);

        // Confirm Password field
        Label confirmPasswordLabel = new Label("Confirm Password:");
        confirmPasswordLabel.setTextFill(Color.WHITE);
        confirmPasswordLabel.setFont(Font.font("Jost", 12));

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Re-enter your password");
        confirmPasswordField.setStyle(getInputFieldStyle());
        confirmPasswordField.setPrefHeight(35);



        // Terms and Conditions checkbox
        CheckBox agreeTerms = new CheckBox("I agree to the Terms and Conditions");
        agreeTerms.setTextFill(Color.WHITE);
        agreeTerms.setFont(Font.font("Jost", 11));

        // Sign Up button
        Button signUpButton = new Button("Sign Up");
        signUpButton.setStyle(getButtonStyle());
        signUpButton.setPrefWidth(300);

        signUpButton.setOnMouseEntered(e -> 
            signUpButton.setStyle(getButtonHoverStyle())
        );

        signUpButton.setOnMouseExited(e -> 
            signUpButton.setStyle(getButtonStyle())
        );

        signUpButton.setOnAction(e -> {
            String fullName = fullNameField.getText();
            String phoneNumber = phoneField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            RadioButton selectedRadio = (RadioButton) roleGroup.getSelectedToggle();
            String selectedRole = selectedRadio.getText();

            if (fullName.isEmpty() || phoneNumber.isEmpty()  ||
                password.isEmpty() || confirmPassword.isEmpty()) {
                showAlert("Error", "Please fill in all required fields");
            } else if (!password.equals(confirmPassword)) {
                showAlert("Error", "Passwords do not match");
            } else if (!isValidPhoneNumber(phoneNumber)) {
                showAlert("Error", "Please enter a valid phone number ");
            } else if (password.length() < 6) {
                showAlert("Error", "Password must be at least 6 characters long");
            } else if (!agreeTerms.isSelected()) {
                showAlert("Error", "Please agree to the Terms and Conditions");
            } else {
                if (saveUser(fullName, phoneNumber, password, selectedRole)) {
                    showAlert("Success", "Account created as " + selectedRole + "!");
                } else {
                    showAlert("Error", "Database error!");
                }

                System.out.println("Role: " + selectedRole);

                // reset
                fullNameField.clear();
                phoneField.clear();
                passwordField.clear();
                confirmPasswordField.clear();
                agreeTerms.setSelected(false);
                roleGroup.selectToggle(staffRadio); // reset default
            }
        });

        // Already have account link
        HBox loginBox = new HBox();
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setSpacing(5);

        Text alreadyAccountText = new Text("Already have an account? ");
        alreadyAccountText.setFill(Color.WHITE);
        alreadyAccountText.setFont(Font.font("Jost", 11));

        Hyperlink loginLink = new Hyperlink("Log In");
        loginLink.setFont(Font.font("Jost", 11));
        loginLink.setTextFill(Color.web("#00f5ff"));
        loginLink.setOnAction(e -> navigationCallback.navigateToLogin());

        loginBox.getChildren().addAll(alreadyAccountText, loginLink);

        // Add all elements to card

        //VBox roleBox = new VBox(5, roleLabel, roleOptions);
        card.getChildren().add(roleBox);
        card.getChildren().addAll(
                fullNameLabel, fullNameField,
                phoneLabel, phoneField,
                passwordLabel, passwordField,
                confirmPasswordLabel, confirmPasswordField,
                agreeTerms,
                signUpButton,
                loginBox
        );

        return card;
    }

    private String getInputFieldStyle() {
        return """
                -fx-font-size: 12;
                -fx-padding: 8;
                -fx-background-color: #ffffff;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                """;
    }

    private String getButtonStyle() {
        return """
                -fx-font-size: 16;
                -fx-font-weight: bold;
                -fx-padding: 12 40 12 40;
                -fx-background-color: #00f5ff;
                -fx-text-fill: #0a192f;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """;
    }

    private String getButtonHoverStyle() {
        return """
                -fx-font-size: 16;
                -fx-font-weight: bold;
                -fx-padding: 12 40 12 40;
                -fx-background-color: #00d1d1;
                -fx-text-fill: #0a192f;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{11}");
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
}
