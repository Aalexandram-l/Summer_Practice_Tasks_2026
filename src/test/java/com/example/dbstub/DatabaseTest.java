package com.example.dbstub;

import org.junit.jupiter.api.Test;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {

    @Test
    void testSchemaExists() throws SQLException {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/mydb",
                "myuser", "mypassword")) {
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(
                    "SELECT schema_name FROM information_schema.schemata WHERE schema_name = 'myschema'"
                );
                assertTrue(rs.next());
            }
        }
    }

    @Test
    void testTablesExist() throws SQLException {
        String[] tables = {"task", "response", "request"};
        try (Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/mydb",
                "myuser", "mypassword")) {
            for (String table : tables) {
                try (Statement stmt = conn.createStatement()) {
                    ResultSet rs = stmt.executeQuery(
                        "SELECT table_name FROM information_schema.tables WHERE table_schema = 'myschema' AND table_name = '" + table + "'"
                    );
                    assertTrue(rs.next());
                }
            }
        }
    }
}
