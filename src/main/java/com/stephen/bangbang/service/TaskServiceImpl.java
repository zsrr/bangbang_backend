package com.stephen.bangbang.service;

import com.stephen.bangbang.dao.TaskRepository;
import com.stephen.bangbang.dao.UserInfoRepository;
import com.stephen.bangbang.domain.HelpingTask;
import com.stephen.bangbang.dto.TasksResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskDAO;
    private UserInfoRepository userDAO;

    @Autowired
    public TaskServiceImpl(TaskRepository taskDAO, UserInfoRepository userDAO) {
        this.taskDAO = taskDAO;
        this.userDAO = userDAO;
    }

    @Override
    public TasksResponse getAllTasks(Long lastTaskId, int number) {
        invalidTask(lastTaskId, taskDAO);
        return taskDAO.findAllTasks(lastTaskId, number);
    }

    @Override
    public TasksResponse getAllTasksByUser(Long userId, Long lastTaskId, int number) {
        invalidTask(lastTaskId, taskDAO);
        invalidUser(userId, userDAO);
        return taskDAO.findAllTasksByUserId(userId, lastTaskId, number);
    }

    @Override
    public TasksResponse getTasksMadeByFriends(Long userId, Long lastTaskId, int number) {
        invalidTask(lastTaskId, taskDAO);
        invalidUser(userId, userDAO);
        return taskDAO.findTasksPublishedByFriends(userId, lastTaskId, number);
    }

    @Override
    public TasksResponse getTasksMadeByStrangers(Long userId, Long lastTaskId, int number) {
        invalidTask(lastTaskId, taskDAO);
        invalidUser(userId, userDAO);
        return taskDAO.findTasksPublishedByStrangers(userId, lastTaskId, number);
    }

    @Override
    public void publish(Long userId, HelpingTask helpingTask) {
        taskDAO.publish(userId, helpingTask);
    }
}
