package com.example.dbstub;

import java.sql.*;

public class DbStubApplication {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/mydb";
    private static final String DB_USER = "myuser";
    private static final String DB_PASSWORD = "mypassword";

    public static void main(String[] args) {
        System.out.println("Database Stub Application starting...");

        try {
            System.out.println("Waiting for PostgreSQL...");
            waitForDatabase();
            System.out.println("PostgreSQL is ready");

            System.out.println("Running Liquibase migrations...");
            runLiquibase();
            System.out.println("Liquibase migrations completed");

            System.out.println("Application is ready");
            System.out.println("Tables: task, response, request");
            System.out.println("Press Ctrl+C to stop");

            keepAlive();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void waitForDatabase() throws Exception {
        int attempts = 0;
        int maxAttempts = 30;

        while (attempts < maxAttempts) {
            try {
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                conn.close();
                return;
            } catch (SQLException e) {
                attempts++;
                System.out.println("Attempt " + attempts + "/" + maxAttempts + " - waiting...");
                Thread.sleep(2000);
            }
        }
        throw new RuntimeException("Database not ready after " + maxAttempts + " attempts");
    }

    private static void runLiquibase() throws Exception {
        String command = "liquibase " +
            "--url=" + DB_URL + " " +
            "--username=" + DB_USER + " " +
            "--password=" + DB_PASSWORD + " " +
            "--changeLogFile=src/main/resources/db/changelog/db.changelog-master.yaml " +
            "--defaultSchemaName=myschema " +
            "update";

        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Liquibase failed with exit code: " + exitCode);
        }
    }

    private static void keepAlive() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
        }));

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
