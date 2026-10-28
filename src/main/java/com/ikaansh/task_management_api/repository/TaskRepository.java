
package com.ikaansh.task_management_api.repository;

import com.ikaansh.task_management_api.entity.Task;
import com.ikaansh.task_management_api.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByUserId(Long userId, Pageable pageable);

    Page<Task> findByUserIdAndStatus(
            Long userId, TaskStatus status, Pageable pageable);
}
