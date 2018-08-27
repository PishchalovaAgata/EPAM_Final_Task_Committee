package com.pishchalova.committee.dao;

import com.pishchalova.committee.entity.User;
import com.pishchalova.committee.exception.DAOException;

public interface UserDao extends AbstractDao<Integer, User> {
    User findUserByLogin(String login) throws DAOException;
    User findUserByEmail(String email) throws DAOException;
}
