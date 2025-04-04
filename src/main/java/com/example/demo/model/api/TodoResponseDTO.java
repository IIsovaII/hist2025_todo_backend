package com.example.demo.model.api;

import com.example.demo.model.enums.TodoPriority;
import com.example.demo.model.enums.TodoStatus;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class TodoResponseDTO {
    private UUID id;
    private String title;
    private String description;
    private boolean completed;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime deadline;
    private TodoStatus status;
    private TodoPriority priority;
}