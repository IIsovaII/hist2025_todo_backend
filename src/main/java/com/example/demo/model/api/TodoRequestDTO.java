package com.example.demo.model.api;

import com.example.demo.model.enums.TodoPriority;
import jakarta.annotation.Nullable;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class TodoRequestDTO {
    private String title;
    private String description;
    private boolean completed;
    private OffsetDateTime deadline;
    private TodoPriority priority;
}
