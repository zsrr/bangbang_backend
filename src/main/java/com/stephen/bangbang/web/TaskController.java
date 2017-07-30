package com.stephen.bangbang.web;

import com.stephen.bangbang.authorization.Authorization;
import com.stephen.bangbang.authorization.CurrentUser;
import com.stephen.bangbang.domain.HelpingTask;
import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.dto.BaseResponse;
import com.stephen.bangbang.dto.ErrorDetail;
import com.stephen.bangbang.dto.TasksResponse;
import com.stephen.bangbang.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ValidationException;

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
            tr = taskService.getAllTasks(lastTaskId, numberPerPage);
        } else {
            tr = taskService.getAllTasksByUser(userId, lastTaskId, numberPerPage);
        }
        return new ResponseEntity<TasksResponse>(tr, HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse> publish(@RequestBody HelpingTask ht, @CurrentUser User user) {
        taskService.publish(user.getId(), ht);
        return new ResponseEntity<BaseResponse>(new BaseResponse(HttpStatus.CREATED, null), HttpStatus.CREATED);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<BaseResponse> validationError() {
        ErrorDetail ed = new ErrorDetail("Validation error", ValidationException.class, "数据不符合规范");
        BaseResponse br = new BaseResponse(HttpStatus.NOT_ACCEPTABLE, ed);
        return new ResponseEntity<BaseResponse>(br, HttpStatus.NOT_ACCEPTABLE);
    }
}
