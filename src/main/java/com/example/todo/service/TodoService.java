package com.example.todo.service;

import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoRequest;
import com.example.todo.repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    public List<Todo> getTodosByStatus(boolean completed) {
        return todoRepository.findByCompleted(completed);
    }

    public List<Todo> searchTodos(String keyword) {
        return todoRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public Todo getTodoById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    @Transactional
    public Todo createTodo(TodoRequest request) {
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setCompleted(request.isCompleted());
        return todoRepository.save(todo);
    }

    @Transactional
    public Todo updateTodo(Long id, TodoRequest request) {
        Todo todo = getTodoById(id);
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setCompleted(request.isCompleted());
        return todoRepository.save(todo);
    }

    @Transactional
    public Todo toggleCompleted(Long id) {
        Todo todo = getTodoById(id);
        todo.setCompleted(!todo.isCompleted());
        return todoRepository.save(todo);
    }

    @Transactional
    public void deleteTodo(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new TodoNotFoundException(id);
        }
        todoRepository.deleteById(id);
    }
}
