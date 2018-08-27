package com.pishchalova.committee.dao.impl;


import com.pishchalova.committee.dao.SubjectDao;
import com.pishchalova.committee.entity.Subject;
import com.pishchalova.committee.exception.DAOException;
import com.pishchalova.committee.pool.ConnectionPool;
import com.pishchalova.committee.pool.ProxyConnection;
import org.apache.logging.log4j.Level;
import com.pishchalova.committee.util.helper.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;


public class SubjectDaoImpl implements SubjectDao {
    private static final Logger LOGGER = LogManager.getLogger(SubjectDaoImpl.class);

    private static final String SELECT_SUBJECT_BY_ID = "SELECT committee_db.subject.id, committee_db.subject.name," +
            " committee_db.subject.is_unavailable FROM committee_db.subject WHERE committee_db.subject.id = ? ";
    private static final String SELECT_ALL_FROM_SUBJECT = "SELECT committee_db.subject.id, committee_db.subject.name," +
            " committee_db.subject.is_unavailable FROM committee_db.subject WHERE committee_db.subject.is_unavailable = 0";
    private static final String SELECT_SUBJECT_BY_NAME = "SELECT committee_db.subject.id, committee_db.subject.name," +
            " committee_db.subject.is_unavailable FROM committee_db.subject WHERE committee_db.subject.name = ? ";
    private static final String DELETE_SUBJECT_BY_ID = "UPDATE committee_db.subject SET committee_db.subject.is_unavailable = 1 " +
            "WHERE committee_db.subject.id = ?";
    private static final String INSERT_SUBJECT = "INSERT INTO committee_db.subject(committee_db.subject.name, committee_db.subject.is_unavailable) " +
            "VALUES (?,?)";
    private static final String UPDATE_SUBJECT = "UPDATE committee_db.subject SET committee_db.subject.name = ? WHERE committee_db.subject.id = ?";

    public enum SubjectColumn {
        ID_COLUMN("id"),
        NAME_COLUMN("name"),
        IS_UNAVAILABLE_COLUMN("is_unavailable");

        private String columnValue;

        SubjectColumn(String columnValue) {
            this.columnValue = columnValue;
        }

        public String getColumnValue() {
            return columnValue;
        }

        public void setColumnValue(String columnValue) {
            this.columnValue = columnValue;
        }

        public static SubjectColumn hasColumnInSubjectColumn(String value) {
            for (SubjectColumn subjectColumn : SubjectColumn.values()) {
                if (subjectColumn.getColumnValue().equals(value)) {
                    return subjectColumn;
                }
            }
            return null;
        }
    }

    @Override
    public ArrayList<Subject> findAll() throws DAOException {
        LOGGER.log(Level.INFO, "Selecting all subjects!");
        ProxyConnection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            statement = Objects.requireNonNull(connection).createStatement();
            resultSet = statement.executeQuery(SELECT_ALL_FROM_SUBJECT);
            ArrayList<Subject> subjectList = new ArrayList<>();
            while (resultSet.next()) {
                subjectList.add(new Subject(resultSet.getInt(SubjectColumn.ID_COLUMN.getColumnValue()),
                        resultSet.getString(SubjectColumn.NAME_COLUMN.getColumnValue()),
                        resultSet.getBoolean(SubjectColumn.IS_UNAVAILABLE_COLUMN.getColumnValue())));
            }
            return subjectList;
        } catch (SQLException e) {
            throw new DAOException("There is a problem while selecting all subjects!", e);
        } finally {
            DaoHelper.closeResource(resultSet, connection, statement);
        }
    }

    @Override
    public Subject findEntityById(Integer id) throws DAOException {
        LOGGER.log(Level.INFO, "Selecting subject by id. Id: " + id);
        ResultSet resultSet = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(SELECT_SUBJECT_BY_ID);
            if (preparedStatement != null) {
                preparedStatement.setInt(1, id);
                resultSet = preparedStatement.executeQuery();
            }
            if (resultSet != null && resultSet.next()) {
                return new Subject(resultSet.getInt(SubjectColumn.ID_COLUMN.getColumnValue()),
                        resultSet.getString(SubjectColumn.NAME_COLUMN.getColumnValue()),
                        resultSet.getBoolean(SubjectColumn.IS_UNAVAILABLE_COLUMN.getColumnValue()));
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException("There is a problem while selecting subject by id! Id: " + id, e);
        } finally {
            DaoHelper.closeResource(resultSet, connection, preparedStatement);
        }
    }

    @Override
    public boolean create(Subject entity) throws DAOException {
        LOGGER.log(Level.DEBUG, "Creating new subject. " + entity.toString());
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(INSERT_SUBJECT);
            if (preparedStatement != null) {
                preparedStatement.setString(1, entity.getName());
                preparedStatement.setBoolean(2, entity.isUnavailable());
                return preparedStatement.executeUpdate() > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new DAOException("Problem with creating new Subject! " + entity.toString(), e);
        } finally {
            DaoHelper.closeResource(connection, preparedStatement);
        }

    }

    @Override
    public boolean update(Subject entity) throws DAOException {
        LOGGER.log(Level.DEBUG, "Updating subject. " + entity.toString());
        ProxyConnection connection;
        PreparedStatement preparedStatement;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(UPDATE_SUBJECT);
            if (preparedStatement != null) {
                preparedStatement.setInt(2, entity.getId());
                preparedStatement.setString(1, entity.getName());
                return preparedStatement.executeUpdate() > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new DAOException("Problem with Updating Subject! " + entity.toString(), e);
        }
    }

    @Override
    public boolean delete(Integer id) throws DAOException {
        LOGGER.log(Level.INFO, "Deleting subject by id. Id: " + id);
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(DELETE_SUBJECT_BY_ID);
            if (preparedStatement != null) {
                preparedStatement.setInt(1, id);
                return preparedStatement.executeUpdate() > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new DAOException("Problem with deleting subject! Id:" + id, e);
        } finally {
            DaoHelper.closeResource(connection, preparedStatement);
        }
    }

    @Override
    public Subject filterSubjectsByName(String name) throws DAOException {
        LOGGER.log(Level.INFO, "Filter Subjects by Name! Name: " + name);
        ResultSet resultSet = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(SELECT_SUBJECT_BY_NAME);
            if (preparedStatement != null) {
                preparedStatement.setString(1, name);
                resultSet = preparedStatement.executeQuery();
            }
            if (resultSet != null && resultSet.next()) {
                return new Subject(resultSet.getInt(SubjectColumn.ID_COLUMN.getColumnValue()),
                        resultSet.getString(SubjectColumn.NAME_COLUMN.getColumnValue()),
                        resultSet.getBoolean(SubjectColumn.IS_UNAVAILABLE_COLUMN.getColumnValue()));
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException("Problem with filtering subjects by name! name:" + name, e);
        } finally {
            DaoHelper.closeResource(resultSet, connection, preparedStatement);
        }
    }
}
