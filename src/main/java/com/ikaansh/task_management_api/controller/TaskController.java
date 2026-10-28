package com.ikaansh.task_management_api.controller;

import com.ikaansh.task_management_api.dto.TaskRequest;
import com.ikaansh.task_management_api.dto.TaskResponse;
import com.ikaansh.task_management_api.enums.TaskStatus;
import com.ikaansh.task_management_api.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            Authentication authentication,
            @Valid @RequestBody TaskRequest request) {

        TaskResponse response = taskService.createTask(
                authentication.getName(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getMyTasks(
            Authentication authentication,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page must be non-negative and size must be between 1 and 100");
        }

        return ResponseEntity.ok(
                taskService.getMyTasks(
                        authentication.getName(), status, page, size));
    }



    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(
            Authentication authentication,
            @PathVariable Long id) {

        return ResponseEntity.ok(
                taskService.getTask(authentication.getName(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {

        return ResponseEntity.ok(
                taskService.updateTask(authentication.getName(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            Authentication authentication,
            @PathVariable Long id) {

        taskService.deleteTask(authentication.getName(), id);

        return ResponseEntity.noContent().build();
    }
}