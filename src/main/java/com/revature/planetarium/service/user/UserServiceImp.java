package com.revature.planetarium.service.user;


import java.util.Optional;

import com.revature.planetarium.entities.User;
import com.revature.planetarium.exceptions.UserFail;
import com.revature.planetarium.repository.user.UserDao;

public class UserServiceImp implements UserService {
    
    private UserDao userDao;

    public UserServiceImp(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public String createUser(User newUser) {
        if (newUser.getUsername().length() < 1 || newUser.getUsername().length() > 30) {
            throw new UserFail("Username must be between 1 and 30 characters");
        }
        if (newUser.getPassword().length() < 1 || newUser.getPassword().length() > 30) {
            throw new UserFail("Password must be between 1 and 30 characters");
        }
        Optional<User> existingUser = userDao.findUserByUsername(newUser.getUsername());
        if (existingUser.isPresent()) {
            throw new UserFail("Username is already in use");
        }
        Optional<User> createdUser = userDao.createUser(newUser);
        if (createdUser.isPresent()) {
            return "Created user with username " + createdUser.get().getUsername() + " and password " + createdUser.get().getPassword();
        } else {
            throw new UserFail("Failed to create user, please try again");
        }
    }

    @Override
    public User authenticate(User credentials) {
        Optional<User> foundUser = userDao.findUserByUsername(credentials.getUsername());
        if (foundUser.isPresent()) {
            if (foundUser.get().getPassword().equals(credentials.getPassword())) {
                return foundUser.get();
            }
        }
        throw new UserFail("Username and/or password do not match");
    }

}
