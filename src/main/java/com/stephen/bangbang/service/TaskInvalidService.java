package com.stephen.bangbang.service;

import com.stephen.bangbang.dao.TaskRepository;
import com.stephen.bangbang.domain.HelpingTask;
import com.stephen.bangbang.exception.task.TaskNotFoundException;

public interface TaskInvalidService {
    default void invalidTask(Long taskId, TaskRepository taskDAO) {
        if (taskId == 0)
            return;
        HelpingTask task = taskDAO.findTask(taskId);
        if (task == null)
            throw new TaskNotFoundException();
    }
}
