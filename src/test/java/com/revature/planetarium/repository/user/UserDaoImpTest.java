package com.revature.planetarium.repository.user;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.Setup;
import com.revature.planetarium.entities.User;
import com.revature.planetarium.exceptions.UserFail;

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

    // CREATE USER TESTS - POSITIVE
    @Test
    public void createUserPositive() {
        Optional<User> returnedUser = dao.createUser(positiveCreateUser);
        Assert.assertSame(positiveCreateUser, returnedUser.get());
    }

    // CREATE USER TESTS - NEGATIVE
    @Test
    public void createUserNegativeDuplicateUsername() {
        User duplicateUser = new User(6, "Batman", "duplicatePassword");
        UserFail e = Assert.assertThrows(UserFail.class, () -> {
            dao.createUser(duplicateUser);
        });
        System.out.println(e.getMessage());
        Assert.assertEquals(
                "[SQLITE_CONSTRAINT_UNIQUE] A UNIQUE constraint failed (UNIQUE constraint failed: users.username)",
                e.getMessage());
    }

    @Test
    public void createUserNegativeNullUsername() {
        User userWithNullUsername = new User(7, null, "password");
        UserFail e = Assert.assertThrows(UserFail.class, () -> {
            dao.createUser(userWithNullUsername);
        });
        System.out.println(e.getMessage());
        Assert.assertEquals(
                "[SQLITE_CONSTRAINT_NOTNULL] A NOT NULL constraint failed (NOT NULL constraint failed: users.username)",
                e.getMessage());
    }

    @Test
    public void createUserNegativeNullPassword() {
        User userWithNullPassword = new User(8, "uniqueName1", null);
        UserFail e = Assert.assertThrows(UserFail.class, () -> {
            dao.createUser(userWithNullPassword);
        });
        System.out.println(e.getMessage());
        Assert.assertEquals(
                "[SQLITE_CONSTRAINT_NOTNULL] A NOT NULL constraint failed (NOT NULL constraint failed: users.password)",
                e.getMessage());
    }

    // @Test(expected = UserFail.class)
    // public void createUserDuplicateUsernameShort() {
    //     User duplicateUser = new User(6, "Batman", "duplicatePassword");
    //     dao.createUser(duplicateUser);
    // }

    // FIND USER TESTS - POSITIVE
    @Test
    public void findUserByUsernamePositiveNameExists() {
        Assert.assertEquals(dao.findUserByUsername("Batman"), Optional.of(existingUser));
    }

    // FIND USER TESTS - NEGATIVE
    @Test
    public void findUserByUsernameNegativeNameDoesNotExist() {
        Assert.assertEquals(dao.findUserByUsername("userDoesNotExist"), Optional.empty());
    }

    @Test
    public void findUserByUsernameNegativeNameIsNull() {
        Assert.assertEquals(dao.findUserByUsername(null), Optional.empty());
    }

    // SQL INJECTION ATTEMPT
    @Test
    public void findUserByUsernameNegativeSqlInjection() {
        Optional<User> result = dao.findUserByUsername("'; DROP TABLE users; --");
        Assert.assertEquals(Optional.empty(), result);
    }
}