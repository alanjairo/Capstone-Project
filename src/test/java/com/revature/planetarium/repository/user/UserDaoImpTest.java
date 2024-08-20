package com.revature.planetarium.repository.user;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.Setup;
import com.revature.planetarium.entities.User;

import static org.junit.Assert.*;

import java.sql.SQLException;

import java.util.Optional;

public class UserDaoImpTest {

    private User positiveCreateUser;
    private User existingUser;

    private UserDaoImp dao;

    @BeforeClass
    public static void testDatabaseSetup() throws SQLException {
        Setup.getConnection();
        Setup.resetTestDatabase();
    }

    @Before
    public void setUp() throws Exception {
        positiveCreateUser = new User(5, "positiveUserTest", "positiveUserTest");
        existingUser = new User(1, "Batman", "I am the night");
        dao = new UserDaoImp();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void createUserPositive() {
        Optional<User> returnedUser = dao.createUser(positiveCreateUser);
        Assert.assertSame(positiveCreateUser, returnedUser.get());
    }

    @Test
    public void findUserByUsernamePositive() {
        Assert.assertEquals(dao.findUserByUsername("Batman"), Optional.of(existingUser));
    }
}