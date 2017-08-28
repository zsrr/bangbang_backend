package com.stephen.bangbang.service;

import com.stephen.bangbang.dao.TaskRepository;
import com.stephen.bangbang.exception.TaskAlreadyBeenTakenException;
import com.stephen.bangbang.exception.TaskNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class TaskValidationServiceImpl implements TaskValidationService {

    TaskRepository taskDAO;

    @Autowired
    public TaskValidationServiceImpl(TaskRepository taskDAO) {
        this.taskDAO = taskDAO;
    }

    @Override
    public void invalidTask(Long taskId) {
        if (taskId.equals(0L)) {
            return;
        }

        if (!taskDAO.hasTask(taskId)) {
            throw new TaskNotFoundException();
        }
    }

    @Override
    public void isTaskTaken(Long taskId) {
        if (taskDAO.findResponsiblePersonFor(taskId) != null) {
            throw new TaskAlreadyBeenTakenException();
        }
    }
}
