package com.example.demo.service;

import com.example.demo.mappers.TodoMapper;
import com.example.demo.model.api.TodoRequestDTO;
import com.example.demo.model.api.TodoResponseDTO;
import com.example.demo.model.api.TodoUpdateRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.example.demo.model.domain.Todo;
import com.example.demo.model.enums.TodoPriority;
import com.example.demo.repository.TodoRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class TodoServiceTest {
    @Mock
    private TodoRepository todoRepository;

    @Mock
    private TodoMapper mapper;

    @InjectMocks
    private TodoService todoService;

    @Test
    @DisplayName("Получение всего списка задач")
    void getAllTodos() {
        List<Todo> allTodos = todoRepository.findAll();
        assertEquals(allTodos, todoService.getAllTodos());
    }

    @Test
    void getTodoById_TodoExists_ReturnTodo() {
        Todo mockTodo = new Todo();
        UUID id = mockTodo.getId();
        mockTodo.setTitle("Test todo");
        mockTodo.setPriority(TodoPriority.MEDIUM);
        mockTodo.setDescription("Description");
        mockTodo.setCompleted(false);

        TodoResponseDTO expectedResponse = new TodoResponseDTO();
        expectedResponse.setId(id);
        expectedResponse.setTitle("Test todo");
        expectedResponse.setTitle(mockTodo.getTitle());
        expectedResponse.setPriority(mockTodo.getPriority());
        expectedResponse.setDescription(mockTodo.getDescription());
        expectedResponse.setCompleted(mockTodo.isCompleted());

        when(todoRepository.findById(id)).thenReturn(Optional.of(mockTodo));
        when(mapper.toDto(mockTodo)).thenReturn(expectedResponse);

        TodoResponseDTO result = todoService.getTodoById(id);

        assertEquals(expectedResponse, result);
        verify(todoRepository).findById(id);
    }

    @Test
    @DisplayName("Поиск несуществующего todo")
    void getTodoById_TodoNotExists_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(todoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> todoService.getTodoById(id));
    }

    @Test
    void createTodo() {
        TodoRequestDTO requestDTO = new TodoRequestDTO();
        requestDTO.setTitle("Test todo");
        requestDTO.setPriority(TodoPriority.MEDIUM);
        requestDTO.setDescription("Description");
        requestDTO.setCompleted(false);

        Todo mockTodo = new Todo();
        mockTodo.setTitle(requestDTO.getTitle());
        mockTodo.setPriority(requestDTO.getPriority());
        mockTodo.setDescription(requestDTO.getDescription());
        mockTodo.setCompleted(requestDTO.isCompleted());

        TodoResponseDTO expectedResponse = new TodoResponseDTO();
        expectedResponse.setId(mockTodo.getId());
        expectedResponse.setTitle(mockTodo.getTitle());
        expectedResponse.setPriority(mockTodo.getPriority());
        expectedResponse.setDescription(mockTodo.getDescription());
        expectedResponse.setCompleted(mockTodo.isCompleted());

        when(mapper.toEntity(requestDTO)).thenReturn(mockTodo);
        when(todoRepository.save(mockTodo)).thenReturn(mockTodo);
        when(mapper.toDto(mockTodo)).thenReturn(expectedResponse);

        TodoResponseDTO result = todoService.createTodo(requestDTO);

        assertEquals(expectedResponse, result);
        verify(mapper, times(1)).toEntity(requestDTO);
        verify(todoRepository, times(1)).save(mockTodo);
        verify(mapper, times(1)).toDto(mockTodo);
    }

    @Test
    void updateTodo_withAllParams() {
        Todo mockTodo = new Todo();
        UUID id = mockTodo.getId();
        mockTodo.setTitle("Test todo");
        mockTodo.setPriority(TodoPriority.MEDIUM);
        mockTodo.setDescription("Description");
        mockTodo.setCompleted(false);
        mockTodo.setDeadline(OffsetDateTime.now());

        when(todoRepository.findById(id)).thenReturn(Optional.of(mockTodo));
        mockTodo.setTitle("New title");

        TodoResponseDTO expectedResponse = new TodoResponseDTO();
        expectedResponse.setId(id);
        expectedResponse.setTitle(mockTodo.getTitle());
        expectedResponse.setPriority(mockTodo.getPriority());
        expectedResponse.setDescription(mockTodo.getDescription());
        expectedResponse.setCompleted(mockTodo.isCompleted());
        expectedResponse.setDeadline(mockTodo.getDeadline());


        when(mapper.toDto(mockTodo)).thenReturn(expectedResponse);

        TodoUpdateRequestDTO requestDTO = new TodoUpdateRequestDTO();
        requestDTO.setTitle(mockTodo.getTitle());
        requestDTO.setPriority(mockTodo.getPriority());
        requestDTO.setDescription(mockTodo.getDescription());
        requestDTO.setCompleted(mockTodo.isCompleted());
        requestDTO.setDeadline(mockTodo.getDeadline());

        TodoResponseDTO result = todoService.updateTodo(id, requestDTO);

        assertEquals(expectedResponse, result);
        verify(todoRepository).findById(id);
    }

    @Test
    void updateTodo_justCompleted() {
        Todo mockTodo = new Todo();
        UUID id = mockTodo.getId();
        mockTodo.setTitle("Test todo");
        mockTodo.setPriority(TodoPriority.MEDIUM);
        mockTodo.setDescription("Description");
        mockTodo.setCompleted(false);
        mockTodo.setDeadline(OffsetDateTime.now());

        when(todoRepository.findById(id)).thenReturn(Optional.of(mockTodo));
        mockTodo.setCompleted(true);

        TodoResponseDTO expectedResponse = new TodoResponseDTO();
        expectedResponse.setId(id);
        expectedResponse.setTitle(mockTodo.getTitle());
        expectedResponse.setPriority(mockTodo.getPriority());
        expectedResponse.setDescription(mockTodo.getDescription());
        expectedResponse.setCompleted(mockTodo.isCompleted());
        expectedResponse.setDeadline(mockTodo.getDeadline());

        when(mapper.toDto(mockTodo)).thenReturn(expectedResponse);

        TodoUpdateRequestDTO requestDTO = new TodoUpdateRequestDTO();
        requestDTO.setCompleted(mockTodo.isCompleted());

        TodoResponseDTO result = todoService.updateTodo(id, requestDTO);

        assertEquals(expectedResponse, result);
        verify(todoRepository).findById(id);
    }

    @Test
    void deleteTodo() {
        UUID id = UUID.randomUUID();
        todoService.deleteTodo(id);
        verify(todoRepository, times(1)).deleteById(id);
    }

}