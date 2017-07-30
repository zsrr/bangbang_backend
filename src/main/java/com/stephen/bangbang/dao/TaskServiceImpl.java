package com.stephen.bangbang.dao;

import com.stephen.bangbang.domain.HelpingTask;
import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.dto.TasksResponse;
import com.stephen.bangbang.exception.task.TaskNotFoundException;
import com.stephen.bangbang.exception.user.UserNotFoundException;
import com.stephen.bangbang.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// 太空，虚无，不如dao和controller层直接进行交互
@Service
public class TaskServiceImpl implements TaskService {

    private UserInfoRepository userDAO;
    private TaskRepository taskDAO;

    @Autowired
    public TaskServiceImpl(UserInfoRepository userDAO, TaskRepository taskDAO) {
        this.userDAO = userDAO;
        this.taskDAO = taskDAO;
    }

    @Override
    public TasksResponse findAllTasks(Long lastTaskId, int number) {
        invalidTask(lastTaskId);
        return taskDAO.findAllTasks(lastTaskId, number);
    }

    @Override
    public TasksResponse findAllTasksByUserId(Long userId, Long lastTaskId, int number) {
        invalidTask(lastTaskId);
        invalidUser(userId);
        return taskDAO.findAllTasksByUserId(userId, lastTaskId, number);
    }

    @Override
    public void publish(Long userId, HelpingTask helpingTask) {
        invalidUser(userId);
        taskDAO.publish(userId, helpingTask);
    }

    @Override
    public void invalidTask(Long taskId) {
        HelpingTask ht = taskDAO.findTask(taskId);
        if (ht == null) {
            throw new TaskNotFoundException();
        }
    }

    @Override
    public void invalidUser(Long userId) {
        User user = userDAO.findUser(userId);
        if (user == null)
            throw new UserNotFoundException();
    }

    @Override
    public void invalidUser(String username) {

    }
}
