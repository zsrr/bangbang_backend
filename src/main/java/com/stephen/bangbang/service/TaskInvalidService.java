package com.stephen.bangbang.service;

import com.stephen.bangbang.dao.TaskRepository;
import com.stephen.bangbang.exception.TaskNotFoundException;


public interface TaskInvalidService {
    default void invalidTask(Long taskId, TaskRepository taskDAO) {
        if (taskId.equals(0L)) {
            return;
        }
        if (!taskDAO.hasTask(taskId)) {
            throw new TaskNotFoundException();
        }
    }
}
