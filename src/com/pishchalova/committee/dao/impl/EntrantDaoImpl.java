package com.pishchalova.committee.dao.impl;

import com.pishchalova.committee.util.helper.*;
import com.pishchalova.committee.dao.EntrantDao;
import com.pishchalova.committee.entity.Entrant;
import com.pishchalova.committee.entity.Mark;
import com.pishchalova.committee.exception.DAOException;
import com.pishchalova.committee.pool.ConnectionPool;
import com.pishchalova.committee.pool.ProxyConnection;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntrantDaoImpl implements EntrantDao {
    private static final Logger LOGGER = LogManager.getLogger(EntrantDaoImpl.class);

    private static final String SELECT_ENTRANT_BY_ID = "SELECT committee_db.university_entrant.id, committee_db.university_entrant.firstname," +
            " committee_db.university_entrant.surname,  committee_db.university_entrant.certificate, " +
            "committee_db.university_entrant.user_id, committee_db.university_entrant.faculty_id," +
            " committee_db.university_entrant.status, committee_db.university_entrant.is_unavailable " +
            "FROM committee_db.university_entrant WHERE committee_db.university_entrant.id = ?";
    private static final String SELECT_ENTRANT_BY_FACULTY_ID = "SELECT committee_db.university_entrant.id," +
            " committee_db.university_entrant.firstname, committee_db.university_entrant.surname, " +
            " committee_db.university_entrant.certificate, committee_db.university_entrant.user_id," +
            " committee_db.university_entrant.faculty_id, committee_db.university_entrant.status," +
            " committee_db.university_entrant.is_unavailable FROM committee_db.university_entrant " +
            "WHERE committee_db.university_entrant.faculty_id = ? AND committee_db.university_entrant.status = 'enlisted'";

    private static final String SELECT_ALL_FROM_ENTRANT = "SELECT committee_db.show_entrants_with_total_mark.id," +
            " committee_db.show_entrants_with_total_mark.surname, committee_db.show_entrants_with_total_mark.firstname," +
            " committee_db.show_entrants_with_total_mark.faculty_id, committee_db.show_entrants_with_total_mark.user_id," +
            " committee_db.show_entrants_with_total_mark.status, committee_db.show_entrants_with_total_mark.total_mark  " +
            "FROM show_entrants_with_total_mark";
    private static final String INSERT_ENTRANT = "INSERT INTO committee_db.university_entrant(committee_db.university_entrant.firstname," +
            " committee_db.university_entrant.surname, committee_db.university_entrant.certificate," +
            " committee_db.university_entrant.user_id,committee_db.university_entrant.faculty_id," +
            " committee_db.university_entrant.status, committee_db.university_entrant.is_unavailable) VALUES (?,?,?,?,?,?,?)";
    private static final String DELETE_ENTRANT_BY_ID = "DELETE FROM committee_db.university_entrant WHERE committee_db.university_entrant.id = ?";
    private static final String UPDATE_ENTRANT = "UPDATE committee_db.university_entrant " +
            "SET committee_db.university_entrant.firstname= ?, committee_db.university_entrant.surname= ?, " +
            " committee_db.university_entrant.certificate = ?, committee_db.university_entrant.user_id = ?, " +
            "committee_db.university_entrant.faculty_id = ? , committee_db.university_entrant.status = ?, " +
            "committee_db.university_entrant.is_unavailable = ?  WHERE committee_db.university_entrant.id = ?";

    private static final String SELECT_MARKS_BY_ENTRANT_ID = "SELECT committee_db.mark.subject_id, committee_db.mark.value " +
            "FROM committee_db.mark WHERE committee_db.mark.university_entrant_id = ?";
    private static final String DELETE_MARKS_BY_ENTRANT_ID = "DELETE FROM committee_db.mark WHERE committee_db.mark.university_entrant_id = ?";
    private static final String INSERT_MARKS_BY_ENTRANT_ID = "INSERT INTO  committee_db.mark (committee_db.mark.university_entrant_id," +
            " committee_db.mark.subject_id, committee_db.mark.value) VALUES (?,?,?)";
    private static final String FIND_ENTRANT_BY_USER = "SELECT * FROM committee_db.university_entrant" +
            " WHERE committee_db.university_entrant.user_id = ?";
    private static final String SORT_ENTRANTS_OF_FACULTY_BY_TOTAL_MARK = "SELECT *  FROM show_entrants_with_total_mark " +
            "WHERE show_entrants_with_total_mark.faculty_id = ? ORDER BY show_entrants_with_total_mark.total_mark DESC";

    private static final String GET_TOTAL_MARK_FOR_ENTRANT = "SELECT show_entrants_with_total_mark.total_mark  " +
            "FROM show_entrants_with_total_mark WHERE show_entrants_with_total_mark.id = ? ";

    private static final String GET_SUBJECTS_FOR_ENTRANT = "SELECT committee_db.mark.subject_id FROM committee_db.mark " +
            "WHERE committee_db.mark.university_entrant_id = ?";

    private static final Integer AMOUNT_OF_SUBJECTS_FOR_ONE_FACULTY = 3;

    public enum EntrantColumn {
        ID_COLUMN("id"),
        FIRST_NAME_COLUMN("firstname"),
        SURNAME_COLUMN("surname"),
        CERTIFICATE_COLUMN("certificate"),
        FACULTY_ID_COLUMN("faculty_id"),
        USER_ID_COLUMN("user_id"),
        STATUS_COLUMN("status"),
        TOTAL_MARK_COLUMN("total_mark"),
        IS_UNAVAILABLE_COLUMN("is_unavailable"),
        SUBJECT_ID_COLUMN("subject_id"),
        VALUE_MARK_COLUMN("value");

        private String columnValue;

        EntrantColumn(String columnValue) {
            this.columnValue = columnValue;
        }

        public String getColumnValue() {
            return columnValue;
        }

        public void setColumnValue(String columnValue) {
            this.columnValue = columnValue;
        }

        public static EntrantColumn hasColumnInEntrantColumn(String value) {
            for (EntrantColumn entrantColumn : EntrantColumn.values()) {
                if (entrantColumn.getColumnValue().equals(value)) {
                    return entrantColumn;
                }
            }
            return null;
        }
    }

    @Override
    public List<Entrant> findAll() throws DAOException {
        LOGGER.log(Level.INFO, "Selecting all entrants!");
        ProxyConnection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            statement = Objects.requireNonNull(connection).createStatement();
            resultSet = statement.executeQuery(SELECT_ALL_FROM_ENTRANT);
            ArrayList<Entrant> entityList = new ArrayList<>();
            while (resultSet.next()) {
                entityList.add(new Entrant(resultSet.getInt(EntrantColumn.ID_COLUMN.getColumnValue()),
                        resultSet.getString(EntrantColumn.FIRST_NAME_COLUMN.getColumnValue()),
                        resultSet.getString(EntrantColumn.SURNAME_COLUMN.getColumnValue()),
                        resultSet.getInt(EntrantColumn.CERTIFICATE_COLUMN.getColumnValue()),
                        resultSet.getInt(EntrantColumn.FACULTY_ID_COLUMN.getColumnValue()),
                        resultSet.getInt(EntrantColumn.USER_ID_COLUMN.getColumnValue()),
                        findMarksByEntrantId(resultSet.getInt(EntrantColumn.ID_COLUMN.getColumnValue())),
                        Entrant.Status.valueOf(resultSet.getString(EntrantColumn.STATUS_COLUMN.getColumnValue()).toUpperCase()),
                        resultSet.getBoolean(EntrantColumn.IS_UNAVAILABLE_COLUMN.getColumnValue())));
            }
            return entityList;
        } catch (SQLException e) {
            throw new DAOException("Problem with selecting all entrants! ", e);
        } finally {
            DaoHelper.closeResource(resultSet, connection, statement);
        }
    }

    @Override
    public Entrant findEntityById(Integer id) throws DAOException {
        LOGGER.log(Level.INFO, "Selecting entrant by id. Id: " + id);
        ResultSet resultSet = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(SELECT_ENTRANT_BY_ID);
            if (preparedStatement != null) {
                preparedStatement.setInt(1, id);
                resultSet = preparedStatement.executeQuery();
            }
            if (resultSet != null && resultSet.next()) {
                return new Entrant(resultSet.getInt(EntrantColumn.ID_COLUMN.getColumnValue()),
                        resultSet.getString(EntrantColumn.FIRST_NAME_COLUMN.getColumnValue()),
                        resultSet.getString(EntrantColumn.SURNAME_COLUMN.getColumnValue()),
                        resultSet.getInt(EntrantColumn.CERTIFICATE_COLUMN.getColumnValue()),
                        resultSet.getInt(EntrantColumn.FACULTY_ID_COLUMN.getColumnValue()),
                        resultSet.getInt(EntrantColumn.USER_ID_COLUMN.getColumnValue()),
                        findMarksByEntrantId(resultSet.getInt(EntrantColumn.ID_COLUMN.getColumnValue())),
                        Entrant.Status.valueOf(resultSet.getString(EntrantColumn.STATUS_COLUMN.getColumnValue()).toUpperCase()),
                        resultSet.getBoolean(EntrantColumn.IS_UNAVAILABLE_COLUMN.getColumnValue()));
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException("Problem with selecting entrant by Id! Id: " + id, e);
        } finally {
            DaoHelper.closeResource(resultSet, connection, preparedStatement);
        }
    }

    @Override
    public boolean create(Entrant entity) throws DAOException {
        LOGGER.log(Level.DEBUG, "Creating new Entrant." + entity.toString());
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        boolean flag = false;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(INSERT_ENTRANT);
            if (preparedStatement != null) {
                preparedStatement.setString(1, entity.getEntrantFirstName());
                preparedStatement.setString(2, entity.getEntrantSurName());
                preparedStatement.setInt(3, entity.getCertificate());
                preparedStatement.setInt(4, entity.getUserId());
                preparedStatement.setInt(5, entity.getFacultyId());
                preparedStatement.setString(6, entity.getStatus().name().toLowerCase());
                preparedStatement.setBoolean(7, entity.isUnavailable());
                flag = preparedStatement.executeUpdate() > 0;
                entity.setEntrantId(filterEntrantsByUserId(entity.getUserId()).getEntrantId());
                flag &= createMarksForEntrantId(entity);
            }
            return flag;
        } catch (SQLException e) {
            throw new DAOException("Problem with creation Entrant! " + entity.toString(), e);
        } finally {
            DaoHelper.closeResource(connection, preparedStatement);
        }
    }

    @Override
    public boolean update(Entrant entity) throws DAOException {
        LOGGER.log(Level.INFO, "Updating entrant. " + entity.toString());
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(UPDATE_ENTRANT);
            if (preparedStatement != null) {
                preparedStatement.setInt(8, entity.getEntrantId());
                preparedStatement.setString(1, entity.getEntrantFirstName());
                preparedStatement.setString(2, entity.getEntrantSurName());
                preparedStatement.setInt(3, entity.getCertificate());
                preparedStatement.setInt(4, entity.getUserId());
                preparedStatement.setInt(5, entity.getFacultyId());
                preparedStatement.setString(6, entity.getStatus().name().toLowerCase());
                preparedStatement.setBoolean(7, entity.isUnavailable());
                return updateMarksForEntrantId(entity) && preparedStatement.executeUpdate() > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new DAOException("Problem with Updating Entrant! " + entity.toString(), e);
        } finally {
            DaoHelper.closeResource(connection, preparedStatement);
        }
    }

    @Override
    public boolean delete(Integer id) throws DAOException {
        LOGGER.log(Level.INFO, "Deleting entrant by id. Id: " + id);
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(DELETE_ENTRANT_BY_ID);
            if (preparedStatement != null) {
                preparedStatement.setInt(1, id);
                return deleteMarksForEntrantId(id) && preparedStatement.executeUpdate() > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new DAOException("Problem with deleting entrant! Id:" + id, e);
        } finally {
            DaoHelper.closeResource(connection, preparedStatement);
        }
    }

    private boolean createMarksForEntrantId(Entrant entity) throws DAOException {
        LOGGER.log(Level.DEBUG, "Creating marks for new Entrant. " + entity.toString());
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        boolean flag = false;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(INSERT_MARKS_BY_ENTRANT_ID);
            for (int i = 0; i < AMOUNT_OF_SUBJECTS_FOR_ONE_FACULTY; i++) {
                if (preparedStatement != null) {
                    preparedStatement.setInt(1, entity.getEntrantId());
                    preparedStatement.setInt(2, entity.getMarks().get(i).getSubjectId());
                    preparedStatement.setInt(3, entity.getMarks().get(i).getValue());
                    flag = preparedStatement.executeUpdate() > 0;
                }
            }
            return flag;
        } catch (SQLException e) {
            throw new DAOException("Problem with creating marks for entrant! ", e);
        } finally {
            DaoHelper.closeResource(connection, preparedStatement);
        }
    }

    private boolean updateMarksForEntrantId(Entrant entrant) throws DAOException {
        LOGGER.log(Level.DEBUG, "Updating entrant marks.Entrant: " + entrant.toString());
        return deleteMarksForEntrantId(entrant.getEntrantId()) && createMarksForEntrantId(entrant);
    }

    private boolean deleteMarksForEntrantId(Integer entrantId) throws DAOException {
        LOGGER.log(Level.INFO, "Deleting marks for entrant id. Id: " + entrantId);
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(DELETE_MARKS_BY_ENTRANT_ID);
            if (preparedStatement != null) {
                preparedStatement.setInt(1, entrantId);
                return preparedStatement.executeUpdate() > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new DAOException("Problem with deleting marks for entrant! id: " + entrantId, e);
        } finally {
            DaoHelper.closeResource(connection, preparedStatement);
        }
    }

    private ArrayList<Mark> findMarksByEntrantId(Integer entrantId) throws DAOException {
        LOGGER.log(Level.DEBUG, "Finding Marks for entrant. id: " + entrantId);
        ResultSet resultSet = null;
        ProxyConnection connection = null;
        ArrayList<Mark> marksForEntrant = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(SELECT_MARKS_BY_ENTRANT_ID);
            if (preparedStatement != null) {
                preparedStatement.setInt(1, entrantId);
                resultSet = preparedStatement.executeQuery();
            }
            while (resultSet != null && resultSet.next()) {
                marksForEntrant.add(new Mark(resultSet.getInt(EntrantColumn.SUBJECT_ID_COLUMN.getColumnValue()),
                        resultSet.getInt(EntrantColumn.VALUE_MARK_COLUMN.getColumnValue())));
            }
            return marksForEntrant;
        } catch (SQLException e) {
            throw new DAOException("Problem with finding Marks for entrant with id: " + entrantId, e);
        } finally {
            DaoHelper.closeResource(resultSet, connection, preparedStatement);
        }
    }

    public ArrayList<Integer> findSubjectsByEntrantId(Integer entrantId) throws DAOException {
        LOGGER.log(Level.DEBUG, "Finding Subjects for entrant. Entrant id: " + entrantId);
        ResultSet resultSet = null;
        ProxyConnection connection = null;
        ArrayList<Integer> subjectIdsForEntrant = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(GET_SUBJECTS_FOR_ENTRANT);
            if (preparedStatement != null) {
                preparedStatement.setInt(1, entrantId);
                resultSet = preparedStatement.executeQuery();
            }
            while (resultSet != null && resultSet.next()) {
                subjectIdsForEntrant.add(resultSet.getInt(EntrantColumn.SUBJECT_ID_COLUMN.getColumnValue()));
            }
            return subjectIdsForEntrant;
        } catch (SQLException e) {
            throw new DAOException("Problem with finding subjects for entrant with id:  " + entrantId, e);
        } finally {
            DaoHelper.closeResource(resultSet, connection, preparedStatement);
        }
    }

    @Override
    public Entrant filterEntrantsByUserId(Integer value) throws DAOException {
        LOGGER.log(Level.DEBUG, "Finding entrant by user id, where id " + value);
        ResultSet resultSet = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(FIND_ENTRANT_BY_USER);
            if (preparedStatement != null) {
                preparedStatement.setInt(1, value);
                System.out.println(preparedStatement.toString());
                resultSet = preparedStatement.executeQuery();
            }
            if (resultSet != null && resultSet.next()) {
                return new Entrant(resultSet.getInt(EntrantColumn.ID_COLUMN.getColumnValue()),
                        resultSet.getString(EntrantColumn.FIRST_NAME_COLUMN.getColumnValue()),
                        resultSet.getString(EntrantColumn.SURNAME_COLUMN.getColumnValue()),
                        resultSet.getInt(EntrantColumn.CERTIFICATE_COLUMN.getColumnValue()),
                        resultSet.getInt(EntrantColumn.FACULTY_ID_COLUMN.getColumnValue()),
                        resultSet.getInt(EntrantColumn.USER_ID_COLUMN.getColumnValue()),
                        findMarksByEntrantId(resultSet.getInt(EntrantColumn.ID_COLUMN.getColumnValue())),
                        Entrant.Status.valueOf(resultSet.getString(EntrantColumn.STATUS_COLUMN.getColumnValue()).toUpperCase()),
                        resultSet.getBoolean(EntrantColumn.IS_UNAVAILABLE_COLUMN.getColumnValue()));
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException("Problem with finding entrant by user id! Id: " + value, e);
        } finally {
            DaoHelper.closeResource(resultSet, connection, preparedStatement);
        }
    }

    @Override
    public Integer getTotalMarkForEntrant(Integer entrantId) throws DAOException {
        LOGGER.log(Level.INFO, "Selecting entrant total mark by Entrant id. Id: " + entrantId);
        ResultSet resultSet = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(GET_TOTAL_MARK_FOR_ENTRANT);
            if (preparedStatement != null) {
                preparedStatement.setInt(1, entrantId);
                resultSet = preparedStatement.executeQuery();
            }
            if (resultSet != null && resultSet.next()) {
                return resultSet.getInt(EntrantColumn.TOTAL_MARK_COLUMN.getColumnValue());
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException("Problem with getting total mark for entrant with id: " + entrantId, e);
        } finally {
            DaoHelper.closeResource(resultSet, connection, preparedStatement);
        }
    }

    @Override
    public ArrayList<Entrant> filterEntityByFacultyId(Integer facultyId) throws DAOException {
        LOGGER.log(Level.INFO, "Selecting entrant by Faculty id. facultyId: " + facultyId);
        ResultSet resultSet = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(SELECT_ENTRANT_BY_FACULTY_ID);
            if (preparedStatement != null) {
                preparedStatement.setInt(1, facultyId);
                resultSet = preparedStatement.executeQuery();
            }
            ArrayList<Entrant> entrantArrayList = new ArrayList<>();
            if (resultSet != null && resultSet.next()) {
                entrantArrayList.add(new Entrant(resultSet.getInt(EntrantColumn.ID_COLUMN.getColumnValue()),
                        resultSet.getString(EntrantColumn.FIRST_NAME_COLUMN.getColumnValue()),
                        resultSet.getString(EntrantColumn.SURNAME_COLUMN.getColumnValue()),
                        resultSet.getInt(EntrantColumn.CERTIFICATE_COLUMN.getColumnValue()),
                        resultSet.getInt(EntrantColumn.FACULTY_ID_COLUMN.getColumnValue()),
                        resultSet.getInt(EntrantColumn.USER_ID_COLUMN.getColumnValue()),
                        findMarksByEntrantId(resultSet.getInt(EntrantColumn.ID_COLUMN.getColumnValue())),
                        Entrant.Status.valueOf(resultSet.getString(EntrantColumn.STATUS_COLUMN.getColumnValue()).toUpperCase()),
                        resultSet.getBoolean(EntrantColumn.IS_UNAVAILABLE_COLUMN.getColumnValue())));
            }
            return entrantArrayList;
        } catch (SQLException e) {
            throw new DAOException("Problem with selecting entrant by Id! Id: " + facultyId, e);
        } finally {
            DaoHelper.closeResource(resultSet, connection, preparedStatement);
        }
    }

    @Override
    public ArrayList<Integer> sortEntrantsOfFaculty(Integer facultyId) throws DAOException {
        LOGGER.log(Level.INFO, "Sort entrants of faculty by total mark! Faculty ID:" + facultyId);
        ResultSet resultSet = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(SORT_ENTRANTS_OF_FACULTY_BY_TOTAL_MARK);
            if (preparedStatement != null) {
                preparedStatement.setInt(1, facultyId);
                resultSet = preparedStatement.executeQuery();
            }
            ArrayList<Integer> entrantsId = new ArrayList<>();
            while (resultSet != null && resultSet.next()) {
                entrantsId.add(resultSet.getInt(EntrantColumn.ID_COLUMN.getColumnValue()));
            }
            return entrantsId;
        } catch (SQLException e) {
            throw new DAOException("Problem with sorting entrants for faculty by total mark! Faculty ID: " + facultyId, e);
        } finally {
            DaoHelper.closeResource(resultSet, connection, preparedStatement);
        }
    }
}