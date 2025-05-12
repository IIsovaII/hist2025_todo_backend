package com.example.demo.model.domain;

import com.example.demo.model.enums.TodoStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class TodoTest {
    @Test
    @DisplayName("Обновление времени изменения при изменении")
    void whenPrePersist_thenSetsCreatedAtAndUpdatedAt() {
        Todo todo = new Todo();
        todo.setCompleted(false);
        todo.setDeadline(null);

        todo.updateTimeStampsAndStatus();

        assertNotNull(todo.getCreatedAt());
        assertNotNull(todo.getUpdatedAt());
        assertEquals(todo.getCreatedAt(), todo.getUpdatedAt());
        assertEquals(TodoStatus.ACTIVE, todo.getStatus());
    }

    @Test
    @DisplayName("Статус Overdue")
    void whenDeadlineIsPastAndNotCompleted_thenStatusOverdue() {
        Todo todo = new Todo();
        todo.setCompleted(false);
        todo.setDeadline(OffsetDateTime.now().minusDays(1));

        todo.updateTimeStampsAndStatus();

        assertEquals(TodoStatus.OVERDUE, todo.getStatus());
    }

    @Test
    @DisplayName("Статус Late")
    void whenDeadlineIsPastAndCompleted_thenStatusLate() {
        Todo todo = new Todo();
        todo.setCompleted(true);
        todo.setDeadline(OffsetDateTime.now().minusDays(1));

        todo.updateTimeStampsAndStatus();

        assertEquals(TodoStatus.LATE, todo.getStatus());
    }

    @Test
    @DisplayName("Статус Completed")
    void whenDeadlineIsFutureAndCompleted_thenStatusCompleted() {
        Todo todo = new Todo();
        todo.setCompleted(true);
        todo.setDeadline(OffsetDateTime.now().plusDays(1));

        todo.updateTimeStampsAndStatus();

        assertEquals(TodoStatus.COMPLETED, todo.getStatus());
    }

    @Test
    @DisplayName("Статус Active")
    void whenDeadlineIsFutureAndNotCompleted_thenStatusActive() {
        Todo todo = new Todo();
        todo.setCompleted(false);
        todo.setDeadline(OffsetDateTime.now().plusDays(1));

        todo.updateTimeStampsAndStatus();

        assertEquals(TodoStatus.ACTIVE, todo.getStatus());
    }

    @Test
    @DisplayName("Заполнения времени создания при создании задачи")
    void prePersist_SetsCreatedAt_WhenNull() {
        Todo todo = new Todo();
        todo.setTitle("Test");

        todo.updateTimeStampsAndStatus();

        assertNotNull(todo.getCreatedAt());
        assertNotNull(todo.getUpdatedAt());
        assertEquals(todo.getCreatedAt(), todo.getUpdatedAt());
    }

    @Test
    @DisplayName("Неизменность времени создания при изменении задачи")
    void prePersist_DoesNotChangeCreatedAt_WhenAlreadySet() {
        OffsetDateTime fixedTime = OffsetDateTime.now().minusDays(1);
        Todo todo = new Todo();
        todo.setTitle("Test");
        todo.setCreatedAt(fixedTime);

        todo.updateTimeStampsAndStatus();

        assertEquals(todo.getCreatedAt(), fixedTime);
        assertTrue(todo.getUpdatedAt().isAfter(fixedTime));
    }

}