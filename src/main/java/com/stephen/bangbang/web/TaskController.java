package com.stephen.bangbang.web;

import com.stephen.bangbang.authorization.Authorization;
import com.stephen.bangbang.dto.TasksResponse;
import com.stephen.bangbang.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/tasks")
@Authorization
public class TaskController {

    private TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<TasksResponse> findAllTasks(@RequestParam(value = "lastTaskId", defaultValue = "0") Long lastTaskId,
                                                      @RequestParam(value = "numberPerPage", defaultValue = "5") int numberPerPage,
                                                      @PathVariable(value = "userId", required = false) Long userId) {
        TasksResponse tr = null;
        if (userId == null) {
            tr = taskService.findAllTasks(lastTaskId, numberPerPage);
        } else {
            tr = taskService.findAllTasksByUserId(userId, lastTaskId, numberPerPage);
        }
        return new ResponseEntity<TasksResponse>(tr, HttpStatus.OK);
    }
}