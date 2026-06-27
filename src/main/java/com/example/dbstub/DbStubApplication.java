package com.example.dbstub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class DbStubApplication {

    private static final Logger log = LoggerFactory.getLogger(DbStubApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DbStubApplication.class, args);
        log.info("Database Stub Application started successfully!");
        keepAlive();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        log.info("===========================================");
        log.info("Application is ready!");
        log.info("Tables: task, response, request");
        log.info("Press Ctrl+C to stop");
        log.info("===========================================");
    }

    private static void keepAlive() {
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
