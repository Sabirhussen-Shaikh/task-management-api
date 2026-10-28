package com.ikaansh.task_management_api.service;

import com.ikaansh.task_management_api.dto.TaskRequest;
import com.ikaansh.task_management_api.dto.TaskResponse;
import com.ikaansh.task_management_api.entity.Category;
import com.ikaansh.task_management_api.entity.Task;
import com.ikaansh.task_management_api.entity.User;
import com.ikaansh.task_management_api.enums.TaskStatus;
import com.ikaansh.task_management_api.repository.CategoryRepository;
import com.ikaansh.task_management_api.repository.TaskRepository;
import com.ikaansh.task_management_api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public TaskService(
            TaskRepository taskRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public TaskResponse createTask(String email, TaskRequest request) {
        User user = getUser(email);

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus());
        task.setUser(user);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Category not found"));
            task.setCategory(category);
        }

        return toResponse(taskRepository.save(task));
    }


    @Transactional(readOnly = true)
    public Page<TaskResponse> getMyTasks(
            String email, TaskStatus status, int page, int size) {

        User user = getUser(email);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Task> tasks = status == null
                ? taskRepository.findByUserId(user.getId(), pageable)
                : taskRepository.findByUserIdAndStatus(
                user.getId(), status, pageable);

        return tasks.map(this::toResponse);
    }



    @Transactional(readOnly = true)
    public TaskResponse getTask(String email, Long taskId) {
        User user = getUser(email);
        Task task = getOwnedTask(taskId, user.getId());

        return toResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(
            String email, Long taskId, TaskRequest request) {
        User user = getUser(email);
        Task task = getOwnedTask(taskId, user.getId());

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Category not found"));
            task.setCategory(category);
        } else {
            task.setCategory(null);
        }

        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(String email, Long taskId) {
        User user = getUser(email);
        Task task = getOwnedTask(taskId, user.getId());

        taskRepository.delete(task);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private Task getOwnedTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!task.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You are not allowed to access this task");
        }

        return task;
    }

    private TaskResponse toResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setDueDate(task.getDueDate());
        response.setPriority(task.getPriority());
        response.setStatus(task.getStatus());
        response.setUserId(task.getUser().getId());
        response.setCategoryName(
                task.getCategory() != null ? task.getCategory().getName() : null);
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());

        return response;
    }
}
