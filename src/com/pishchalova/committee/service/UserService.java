package com.pishchalova.committee.service;

import com.pishchalova.committee.dao.impl.UserDaoImpl;
import com.pishchalova.committee.entity.User;
import com.pishchalova.committee.exception.DAOException;
import com.pishchalova.committee.exception.ServiceException;
import com.pishchalova.committee.util.EncryptPassword;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserService {
    private static final Logger LOGGER = LogManager.getLogger(UserService.class);
    private static UserDaoImpl userDao = new UserDaoImpl();

    public final boolean registerUser(String login, String password, String email) throws ServiceException {
        LOGGER.log(Level.INFO, "Try to register user with info: Login: " + login + " password " + " Email: " + email);
        User user = new User(login, EncryptPassword.encryptPass(password), email);
        try {
            if (userDao.create(user)) {
                LOGGER.log(Level.INFO, "User is registered!!!");
                return true;
            }
            LOGGER.log(Level.INFO, "Not correct login or password!");
            return false;
        } catch (DAOException e) {
            throw new ServiceException("Couldn't register User! With login: " + login, e);
        }
    }


    public final User authorizeUser(String login, String password) throws ServiceException {
        LOGGER.log(Level.INFO, "Try to authorise user with info: Login: " + login);
        try {
            User user = userDao.findUserByLogin(login);
            if (user != null
                    && user.getPassword().equals(EncryptPassword.encryptPass(password))
                    && user.getLogin().equals(login)
                    && user.isActivated()) {
                LOGGER.log(Level.INFO, "User is authorised!");
                return user;
            } else {
                LOGGER.log(Level.INFO, "Not correct login or password!");
                return null;
            }
        } catch (DAOException e) {
            throw new ServiceException("Couldn't find User! " + login, e);
        }
    }

    public final User findUserById(Integer id) throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to find user by Id. Id: " + id);
        try {
            return userDao.findEntityById(id);
        } catch (DAOException e) {
            throw new ServiceException("Problem with finding usr by Id! (User Service)  ", e);
        }
    }

    public final User filterUsersByLogin(String login) throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to filter user by login. Login: " + login);
        try {
            return userDao.findUserByLogin(login);
        } catch (DAOException e) {
            throw new ServiceException("Problem with filtering user by login! (User Service) login: " + login, e);
        }
    }

    public final User filterUsersByEmail(String email) throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to filter users by email. Email: " + email);
        try {
            return userDao.findUserByEmail(email);
        } catch (DAOException e) {
            throw new ServiceException("Problem with filtering user by email! (User Service) email: " + email, e);
        }
    }

    public final boolean updateUser(User user) throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to update user: User id: " + user.getId());
        try {
            if (findUserById(user.getId()) != null) {
                return userDao.update(user);
            }
            return false;
        } catch (DAOException e) {
            throw new ServiceException("Problem with filtering user by login! (User Service)", e);
        }
    }
}
