package org.example.healthaid;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ModuleRequestService {

    public enum ModuleType {
        LAB("lab_requests", true),
        PHARMACY("pharmacy_requests", false),
        OPERATIONS("operation_requests", true),
        BILLING("billing_requests", false),
        BEDS("bed_requests", false);

        private final String tableName;
        private final boolean visibleToDoctor;

        ModuleType(String tableName, boolean visibleToDoctor) {
            this.tableName = tableName;
            this.visibleToDoctor = visibleToDoctor;
        }

        public String tableName() {
            return tableName;
        }

        public boolean visibleToDoctor() {
            return visibleToDoctor;
        }
    }

    public List<RequestRecord> loadRequests(ModuleType moduleType, String role, Integer userId) {
        List<RequestRecord> requests = new ArrayList<>();
        String sql = buildSelectQuery(moduleType, role);
        if (sql == null) {
            return requests;
        }

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (requiresUserId(role) && userId != null) {
                pstmt.setInt(1, userId);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(new RequestRecord(
                            rs.getInt("id"),
                            rs.getInt("patient_id"),
                            rs.getString("patient_name"),
                            rs.getString("request_details"),
                            rs.getString("request_date"),
                            normalizeStatus(rs.getString("status")),
                            rs.getString("created_at")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return requests;
    }

    public boolean createRequest(ModuleType moduleType, int patientId, RequestPayload payload) {
        String sql = switch (moduleType) {
            case LAB -> """
                    INSERT INTO lab_requests (patient_id, request_details, date, status)
                    VALUES (?, ?, ?, 'PENDING')
                    """;
            case PHARMACY -> """
                    INSERT INTO pharmacy_requests (patient_id, medications, request_details, date, status)
                    VALUES (?, ?, ?, ?, 'PENDING')
                    """;
            case OPERATIONS -> """
                    INSERT INTO operation_requests (patient_id, operation_name, description, request_details, date, status)
                    VALUES (?, ?, ?, ?, ?, 'PENDING')
                    """;
            case BILLING -> """
                    INSERT INTO billing_requests (patient_id, description, amount, request_details, date, status)
                    VALUES (?, ?, ?, ?, ?, 'PENDING')
                    """;
            case BEDS -> """
                    INSERT INTO bed_requests (patient_id, reason, duration, request_details, date, status)
                    VALUES (?, ?, ?, ?, ?, 'PENDING')
                    """;
        };

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            switch (moduleType) {
                case LAB -> {
                    pstmt.setString(2, payload.requestDetails());
                    pstmt.setString(3, payload.date());
                }
                case PHARMACY -> {
                    pstmt.setString(2, payload.primaryValue());
                    pstmt.setString(3, payload.requestDetails());
                    pstmt.setString(4, payload.date());
                }
                case OPERATIONS -> {
                    pstmt.setString(2, payload.primaryValue());
                    pstmt.setString(3, payload.secondaryValue());
                    pstmt.setString(4, payload.requestDetails());
                    pstmt.setString(5, payload.date());
                }
                case BILLING -> {
                    pstmt.setString(2, payload.primaryValue());
                    pstmt.setDouble(3, payload.amount());
                    pstmt.setString(4, payload.requestDetails());
                    pstmt.setString(5, payload.date());
                }
                case BEDS -> {
                    pstmt.setString(2, payload.primaryValue());
                    pstmt.setString(3, payload.secondaryValue());
                    pstmt.setString(4, payload.requestDetails());
                    pstmt.setString(5, payload.date());
                }
            }
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRequestStatus(ModuleType moduleType, int requestId, String status, Integer handledBy) {
        String sql = "UPDATE " + moduleType.tableName() + " SET status = ?, handled_by = ? WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, normalizeStatus(status));
            if (handledBy == null) {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(2, handledBy);
            }
            pstmt.setInt(3, requestId);
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String buildSelectQuery(ModuleType moduleType, String role) {
        String detailsExpression = switch (moduleType) {
            case LAB -> "COALESCE(r.request_details, '')";
            case PHARMACY -> "COALESCE(r.request_details, r.medications)";
            case OPERATIONS -> "COALESCE(r.request_details, r.operation_name || ' - ' || r.description)";
            case BILLING -> "COALESCE(r.request_details, r.description || ' | Amount: ' || COALESCE(r.amount, 0))";
            case BEDS -> "COALESCE(r.request_details, r.reason || ' | Duration: ' || r.duration)";
        };

        String dateExpression = "COALESCE(r.date, substr(r.created_at, 1, 10), '')";
        String baseSql = """
                SELECT r.id,
                       r.patient_id,
                       u.full_name AS patient_name,
                       %s AS request_details,
                       %s AS request_date,
                       r.status,
                       r.created_at
                FROM %s r
                JOIN users u ON u.id = r.patient_id
                WHERE (UPPER(r.status) <> 'REJECTED' OR datetime(r.created_at) >= datetime('now', '-1 day'))
                """.formatted(detailsExpression, dateExpression, moduleType.tableName());

        if (role == null) {
            return null;
        }

        String normalizedRole = role.toUpperCase();
        return switch (normalizedRole) {
            case "PATIENT" -> baseSql + " AND r.patient_id = ? ORDER BY r.created_at DESC";
            case "STAFF", "ADMIN" -> baseSql + " ORDER BY CASE WHEN UPPER(r.status) = 'PENDING' THEN 0 ELSE 1 END, r.created_at DESC";
            case "DOCTOR" -> moduleType.visibleToDoctor()
                    ? baseSql + " AND UPPER(r.status) = 'APPROVED' ORDER BY r.created_at DESC"
                    : null;
            default -> null;
        };
    }

    private boolean requiresUserId(String role) {
        return role != null && "PATIENT".equalsIgnoreCase(role);
    }

    private String normalizeStatus(String status) {
        return status == null ? "" : status.toUpperCase();
    }

    public record RequestPayload(String requestDetails, String date, String primaryValue, String secondaryValue, double amount) {
    }

    public record RequestRecord(int id, int patientId, String patientName, String requestDetails,
                                String requestDate, String status, String createdAt) {
    }
}
