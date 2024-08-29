package com.revature.planetarium.service.user;

import com.revature.Setup;
import com.revature.planetarium.entities.User;
import com.revature.planetarium.exceptions.UserFail;
import com.revature.planetarium.repository.user.UserDao;
import com.revature.planetarium.repository.user.UserDaoImp;
import org.junit.*;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserServiceIntegrationTest {
    private UserDao userDao;
    private UserService userService;

    private User newUserCredTestData;

    @BeforeClass
    public static void testDatabaseSetup() throws SQLException {
        Setup.getConnection();

    }

    @Before
    public void setUp() throws Exception {
        Setup.resetTestDatabase();
        userDao = new UserDaoImp();
        userService = new UserServiceImp(userDao);
        newUserCredTestData = new User();

    }

    @Test
    public void createUserPos() {
        newUserCredTestData.setUsername("createdUser");
        newUserCredTestData.setPassword("createdPassword");
        Assert.assertEquals("Created user with username " + newUserCredTestData.getUsername() + " and password "
                + newUserCredTestData.getPassword(),userService.createUser(newUserCredTestData));
    }

    @Test
    public void createUserNegativeUserNameTooLong() {
        newUserCredTestData.setUsername("YouCantUseThisUsernameIsTooLong");
        newUserCredTestData.setPassword("123");

        UserFail e = Assert.assertThrows(UserFail.class, ()-> {
            userService.createUser(newUserCredTestData);
        });
        System.out.println(e.getMessage());
        Assert.assertEquals("Username must be between 1 and 30 characters", e.getMessage());
    }

    @Test
    public void createUserNegativePasswordTooLong() {
        newUserCredTestData.setUsername("JohnDoe");
        newUserCredTestData.setPassword("YouCantUseThisPasswordIsTooLong");

        UserFail e = Assert.assertThrows(UserFail.class, ()-> {
            userService.createUser(newUserCredTestData);
        });
        System.out.println(e.getMessage());
        Assert.assertEquals("Password must be between 1 and 30 characters", e.getMessage());
    }

    @Test
    public void createUserNegativeUsernameExists() {
        newUserCredTestData.setUsername("Batman");
        newUserCredTestData.setPassword("123");

        UserFail e = Assert.assertThrows(UserFail.class, ()-> {
            userService.createUser(newUserCredTestData);
        });
        System.out.println(e.getMessage());
        Assert.assertEquals("Username is already in use", e.getMessage());
    }

    @Test
    public void authenticatePos() {
        newUserCredTestData.setUsername("createdUser");
        newUserCredTestData.setPassword("createdPassword");
        userService.createUser(newUserCredTestData);
        Assert.assertEquals(newUserCredTestData, userService.authenticate(newUserCredTestData));
    }

    @Test
    public void authenticateNegative() {
        newUserCredTestData.setUsername("JohnDoe");
        newUserCredTestData.setPassword("123");

        UserFail e = Assert.assertThrows(UserFail.class, ()-> {
            userService.authenticate(newUserCredTestData);
        });
        System.out.println(e.getMessage());
        Assert.assertEquals("Username and/or password do not match", e.getMessage());
    }

}
