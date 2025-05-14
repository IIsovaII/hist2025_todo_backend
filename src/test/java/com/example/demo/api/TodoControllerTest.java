package com.example.demo.api;

import com.example.demo.model.api.TodoRequestDTO;
import com.example.demo.model.api.TodoUpdateRequestDTO;
import com.example.demo.model.enums.TodoPriority;
import com.example.demo.service.TodoService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.ResultActions;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TodoService todoService;

    private UUID existingTodoId;
    private final UUID nonExistingTodoId = UUID.randomUUID();

    @BeforeEach
    void setup() {
        TodoRequestDTO request = new TodoRequestDTO();
        request.setTitle("Initial Todo");
        request.setPriority(TodoPriority.MEDIUM);
        request.setDescription("Test description");
        request.setCompleted(false);

        existingTodoId = todoService.createTodo(request).getId();
    }

    @AfterEach
    void tearDown() {
        todoService.getAllTodos().forEach(todo -> todoService.deleteTodo(todo.getId()));
    }

    @Test
    @DisplayName("Вывод списка задач")
    void getMyTodos_ShouldReturnAllTodos() throws Exception {
        mockMvc.perform(get("/todo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].title").value("Initial Todo"));
    }

    @Test
    @DisplayName("Получение задачи по валидному id")
    void getTodo_WhenExists_ShouldReturnTodo() throws Exception {
        mockMvc.perform(get("/todo/{id}", existingTodoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingTodoId.toString()))
                .andExpect(jsonPath("$.title").value("Initial Todo"));
    }

    @Test
    @DisplayName("Получение задачи по не валидному id -> исключение")
    void getTodo_WhenNotExists_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/todo/{id}", nonExistingTodoId))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("validTodoRequests")
    @DisplayName("Создание задачи с валидными данными")
    void createTodo_WithValidRequest_ShouldReturnCreatedTodo(
            String title, String description, boolean completed,
            OffsetDateTime deadline, TodoPriority priority) throws Exception {

        TodoRequestDTO request = new TodoRequestDTO();
        request.setTitle(title);
        request.setDescription(description);
        request.setCompleted(completed);
        request.setDeadline(deadline);
        request.setPriority(priority);

        performCreateRequest(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.completed").value(completed))
                .andExpect(jsonPath("$.priority").value(priority == null ? null : priority.toString()));
    }

    private ResultActions performCreateRequest(TodoRequestDTO request) throws Exception {
        return mockMvc.perform(post("/todo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    private static Stream<Arguments> validTodoRequests() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime past = now.minusDays(1);
        OffsetDateTime future = now.plusDays(1);

        return Stream.of(
                // title, description, completed, deadline, priority
                Arguments.of("Valid title", "Normal description", false, null, TodoPriority.MEDIUM),
                Arguments.of("Short", "", true, OffsetDateTime.now(), TodoPriority.HIGH),
                Arguments.of("Long title".repeat(10), null, false, OffsetDateTime.now().plusDays(1), TodoPriority.LOW),
                Arguments.of("With special chars !@#*/-+.%^&(){}ы", "   ", true, null, TodoPriority.MEDIUM),

                // граничные для title
                Arguments.of("abcd", "Desc", false, null, TodoPriority.MEDIUM),
                Arguments.of("X".repeat(200), "Desc", false, null, TodoPriority.MEDIUM),

                // Комбинации для deadline
                Arguments.of("Valid title", "Desc", false, null, TodoPriority.MEDIUM),         // null deadline
                Arguments.of("Valid title", "Desc", false, past, TodoPriority.MEDIUM),          // Прошедшая дата
                Arguments.of("Valid title", "Desc", false, now, TodoPriority.MEDIUM),           // Текущая дата
                Arguments.of("Valid title", "Desc", false, future, TodoPriority.MEDIUM),         // Будущая дата

                // Комбинации для priority
                Arguments.of("Valid title", "Desc", false, null, TodoPriority.LOW),
                Arguments.of("Valid title", "Desc", false, null, TodoPriority.MEDIUM),
                Arguments.of("Valid title", "Desc", false, null, TodoPriority.HIGH),
                Arguments.of("Valid title", "Desc", false, null, TodoPriority.CRITICAL),

                // Комбинации completed
                Arguments.of("Valid title", "Desc", true, null, TodoPriority.MEDIUM),           // completed=true
                Arguments.of("Valid title", "Desc", false, null, TodoPriority.MEDIUM)           // completed=false
        );
    }


    @ParameterizedTest
    @MethodSource("invalidTodoRequests")
    @DisplayName("Создание задачи с невалидными данными -> исключение")
    void createTodo_WithInvalidRequest_ShouldReturnBadRequest(
            String title, String description, boolean completed,
            OffsetDateTime deadline, TodoPriority priority,
            List<String> expectedErrorFields) throws Exception {

        TodoRequestDTO request = new TodoRequestDTO();
        request.setTitle(title);
        request.setDescription(description);
        request.setCompleted(completed);
        request.setDeadline(deadline);
        request.setPriority(priority);

        ResultActions result = performCreateRequest(request)
                .andExpect(status().isBadRequest());

        for (String field : expectedErrorFields) {
            result.andExpect(jsonPath("$." + field).exists());
        }
    }

    private static Stream<Arguments> invalidTodoRequests() {
        OffsetDateTime pastDeadline = OffsetDateTime.now().minusDays(1);
        OffsetDateTime futureDeadline = OffsetDateTime.now().plusDays(1);

        return Stream.of(
                // title, description, completed, deadline, priority, массив ошибок
                // Невалидный title
                Arguments.of(
                        null, "Description", false, futureDeadline, TodoPriority.HIGH,
                        List.of("title")
                ),
                Arguments.of(
                        "", "Description", true, null, TodoPriority.MEDIUM,
                        List.of("title")
                ),
                Arguments.of(
                        "   ", "Description", false, pastDeadline, TodoPriority.MEDIUM,
                        List.of("title")
                ),
                Arguments.of(
                        "A", "Description", true, null, TodoPriority.MEDIUM,
                        List.of("title")
                ),
                Arguments.of(
                        "X".repeat(300), "Description", true, null, TodoPriority.MEDIUM,
                        List.of("title")
                ),
                // граничные на title
                Arguments.of(
                        "abc", "Description", true, null, TodoPriority.MEDIUM,
                        List.of("title")
                ),
                Arguments.of(
                        "X".repeat(201), "Description", true, null, TodoPriority.MEDIUM,
                        List.of("title")
                ),


                // Невалидный priority
                Arguments.of(
                        "Valid title", "Desc", false, null, null,
                        List.of("priority")
                ),

                // Комбинированные ошибки
                Arguments.of(
                        null, null, true, null, null,
                        List.of("title", "priority")
                )
        );
    }


    @Test
    @DisplayName("Обновление задачи по валидному id")
    void updateTodo_WhenExists_ShouldReturnUpdatedTodo() throws Exception {
        TodoUpdateRequestDTO updateRequest = new TodoUpdateRequestDTO();
        updateRequest.setTitle("Updated Title");
        updateRequest.setPriority(TodoPriority.MEDIUM);
        updateRequest.setDescription("Updated description");
        updateRequest.setCompleted(false);

        mockMvc.perform(put("/todo/{id}", existingTodoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }


    @Test
    @DisplayName("Обновление задачи по не валидному id -> исключение")
    void updateTodo_WhenNotExists_ShouldReturnNotFound() throws Exception {
        TodoUpdateRequestDTO updateRequest = new TodoUpdateRequestDTO();
        updateRequest.setTitle("Title");
        updateRequest.setPriority(TodoPriority.MEDIUM);
        updateRequest.setDescription("Updated description");
        updateRequest.setCompleted(false);

        mockMvc.perform(put("/todo/{id}", nonExistingTodoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Удаление задачи по валидному id")
    void deleteTodo_WhenExists_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/todo/{id}", existingTodoId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/todo/{id}", existingTodoId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Удаление задачи по не валидному id -> исключение")
    void deleteTodo_WhenNotExists_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/todo/{id}", nonExistingTodoId))
                .andExpect(status().isNotFound());
    }
}