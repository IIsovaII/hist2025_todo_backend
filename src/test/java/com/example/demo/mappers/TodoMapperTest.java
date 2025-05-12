package com.example.demo.mappers;

import com.example.demo.model.api.TodoRequestDTO;
import com.example.demo.model.api.TodoResponseDTO;
import com.example.demo.model.domain.Todo;
import com.example.demo.model.enums.TodoPriority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TodoMapperTest {
    private final TodoMapper mapper = new TodoMapper(); // Создаём экземпляр

    @Test
    @DisplayName("Из сущности в дто")
    void toDto() {
        Todo todo = new Todo();
        todo.setTitle("Test");
        todo.setDescription("Description");
        todo.setCompleted(false);
        todo.setPriority(TodoPriority.MEDIUM);
        todo.setDeadline(OffsetDateTime.now());

        TodoResponseDTO dto = mapper.toDto(todo);
        assertNotNull(dto);

        assertEquals("Test", dto.getTitle());
        assertEquals("Description", dto.getDescription());
        assertFalse(dto.isCompleted());
        assertEquals(TodoPriority.MEDIUM, dto.getPriority());
        assertEquals(todo.getDeadline(), dto.getDeadline());
    }

    @Test
    @DisplayName("Из дто в сущность")
    void toEntity() {
        TodoRequestDTO requestDTO = new TodoRequestDTO();
        requestDTO.setTitle("title");
        requestDTO.setPriority(TodoPriority.MEDIUM);
        requestDTO.setDescription("description");
        requestDTO.setCompleted(true);
        requestDTO.setDeadline(OffsetDateTime.now());

        Todo todo = mapper.toEntity(requestDTO);
        assertNotNull(todo);

        assertEquals(requestDTO.getTitle(), todo.getTitle());
        assertEquals(requestDTO.getDescription(), todo.getDescription());
        assertEquals(requestDTO.isCompleted(), todo.isCompleted());
        assertEquals(requestDTO.getPriority(), todo.getPriority());
        assertEquals(requestDTO.getDeadline(), todo.getDeadline());
    }

}