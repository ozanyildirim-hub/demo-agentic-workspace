package com.example.todo;

import com.example.todo.model.Todo;
import com.example.todo.model.TodoRequest;
import com.example.todo.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Todo buildTodo(Long id, String title, boolean completed) {
        Todo todo = new Todo();
        todo.setId(id);
        todo.setTitle(title);
        todo.setCompleted(completed);
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());
        return todo;
    }

    @Test
    void getAllTodos_returnsList() throws Exception {
        when(todoService.getAllTodos()).thenReturn(List.of(
                buildTodo(1L, "Buy groceries", false),
                buildTodo(2L, "Read a book", true)
        ));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Buy groceries"));
    }

    @Test
    void getTodoById_found() throws Exception {
        when(todoService.getTodoById(1L)).thenReturn(buildTodo(1L, "Buy groceries", false));

        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Buy groceries"));
    }

    @Test
    void createTodo_valid() throws Exception {
        TodoRequest request = new TodoRequest();
        request.setTitle("New Task");

        Todo saved = buildTodo(3L, "New Task", false);
        when(todoService.createTodo(any(TodoRequest.class))).thenReturn(saved);

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    @Test
    void createTodo_blankTitle_returnsBadRequest() throws Exception {
        TodoRequest request = new TodoRequest();
        request.setTitle("");

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTodo_valid() throws Exception {
        TodoRequest request = new TodoRequest();
        request.setTitle("Updated Task");
        request.setCompleted(true);

        Todo updated = buildTodo(1L, "Updated Task", true);
        when(todoService.updateTodo(eq(1L), any(TodoRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void toggleCompleted() throws Exception {
        Todo toggled = buildTodo(1L, "Buy groceries", true);
        when(todoService.toggleCompleted(1L)).thenReturn(toggled);

        mockMvc.perform(patch("/api/todos/1/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void deleteTodo() throws Exception {
        doNothing().when(todoService).deleteTodo(1L);

        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isNoContent());

        verify(todoService).deleteTodo(1L);
    }
}
