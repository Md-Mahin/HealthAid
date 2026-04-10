package org.example.healthaid;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginPage {
    private Scene scene;
    private NavigationCallback navigationCallback;

    public interface NavigationCallback {
        void navigateToHome();
        void navigateToSignUp();
        void onLoginSuccess(int userId, String name, String role, boolean rememberMe);
    }

    public LoginPage(NavigationCallback navigationCallback) {
        this.navigationCallback = navigationCallback;
        this.scene = createLoginScene();
    }

    private Scene createLoginScene() {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #1db891;");
        root.setPadding(new Insets(20));
        root.setSpacing(20);
        root.setAlignment(Pos.TOP_CENTER);

        HBox header = createHeader();
        root.getChildren().add(header);

        Text title = new Text("Log In");
        title.setFont(Font.font("Jost", 50));
        title.setFill(Color.WHITE);
        VBox titleBox = new VBox(title);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(20, 0, 30, 0));
        root.getChildren().add(titleBox);

        VBox formCard = createFormCard();
        VBox cardContainer = new VBox(formCard);
        cardContainer.setAlignment(Pos.TOP_CENTER);
        cardContainer.setPadding(new Insets(20));
        root.getChildren().add(cardContainer);

        return new Scene(root, 1000, 600);
    }

    private LoginResult verifyUser(String phone, String password) {
        String sql = "SELECT id, password, role, full_name FROM users WHERE phone = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phone);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (!storedPassword.equals(password)) {
                    return new LoginResult(0, null, null, "WRONG_PASSWORD");
                }

                return new LoginResult(
                        rs.getInt("id"),
                        rs.getString("role"),
                        rs.getString("full_name"),
                        null
                );
            }

            return new LoginResult(0, null, null, "USER_NOT_FOUND");
        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResult(0, null, null, "ERROR");
        }
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(10));
        header.setAlignment(Pos.TOP_LEFT);

        Button backButton = new Button("Back");
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
        card.setSpacing(20);
        card.setMaxWidth(400);
        card.setMinWidth(350);

        Label phoneLabel = new Label("Phone Number:");
        phoneLabel.setTextFill(Color.WHITE);
        phoneLabel.setFont(Font.font("Jost", 14));

        TextField phoneField = new TextField();
        phoneField.setPromptText("Enter your phone number");
        phoneField.setStyle("""
                -fx-font-size: 14;
                -fx-padding: 10;
                -fx-background-color: #ffffff;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                """);
        phoneField.setPrefHeight(40);

        Label passwordLabel = new Label("Password:");
        passwordLabel.setTextFill(Color.WHITE);
        passwordLabel.setFont(Font.font("Jost", 14));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle("""
                -fx-font-size: 14;
                -fx-padding: 10;
                -fx-background-color: #ffffff;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                """);
        passwordField.setPrefHeight(40);

        CheckBox rememberMe = new CheckBox("Remember me");
        rememberMe.setTextFill(Color.WHITE);
        rememberMe.setFont(Font.font("Jost", 12));

        Button loginButton = new Button("Log In");
        loginButton.setStyle("""
                -fx-font-size: 16;
                -fx-font-weight: bold;
                -fx-padding: 12 40 12 40;
                -fx-background-color: #00f5ff;
                -fx-text-fill: #0a192f;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """);
        loginButton.setPrefWidth(300);

        loginButton.setOnMouseEntered(e ->
            loginButton.setStyle("""
                -fx-font-size: 16;
                -fx-font-weight: bold;
                -fx-padding: 12 40 12 40;
                -fx-background-color: #00d1d1;
                -fx-text-fill: #0a192f;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """)
        );

        loginButton.setOnMouseExited(e ->
            loginButton.setStyle("""
                -fx-font-size: 16;
                -fx-font-weight: bold;
                -fx-padding: 12 40 12 40;
                -fx-background-color: #00f5ff;
                -fx-text-fill: #0a192f;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """)
        );

        loginButton.setOnAction(e -> {
            String phoneNumber = phoneField.getText();
            String password = passwordField.getText();

            if (phoneNumber.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Please fill in all fields");
                return;
            }

            LoginResult result = verifyUser(phoneNumber, password);

            if ("USER_NOT_FOUND".equals(result.errorCode())) {
                showAlert("Error", "User not found");
            } else if ("WRONG_PASSWORD".equals(result.errorCode())) {
                showAlert("Error", "Incorrect password");
            } else if ("ERROR".equals(result.errorCode())) {
                showAlert("Error", "Database error occurred");
            } else {
                showAlert("Success", "Logged in as: " + result.role());
                navigationCallback.onLoginSuccess(result.userId(), result.fullName(), result.role(), rememberMe.isSelected());

                phoneField.clear();
                passwordField.clear();
                rememberMe.setSelected(false);
            }
        });

        Hyperlink forgotPassword = new Hyperlink("Forgot Password?");
        forgotPassword.setFont(Font.font("Jost", 12));
        forgotPassword.setTextFill(Color.web("#00f5ff"));
        forgotPassword.setOnAction(e -> showAlert("Info", "Password reset functionality coming soon!"));

        HBox signupBox = new HBox();
        signupBox.setAlignment(Pos.CENTER);
        signupBox.setSpacing(5);

        Text noAccountText = new Text("Don't have an account? ");
        noAccountText.setFill(Color.WHITE);
        noAccountText.setFont(Font.font("Jost", 12));

        Hyperlink signupLink = new Hyperlink("Sign Up");
        signupLink.setFont(Font.font("Jost", 12));
        signupLink.setTextFill(Color.web("#00f5ff"));
        signupLink.setOnAction(e -> navigationCallback.navigateToSignUp());

        signupBox.getChildren().addAll(noAccountText, signupLink);

        card.getChildren().addAll(
                phoneLabel, phoneField,
                passwordLabel, passwordField,
                rememberMe,
                loginButton,
                forgotPassword,
                signupBox
        );

        return card;
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

    private record LoginResult(int userId, String role, String fullName, String errorCode) {
    }
}
