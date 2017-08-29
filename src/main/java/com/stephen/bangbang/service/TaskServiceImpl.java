package com.stephen.bangbang.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stephen.bangbang.dao.TaskRepository;
import com.stephen.bangbang.domain.HelpingTask;
import com.stephen.bangbang.dto.Pagination;
import com.stephen.bangbang.dto.TaskSnapshot;
import com.stephen.bangbang.dto.TasksResponse;
import com.stephen.bangbang.exception.JsonInvalidException;
import com.stephen.bangbang.exception.TaskWithNoPersonInChargeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskDAO;
    private JedisPool jedisPool;

    @Autowired
    public TaskServiceImpl(TaskRepository taskDAO, JedisPool jedisPool) {
        this.taskDAO = taskDAO;
        this.jedisPool = jedisPool;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TasksResponse getAllTasks(Long lastTaskId, int number) {
        return taskDAO.findAllTasks(lastTaskId, number);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TasksResponse getAllTasksByUser(Long userId, Long lastTaskId, int number) {
        return taskDAO.findAllTasksByUserId(userId, lastTaskId, number);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TasksResponse getTasksMadeByFriends(Long userId, Long lastTaskId, int number) {
        return taskDAO.findTasksPublishedByFriends(userId, lastTaskId, number);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TasksResponse getTasksMadeByStrangers(Long userId, Long lastTaskId, int number) {
        return taskDAO.findTasksPublishedByStrangers(userId, lastTaskId, number);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void publish(Long userId, HelpingTask helpingTask) {
        taskDAO.publish(userId, helpingTask);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void claimFor(Long userId, Long taskId) {
        taskDAO.userInChargeOf(userId, taskId);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteTask(Long taskId, boolean hasDone) {
        HelpingTask task = taskDAO.findTask(taskId);
        TaskSnapshot snapshot = new TaskSnapshot(task);
        taskDAO.deleteTask(taskId);
        if (!hasDone)
            return;

        if (snapshot.getPersonInCharge() == null) {
            throw new TaskWithNoPersonInChargeException();
        }

        // 保存用户最近完成的
        try (Jedis jedis = jedisPool.getResource()) {
            Long finishedTime = System.currentTimeMillis();
            snapshot.setFinishedTime(finishedTime);

            String key = snapshot.getPersonInCharge() + "-recently-finished";

            if (jedis.llen(key) > 30) {
                jedis.lpop(key);
            }

            jedis.rpush(key, new ObjectMapper().writeValueAsString(snapshot));
        } catch (JsonProcessingException ignore) {
        }
    }

    @Override
    public TasksResponse getTasksRecentlyFinished(Long userId) {
        try (Jedis jedis = jedisPool.getResource()) {
            TasksResponse tasksResponse = new TasksResponse(new Pagination(1, 1), null);
            List<String> results = jedis.lrange(userId + "-recently-finished", 0, -1);
            tasksResponse.setSnapshots(transform(results));
            return tasksResponse;
        }
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
