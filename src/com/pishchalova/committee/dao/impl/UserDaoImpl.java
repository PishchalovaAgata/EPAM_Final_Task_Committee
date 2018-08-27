package com.pishchalova.committee.dao.impl;

import com.pishchalova.committee.util.ParameterConst.CommandParameterConst;
import com.pishchalova.committee.util.helper.*;
import com.pishchalova.committee.dao.UserDao;
import com.pishchalova.committee.entity.User;
import com.pishchalova.committee.exception.DAOException;
import com.pishchalova.committee.pool.ConnectionPool;
import com.pishchalova.committee.pool.ProxyConnection;
import com.pishchalova.committee.util.EncryptPassword;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;


public class UserDaoImpl implements UserDao {
    private static final Logger LOGGER = LogManager.getLogger(UserDaoImpl.class);

    private static final String SELECT_USER_BY_ID = "SELECT committee_db.user.id, committee_db.user.login, committee_db.user.password," +
            " committee_db.user.role,  committee_db.user.email, committee_db.user.is_activated " +
            "FROM  committee_db.user WHERE committee_db.user.id = ?";
    private static final String SELECT_ALL_FROM_USER = "SELECT committee_db.user.id, committee_db.user.login, committee_db.user.password," +
            " committee_db.user.role,  committee_db.user.email, committee_db.user.is_activated FROM  committee_db.user";
    private static final String DELETE_USER_BY_ID = "DELETE FROM committee_db.user WHERE committee_db.user.id = ?";
    private static final String INSERT_USER = "INSERT INTO committee_db.user(committee_db.user.login, committee_db.user.password, " +
            "committee_db.user.role,  committee_db.user.email, committee_db.user.is_activated) VALUES (?,?,?,?,?)";
    private static final String UPDATE_USER = "UPDATE committee_db.user SET committee_db.user.login = ?, committee_db.user.password = ?, " +
            "committee_db.user.email = ?, committee_db.user.is_activated = ? WHERE committee_db.user.id = ?";
    private static final String SELECT_USER_BY_LOGIN = "SELECT committee_db.user.id, committee_db.user.login, committee_db.user.password," +
            " committee_db.user.role, committee_db.user.email, committee_db.user.is_activated" +
            " FROM committee_db.user WHERE committee_db.user.login = ?";
    private static final String SELECT_USER_BY_EMAIL = "SELECT committee_db.user.id, committee_db.user.login, committee_db.user.password," +
            " committee_db.user.role, committee_db.user.email, committee_db.user.is_activated " +
            "FROM committee_db.user WHERE committee_db.user.email = ?";


    public enum UserColumn {
        ID_COLUMN("id"),
        LOGIN_COLUMN("login"),
        PASS_COLUMN("password"),
        ROLE_COLUMN("role"),
        EMAIL_COLUMN("email"),
        IS_ACTIVATED("is_activated");

        private String columnValue;

        UserColumn(String columnValue) {
            this.columnValue = columnValue;
        }

        public String getColumnValue() {
            return columnValue;
        }

        public void setColumnValue(String columnValue) {
            this.columnValue = columnValue;
        }

        public static UserColumn hasColumnInUserColumn(String value) {
            for (UserColumn userColumn : UserColumn.values()) {
                if (userColumn.getColumnValue().equals(value)) {
                    return userColumn;
                }
            }
            return null;
        }
    }

