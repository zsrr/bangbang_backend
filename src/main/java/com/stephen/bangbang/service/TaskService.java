package com.stephen.bangbang.service;

import com.stephen.bangbang.domain.HelpingTask;
import com.stephen.bangbang.dto.TasksResponse;

public interface TaskService extends TaskInvalidService, UserInvalidService {
    TasksResponse findAllTasks(Long lastTaskId, int number);
    TasksResponse findAllTasksByUserId(Long userId, Long lastTaskId, int number);
    void publish(Long userId, HelpingTask helpingTask);
}
