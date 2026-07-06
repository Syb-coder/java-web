package com.example.java1.repository;

import com.example.java1.model.Priority;
import com.example.java1.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 待办事项数据访问层
 */
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByCompleted(Boolean completed);

    List<Todo> findByCategory(String category);

    List<Todo> findByPriority(Priority priority);

    List<Todo> findByCompletedAndCategory(Boolean completed, String category);

    List<Todo> findByCompletedAndPriority(Boolean completed, Priority priority);
}