    @Override
    public ArrayList<User> findAll() throws DAOException {
        LOGGER.log(Level.INFO, "Selecting all users!");
        ProxyConnection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            statement = Objects.requireNonNull(connection).createStatement();
            resultSet = statement.executeQuery(SELECT_ALL_FROM_USER);
            ArrayList<User> userList = new ArrayList<>();
            while (resultSet.next()) {
                userList.add(new User(resultSet.getInt(UserColumn.ID_COLUMN.getColumnValue()),
                        User.Role.valueOf(resultSet.getString(UserColumn.ROLE_COLUMN.getColumnValue())),
                        resultSet.getString(UserColumn.LOGIN_COLUMN.getColumnValue()),
                        resultSet.getString(UserColumn.PASS_COLUMN.getColumnValue()),
                        resultSet.getString(UserColumn.EMAIL_COLUMN.getColumnValue()),
                        resultSet.getBoolean(UserColumn.IS_ACTIVATED.getColumnValue())));
            }
            return userList;
        } catch (SQLException e) {
            throw new DAOException("There is a problem while selecting all users!", e);
        } finally {
            DaoHelper.closeResource(resultSet, connection, statement);
        }
    }

    @Override
    public User findEntityById(Integer id) throws DAOException {
        LOGGER.log(Level.INFO, "Selecting user by id. Id: " + id);
        ResultSet resultSet = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            System.out.println("In try block");
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(SELECT_USER_BY_ID);
            if (preparedStatement != null) {
                preparedStatement.setInt(1, id);
                resultSet = preparedStatement.executeQuery();
            }
            if (resultSet != null && resultSet.next()) {
                return new User(resultSet.getInt(UserColumn.ID_COLUMN.getColumnValue()),
                        User.Role.valueOf(resultSet.getString(UserColumn.ROLE_COLUMN.getColumnValue()).toUpperCase()),
                        resultSet.getString(UserColumn.LOGIN_COLUMN.getColumnValue()),
                        resultSet.getString(UserColumn.PASS_COLUMN.getColumnValue()),
                        resultSet.getString(UserColumn.EMAIL_COLUMN.getColumnValue()),
                        resultSet.getBoolean(UserColumn.IS_ACTIVATED.getColumnValue()));
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException("There is a problem while finding user by id! " + id, e);
        } finally {
            DaoHelper.closeResource(resultSet, connection, preparedStatement);
        }

    }

    @Override
    public boolean create(User entity) throws DAOException {
        LOGGER.log(Level.DEBUG, "Creating new User " + entity.toString());
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            if (findUserByLogin(entity.getLogin()) == null) {
                preparedStatement = Objects.requireNonNull(connection).prepareStatement(INSERT_USER);
                if (preparedStatement != null) {
                    preparedStatement.setString(1, entity.getLogin());
                    preparedStatement.setString(2, entity.getPassword());
                    preparedStatement.setString(3, entity.getRole().name().toLowerCase());
                    preparedStatement.setString(4, entity.getEmail());
                    preparedStatement.setBoolean(5, entity.isActivated());
                    return preparedStatement.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            throw new DAOException("There is a problem while creating User! ", e);
        } finally {
            DaoHelper.closeResource(connection, preparedStatement);
        }
        return false;
    }

    @Override
    public boolean update(User entity) throws DAOException {
        LOGGER.log(Level.DEBUG, "Updating user!" + entity.toString());
        ProxyConnection connection;
        PreparedStatement preparedStatement;
        String password;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            if (entity.getPassword().equals(CommandParameterConst.EMPTY_STRING)) {
                password = findEntityById(entity.getId()).getPassword();
            } else {
                password = EncryptPassword.encryptPass(entity.getPassword());
            }
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(UPDATE_USER);
            if (preparedStatement != null) {
                preparedStatement.setInt(5, entity.getId());
                preparedStatement.setString(1, entity.getLogin());
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, entity.getEmail());
                preparedStatement.setBoolean(4, entity.isActivated());
                return preparedStatement.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new DAOException("There is a problem with Updating User! User: " + entity.toString(), e);
        }
        return false;
    }

    @Override
    public boolean delete(Integer id) throws DAOException {
        LOGGER.log(Level.INFO, "Deleting user by id. Id: " + id);
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(DELETE_USER_BY_ID);
            if (preparedStatement != null) {
                preparedStatement.setInt(1, id);
                return preparedStatement.executeUpdate() > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new DAOException("There is a problem with deleting User! Id: " + id, e);
        } finally {
            DaoHelper.closeResource(connection, preparedStatement);
        }
    }

    @Override
    public User findUserByLogin(String login) throws DAOException {
        LOGGER.log(Level.DEBUG, "Finding user by login, where login: " + login);
        ResultSet resultSet = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(SELECT_USER_BY_LOGIN);
            if (preparedStatement != null) {
                preparedStatement.setString(1, login);
                resultSet = preparedStatement.executeQuery();
            }
            if (resultSet != null && resultSet.next()) {
                return new User(resultSet.getInt(UserColumn.ID_COLUMN.getColumnValue()),
                        User.Role.valueOf(resultSet.getString(UserColumn.ROLE_COLUMN.getColumnValue()).toUpperCase()),
                        resultSet.getString(UserColumn.LOGIN_COLUMN.getColumnValue()),
                        resultSet.getString(UserColumn.PASS_COLUMN.getColumnValue()),
                        resultSet.getString(UserColumn.EMAIL_COLUMN.getColumnValue()),
                        resultSet.getBoolean(UserColumn.IS_ACTIVATED.getColumnValue()));
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException("Problem with finding User by login" + login, e);
        } finally {
            DaoHelper.closeResource(resultSet, connection, preparedStatement);
        }
    }

@Override
    public User findUserByEmail(String email) throws DAOException {
        LOGGER.log(Level.DEBUG, "Finding user by login, where login: " + email);
        ResultSet resultSet = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(SELECT_USER_BY_EMAIL);
            if (preparedStatement != null) {
                preparedStatement.setString(1, email);
                resultSet = preparedStatement.executeQuery();
            }
            if (resultSet != null && resultSet.next()) {
                return new User(resultSet.getInt(UserColumn.ID_COLUMN.getColumnValue()),
                        User.Role.valueOf(resultSet.getString(UserColumn.ROLE_COLUMN.getColumnValue()).toUpperCase()),
                        resultSet.getString(UserColumn.LOGIN_COLUMN.getColumnValue()),
                        resultSet.getString(UserColumn.PASS_COLUMN.getColumnValue()),
                        resultSet.getString(UserColumn.EMAIL_COLUMN.getColumnValue()),
                        resultSet.getBoolean(UserColumn.IS_ACTIVATED.getColumnValue()));
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException("Problem with finding User by login" + email, e);
        } finally {
            DaoHelper.closeResource(resultSet, connection, preparedStatement);
        }
    }
}