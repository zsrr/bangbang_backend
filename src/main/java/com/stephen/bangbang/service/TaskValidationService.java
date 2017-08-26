package com.stephen.bangbang.service;


public interface TaskValidationService {
    void invalidTask(Long taskId);
    void isTaskTaken(Long taskId);
}
