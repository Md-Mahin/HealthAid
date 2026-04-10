package org.example.healthaid;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.effect.DropShadow;
import javafx.animation.TranslateTransition;
import javafx.animation.PauseTransition;
import org.example.healthaid.LoginPage.NavigationCallback;


public class HelloApplication extends Application {

    private Stage primaryStage;
    private Scene homeScene;
    private LoginPage loginPage;
    private SignUpPage signUpPage;
    private HomePage homePage;
    private EmergencyMessagesPage emergencyPage;
    private AppointmentsPage appointmentsPage;
    private OperationSchedulesPage operationsPage;
    private RoleSelectionPage roleSelectionPage;
    private PatientDashboard patientDashboard;
    private DoctorDashboard doctorDashboard;
    private NurseDashboard nurseDashboard;
    private AdminDashboard adminDashboard;
    private UserProfilePage userProfilePage;
    private MedicalRecordsPage medicalRecordsPage;
    private StaffSchedulingPage staffSchedulingPage;
    private LabManagementPage labManagementPage;
    private PharmacyManagementPage pharmacyManagementPage;
    private BillingInsuranceManagementPage billingInsurancePage;
    private BedManagementPage bedManagementPage;
    private AppointmentRequestPage appointmentRequestPage;
    private OperationRequestPage operationRequestPage;
    private PharmacyRequestPage pharmacyRequestPage;
    private BillingRequestPage billingRequestPage;
    //private BedRequestPage bedRequestPage;
    private VBox sidebar;
    private boolean sidebarVisible = false;
    private TranslateTransition slideAnimation;
    private Integer currentUserId = null;
    private String currentUserName = null;
    private String currentUserRole = null;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) throws Exception {

        var fontUrl = getClass().getResource("/fonts/Jost-Regular.ttf");
        if (fontUrl == null) {
            System.out.println("FONT NOT FOUND!");
        } else {
            Font.loadFont(fontUrl.toExternalForm(), 12);
        }

        this.primaryStage = stage;
        Database.initialize();
        this.homePage = new HomePage(this);
        this.emergencyPage = new EmergencyMessagesPage(this);
        this.appointmentsPage = new AppointmentsPage(this);
        this.operationsPage = new OperationSchedulesPage(this);
        this.patientDashboard = new PatientDashboard(this);
        this.doctorDashboard = new DoctorDashboard(this);
        this.nurseDashboard = new NurseDashboard(this);
        this.adminDashboard = new AdminDashboard(this);
        this.userProfilePage = new UserProfilePage(this);
        this.staffSchedulingPage = new StaffSchedulingPage(this);
        this.labManagementPage = new LabManagementPage(this);
        this.pharmacyManagementPage = new PharmacyManagementPage(this);
        this.billingInsurancePage = new BillingInsuranceManagementPage(this);
        this.bedManagementPage = new BedManagementPage(this);

        primaryStage.setTitle("HealthAID");
        Image title_icon = new Image("title_icon.png");
        primaryStage.getIcons().add(title_icon);
        primaryStage.setWidth(1200);
        primaryStage.setHeight(700);

        this.loginPage = new LoginPage(new NavigationCallback() {
            @Override
            public void navigateToHome() {
                primaryStage.setScene(homeScene);
            }

            @Override
            public void navigateToSignUp() {
                primaryStage.setScene(signUpPage.getScene());
            }

            @Override
            public void onLoginSuccess(int userId, String name, String role, boolean rememberMe) {
                if (rememberMe) {
                    RememberMeUtil.saveUser(userId, name, role);
                }

                System.out.println("Callback received:");
                System.out.println("User ID: " + userId);
                System.out.println("Name: " + name);
                System.out.println("Role: " + role);

                currentUserId = userId;
                currentUserName = name;
                currentUserRole = role;

                homeScene = createHomeScene();

                Scene welcomeScene = createWelcomeScene();
                primaryStage.setScene(welcomeScene);

                PauseTransition pause = new PauseTransition(Duration.seconds(3));
                pause.setOnFinished(e -> {
                    navigateToUserDashboard(role);
                });
                pause.play();
            }
        });

        this.signUpPage = new SignUpPage(new SignUpPage.NavigationCallback() {
            @Override
            public void navigateToHome() {
                primaryStage.setScene(homeScene);
            }

            @Override
            public void navigateToLogin() {
                primaryStage.setScene(loginPage.getScene());
            }
        });

        this.roleSelectionPage = new RoleSelectionPage(new RoleSelectionPage.RoleCallback() {
            @Override
            public void onRoleSelected(String role) {
                currentUserRole = role;
                navigateToRoleDashboard(role);
            }

            @Override
            public void onBackToHome() {
                primaryStage.setScene(homeScene);
            }
        });

        if (RememberMeUtil.isRemembered()) {
            currentUserId = RememberMeUtil.getUserId();
            currentUserName = RememberMeUtil.getName();
            currentUserRole = RememberMeUtil.getRole();

            homeScene = createHomeScene();
            navigateToUserDashboard(currentUserRole);
            primaryStage.show();
            return;
        }

        this.homeScene = createHomeScene();
        primaryStage.setScene(homeScene);
        primaryStage.show();
    }

    public void navigateToHome() {
        primaryStage.setScene(homeScene);
        closeSidebar();
    }

    public void navigateToEmergency() {
        primaryStage.setScene(emergencyPage.getScene());
        closeSidebar();
    }

    public void navigateToAppointments() {
        this.appointmentsPage = new AppointmentsPage(this);
        primaryStage.setScene(appointmentsPage.getScene());
        closeSidebar();
    }

    public void navigateToOperations() {
        this.operationsPage = new OperationSchedulesPage(this);
        primaryStage.setScene(operationsPage.getScene());
        closeSidebar();
    }

    public void navigateToLogin() {
        primaryStage.setScene(loginPage.getScene());
        closeSidebar();
    }

    public void navigateToSignUp() {
        primaryStage.setScene(signUpPage.getScene());
        closeSidebar();
    }

    public void navigateToRoleSelection() {
        primaryStage.setScene(roleSelectionPage.getScene());
        closeSidebar();
    }

    public void navigateToStaffScheduling() {
        primaryStage.setScene(staffSchedulingPage.getScene());
        closeSidebar();
    }

    public void navigateToLabManagement() {
        this.labManagementPage = new LabManagementPage(this);
        primaryStage.setScene(labManagementPage.getScene());
        closeSidebar();
    }

    public void navigateToPharmacyManagement() {
        this.pharmacyManagementPage = new PharmacyManagementPage(this);
        primaryStage.setScene(pharmacyManagementPage.getScene());
        closeSidebar();
    }

    public void navigateToBillingInsurance() {
        this.billingInsurancePage = new BillingInsuranceManagementPage(this);

        primaryStage.setScene(billingInsurancePage.getScene());
        closeSidebar();
    }

    public void navigateToBedManagement() {
        this.bedManagementPage = new BedManagementPage(this);
        primaryStage.setScene(bedManagementPage.getScene());
        closeSidebar();
    }

    public void navigateToMedicalRecords() {
        if (medicalRecordsPage == null) {
            this.medicalRecordsPage = new MedicalRecordsPage(this);
        }
        primaryStage.setScene(medicalRecordsPage.getScene());
        closeSidebar();
    }

    public void navigateToAppointmentRequest(int patientId, String patientName) {
        this.appointmentRequestPage = new AppointmentRequestPage(this, patientId, patientName);
        primaryStage.setScene(appointmentRequestPage.getScene());
        closeSidebar();
    }

    public void navigateToOperationRequest(int patientId, String patientName) {
        this.operationRequestPage = new OperationRequestPage(this, patientId, patientName);
        primaryStage.setScene(operationRequestPage.getScene());
        closeSidebar();
    }

    public void navigateToPharmacyRequest(int patientId, String patientName) {
        this.pharmacyRequestPage = new PharmacyRequestPage(this, patientId, patientName);
        primaryStage.setScene(pharmacyRequestPage.getScene());
        closeSidebar();
    }

    public void navigateToBillingRequest(int patientId, String patientName) {
        this.billingRequestPage = new BillingRequestPage(this, patientId, patientName);
        primaryStage.setScene(billingRequestPage.getScene());
        closeSidebar();
    }

    public void navigateToBedRequest(int patientId, String patientName) {
        //this.bedRequestPage = new BedRequestPage(this, patientId, patientName);
        //primaryStage.setScene(bedRequestPage.getScene());
        closeSidebar();
    }

    public boolean isPatientLoggedIn() {
        return currentUserName != null && "PATIENT".equalsIgnoreCase(currentUserRole);
    }
    public boolean isStaffLoggedIn() {
        return currentUserName != null && "STAFF".equalsIgnoreCase(currentUserRole);
    }
    public boolean isDoctorLoggedIn() {
        return currentUserName != null && "DOCTOR".equalsIgnoreCase(currentUserRole);
    }
    public boolean isAdminLoggedIn() {
        return currentUserName != null && "ADMIN".equalsIgnoreCase(currentUserRole);
    }

    public boolean isUserLoggedIn() {
        return currentUserName != null && currentUserRole != null;
    }

    public Integer getCurrentUserId() {
        return currentUserId;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public String getCurrentUserRole() {
        return currentUserRole;
    }

    private void navigateToRoleDashboard(String role) {
        // update dashboard contexts to keep user details in view
        if (currentUserName != null && currentUserRole != null) {
            patientDashboard.setUserDetails(currentUserName, currentUserRole);
            doctorDashboard.setUserDetails(currentUserName, currentUserRole);
            nurseDashboard.setUserDetails(currentUserName, currentUserRole);
            adminDashboard.setUserDetails(currentUserName, currentUserRole);
            userProfilePage.setUserDetails(currentUserName, currentUserRole);
        }

        switch (role) {
            case "PATIENT":
                primaryStage.setScene(patientDashboard.getScene());
                break;
            case "DOCTOR":
                primaryStage.setScene(doctorDashboard.getScene());
                break;
            case "NURSE":
                primaryStage.setScene(nurseDashboard.getScene());
                break;
            case "ADMIN":
            case "STAFF":
                primaryStage.setScene(adminDashboard.getScene());
                break;
            default:
                primaryStage.setScene(homeScene);
        }
    }

//    private void navigateToUserDashboard(String role) {
//        userProfilePage.setUserDetails(currentUserName, role);
//        primaryStage.setScene(userProfilePage.getScene());
//    }
public void navigateToUserDashboard(String role) {
    switch (role.toLowerCase()) {
        case "admin":
            setScene(new AdminDashboard(this).getScene());
            break;
        case "nurse":
            setScene(new NurseDashboard(this).getScene());
            break;
        case "doctor":
            setScene(new DoctorDashboard(this).getScene());
            break;
        case "patient":
            setScene(new PatientDashboard(this).getScene());
            break;
        case "staff":
            setScene(new AdminDashboard(this).getScene());
            break;
        default:
            System.out.println("Unknown role: " + role);
    }
}
    private void setScene(Scene scene) {
        primaryStage.setScene(scene);
    }
    private void closeSidebar() {
        if (sidebarVisible) {
            // move back to right offscreen (width of sidebar)
            slideAnimation.setToX(40000000);
            slideAnimation.play();
            sidebarVisible = false;
        }
    }

    private void toggleSidebar() {
        double width = primaryStage.getScene().getWidth();
        if (sidebarVisible) {
            slideAnimation.setToX(4000);
        } else {
            slideAnimation.setToX(width - 200);
        }
        slideAnimation.play();
        sidebarVisible = !sidebarVisible;
    }

    private Scene createHomeScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1db891;");

        // Create top header
        HBox header = createHeader();
        root.setTop(header);

        // Create sidebar
        this.sidebar = createSidebar();
        StackPane stackPane = new StackPane();

        // Create main content pane
        VBox mainContent = createMainContent();
        stackPane.getChildren().addAll(mainContent, sidebar);
        // align sidebar to right side instead of left
        StackPane.setAlignment(sidebar, Pos.TOP_RIGHT);

        root.setCenter(stackPane);

        Scene scene = new Scene(root);

        var css = getClass().getResource("/styles.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS NOT FOUND!");
        }

        // Bind scene width and height to stage for responsive design
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            // Scene will handle responsive layout through layout managers
        });

        return scene;
    }

    private Scene createWelcomeScene() {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #1db891;");
        root.setAlignment(Pos.CENTER);
        root.setSpacing(30);
        root.setPadding(new Insets(50));

        // The Welcome title
        Text welcomeTitle = new Text("Welcome!");
        welcomeTitle.setFont(Font.font("Jost", 60));
        welcomeTitle.setFill(Color.WHITE);

        // User name
        Text userName = new Text(currentUserName);
        userName.setFont(Font.font("Jost", 40));
        userName.setFill(Color.web("#00f5ff"));

        // Role
        Text roleText = new Text(currentUserRole);
        roleText.setFont(Font.font("Jost", 28));
        roleText.setFill(Color.WHITE);

        // Loading message
        Text loadingText = new Text("Redirecting to dashboard...");
        loadingText.setFont(Font.font("Jost", 18));
        loadingText.setFill(Color.WHITE);

        root.getChildren().addAll(welcomeTitle, userName, roleText, loadingText);

        return new Scene(root);
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #1db891; -fx-padding: 15;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);
        header.setPrefHeight(70);

        //button b = new button()
        try {
            // Logo image
            Image logo = new Image("title_icon.png");
            ImageView logo_view = new ImageView(logo);
            logo_view.setFitWidth(50);
            logo_view.setFitHeight(50);
            logo_view.setPreserveRatio(true);

            // Logo text
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

        // Spacer
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().add(spacer);

        // Menu icon button
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

            home_menu_view.setOnMouseClicked(e -> toggleSidebar());

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
            menuButton.setOnAction(e2 -> toggleSidebar());
            header.getChildren().add(menuButton);
        }

        return header;
    }

    private VBox createMainContent() {
        VBox mainContent = new VBox();
        // mainContent.setStyle("-fx-background-color: #1db891;");
        mainContent.setSpacing(0);
        mainContent.setPadding(new Insets(0));
        mainContent.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        // Welcome section
        VBox welcomeSection = homePage.createWelcomeSection();
        VBox.setVgrow(welcomeSection, Priority.ALWAYS);
        mainContent.getChildren().add(welcomeSection);

        return mainContent;
    }

    private VBox createSidebar() {
        VBox sidebarContainer = new VBox();
        // slide in from right: start off-screen by width amount
        sidebarContainer.setTranslateX(400000);
        sidebarContainer.setSpacing(0);
        sidebarContainer.setLayoutY(70);
        sidebarContainer.setStyle("""
                -fx-background-color: #1b6f55;
                -fx-padding: 0;
                """);
        // widen sidebar to accommodate text labels permanently
        sidebarContainer.setPrefWidth(200);
        sidebarContainer.setPrefHeight(600);
        sidebarContainer.setAlignment(Pos.TOP_LEFT);

        DropShadow shadow = new DropShadow();
        shadow.setRadius(10);
        shadow.setOffsetX(5);
        shadow.setColor(Color.rgb(0, 0, 0, 0.3));
        sidebarContainer.setEffect(shadow);

        this.slideAnimation = new TranslateTransition(Duration.millis(300), sidebarContainer);
        // animation will assume positive offset when hidden due to new direction

        // Create scrollable content container
        VBox scrollContent = new VBox();
        scrollContent.setSpacing(3);
        scrollContent.setAlignment(Pos.TOP_LEFT);
        scrollContent.setPadding(new Insets(5));
        scrollContent.setStyle("-fx-background-color: #1b6f55;");


        if (currentUserName != null && currentUserRole != null) {
            VBox welcomeBox = new VBox();
            welcomeBox.setAlignment(Pos.CENTER);
            welcomeBox.setPadding(new Insets(15));
            welcomeBox.setSpacing(10);
            welcomeBox.setTranslateY(-100);

            HBox content = new HBox(40);
            content.setAlignment(Pos.TOP_CENTER);
            welcomeBox.setPrefWidth(190);
            welcomeBox.setMaxWidth(Double.MAX_VALUE);
            welcomeBox.setStyle("-fx-background-color: rgba(0, 245, 255, 0.1); -fx-border-radius: 5; -fx-background-radius: 5;");

            Label nameLabel = new Label(currentUserName);
            nameLabel.setTextFill(Color.web("#00f5ff"));

            nameLabel.setMaxWidth(Double.MAX_VALUE);
            nameLabel.setAlignment(Pos.CENTER);
            nameLabel.setWrapText(true);

            Label roleLabel = new Label(currentUserRole);
            roleLabel.setTextFill(Color.WHITE);

            roleLabel.setFont(Font.font("Jost", 12));
            roleLabel.setMaxWidth(Double.MAX_VALUE);
            roleLabel.setAlignment(Pos.CENTER);
            roleLabel.setWrapText(true);

            welcomeBox.getChildren().addAll(nameLabel, roleLabel);
            scrollContent.getChildren().add(welcomeBox);

            // Add separator after welcome
            Separator welcomeSep = new Separator();
            welcomeSep.setPrefHeight(1);
            welcomeSep.setStyle("-fx-text-fill: rgba(255,255,255,0.2);");
            scrollContent.getChildren().add(welcomeSep);
        }

        // Create scroll pane for menu items
        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.getStyleClass().add("sidebar-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("""
                -fx-background-color: transparent;
                -fx-background-insets: 0;
                -fx-padding: 0;
                """);
        scrollPane.setPrefWidth(200);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        sidebarContainer.getChildren().add(scrollPane);

        VBox sidebar = scrollContent;

        // Create menu items - ICON ONLY
        Label home = createIconicNavItem("Home", "\uD83C\uDFE0");
        home.setOnMouseClicked(e -> navigateToHome());

        Label emergency = createIconicNavItem("Emergency", "🚨");
        emergency.setOnMouseClicked(e -> navigateToEmergency());

        Label appointments = createIconicNavItem("Appointments", "📅");
        appointments.setOnMouseClicked(e -> navigateToAppointments());

        Label operations = createIconicNavItem("Operations", "⏱️");
        operations.setOnMouseClicked(e -> navigateToOperations());

        Label dashboard = createIconicNavItem("Dashboard", "🎯");

        dashboard.setOnMouseClicked(e -> {
            if (currentUserRole != null) {
                System.out.println("Dashboard clicked! Role: " + currentUserRole);
                navigateToUserDashboard(currentUserRole);
            }
        });

        if (currentUserName == null) {
            Label login = createIconicNavItem("Log IN", "🔐");
            login.setOnMouseClicked(e -> navigateToLogin());

            Label signUp = createIconicNavItem("Sign UP", "✏️");
            signUp.setOnMouseClicked(e -> navigateToSignUp());

            sidebar.getChildren().addAll(login, signUp);
        } else {
            Label profile = createIconicNavItem("My Profile", "👤");
            profile.setOnMouseClicked(e -> navigateToUserDashboard(currentUserRole));
            sidebar.getChildren().add(profile);
        }

        Label staffScheduling = createIconicNavItem("Staff", "📋");
        staffScheduling.setOnMouseClicked(e -> navigateToStaffScheduling());

        Label labManagement = createIconicNavItem("Lab", "🔬");
        labManagement.setOnMouseClicked(e -> navigateToLabManagement());

        Label pharmacyManagement = createIconicNavItem("Pharmacy", "💊");
        pharmacyManagement.setOnMouseClicked(e -> navigateToPharmacyManagement());

        Label billingInsurance = createIconicNavItem("Billing", "💳");
        billingInsurance.setOnMouseClicked(e -> navigateToBillingInsurance());

        Label bedManagement = createIconicNavItem("Beds", "🛌");
        bedManagement.setOnMouseClicked(e -> navigateToBedManagement());



        sidebar.getChildren().addAll(
                home, emergency, appointments, operations
        );


        if (currentUserName != null && currentUserRole != null) {
            sidebar.getChildren().add(dashboard);
        }

        sidebar.getChildren().addAll(
                staffScheduling, labManagement, pharmacyManagement, billingInsurance, bedManagement
        );

        if (currentUserName != null) {
    Label logOut = createIconicNavItem("Log Out", "?");
    logOut.setOnMouseClicked(event -> {
    currentUserId = null;
    currentUserName = null;
    currentUserRole = null;
    RememberMeUtil.clearUser();

    VBox root = new VBox();
    root.setAlignment(Pos.CENTER);
    root.setStyle("-fx-background-color: #1db891;");
    Text logoutText = new Text("Logging out...");
    logoutText.setFont(Font.font("Jost", 40));
    logoutText.setFill(Color.WHITE);
    root.getChildren().add(logoutText);

    Scene logoutScene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
    primaryStage.setScene(logoutScene);

    PauseTransition pause = new PauseTransition(Duration.seconds(2));
    pause.setOnFinished(e -> {
        homeScene = createHomeScene();
        primaryStage.setScene(homeScene);
    });
    pause.play();
});

    sidebar.getChildren().add(logOut);
}



        return sidebarContainer;
    }

    private Label createIconicNavItem(String text, String icon) {
        Label label = new Label(icon + " " + text);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Jost", 16));
        label.setPadding(new Insets(10, 8, 10, 8));
        label.setPrefWidth(180);
        label.setAlignment(Pos.CENTER_LEFT);

        label.setStyle("""
                -fx-cursor: hand;
                -fx-text-fill: white;
                -fx-text-alignment: left;
                """);

        // simple hover highlight without changing text
        label.setOnMouseEntered(e -> label.setStyle("""
                -fx-background-color: rgba(29, 184, 145, 0.5);
                -fx-text-fill: #00f5ff;
                -fx-cursor: hand;
                -fx-padding: 10 8 10 8;
                -fx-background-radius: 5;
                -fx-text-alignment: left;
                """));

        label.setOnMouseExited(e -> label.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: white;
                -fx-cursor: hand;
                -fx-text-alignment: left;
                """));

        return label;
    }
}
