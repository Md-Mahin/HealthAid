package org.example.healthaid;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public final class ModuleRequestSectionFactory {

    private ModuleRequestSectionFactory() {
    }

    public static SectionBundle createSection(HelloApplication mainApp,
                                              ModuleRequestService service,
                                              ModuleRequestService.ModuleType moduleType,
                                              String titleText) {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setStyle("-fx-background-color: rgba(27, 110, 85, 0.8); -fx-background-radius: 10;");
        section.setPadding(new Insets(20));

        Text sectionTitle = new Text(titleText);
        sectionTitle.setFont(Font.font("Jost", 18));
        sectionTitle.setFill(Color.WHITE);

        if (mainApp.isDoctorLoggedIn() && !moduleType.visibleToDoctor()) {
            Label accessLabel = new Label("This module is not available for doctor accounts.");
            accessLabel.setTextFill(Color.WHITE);
            accessLabel.setFont(Font.font("Jost", 14));
            section.getChildren().addAll(sectionTitle, accessLabel);
            return new SectionBundle(section, () -> {
            });
        }

        TableView<ModuleRequestService.RequestRecord> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setMinHeight(250);
        table.setPrefHeight(280);
        table.setMaxHeight(280);
        table.setPlaceholder(createPlaceholder(mainApp));
        table.getStyleClass().add("healthaid-table");
        if (moduleType == ModuleRequestService.ModuleType.LAB) {
            table.getStyleClass().add("lab-request-table");
            table.setStyle("""
                    -fx-background-color: #1b6f55;
                    -fx-control-inner-background: #1b6f55;
                    -fx-control-inner-background-alt: #157a52;
                    -fx-table-cell-border-color: rgba(255,255,255,0.12);
                    -fx-text-background-color: white;
                    """);
            table.setRowFactory(tv -> {
                TableRow<ModuleRequestService.RequestRecord> row = new TableRow<>();
                row.setStyle("""
                        -fx-background-color: #1b6f55;
                        -fx-background-insets: 0;
                        -fx-text-fill: white;
                        """);
                return row;
            });
        }

        TableColumn<ModuleRequestService.RequestRecord, String> patientColumn = new TableColumn<>("Patient Name");
        patientColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().patientName()));
        patientColumn.setVisible(mainApp.isStaffLoggedIn() || mainApp.isAdminLoggedIn());

        TableColumn<ModuleRequestService.RequestRecord, String> detailsColumn = new TableColumn<>("Request Details");
        detailsColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().requestDetails()));

        TableColumn<ModuleRequestService.RequestRecord, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().requestDate()));

        TableColumn<ModuleRequestService.RequestRecord, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().status()));

        table.getColumns().addAll(patientColumn, detailsColumn, dateColumn, statusColumn);

        ObservableList<ModuleRequestService.RequestRecord> items = FXCollections.observableArrayList();
        table.setItems(items);

        Runnable refresh = () -> items.setAll(
                service.loadRequests(moduleType, mainApp.getCurrentUserRole(), mainApp.getCurrentUserId())
        );

        if (mainApp.isStaffLoggedIn() || mainApp.isAdminLoggedIn()) {
            TableColumn<ModuleRequestService.RequestRecord, Void> approveColumn = new TableColumn<>("Approve");
            approveColumn.setCellFactory(column -> createActionCell(mainApp, service, moduleType, refresh, "APPROVED", "Approve"));

            TableColumn<ModuleRequestService.RequestRecord, Void> rejectColumn = new TableColumn<>("Reject");
            rejectColumn.setCellFactory(column -> createActionCell(mainApp, service, moduleType, refresh, "REJECTED", "Reject"));

            table.getColumns().addAll(approveColumn, rejectColumn);
        }

        refresh.run();
        VBox.setVgrow(table, Priority.NEVER);
        section.getChildren().addAll(sectionTitle, table);
        return new SectionBundle(section, refresh);
    }

    private static TableCell<ModuleRequestService.RequestRecord, Void> createActionCell(HelloApplication mainApp,
                                                                                         ModuleRequestService service,
                                                                                         ModuleRequestService.ModuleType moduleType,
                                                                                         Runnable refresh,
                                                                                         String targetStatus,
                                                                                         String buttonText) {
        return new TableCell<>() {
            private final Button actionButton = new Button(buttonText);

            {
                actionButton.setStyle("""
                        -fx-font-size: 11;
                        -fx-padding: 6 12 6 12;
                        -fx-background-color: #00f5ff;
                        -fx-text-fill: #0a192f;
                        -fx-background-radius: 4;
                        -fx-cursor: hand;
                        """);
                if ("REJECTED".equals(targetStatus)) {
                    actionButton.setStyle("""
                            -fx-font-size: 11;
                            -fx-padding: 6 12 6 12;
                            -fx-background-color: #ff6b6b;
                            -fx-text-fill: white;
                            -fx-background-radius: 4;
                            -fx-cursor: hand;
                            """);
                }

                actionButton.setOnAction(event -> {
                    ModuleRequestService.RequestRecord record = getTableView().getItems().get(getIndex());
                    boolean updated = service.updateRequestStatus(moduleType, record.id(), targetStatus, mainApp.getCurrentUserId());
                    if (updated) {
                        refresh.run();
                        showInfo("Request Updated", "Request marked as " + targetStatus + ".");
                    } else {
                        showInfo("Error", "Failed to update request.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }

                ModuleRequestService.RequestRecord record = getTableRow().getItem();
                actionButton.setDisable(!"PENDING".equalsIgnoreCase(record.status()));
                setGraphic(actionButton);
            }
        };
    }

    private static Label createPlaceholder(HelloApplication mainApp) {
        Label label = new Label("No requests found for this role.");
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Jost", 14));
        return label;
    }

    private static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public record SectionBundle(VBox section, Runnable refresh) {
    }
}
