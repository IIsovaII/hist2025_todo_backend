package com.example.demo.model.domain;

import com.example.demo.model.enums.TodoPriority;
import com.example.demo.model.enums.TodoStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private boolean completed;

    @Column(columnDefinition = "TIMESTAMP", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(columnDefinition = "TIMESTAMP")
    private OffsetDateTime deadline;

    private TodoStatus status;

    private TodoPriority priority;

    @PrePersist
    @PreUpdate
    public void updateTimeStampsAndStatus() {
        OffsetDateTime now = OffsetDateTime.now();

        this.updatedAt = now;
        if (this.createdAt == null) {
            this.createdAt = now;
        }

        if (this.deadline == null || now.isBefore(this.deadline)) {
            if (this.completed) {
                this.status = TodoStatus.COMPLETED;
            } else {
                this.status = TodoStatus.ACTIVE;
            }
        } else {
            if (this.completed) {
                this.status = TodoStatus.LATE;
            } else {
                this.status = TodoStatus.OVERDUE;
            }
        }
    }
}
