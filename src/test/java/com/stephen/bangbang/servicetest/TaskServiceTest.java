package com.stephen.bangbang.servicetest;

import com.stephen.bangbang.config.RootConfig;
import com.stephen.bangbang.dao.TaskRepository;
import com.stephen.bangbang.domain.HelpingTask;
import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.dto.FriendsResponse;
import com.stephen.bangbang.dto.TasksResponse;
import com.stephen.bangbang.exception.JPushException;
import com.stephen.bangbang.service.TaskService;
import com.stephen.bangbang.service.UserService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RootConfig.class)
@ActiveProfiles("dev")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TaskServiceTest {

    private static int TASK_COUNT = 0;

    @Autowired
    UserService userService;

    @Autowired
    TaskService taskService;

    @Autowired
    JedisPool jedisPool;

    @Autowired
    TaskRepository taskRepository;

    User zsr;

    User zxq;

    User lgd;

    Jedis jedis;

    @Before
    public void setUp() {
        zsr = userService.getUser("StephenZhang");
        zxq = userService.getUser("StephanieZhang");
        lgd = userService.getUser("SabarDaLee");
        jedis = jedisPool.getResource();

        if (zsr == null && zxq == null && lgd == null) {
            zsr = userService.register("StephenZhang", "1234567");
            zxq = userService.register("StephanieZhang", "1234567");
            lgd = userService.register("SabarDaLee", "1234567");

            try {
                userService.login(zsr.getId(), zsr.getPassword(), zsr.getNickname());
            } catch (JPushException ignore) {
            }

            try {
                userService.login(zxq.getId(), zxq.getPassword(), zxq.getNickname());
            } catch (JPushException ignore) {
            }

            try {
                userService.login(lgd.getId(), lgd.getPassword(), lgd.getNickname());
            } catch (JPushException ignore) {
            }

            try {
                userService.makeFriendOnMake(zsr.getId(), zxq.getId());
            } catch (JPushException ignore) {
            }

            try {
                userService.makeFriendOnAgree(zxq.getId(), zsr.getId());
            } catch (JPushException ignore) {
            }
        }
    }

    @After
    public void finish() {
        jedis.close();
    }

    private HelpingTask generateTask() {
        HelpingTask ht = new HelpingTask();

        ++TASK_COUNT;

        ht.setTitle("" + TASK_COUNT);
        ht.setLocation("LaiwuPQD");
        ht.setStartTime(new Date());
        ht.setEndTime(new Date());
        ht.setMinPrice(10000);
        ht.setPhone("17763406721");
        return ht;
    }

    // 共发布300个任务，所以id是从4到303，前面还有三个用户
    @Test
    public void test001Publish() {
        for (int i = 0; i < 100; i++) {
            taskService.publish(zsr.getId(), generateTask());
        }

        for (int i = 0; i < 100; i++) {
            taskService.publish(zxq.getId(), generateTask());
        }

        for (int i = 0; i < 100; i++) {
            taskService.publish(lgd.getId(), generateTask());
        }
    }

    @Test
    public void test002getAllTasks() {
        TasksResponse response = taskService.getAllTasks(0L, 5);

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getSnapshots());
        Assert.assertNotNull(response.getPagination());
        Assert.assertEquals(response.getSnapshots().size(), 5);
        Assert.assertEquals(response.getSnapshots().get(0).getTaskId(), (Long) 303L);
        Assert.assertEquals(response.getSnapshots().get(4).getTaskId(), (Long) 299L);
        Assert.assertEquals(response.getPagination().getCurrentPage(), 1);
        Assert.assertEquals(response.getPagination().getTotalPage(), 60);

        TasksResponse response1 = taskService.getAllTasks(104L, 5);

        Assert.assertNotNull(response1);
        Assert.assertNotNull(response1.getSnapshots());
        Assert.assertNotNull(response1.getPagination());
        Assert.assertEquals(response1.getSnapshots().size(), 5);
        Assert.assertEquals(response1.getSnapshots().get(0).getTaskId(), (Long) 103L);
        Assert.assertEquals(response1.getSnapshots().get(4).getTaskId(), (Long) 99L);
        Assert.assertEquals(response1.getPagination().getCurrentPage(), 41);
        Assert.assertEquals(response1.getPagination().getTotalPage(), 60);

        Assert.assertEquals(response.getSnapshots().get(0).getUserId(), lgd.getId());
        Assert.assertEquals(response1.getSnapshots().get(0).getUserId(), zsr.getId());
    }

    @Test
    public void test003getAllTasksByFriends() {
        FriendsResponse friendsResponse = userService.getFriends(zsr.getId());
        Assert.assertEquals(friendsResponse.getFriendsInfo().get(0).getId(), zxq.getId());

        TasksResponse tr = taskService.getTasksMadeByFriends(zsr.getId(), 0L, 10);

        Assert.assertNotNull(tr);
        Assert.assertNotNull(tr.getPagination());
        Assert.assertNotNull(tr.getSnapshots());
        Assert.assertEquals(tr.getSnapshots().size(), 10);
        Assert.assertEquals(tr.getSnapshots().get(0).getTaskId(), (Long) 203L);
        Assert.assertEquals(tr.getPagination().getCurrentPage(), 1);
        Assert.assertEquals(tr.getPagination().getTotalPage(), 10);

        TasksResponse tr1 = taskService.getTasksMadeByFriends(lgd.getId(), 0L, 10);
        Assert.assertEquals(tr1.getSnapshots().size(), 0);
        Assert.assertEquals(tr1.getPagination().getCurrentPage(), 0);
        Assert.assertEquals(tr1.getPagination().getTotalPage(), 0);
    }

    @Test
    public void test004getAllTasksByStrangers() {
        TasksResponse tr = taskService.getTasksMadeByStrangers(zsr.getId(), 0L, 10);

        Assert.assertNotNull(tr);
        Assert.assertNotNull(tr.getPagination());
        Assert.assertNotNull(tr.getSnapshots());
        Assert.assertEquals(tr.getSnapshots().size(), 10);
        Assert.assertEquals(tr.getSnapshots().get(0).getTaskId(), (Long) 303L);
        Assert.assertEquals(tr.getPagination().getCurrentPage(), 1);
        Assert.assertEquals(tr.getPagination().getTotalPage(), 10);

        TasksResponse tr1 = taskService.getTasksMadeByStrangers(lgd.getId(), 0L, 10);
        Assert.assertEquals(tr1.getSnapshots().size(), 10);
        Assert.assertEquals(tr1.getPagination().getCurrentPage(), 1);
        Assert.assertEquals(tr1.getPagination().getTotalPage(), 20);
    }

    @Test
    public void test005ClaimFor() {
        taskService.claimFor(zsr.getId(), 203L);
    }

    @Test
    public void test006DeleteTask() {
        jedis.del(zsr.getId() + "-recently-finished");
        taskService.deleteTask(203L, true);
        taskService.deleteTask(303L, false);
    }

    @Test
    public void test007getRecentlyFinished() {
        TasksResponse response = taskService.getTasksRecentlyFinished(zsr.getId());
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getSnapshots().size(), 1);
        Assert.assertEquals(response.getSnapshots().get(0).getUserId(), zxq.getId());
        Assert.assertEquals(response.getSnapshots().get(0).getTaskId(), (Long) 203L);
    }
}
