package com.stephen.bangbang.web;

import com.stephen.bangbang.base.authorization.Authorization;
import com.stephen.bangbang.base.annotation.CurrentUserId;
import com.stephen.bangbang.dto.BaseResponse;
import com.stephen.bangbang.dto.TasksResponse;
import com.stephen.bangbang.exception.ScopeResolveException;
import com.stephen.bangbang.service.TaskService;
import com.stephen.bangbang.service.TaskValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/tasks")
@Authorization
public class TaskController {

    private TaskService taskService;
    private TaskValidationService taskValidationService;

    @Autowired
    public TaskController(TaskService taskService, TaskValidationService taskValidationService) {
        this.taskService = taskService;
        this.taskValidationService = taskValidationService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<TasksResponse> findAllTasks(@RequestParam(value = "lastTaskId", defaultValue = "0") Long lastTaskId,
                                                      @RequestParam(value = "numberPerPage", defaultValue = "5") int numberPerPage,
                                                      @RequestParam(value = "scope") String scope,
                                                      @CurrentUserId Long currentUserId) {
        taskValidationService.invalidTask(lastTaskId);
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

    @RequestMapping(value = "/{taskId}", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse> claimFor(@PathVariable("taskId") Long taskId, @CurrentUserId Long currentUserId) {
        taskValidationService.isTaskTaken(taskId);
        taskService.claimFor(currentUserId, taskId);
        return new ResponseEntity<BaseResponse>(new BaseResponse(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{taskId}", method = RequestMethod.DELETE)
    public ResponseEntity<BaseResponse> deleteTask(@PathVariable("taskId") Long taskId, @RequestParam("hasDone") boolean hasDone) {
        taskValidationService.invalidTask(taskId);
        taskService.deleteTask(taskId, hasDone);
        return new ResponseEntity<BaseResponse>(new BaseResponse(), HttpStatus.OK);
    }
}
