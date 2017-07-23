import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.exception.PasswordIncorrectException;
import com.stephen.bangbang.exception.UserNotFoundException;
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
public class UserLoginTest {

    @Autowired
    private UserService userService;

    @Test
    public void login() {
        userService.register("zsrrrr", "123456");
        User user = userService.login("zsrrrr", "123456");
        assertEquals("zsrrrr", user.getUsername());
        assertEquals("123456", user.getPassword());
    }

    @Test(expected = UserNotFoundException.class)
    public void loginUserNotFound() {
        userService.login("zsrrrrrr", "123456");
    }

    @Test(expected = PasswordIncorrectException.class)
    public void loginPasswordIncorrect() {
        userService.register("zsrrrr1", "123456");
        userService.login("zsrrrr1", "456789");
    }

}
