package com.revature.planetarium.service.user;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.revature.planetarium.entities.User;
import com.revature.planetarium.exceptions.UserFail;
import com.revature.planetarium.repository.user.UserDao;
import com.revature.planetarium.repository.user.UserDaoImp;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserServiceImpTest {

    private UserDaoImp daoImp;
    private UserServiceImp serviceImp;

    private User newUserCredTestData;
    private User newUserUsernameTooLong;
    private User newUserPasswordTooLong;
    private User userNotNew;
    private User userFail;

    //private List<User> premadeUsers;


    @Before
    public void setUp() throws Exception {
        daoImp = Mockito.mock(UserDaoImp.class);
        serviceImp = new UserServiceImp(daoImp);
        newUserCredTestData = new User(1, "Batman","Your mom");
        newUserUsernameTooLong = new User(2,"This is a username that is too long for the service", "I am the night");
        newUserPasswordTooLong = new User(3, "Batman", "This is a password that is too long for the service");
        // premadeUsers = new ArrayList<>();
        // premadeUsers.add(new User(1,"mockAdmin","1234"));
        // premadeUsers.add(new User(2,"mockAdmin2","12345"));
        userNotNew = new User(4,"mockAdmin", "1234");
        userFail = new User(5, "", "");
    }

    @Test
    public void createUserPos() {
        Mockito.when(daoImp.createUser(newUserCredTestData)).thenReturn(Optional.of(newUserCredTestData));
        Assert.assertEquals("Created user with username " + newUserCredTestData.getUsername() + " and password "
                    + newUserCredTestData.getPassword(),serviceImp.createUser(newUserCredTestData));
    }

    @Test
    public void createUserNegU2Long() {
        Mockito.when(daoImp.createUser(newUserUsernameTooLong)).thenReturn(Optional.of(newUserUsernameTooLong));
        UserFail e = Assert.assertThrows(UserFail.class, ()->{
            serviceImp.createUser(newUserUsernameTooLong);
        });
        System.out.println(e.getMessage());
        Assert.assertEquals("Username must be between 1 and 30 characters",e.getMessage());
    }

    @Test
    public void createUserNegP2Long() {
        Mockito.when(daoImp.createUser(newUserPasswordTooLong)).thenReturn(Optional.of(newUserPasswordTooLong));
        UserFail e = Assert.assertThrows(UserFail.class, ()->{
            serviceImp.createUser(newUserPasswordTooLong);
        });
        System.out.println(e.getMessage());
        Assert.assertEquals("Password must be between 1 and 30 characters",e.getMessage());
    }

    @Test
    public void createUserNegUExists() {
        Mockito.when(daoImp.findUserByUsername(userNotNew.getUsername())).thenReturn(Optional.of(userNotNew));
        UserFail e = Assert.assertThrows(UserFail.class, ()->{
            serviceImp.createUser(userNotNew);
        });
        System.out.println(e.getMessage());
        Assert.assertEquals("Username is already in use",e.getMessage());
    }

    // @Test
    // public void createUserNegUFail() {
    //     Mockito.when(daoImp.createUser(userFail)).thenReturn(Optional.of(userFail));
    //     UserFail e = Assert.assertThrows(UserFail.class, ()->{
    //         serviceImp.createUser(userFail);
    //     });
    //     System.out.println(e.getMessage());
    //     Assert.assertEquals("Failed to create user, please try again",e.getMessage());
    // }

    @Test
    public void authenticatePos() {
        Mockito.when(daoImp.findUserByUsername(newUserCredTestData.getUsername())).thenReturn(Optional.of(newUserCredTestData));
        Assert.assertEquals(newUserCredTestData, serviceImp.authenticate(newUserCredTestData));
    }
}