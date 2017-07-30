package com.stephen.bangbang.dao;

import com.stephen.bangbang.domain.HelpingTask;
import com.stephen.bangbang.dto.TasksResponse;

// 按照时间从新到旧进行排序，采用搜寻分页，后插入的id较大，采用id作为比较
public interface TaskRepository {
    TasksResponse findAllTasks(Long lastTaskId, int number);
    TasksResponse findAllTasksByUserId(Long userId, Long lastTaskId, int number);
    void publish(Long userId, HelpingTask helpingTask);
    HelpingTask findTask(Long taskId);
}
