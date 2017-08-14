package com.stephen.bangbang.service;

import com.stephen.bangbang.domain.HelpingTask;
import com.stephen.bangbang.dto.TasksResponse;

public interface TaskService extends TaskInvalidService, UserInvalidService {
    TasksResponse getAllTasks(Long lastTaskId, int number);
    TasksResponse getAllTasksByUser(Long userId, Long lastTaskId, int number);
    TasksResponse getTasksMadeByFriends(Long userId, Long lastTaskId, int number);
    TasksResponse getTasksMadeByStrangers(Long userId, Long lastTaskId, int number);
    void publish(Long userId, HelpingTask helpingTask);
}
