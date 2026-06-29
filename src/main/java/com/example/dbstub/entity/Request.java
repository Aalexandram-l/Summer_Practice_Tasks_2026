package com.example.dbstub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "request",
    schema = "myschema",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_request_task_id", columnNames = "task_id"),
        @UniqueConstraint(name = "uk_request_response_id", columnNames = "response_id")
    }
)
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "task_id", nullable = false, foreignKey = @ForeignKey(name = "fk_request_task"))
    private Task task;

    @OneToOne
    @JoinColumn(name = "response_id", nullable = false, foreignKey = @ForeignKey(name = "fk_request_response"))
    private Response response;

    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }
    public Response getResponse() { return response; }
    public void setResponse(Response response) { this.response = response; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
