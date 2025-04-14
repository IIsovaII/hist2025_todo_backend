package com.example.demo.mappers;

import com.example.demo.model.api.TodoRequestDTO;
import com.example.demo.model.api.TodoResponseDTO;
import com.example.demo.model.domain.Todo;
import org.springframework.stereotype.Component;

@Component
public class TodoMapper {

    public Todo toEntity(TodoRequestDTO dto) {
        Todo todo = new Todo();
        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());
        todo.setCompleted(dto.isCompleted());
        todo.setDeadline(dto.getDeadline());
        todo.setPriority(dto.getPriority());
        return todo;
    }

    public TodoResponseDTO toDto(Todo entity) {
        TodoResponseDTO dto = new TodoResponseDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setCompleted(entity.isCompleted());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setDeadline(entity.getDeadline());
        dto.setStatus(entity.getStatus());
        dto.setPriority(entity.getPriority());
        return dto;
    }
}
