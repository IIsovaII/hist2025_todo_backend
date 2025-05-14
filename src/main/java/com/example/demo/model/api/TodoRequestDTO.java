package com.example.demo.model.api;

import com.example.demo.model.enums.TodoPriority;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class TodoRequestDTO {
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 4, max = 200, message = "Title must be between 3 and 100 characters")
    private String title;
    private String description;
    @NotNull(message = "Completed cannot be null")
    private boolean completed;
    private OffsetDateTime deadline;
    @NotNull(message = "Priority cannot be null")
    private TodoPriority priority;
}
