package org.example.healthaid;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AppointmentsPage {
    private static final DateTimeFormatter TIME_INPUT_FORMAT = DateTimeFormatter.ofPattern("h:mm a");
    private static final DateTimeFormatter TIME_STORAGE_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final Scene scene;
    private final HelloApplication mainApp;
    private final ObservableList<AppointmentRecord> appointments = FXCollections.observableArrayList();
    private final ObservableList<DoctorOption> doctors = FXCollections.observableArrayList();

    private TableView<AppointmentRecord> appointmentsTable;

    public AppointmentsPage(HelloApplication mainApp) {
        this.mainApp = mainApp;
        loadDoctors();
        this.scene = createScene();
        loadAppointments();
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

        Text title = new Text("Appointments");
        title.setFont(Font.font("Jost", 28));
        title.setFill(Color.WHITE);

        header.getChildren().add(title);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().add(spacer);

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
                -fx-background-color: #144d3a;
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

        Text title = new Text("Manage Your Appointments");
        title.setFont(Font.font("Jost", 40));
        title.setFill(Color.WHITE);

        VBox upcomingSection = createUpcomingAppointmentsSection();
        VBox bookSection = createBookAppointmentSection();

        content.getChildren().addAll(title, upcomingSection, bookSection);
        return content;
    }

    private VBox createUpcomingAppointmentsSection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(25));
        section.setPrefWidth(980);
        section.setMaxWidth(Double.MAX_VALUE);

        Text sectionTitle = new Text(getAppointmentsHeading());
        sectionTitle.setFont(Font.font("Jost", 22));
        sectionTitle.setFill(Color.WHITE);

        appointmentsTable = new TableView<>(appointments);
        appointmentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        appointmentsTable.setPlaceholder(createPlaceholderLabel());
        appointmentsTable.setPrefHeight(360);
        appointmentsTable.getStyleClass().add("healthaid-table");
        appointmentsTable.setStyle("""
                -fx-background-color: rgba(29, 184, 145, 0.2);
                -fx-control-inner-background: rgba(29, 184, 145, 0.2);
                -fx-table-cell-border-color: rgba(255,255,255,0.1);
                -fx-selection-bar: rgba(0, 245, 255, 0.35);
                -fx-selection-bar-non-focused: rgba(0, 245, 255, 0.2);
                """);

        TableColumn<AppointmentRecord, String> patientColumn = new TableColumn<>("Patient");
        patientColumn.setCellValueFactory(data -> data.getValue().patientNameProperty());
        patientColumn.setVisible(mainApp.isStaffLoggedIn());

        TableColumn<AppointmentRecord, String> doctorColumn = new TableColumn<>("Doctor");
        doctorColumn.setCellValueFactory(data -> data.getValue().doctorNameProperty());

        TableColumn<AppointmentRecord, String> dateColumn = new TableColumn<>("Appointment Date");
        dateColumn.setCellValueFactory(data -> data.getValue().appointmentDateProperty());

        TableColumn<AppointmentRecord, String> reasonColumn = new TableColumn<>("Reason");
        reasonColumn.setCellValueFactory(data -> data.getValue().reasonProperty());

        TableColumn<AppointmentRecord, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());

        TableColumn<AppointmentRecord, Number> serialColumn = new TableColumn<>("Serial No.");
        serialColumn.setCellValueFactory(data -> data.getValue().serialNumberProperty());
        serialColumn.setVisible(mainApp.isDoctorLoggedIn() || mainApp.isStaffLoggedIn());
        serialColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<AppointmentRecord, Number> call(TableColumn<AppointmentRecord, Number> column) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Number item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setText(null);
                        } else if (item == null || item.intValue() <= 0) {
                            setText("-");
                        } else {
                            setText(String.valueOf(item.intValue()));
                        }
                    }
                };
            }
        });

        appointmentsTable.getColumns().addAll(doctorColumn, patientColumn, dateColumn, reasonColumn, statusColumn, serialColumn);

        if (mainApp.isStaffLoggedIn()) {
            TableColumn<AppointmentRecord, Void> actionColumn = new TableColumn<>("Action");

            actionColumn.setCellFactory(column -> new TableCell<>() {

                private final Button approveButton = new Button("Approve");
                private final Button rejectButton = new Button("Reject");
                private final HBox actionBox = new HBox(10, approveButton, rejectButton);

                {
                    approveButton.setStyle("""
                    -fx-font-size: 12;
                    -fx-font-weight: bold;
                    -fx-padding: 8 12 8 12;
                    -fx-background-color: #00f5ff;
                    -fx-text-fill: #0a192f;
                    -fx-background-radius: 5;
                    -fx-cursor: hand;
                    """);

                    rejectButton.setStyle("""
                    -fx-font-size: 12;
                    -fx-font-weight: bold;
                    -fx-padding: 8 12 8 12;
                    -fx-background-color: #ff4d4d;
                    -fx-text-fill: white;
                    -fx-background-radius: 5;
                    -fx-cursor: hand;
                    """);

                    approveButton.setOnAction(event -> {
                        AppointmentRecord record = getTableView().getItems().get(getIndex());
                        approveAppointment(record);
                    });

                    rejectButton.setOnAction(event -> {
                        AppointmentRecord record = getTableView().getItems().get(getIndex());
                        rejectAppointment(record);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setGraphic(null);
                        return;
                    }

                    AppointmentRecord record = getTableRow().getItem();

                    boolean isProcessed =
                            "APPROVED".equalsIgnoreCase(record.getStatus()) ||
                                    "REJECTED".equalsIgnoreCase(record.getStatus());

                    approveButton.setDisable(isProcessed);
                    rejectButton.setDisable(isProcessed);

                    setGraphic(actionBox);
                }
            });

            appointmentsTable.getColumns().add(actionColumn);
        }

        section.getChildren().addAll(sectionTitle, appointmentsTable);
        return section;
    }

    private VBox createBookAppointmentSection() {
        VBox section = new VBox();
        section.setSpacing(20);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(25));
        section.setMaxWidth(800);

        if (!mainApp.isPatientLoggedIn()) {
            return section;
        }

        Text sectionTitle = new Text("Book New Appointment");
        sectionTitle.setFont(Font.font("Jost", 22));
        sectionTitle.setFill(Color.WHITE);

        Label deptLabel = new Label("Select Department:");
        deptLabel.setTextFill(Color.WHITE);
        deptLabel.setFont(Font.font("Jost", 12));

        ComboBox<String> deptCombo = new ComboBox<>();
        deptCombo.getItems().addAll("Cardiology", "Orthopedics", "Dental", "Neurology", "Pediatrics", "General Practice");
        styleComboBox(deptCombo);

        Label doctorLabel = new Label("Select Doctor:");
        doctorLabel.setTextFill(Color.WHITE);
        doctorLabel.setFont(Font.font("Jost", 12));

        ComboBox<DoctorOption> doctorCombo = new ComboBox<>(doctors);
        styleComboBox(doctorCombo);
        doctorCombo.setPromptText(doctors.isEmpty() ? "No doctors available" : "Choose doctor");

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

        Label timeLabel = new Label("Select Time:");
        timeLabel.setTextFill(Color.WHITE);
        timeLabel.setFont(Font.font("Jost", 12));

        ComboBox<String> timeCombo = new ComboBox<>();
        timeCombo.getItems().addAll("9:00 AM", "10:00 AM", "11:00 AM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM");
        styleComboBox(timeCombo);

        Label reasonLabel = new Label("Reason:");
        reasonLabel.setTextFill(Color.WHITE);
        reasonLabel.setFont(Font.font("Jost", 12));

        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("Describe the reason for the appointment");
        reasonArea.setWrapText(true);
        reasonArea.setPrefRowCount(3);
        reasonArea.setStyle("""
                -fx-font-size: 12;
                -fx-background-color: rgba(255,255,255,0.9);
                -fx-control-inner-background: rgba(255,255,255,0.95);
                -fx-background-radius: 5;
                -fx-border-radius: 5;
                """);

        Button bookButton = new Button("Book Appointment");
        bookButton.setStyle("""
                -fx-font-size: 14;
                -fx-font-weight: bold;
                -fx-padding: 12 30 12 30;
                -fx-background-color: #00f5ff;
                -fx-text-fill: #0a192f;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """);
        bookButton.setDisable(doctors.isEmpty());
        bookButton.setOnAction(e -> handleBookAppointment(deptCombo, doctorCombo, datePicker, timeCombo, reasonArea));

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(bookButton);

        section.getChildren().addAll(
                sectionTitle,
                deptLabel, deptCombo,
                doctorLabel, doctorCombo,
                dateLabel, datePicker,
                timeLabel, timeCombo,
                reasonLabel, reasonArea,
                buttonBox
        );
        return section;
    }

    private void styleComboBox(ComboBox<?> comboBox) {
        comboBox.setStyle("""
                -fx-font-size: 12;
                -fx-padding: 10;
                -fx-background-color: rgba(255,255,255,0.9);
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                """);
        comboBox.setPrefWidth(300);
        comboBox.setPrefHeight(40);
    }

    private Label createPlaceholderLabel() {
        Label placeholder = new Label(getEmptyTableMessage());
        placeholder.setTextFill(Color.WHITE);
        placeholder.setFont(Font.font("Jost", 14));
        return placeholder;
    }

    private String getAppointmentsHeading() {
        if (mainApp.isStaffLoggedIn()) {
            return "Appointment Verification Queue";
        }
        if (mainApp.isDoctorLoggedIn()) {
            return "Approved Appointments";
        }
        return "Upcoming Appointments";
    }

    private String getEmptyTableMessage() {
        if (mainApp.isDoctorLoggedIn()) {
            return "No approved appointments found.";
        }
        if (mainApp.isPatientLoggedIn()) {
            return "No appointments found for the logged-in patient.";
        }
        if (mainApp.isStaffLoggedIn()) {
            return "No appointments available for staff review.";
        }
        return "Log in to view appointments.";
    }

    private void loadDoctors() {
        doctors.clear();
        String sql = "SELECT id, full_name FROM users WHERE UPPER(role) = 'DOCTOR' ORDER BY full_name";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                doctors.add(new DoctorOption(rs.getInt("id"), rs.getString("full_name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAppointments() {
        deleteOldAppointments();
        appointments.clear();

        String sql = buildAppointmentsQuery();
        if (sql == null) {
            return;
        }

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            bindAppointmentsQueryParameters(pstmt);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(new AppointmentRecord(
                            rs.getInt("id"),
                            rs.getInt("patient_id"),
                            rs.getInt("doctor_id"),
                            rs.getString("patient_name"),
                            rs.getString("doctor_name"),
                            rs.getString("appointment_date"),
                            rs.getString("reason"),
                            normalizeStatus(rs.getString("status")),
                            rs.getInt("serial_number")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load appointments.");
        }
    }

    private String buildAppointmentsQuery() {
        String baseSql = """
                SELECT ar.id,
                       ar.patient_id,
                       ar.doctor_id,
                       patient.full_name AS patient_name,
                       doctor.full_name AS doctor_name,
                       ar.appointment_date,
                       ar.reason,
                       ar.status,
                       COALESCE(ar.serial_number, 0) AS serial_number
                FROM appointment_requests ar
                LEFT JOIN users patient ON patient.id = ar.patient_id
                LEFT JOIN users doctor ON doctor.id = ar.doctor_id
                """;

        if (mainApp.isPatientLoggedIn() && mainApp.getCurrentUserId() != null) {
            return baseSql + " WHERE ar.patient_id = ? ORDER BY ar.appointment_date";
        }
        if (mainApp.isDoctorLoggedIn() && mainApp.getCurrentUserId() != null) {
            return baseSql + " WHERE ar.doctor_id = ? AND UPPER(ar.status) = 'APPROVED' ORDER BY ar.appointment_date";
        }
        if (mainApp.isStaffLoggedIn()) {
            return baseSql + " ORDER BY CASE WHEN UPPER(ar.status) = 'PENDING' THEN 0 ELSE 1 END, ar.appointment_date";
        }
        return null;
    }

    private void bindAppointmentsQueryParameters(PreparedStatement pstmt) throws Exception {
        if ((mainApp.isPatientLoggedIn() || mainApp.isDoctorLoggedIn()) && mainApp.getCurrentUserId() != null) {
            pstmt.setInt(1, mainApp.getCurrentUserId());
        }
    }

    private void handleBookAppointment(ComboBox<String> deptCombo,
                                       ComboBox<DoctorOption> doctorCombo,
                                       DatePicker datePicker,
                                       ComboBox<String> timeCombo,
                                       TextArea reasonArea) {
        Integer patientId = mainApp.getCurrentUserId();
        if (patientId == null) {
            showAlert("Error", "Please log in as a patient first.");
            return;
        }

        if (deptCombo.getValue() == null || doctorCombo.getValue() == null || datePicker.getValue() == null ||
                timeCombo.getValue() == null || reasonArea.getText().isBlank()) {
            showAlert("Error", "Please fill in all appointment fields.");
            return;
        }

        String appointmentDateTime = buildAppointmentDateTime(datePicker.getValue(), timeCombo.getValue());
        String reason = deptCombo.getValue() + " - " + reasonArea.getText().trim();

        String sql = """
                INSERT INTO appointment_requests (patient_id, doctor_id, appointment_date, reason, status, serial_number)
                VALUES (?, ?, ?, ?, 'PENDING', NULL)
                """;

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            pstmt.setInt(2, doctorCombo.getValue().id());
            pstmt.setString(3, appointmentDateTime);
            pstmt.setString(4, reason);
            pstmt.executeUpdate();

            showAlert("Appointment Booked", "Your appointment is pending for verification");
            deptCombo.getSelectionModel().clearSelection();
            doctorCombo.getSelectionModel().clearSelection();
            datePicker.setValue(null);
            timeCombo.getSelectionModel().clearSelection();
            reasonArea.clear();
            loadAppointments();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to book appointment.");
        }
    }

    private void approveAppointment(AppointmentRecord record) {
        if (record == null || "APPROVED".equalsIgnoreCase(record.getStatus())) {
            return;
        }


        String appointmentDate = extractDate(record.getAppointmentDate());
        int serialNumber = getNextSerialNumber(appointmentDate);

        String sql = """
                UPDATE appointment_requests
                SET status = 'APPROVED',
                    serial_number = ?,
                    verified_by = ?
                WHERE id = ?
                """;

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, serialNumber);
            if (mainApp.getCurrentUserId() != null) {
                pstmt.setInt(2, mainApp.getCurrentUserId());
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            pstmt.setInt(3, record.getId());
            pstmt.executeUpdate();

            loadAppointments();
            showAlert("Appointment Approved", "Appointment approved with serial number " + serialNumber + ".");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to approve appointment.");
        }
    }

    private void rejectAppointment(AppointmentRecord record) {
        if (record == null || "REJECTED".equalsIgnoreCase(record.getStatus())) {
            return;
        }

        String sql = """
        UPDATE appointment_requests
        SET status = 'REJECTED'
        WHERE id = ?
        """;

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, record.getId());
            pstmt.executeUpdate();

            loadAppointments();
            showAlert("Appointment Rejected", "The appointment has been rejected.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to reject appointment.");
        }
    }

    private int getNextSerialNumber(String appointmentDate) {
        String sql = """
                SELECT COALESCE(MAX(serial_number), 0) + 1 AS next_serial
                FROM appointment_requests
                WHERE substr(appointment_date, 1, 10) = ?
                  AND UPPER(status) = 'APPROVED'
                """;

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, appointmentDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("next_serial");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    private String buildAppointmentDateTime(LocalDate date, String timeText) {
        LocalTime time = LocalTime.parse(timeText.toUpperCase(), TIME_INPUT_FORMAT);
        return date + " " + time.format(TIME_STORAGE_FORMAT);
    }

    private String extractDate(String appointmentDateTime) {
        if (appointmentDateTime == null || appointmentDateTime.length() < 10) {
            return "";
        }
        return appointmentDateTime.substring(0, 10);
    }

    private String normalizeStatus(String status) {
        return status == null ? "" : status.toUpperCase();
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

    private static final class AppointmentRecord {
        private final int id;
        private final int patientId;
        private final int doctorId;
        private final StringProperty patientName;
        private final StringProperty doctorName;
        private final StringProperty appointmentDate;
        private final StringProperty reason;
        private final StringProperty status;
        private final IntegerProperty serialNumber;

        private AppointmentRecord(int id, int patientId, int doctorId, String patientName, String doctorName,
                                  String appointmentDate, String reason, String status, int serialNumber) {
            this.id = id;
            this.patientId = patientId;
            this.doctorId = doctorId;
            this.patientName = new SimpleStringProperty(patientName == null ? "-" : patientName);
            this.doctorName = new SimpleStringProperty(doctorName == null ? "-" : doctorName);
            this.appointmentDate = new SimpleStringProperty(appointmentDate == null ? "-" : appointmentDate);
            this.reason = new SimpleStringProperty(reason == null ? "-" : reason);
            this.status = new SimpleStringProperty(status == null ? "-" : status);
            this.serialNumber = new SimpleIntegerProperty(serialNumber);
        }

        public int getId() {
            return id;
        }

        public int getPatientId() {
            return patientId;
        }

        public int getDoctorId() {
            return doctorId;
        }

        public String getAppointmentDate() {
            return appointmentDate.get();
        }

        public String getStatus() {
            return status.get();
        }

        public StringProperty patientNameProperty() {
            return patientName;
        }

        public StringProperty doctorNameProperty() {
            return doctorName;
        }

        public StringProperty appointmentDateProperty() {
            return appointmentDate;
        }

        public StringProperty reasonProperty() {
            return reason;
        }

        public StringProperty statusProperty() {
            return status;
        }

        public IntegerProperty serialNumberProperty() {
            return serialNumber;
        }
    }

    private record DoctorOption(int id, String name) {
        @Override
        public String toString() {
            return name;
        }
    }

    private void deleteOldAppointments() {
        String sql = """
        DELETE FROM appointment_requests
        WHERE date(appointment_date) < date('now', '-7 days')
        """;

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
