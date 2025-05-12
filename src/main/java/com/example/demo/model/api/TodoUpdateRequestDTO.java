package com.example.demo.model.api;

import com.example.demo.model.enums.TodoPriority;
import jakarta.annotation.Nullable;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class TodoUpdateRequestDTO {
    @Nullable
    private String title;
    @Nullable
    private String description;

    private boolean completed;
    @Nullable
    private OffsetDateTime deadline;
    private boolean deadlineWasSet = false;

    public void setDeadline(OffsetDateTime deadline) {
        this.deadline = deadline;
        this.deadlineWasSet = true;
    }
    public boolean wasDeadlineSet() {
        return deadlineWasSet;
    }

    @Nullable
    private TodoPriority priority;
}
