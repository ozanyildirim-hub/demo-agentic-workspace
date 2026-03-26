package com.example.todo;

import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoRequest;
import com.example.todo.repository.TodoRepository;
import com.example.todo.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private Todo sampleTodo;

    @BeforeEach
    void setUp() {
        sampleTodo = new Todo();
        sampleTodo.setId(1L);
        sampleTodo.setTitle("Sample Task");
        sampleTodo.setCompleted(false);
    }

    @Test
    void getAllTodos_returnsList() {
        when(todoRepository.findAll()).thenReturn(List.of(sampleTodo));

        List<Todo> result = todoService.getAllTodos();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Sample Task");
    }

    @Test
    void getTodoById_found() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));

        Todo result = todoService.getTodoById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getTodoById_notFound_throws() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.getTodoById(99L))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createTodo_savesAndReturns() {
        TodoRequest request = new TodoRequest();
        request.setTitle("New Task");
        request.setDescription("Some description");

        when(todoRepository.save(any(Todo.class))).thenAnswer(inv -> {
            Todo t = inv.getArgument(0);
            t.setId(2L);
            return t;
        });

        Todo result = todoService.createTodo(request);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getTitle()).isEqualTo("New Task");
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    void toggleCompleted_flipsStatus() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(inv -> inv.getArgument(0));

        Todo result = todoService.toggleCompleted(1L);

        assertThat(result.isCompleted()).isTrue();
    }

    @Test
    void deleteTodo_callsRepository() {
        when(todoRepository.existsById(1L)).thenReturn(true);

        todoService.deleteTodo(1L);

        verify(todoRepository).deleteById(1L);
    }

    @Test
    void deleteTodo_notFound_throws() {
        when(todoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> todoService.deleteTodo(99L))
                .isInstanceOf(TodoNotFoundException.class);
    }
}
