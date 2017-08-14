package com.stephen.bangbang.web;

import com.stephen.bangbang.authorization.Authorization;
import com.stephen.bangbang.authorization.CurrentUserId;
import com.stephen.bangbang.dto.TasksResponse;
import com.stephen.bangbang.exception.ScopeResolveException;
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

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<TasksResponse> findAllTasks(@RequestParam(value = "lastTaskId", defaultValue = "0") Long lastTaskId,
                                                      @RequestParam(value = "numberPerPage", defaultValue = "5") int numberPerPage,
                                                      @RequestParam(value = "scope") String scope,
                                                      @CurrentUserId Long currentUserId) {
        TasksResponse tr;
        if (scope.equals("friends")) {
            tr = taskService.getTasksMadeByFriends(currentUserId, lastTaskId, numberPerPage);
        } else if (scope.equals("strangers")) {
            tr = taskService.getTasksMadeByStrangers(currentUserId, lastTaskId, numberPerPage);
        } else {
            throw new ScopeResolveException("名为" + scope + "的scope无法解析");
        }
        return new ResponseEntity<TasksResponse>(tr, HttpStatus.OK);
    }
}
