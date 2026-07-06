package com.example.java1.service;

import com.example.java1.dto.TodoRequest;
import com.example.java1.dto.TodoResponse;
import com.example.java1.model.Priority;
import com.example.java1.model.Todo;
import com.example.java1.repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 待办事项业务层
 */
@Service
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    /**
     * 查询待办列表，支持按状态/分类/优先级过滤
     */
    public List<TodoResponse> list(Boolean completed, String category, Priority priority) {
        List<Todo> todos;
        if (completed != null && category != null) {
            todos = todoRepository.findByCompletedAndCategory(completed, category);
        } else if (completed != null && priority != null) {
            todos = todoRepository.findByCompletedAndPriority(completed, priority);
        } else if (completed != null) {
            todos = todoRepository.findByCompleted(completed);
        } else if (category != null) {
            todos = todoRepository.findByCategory(category);
        } else if (priority != null) {
            todos = todoRepository.findByPriority(priority);
        } else {
            todos = todoRepository.findAll();
        }
        return todos.stream().map(TodoResponse::from).toList();
    }

    public TodoResponse get(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("待办事项不存在: " + id));
        return TodoResponse.from(todo);
    }

    public TodoResponse create(TodoRequest request) {
        Todo todo = new Todo(
                request.title(),
                request.description(),
                request.priority(),
                request.category(),
                request.dueDate()
        );
        return TodoResponse.from(todoRepository.save(todo));
    }

    public TodoResponse update(Long id, TodoRequest request) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("待办事项不存在: " + id));
        if (request.title() != null) todo.setTitle(request.title());
        if (request.description() != null) todo.setDescription(request.description());
        if (request.priority() != null) todo.setPriority(request.priority());
        if (request.category() != null) todo.setCategory(request.category());
        if (request.completed() != null) todo.setCompleted(request.completed());
        if (request.dueDate() != null) todo.setDueDate(request.dueDate());
        return TodoResponse.from(todoRepository.save(todo));
    }

    public void delete(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new IllegalArgumentException("待办事项不存在: " + id);
        }
        todoRepository.deleteById(id);
    }

    public TodoResponse toggle(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("待办事项不存在: " + id));
        todo.setCompleted(!todo.getCompleted());
        return TodoResponse.from(todoRepository.save(todo));
    }
}
