package com.stephen.bangbang.servicetest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stephen.bangbang.base.authorization.TokenManager;
import com.stephen.bangbang.base.authorization.TokenModel;
import com.stephen.bangbang.config.RootConfig;
import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.dto.FriendsResponse;
import com.stephen.bangbang.exception.JPushException;
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

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RootConfig.class)
@ActiveProfiles("dev")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    TokenManager tokenManager;

    @Autowired
    JedisPool jedisPool;

    private User registeredUser01;

    private User registeredUser02;

    private Jedis jedis;

    private static final String REGISTRATION_ID_1 = "this_is_zsr";

    private static final String REGISTRATION_ID_2 = "this_is_zxq";

    @Before
    public void registrationBeforeTest() {
        jedis = jedisPool.getResource();

        User u1 = userService.getUser("StephenZhang");
        User u2 = userService.getUser("StephanieZhang");

        registeredUser01 = u1 == null ? userService.register("StephenZhang", "1234567") : u1;
        registeredUser02 = u2 == null ? userService.register("StephanieZhang", "7654321") : u2;
    }

    @After
    public void releaseJedis() {
        jedis.close();
    }

    private void login(User user, String registrationId) {
        try {
            userService.login(user.getId(), user.getPassword(), registrationId);
        } catch (JPushException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test001Login() {
        login(registeredUser01, REGISTRATION_ID_1);

        TokenModel tokenModel = tokenManager.getToken(registeredUser01.getId());
        Assert.assertNotNull(tokenModel);
        Assert.assertNotNull(tokenModel.getToken());

        String registrationId = jedis.get(registeredUser01.getId() + "-registrationId");
        Assert.assertEquals(registrationId, REGISTRATION_ID_1);

        login(registeredUser02, REGISTRATION_ID_2);
    }

    @Test
    public void test002Update() {
        String updateString = "{ \"nickname\": \"zhangshirui\", " +
                "\"email\": \"zsr13259726721@gmail.com\"," +
                "\"location\": {\"province\": \"ShanDong\", \"city\": \"LaiWu\"}," +
                "\"gender\": \"FEMALE\"}";

        ObjectNode on = null;
        try {
            on = new ObjectMapper().readValue(updateString, ObjectNode.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        userService.update(registeredUser01.getId(), on);

        registeredUser01 = userService.getUser(registeredUser01.getId());

        Assert.assertEquals("zhangshirui", registeredUser01.getNickname());
        Assert.assertEquals("zsr13259726721@gmail.com", registeredUser01.getEmail());
        Assert.assertEquals(User.Gender.FEMALE, registeredUser01.getGender());

        Assert.assertNotNull(registeredUser01.getLocation());

        Assert.assertEquals("ShanDong", registeredUser01.getLocation().getProvince());
        Assert.assertEquals("LaiWu", registeredUser01.getLocation().getCity());
    }

    @Test
    public void test003MakeFriend() {
        try {
            userService.makeFriendOnMake(registeredUser01.getId(), registeredUser02.getId());
        } catch (JPushException e) {
            e.printStackTrace();
        }

        Assert.assertTrue(jedis.sismember("make-friends-requests", registeredUser01.getId() + "-" + registeredUser02.getId()));
    }

    @Test
    public void test004MakeFriendAgree() {
        try {
            userService.makeFriendOnAgree(registeredUser02.getId(), registeredUser01.getId());
        } catch (JPushException e) {
            e.printStackTrace();
        }

        Assert.assertFalse(jedis.sismember("make-friends-requests", registeredUser01.getId() + "-" + registeredUser02.getId()));

        FriendsResponse r1 = userService.getFriends(registeredUser01.getId());
        FriendsResponse r2 = userService.getFriends(registeredUser02.getId());

        Assert.assertNotNull(r1);
        Assert.assertNotNull(r2);

        Assert.assertEquals(1, r1.getFriendsInfo().size());
        Assert.assertEquals(1, r2.getFriendsInfo().size());

        Assert.assertEquals(r1.getFriendsInfo().get(0).getUsername(), "StephanieZhang");
        Assert.assertEquals(r2.getFriendsInfo().get(0).getUsername(), "StephenZhang");
    }

    @Test(expected = JPushException.class)
    public void test005AllopatricLogin() {
        userService.login(registeredUser01.getId(), registeredUser01.getPassword(), REGISTRATION_ID_1 + "_");
    }

}
