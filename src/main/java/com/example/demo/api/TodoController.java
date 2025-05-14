package com.example.demo.api;

import com.example.demo.model.api.TodoRequestDTO;
import com.example.demo.model.api.TodoResponseDTO;
import com.example.demo.model.api.TodoUpdateRequestDTO;
import com.example.demo.model.domain.Todo;
import com.example.demo.service.TodoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/todo")
@Tag(name = "Todo")
public class TodoController {
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<Todo> getMyTodos() {
        return todoService.getAllTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponseDTO> getTodo(@PathVariable UUID id) {
        return ResponseEntity.ok(todoService.getTodoById(id));
    }

    @PostMapping
    public ResponseEntity<TodoResponseDTO> createTodo(@RequestBody @Valid TodoRequestDTO requestDTO) {
        return ResponseEntity.ok(todoService.createTodo(requestDTO));
    }

    @PutMapping("{id}")
    public ResponseEntity<TodoResponseDTO> updateTodo(
            @PathVariable UUID id,
            @RequestBody TodoUpdateRequestDTO requestDTO) {

        return ResponseEntity.ok(todoService.updateTodo(id, requestDTO));

    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable UUID id) {
        todoService.deleteTodo(id);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(EntityNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }
}
