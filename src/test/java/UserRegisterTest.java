import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.exception.DuplicatedUserException;
import com.stephen.bangbang.exception.UserInfoInvalidException;
import com.stephen.bangbang.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/java/com/stephen/bangbang/config/applicationContext.xml")
@ActiveProfiles("test")
public class UserRegisterTest {

    @Autowired
    private UserService userService;

    @Test(expected = DuplicatedUserException.class)
    public void registerDuplicatedUser() {
        userService.register("zsrrrr", "123456");
        User user = userService.findUser("zsrrrr");
        assertNotNull(user);
        assertEquals("123456", user.getPassword());

        // 重复注册
        userService.register("zsrrrr", "zsrdff");
    }

    @Test(expected = UserInfoInvalidException.class)
    public void registerUserInfoInvalid() {
        userService.register(null, "drrrrr");
        userService.register("zsrrrr", null);
    }
}
