package org.example.healthaid;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Database {

    private static final String URL = "jdbc:sqlite:data/healthaid.db";

    public static Connection connect() {
        try {
            System.out.println("Trying to connect to: " + URL);
            Connection conn = DriverManager.getConnection(URL);
            System.out.println("Connection success! DB path: " + System.getProperty("user.dir") + "/healthaid.db");
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void initialize() {
        String[] sqlStatements = {
                """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    full_name TEXT NOT NULL,
                    phone TEXT NOT NULL,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL
                );
                """,
                """
                CREATE TABLE IF NOT EXISTS lab_requests (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    patient_id INTEGER NOT NULL,
                    request_details TEXT NOT NULL,
                    date TEXT,
                    status TEXT DEFAULT 'PENDING',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    handled_by INTEGER,
                    FOREIGN KEY(patient_id) REFERENCES users(id)
                );
                """,
                """
                CREATE TABLE IF NOT EXISTS appointment_requests (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    patient_id INTEGER NOT NULL,
                    doctor_id INTEGER,
                    appointment_date TEXT NOT NULL,
                    reason TEXT NOT NULL,
                    status TEXT DEFAULT 'PENDING',
                    serial_number INTEGER,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    verified_by INTEGER,
                    confirmed_by INTEGER,
                    FOREIGN KEY(patient_id) REFERENCES users(id)
                );
                """,
                """
                CREATE TABLE IF NOT EXISTS operation_requests (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    patient_id INTEGER NOT NULL,
                    operation_name TEXT NOT NULL,
                    description TEXT NOT NULL,
                    request_details TEXT,
                    date TEXT,
                    status TEXT DEFAULT 'PENDING',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    handled_by INTEGER,
                    verified_by INTEGER,
                    confirmed_by INTEGER,
                    FOREIGN KEY(patient_id) REFERENCES users(id)
                );
                """,
                """
                CREATE TABLE IF NOT EXISTS pharmacy_requests (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    patient_id INTEGER NOT NULL,
                    medications TEXT NOT NULL,
                    request_details TEXT,
                    date TEXT,
                    status TEXT DEFAULT 'PENDING',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    handled_by INTEGER,
                    verified_by INTEGER,
                    confirmed_by INTEGER,
                    FOREIGN KEY(patient_id) REFERENCES users(id)
                );
                """,
                """
                CREATE TABLE IF NOT EXISTS billing_requests (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    patient_id INTEGER NOT NULL,
                    description TEXT NOT NULL,
                    amount REAL NOT NULL,
                    request_details TEXT,
                    date TEXT,
                    status TEXT DEFAULT 'PENDING',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    handled_by INTEGER,
                    verified_by INTEGER,
                    confirmed_by INTEGER,
                    FOREIGN KEY(patient_id) REFERENCES users(id)
                );
                """,
                """
                CREATE TABLE IF NOT EXISTS bed_requests (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    patient_id INTEGER NOT NULL,
                    reason TEXT NOT NULL,
                    duration TEXT NOT NULL,
                    request_details TEXT,
                    date TEXT,
                    status TEXT DEFAULT 'PENDING',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    handled_by INTEGER,
                    verified_by INTEGER,
                    confirmed_by INTEGER,
                    FOREIGN KEY(patient_id) REFERENCES users(id)
                );
                """,
                """
                CREATE TABLE IF NOT EXISTS staff_assignments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    staff_id INTEGER NOT NULL,
                    staff_name TEXT NOT NULL,
                    role TEXT NOT NULL,
                    shift_date TEXT NOT NULL,
                    assigned_by INTEGER NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY(staff_id) REFERENCES users(id),
                    FOREIGN KEY(assigned_by) REFERENCES users(id)
                );
                """
        };

        Connection conn = null;
        Statement stmt = null;
        try {
            conn = connect();
            if (conn == null) {
                throw new IllegalStateException("Database connection failed, initialize aborted");
            }

            stmt = conn.createStatement();
            System.out.println("Database connection successful");
            for (String sql : sqlStatements) {
                stmt.execute(sql);
            }

            ensureColumnExists(stmt, "appointment_requests", "serial_number", "INTEGER");
            ensureColumnExists(stmt, "staff_assignments", "staff_name", "TEXT");
            ensureColumnExists(stmt, "staff_assignments", "role", "TEXT");
            ensureColumnExists(stmt, "staff_assignments", "shift_date", "TEXT");
            ensureColumnExists(stmt, "staff_assignments", "assigned_by", "INTEGER");
            ensureRequestColumns(stmt, "operation_requests");
            ensureRequestColumns(stmt, "pharmacy_requests");
            ensureRequestColumns(stmt, "billing_requests");
            ensureRequestColumns(stmt, "bed_requests");
            System.out.println("Database initialized (all tables created if not exists)");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void ensureRequestColumns(Statement stmt, String tableName) throws Exception {
        ensureColumnExists(stmt, tableName, "request_details", "TEXT");
        ensureColumnExists(stmt, tableName, "date", "TEXT");
        ensureColumnExists(stmt, tableName, "handled_by", "INTEGER");
    }

    private static void ensureColumnExists(Statement stmt, String tableName, String columnName, String definition) throws Exception {
        try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + tableName + ")")) {
            while (rs.next()) {
                if (columnName.equalsIgnoreCase(rs.getString("name"))) {
                    return;
                }
            }
        }
        stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + definition);
    }
}
