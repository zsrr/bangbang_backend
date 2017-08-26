package com.stephen.bangbang.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stephen.bangbang.dao.TaskRepository;
import com.stephen.bangbang.dao.UserInfoRepository;
import com.stephen.bangbang.domain.HelpingTask;
import com.stephen.bangbang.dto.Pagination;
import com.stephen.bangbang.dto.TaskSnapshot;
import com.stephen.bangbang.dto.TasksResponse;
import com.stephen.bangbang.exception.JsonInvalidException;
import com.stephen.bangbang.exception.TaskAlreadyBeenTakenException;
import com.stephen.bangbang.exception.TaskWithNoPersonInChargeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskDAO;
    private UserInfoRepository userDAO;
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public TaskServiceImpl(TaskRepository taskDAO, UserInfoRepository userDAO, RedisTemplate redisTemplate) {
        this.taskDAO = taskDAO;
        this.userDAO = userDAO;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TasksResponse getAllTasks(Long lastTaskId, int number) {
        invalidTask(lastTaskId, taskDAO);
        return taskDAO.findAllTasks(lastTaskId, number);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TasksResponse getAllTasksByUser(Long userId, Long lastTaskId, int number) {
        invalidTask(lastTaskId, taskDAO);
        invalidUser(userId, userDAO);
        return taskDAO.findAllTasksByUserId(userId, lastTaskId, number);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TasksResponse getTasksMadeByFriends(Long userId, Long lastTaskId, int number) {
        invalidTask(lastTaskId, taskDAO);
        invalidUser(userId, userDAO);
        return taskDAO.findTasksPublishedByFriends(userId, lastTaskId, number);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TasksResponse getTasksMadeByStrangers(Long userId, Long lastTaskId, int number) {
        invalidTask(lastTaskId, taskDAO);
        invalidUser(userId, userDAO);
        return taskDAO.findTasksPublishedByStrangers(userId, lastTaskId, number);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void publish(Long userId, HelpingTask helpingTask) {
        invalidUser(userId, userDAO);
        taskDAO.publish(userId, helpingTask);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void claimFor(Long userId, Long taskId) {
        invalidUser(userId, userDAO);
        invalidTask(taskId, taskDAO);
        if (taskDAO.findResponsiblePersonFor(taskId) != null) {
            throw new TaskAlreadyBeenTakenException();
        }
        taskDAO.userInChargeOf(userId, taskId);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteTask(Long taskId, boolean hasDone) {
        invalidTask(taskId, taskDAO);
        HelpingTask task = taskDAO.findTask(taskId);
        TaskSnapshot snapshot = new TaskSnapshot(task);
        taskDAO.deleteTask(taskId);
        if (!hasDone)
            return;

        if (snapshot.getPersonInCharge() == null) {
            throw new TaskWithNoPersonInChargeException();
        }

        // 保存用户最近完成的
        Long finishedTime = System.currentTimeMillis();
        snapshot.setFinishedTime(finishedTime);
        BoundListOperations<String, String> ops = redisTemplate.boundListOps(snapshot.getPersonInCharge() + "-recently-finished");

        if (ops.size() > 30) {
            ops.leftPop();
        }

        try {
            ops.rightPush(new ObjectMapper().writeValueAsString(snapshot));
        } catch (JsonProcessingException e) {
            throw  new JsonInvalidException(e);
        }
    }

    @Override
    public TasksResponse getTasksRecentlyFinished(Long userId) {
        TasksResponse tasksResponse = new TasksResponse(new Pagination(1, 1), null);
        List<String> results = redisTemplate.boundListOps(userId + "-recently-finished").range(0, -1);
        tasksResponse.setSnapshots(transform(results));
        return tasksResponse;
    }

    private List<TaskSnapshot> transform(List<String> rawData) {
        List<TaskSnapshot> snapshots = new ArrayList<>(rawData.size());
        ObjectMapper om = new ObjectMapper();
        for (String s : rawData) {
            try {
                snapshots.add(om.readValue(s, TaskSnapshot.class));
            } catch (IOException e) {
                throw new JsonInvalidException(e);
            }
        }
        return snapshots;
    }
}
