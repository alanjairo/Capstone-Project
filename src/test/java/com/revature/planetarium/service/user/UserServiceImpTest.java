package com.revature.planetarium.service.user;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.revature.planetarium.entities.User;
import com.revature.planetarium.exceptions.UserFail;
import com.revature.planetarium.repository.user.UserDaoImp;

import java.util.Optional;

public class UserServiceImpTest {

    private UserDaoImp userDaoImp;
    private UserServiceImp userServiceImp;

    private User newUserCredTestData;
    private User newUserUsernameTooLong;
    private User newUserPasswordTooLong;
    private User userNotNew;
    private User userFail;

    // private List<User> premadeUsers;

    @Before
    public void setUp() throws Exception {
        userDaoImp = Mockito.mock(UserDaoImp.class);
        userServiceImp = new UserServiceImp(userDaoImp);
        newUserCredTestData = new User(1, "Batman", "Your mom");
        newUserUsernameTooLong = new User(2, "This is a username that is too long for the service", "I am the night");
        newUserPasswordTooLong = new User(3, "Batman", "This is a password that is too long for the service");

        userNotNew = new User(4, "mockAdmin", "1234");
        userFail = new User(5, "", "");
    }

    @Test
    public void createUserPos() {
        Mockito.when(userDaoImp.createUser(newUserCredTestData)).thenReturn(Optional.of(newUserCredTestData));
        Assert.assertEquals("Created user with username " + newUserCredTestData.getUsername() + " and password "
                + newUserCredTestData.getPassword(), userServiceImp.createUser(newUserCredTestData));
    }

    @Test
    public void createUserNegU2Long() {
        Mockito.when(userDaoImp.createUser(newUserUsernameTooLong)).thenReturn(Optional.of(newUserUsernameTooLong));
        UserFail e = Assert.assertThrows(UserFail.class, () -> {
            userServiceImp.createUser(newUserUsernameTooLong);
        });
        System.out.println(e.getMessage());
        Assert.assertEquals("Username must be between 1 and 30 characters", e.getMessage());
    }

    @Test
    public void createUserNegP2Long() {
        Mockito.when(userDaoImp.createUser(newUserPasswordTooLong)).thenReturn(Optional.of(newUserPasswordTooLong));
        UserFail e = Assert.assertThrows(UserFail.class, () -> {
            userServiceImp.createUser(newUserPasswordTooLong);
        });
        System.out.println(e.getMessage());
        Assert.assertEquals("Password must be between 1 and 30 characters", e.getMessage());
    }

    @Test
    public void createUserNegUExists() {
        Mockito.when(userDaoImp.findUserByUsername(userNotNew.getUsername())).thenReturn(Optional.of(userNotNew));
        UserFail e = Assert.assertThrows(UserFail.class, () -> {
            userServiceImp.createUser(userNotNew);
        });
        System.out.println(e.getMessage());
        Assert.assertEquals("Username is already in use", e.getMessage());
    }

    @Test
    public void authenticatePos() {
        Mockito.when(userDaoImp.findUserByUsername(newUserCredTestData.getUsername()))
                .thenReturn(Optional.of(newUserCredTestData));
        Assert.assertEquals(newUserCredTestData, userServiceImp.authenticate(newUserCredTestData));
    }
}