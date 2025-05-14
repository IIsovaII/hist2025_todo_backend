package com.example.demo.service;

import com.example.demo.mappers.TodoMapper;
import com.example.demo.model.api.TodoRequestDTO;
import com.example.demo.model.api.TodoResponseDTO;
import com.example.demo.model.api.TodoUpdateRequestDTO;
import com.example.demo.model.domain.Todo;
import com.example.demo.repository.TodoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Slf4j
@Service
public class TodoService {
    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;

    public TodoService(TodoRepository todoRepository, TodoMapper todoMapper) {
        this.todoRepository = todoRepository;
        this.todoMapper = todoMapper;
    }

    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    public TodoResponseDTO getTodoById(UUID id) {
        Todo todo =  todoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("TODO not found"));
        return todoMapper.toDto(todo);
    }

    public TodoResponseDTO createTodo(TodoRequestDTO todoDTO) {
        Todo todo = todoMapper.toEntity(todoDTO);
        Todo savedTodo = todoRepository.save(todo);
        return todoMapper.toDto(savedTodo);
    }


    public TodoResponseDTO updateTodo(UUID id, TodoUpdateRequestDTO requestDTO){
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("TODO not found"));

        if (requestDTO.getTitle() != null) {
            todo.setTitle(requestDTO.getTitle());
        }
        if (requestDTO.getDescription() != null) {
            todo.setDescription(requestDTO.getDescription());
        }

        todo.setCompleted(requestDTO.isCompleted());

//        log.info();
        if (requestDTO.wasDeadlineSet()) {
            todo.setDeadline(requestDTO.getDeadline());
        }

        if (requestDTO.getPriority() != null) {
            todo.setPriority(requestDTO.getPriority());
        }

        todoRepository.save(todo);
        return todoMapper.toDto(todo);
    }

    public void deleteTodo(UUID id) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("TODO not found"));
        todoRepository.delete(todo);
    }
}
