package com.stephen.bangbang.servicetest;

import com.stephen.bangbang.config.RootConfig;
import com.stephen.bangbang.domain.HelpingTask;
import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.exception.*;
import com.stephen.bangbang.service.TaskService;
import com.stephen.bangbang.service.TaskValidationService;
import com.stephen.bangbang.service.UserService;
import com.stephen.bangbang.service.UserValidationService;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RootConfig.class)
@ActiveProfiles("dev")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InvalidServiceTest {

    @Autowired
    UserValidationService userValidationService;

    @Autowired
    TaskValidationService taskValidationService;

    User zsr;

    @Autowired
    UserService userService;

    @Autowired
    TaskService taskService;

    private HelpingTask generateTask() {
        HelpingTask ht = new HelpingTask();

        ht.setTitle("Test");
        ht.setLocation("LaiwuPQD");
        ht.setStartTime(new Date());
        ht.setEndTime(new Date());
        ht.setMinPrice(10000);
        ht.setPhone("17763406721");
        return ht;
    }

    @Test
    public void test001InvalidUser() {
        zsr = userService.register("StephenZhang", "1234567");
        userValidationService.invalidUser(zsr.getId());
        userValidationService.invalidUser(zsr.getUsername());
    }

    @Test(expected = UserNotFoundException.class)
    public void test002InValidUserException() {
        userValidationService.invalidUser("StephenieZhang");
    }

    @Test
    public void test003IsCurrentUser() {
        userValidationService.isCurrentUser(10L, 10L);
    }

    @Test(expected = NotCurrentUserException.class)
    public void test004IsCurrentException() {
        userValidationService.isCurrentUser(10L, 5L);
    }

    @Test
    public void test005RegisterValidation() {
        User user = new User("StephanieZhang", "1234567");
        userValidationService.registerValidation(user);
    }

    @Test(expected = DuplicatedUserException.class)
    public void test006RegisterValidationDuplicatedException() {
        userValidationService.registerValidation(new User("StephenZhang", "1234567"));
    }

    @Test(expected = UserInfoInvalidException.class)
    public void test007RegisterValidationInfoInvalidException() {
        userValidationService.registerValidation(new User("z", "1234567"));
    }

    @Test
    public void test008TaskInvalid() {
        zsr = userService.getUser("StephenZhang");
        taskService.publish(zsr.getId(), generateTask());
        taskValidationService.invalidTask(2L);
        taskValidationService.invalidTask(0L);
    }

    @Test(expected = TaskNotFoundException.class)
    public void test009TaskInvalidException() {
        taskValidationService.invalidTask(10L);
    }

    @Test
    public void test010IsTaskTaken() {
        taskValidationService.isTaskTaken(2L);
    }

    @Test(expected = TaskAlreadyBeenTakenException.class)
    public void test011IsTaskTakenException() {
        taskService.claimFor(1L, 2L);
        taskValidationService.isTaskTaken(2L);
    }
}
