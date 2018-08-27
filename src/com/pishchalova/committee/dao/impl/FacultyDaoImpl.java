package com.pishchalova.committee.dao.impl;

import com.pishchalova.committee.util.helper.*;
import com.pishchalova.committee.dao.FacultyDao;
import com.pishchalova.committee.entity.Faculty;
import com.pishchalova.committee.exception.DAOException;
import com.pishchalova.committee.exception.WrongDateFormattingException;
import com.pishchalova.committee.pool.ConnectionPool;
import com.pishchalova.committee.pool.ProxyConnection;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;


public class FacultyDaoImpl implements FacultyDao {
    private static final Logger LOGGER = LogManager.getLogger(FacultyDaoImpl.class);

    private static final String SELECT_FACULTY_BY_ID = "SELECT committee_db.show_faculties.id, committee_db.show_faculties.name, committee_db.show_faculties.entry_plan," +
            " committee_db.show_faculties.amount_entrant, committee_db.show_faculties.end_date_receiving," +
            " committee_db.show_faculties.is_unavailable, committee_db.show_faculties.enrollment_is_over, " +
            "committee_db.show_faculties.subjects FROM show_faculties HAVING show_faculties.id = ?";
    private static final String SELECT_FACULTIES_BY_DATE = "Select committee_db.faculty.id, committee_db.faculty.end_date_receiving From committee_db.faculty " +
            "Where committee_db.faculty.enrollment_is_over=0 and committee_db.faculty.end_date_receiving <= ? AND committee_db.faculty.id > 0";
    private static final String SELECT_ALL_FROM_FACULTY = "SELECT committee_db.show_faculties.id, committee_db.show_faculties.name, committee_db.show_faculties.entry_plan, " +
            "committee_db.show_faculties.amount_entrant, committee_db.show_faculties.end_date_receiving," +
            " committee_db.show_faculties.is_unavailable, committee_db.show_faculties.enrollment_is_over,  " +
            "committee_db.show_faculties.subjects FROM show_faculties WHERE show_faculties.is_unavailable = 0";
    private static final String DELETE_FACULTY_BY_ID = "UPDATE committee_db.faculty SET committee_db.faculty.is_unavailable = 1 WHERE committee_db.faculty.id = ?";
    private static final String INSERT_FACULTY = "INSERT INTO committee_db.faculty(committee_db.faculty.name, committee_db.faculty.end_date_receiving, \n" +
            "committee_db.faculty.entry_plan,committee_db.faculty.amount_entrant," +
            " committee_db.faculty.is_unavailable, committee_db.faculty.enrollment_is_over) VALUES (?,?,?,?,?,?)";
    private static final String UPDATE_FACULTY = "UPDATE committee_db.faculty SET committee_db.faculty.name = ?, committee_db.faculty.end_date_receiving = ?," +
            " committee_db.faculty.entry_plan = ?,committee_db.faculty.amount_entrant = ?," +
            " committee_db.faculty.enrollment_is_over = ? WHERE committee_db.faculty.id = ?";


    private static final String FILTER_FACULTIES_BY_NAME = "SELECT committee_db.faculty.id, committee_db.faculty.name, committee_db.faculty.entry_plan, " +
            "committee_db.faculty.amount_entrant, committee_db.faculty.end_date_receiving, committee_db.faculty.is_unavailable," +
            " committee_db.faculty.enrollment_is_over from committee_db.faculty WHERE committee_db.faculty.name = ?";
    private static final String DELETE_SUBJECT_BY_FACULTY_ID = "DELETE FROM committee_db.faculty_has_subject WHERE committee_db.faculty_has_subject.faculty_id = ?";
    private static final String FILTER_FACULTIES_BY_SUBJECT = " LOCATE(?, show_faculties.subjects)";
    private static final String LIMIT_AMOUNT_FACULTIES = "LIMIT ? OFFSET ?";
    private static final String INSERT_SUBJECTS_FOR_FACULTY = "INSERT INTO committee_db.faculty_has_subject(committee_db.faculty_has_subject.faculty_id," +
            "committee_db.faculty_has_subject.subject_id) VALUES (?,?)";
    private static final String AND = " AND ";
    private static final String ORDER = " ORDER BY show_faculties.name ";
    private static final String DESC = " DESC ";
    private static final String ASC = " ASC ";
    private static final String COUNT_AMOUNT_OF_FACULTIES = " SELECT COUNT(*) AS amount_of_faculties FROM show_faculties;";


    private static final Integer AMOUNT_FACULTIES_ON_ONE_PAGE = 5;
    private static final Integer AMOUNT_OF_SUBJECTS_FOR_ONE_FACULTY = 3;

    public enum FacultyColumn {
        ID_COLUMN("id"),
        ENTRY_PLAN_COLUMN("entry_plan"),
        AMOUNT_ENTRANT_COLUMN("amount_entrant"),
        NAME_COLUMN("name"),
        DATE_COLUMN("end_date_receiving"),
        LOGO_COLUMN("logo"),
        IS_UNAVAILABLE_COLUMN("is_unavailable"),
        ENROLLMENT_IS_OVER("enrollment_is_over"),
        SUBJECTS_COLUMN("subjects"),
        AMOUNT_OF_FACULTIES_COLUMN("amount_of_faculties");

        private String columnValue;

        FacultyColumn(String columnValue) {
            this.columnValue = columnValue;
        }

        public String getColumnValue() {
            return columnValue;
        }

        public void setColumnValue(String columnValue) {
            this.columnValue = columnValue;
        }

        public static FacultyColumn hasColumnInFacultyColumn(String value) {
            for (FacultyColumn facultyColumn : FacultyColumn.values()) {
                if (facultyColumn.getColumnValue().equals(value)) {
                    return facultyColumn;
                }
            }
            return null;
        }
    }

    @Override
    public ArrayList<Faculty> findAll() throws DAOException {
        LOGGER.log(Level.INFO, "Selecting all faculties!");
        ProxyConnection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            statement = Objects.requireNonNull(connection).createStatement();
            resultSet = statement.executeQuery(SELECT_ALL_FROM_FACULTY);
            ArrayList<Faculty> facultyList = new ArrayList<>();
            while (resultSet.next()) {
                ArrayList<Integer> arrayListSubjects = Arrays.stream(resultSet.getString(FacultyColumn.SUBJECTS_COLUMN.getColumnValue())
                        .split(", ")).map(Integer::parseInt).collect(Collectors.toCollection(ArrayList::new));
                facultyList.add(new Faculty(resultSet.getInt(FacultyColumn.ID_COLUMN.getColumnValue()),
                        resultSet.getString(FacultyColumn.NAME_COLUMN.getColumnValue()),
                        resultSet.getLong(FacultyColumn.DATE_COLUMN.getColumnValue()),
                        resultSet.getInt(FacultyColumn.ENTRY_PLAN_COLUMN.getColumnValue()),
                        resultSet.getInt(FacultyColumn.AMOUNT_ENTRANT_COLUMN.getColumnValue()),
                        resultSet.getBoolean(FacultyColumn.IS_UNAVAILABLE_COLUMN.getColumnValue()),
                        resultSet.getBoolean(FacultyColumn.ENROLLMENT_IS_OVER.getColumnValue()),
                        arrayListSubjects));
            }
            return facultyList;
        } catch (SQLException e) {
            throw new DAOException("There is a problem while selecting all faculties!", e);
        } finally {
            DaoHelper.closeResource(resultSet, connection, statement);
        }
    }

    @Override
    public Faculty findEntityById(Integer id) throws DAOException {
        LOGGER.log(Level.INFO, "Selecting faculty by id. Id: " + id);
        ResultSet resultSet = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(SELECT_FACULTY_BY_ID);

            if (preparedStatement != null) {
                preparedStatement.setInt(1, id);
                resultSet = preparedStatement.executeQuery();
            }
            if (resultSet != null && resultSet.next()) {
                ArrayList<Integer> arrayListSubjects = Arrays.stream(resultSet.getString(FacultyColumn.SUBJECTS_COLUMN.getColumnValue())
                        .split(", ")).map(Integer::parseInt).collect(Collectors.toCollection(ArrayList::new));
                return new Faculty(resultSet.getInt(FacultyColumn.ID_COLUMN.getColumnValue()),
                        resultSet.getString(FacultyColumn.NAME_COLUMN.getColumnValue()),
                        resultSet.getLong(FacultyColumn.DATE_COLUMN.getColumnValue()),
                        resultSet.getInt(FacultyColumn.ENTRY_PLAN_COLUMN.getColumnValue()),
                        resultSet.getInt(FacultyColumn.AMOUNT_ENTRANT_COLUMN.getColumnValue()),
                        resultSet.getBoolean(FacultyColumn.IS_UNAVAILABLE_COLUMN.getColumnValue()),
                        resultSet.getBoolean(FacultyColumn.ENROLLMENT_IS_OVER.getColumnValue()),
                        arrayListSubjects);
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException("There is a problem while finding faculty by id! Id: " + id, e);
        } finally {
            DaoHelper.closeResource(resultSet, connection, preparedStatement);
        }
    }

    @Override
    public boolean create(Faculty entity) throws DAOException {
        LOGGER.log(Level.DEBUG, "Creating new Faculty. " + entity.toString());
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        boolean flag = false;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            if (filterFacultyByName(entity.getFacultyName()) == null) {
                preparedStatement = Objects.requireNonNull(connection).prepareStatement(INSERT_FACULTY);
                if (preparedStatement != null) {
                    preparedStatement.setString(1, entity.getFacultyName());
                    preparedStatement.setLong(2, entity.getEndDateReceivingAsLong());
                    preparedStatement.setInt(3, entity.getEntryPlan());
                    preparedStatement.setInt(4, entity.getAmountEntrant());
                    preparedStatement.setBoolean(5, entity.isUnavailable());
                    preparedStatement.setBoolean(6, entity.isEnrollmentIsOver());
                    flag = preparedStatement.executeUpdate() > 0;
                    entity.setId(filterFacultyByName(entity.getFacultyName()).getId());
                    flag &= createSubjectsForFacultyId(entity);
                }
            }
            return flag;
        } catch (SQLException e) {
            throw new DAOException("Problem with creating Faculty! ", e);
        } catch (WrongDateFormattingException e) {
            throw new DAOException("Problem with date formatting while creating faculty!", e);
        } finally {
            DaoHelper.closeResource(connection, preparedStatement);
        }
    }

    @Override
    public boolean delete(Integer id) throws DAOException {
        LOGGER.log(Level.INFO, "Deleting faculty by id. Id: " + id);
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(DELETE_FACULTY_BY_ID);
            if (preparedStatement != null) {
                preparedStatement.setInt(1, id);
                return preparedStatement.executeUpdate() > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new DAOException("Problem with deleting Faculty! ", e);
        } finally {
            DaoHelper.closeResource(connection, preparedStatement);
        }
    }


    @Override
    public boolean update(Faculty entity) throws DAOException {
        LOGGER.log(Level.DEBUG, "Updating faculty. " + entity.toString());
        ProxyConnection connection;
        PreparedStatement preparedStatement;
        Boolean flag = false;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(UPDATE_FACULTY);
            if (preparedStatement != null) {
                preparedStatement.setInt(6, entity.getId());
                preparedStatement.setString(1, entity.getFacultyName());
                preparedStatement.setLong(2, entity.getEndDateReceivingAsLong());
                preparedStatement.setInt(3, entity.getEntryPlan());
                preparedStatement.setInt(4, entity.getAmountEntrant());
                preparedStatement.setBoolean(5, entity.isEnrollmentIsOver());
                flag = preparedStatement.executeUpdate() > 0;
                entity.setId(filterFacultyByName(entity.getFacultyName()).getId());
                flag &= updateSubjectsForFacultyId(entity);

            }
        } catch (SQLException e) {
            throw new DAOException("Problem with Updating Faculty! ", e);
        } catch (WrongDateFormattingException e) {
            throw new DAOException("Problem with date formatting while updating faculty!", e);
        }
        return flag;
    }

    private boolean createSubjectsForFacultyId(Faculty faculty) throws DAOException {
        LOGGER.log(Level.DEBUG, "Creating subjects for faculty. Id: " + faculty.getId());
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(INSERT_SUBJECTS_FOR_FACULTY);
            for (int i = 0; i < AMOUNT_OF_SUBJECTS_FOR_ONE_FACULTY; i++) {
                if (preparedStatement != null) {
                    preparedStatement.setInt(1, faculty.getId());
                    preparedStatement.setInt(2, faculty.getSubjectsId().get(i));
                    if (!(preparedStatement.executeUpdate() > 0)) {//todo:what&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
                        return false;
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            throw new DAOException("Problem with creation Subjects for faculty! Id: " + faculty.getId(), e);
        } finally {
            DaoHelper.closeResource(connection, preparedStatement);
        }

    }

    private boolean deleteSubjectsForFacultyId(Integer facultyId) throws DAOException {
        LOGGER.log(Level.INFO, "Deleting subjects for faculty id. Id: " + facultyId);
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(DELETE_SUBJECT_BY_FACULTY_ID);
            if (preparedStatement != null) {
                preparedStatement.setInt(1, facultyId);
                return preparedStatement.executeUpdate() > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new DAOException("Problem with deleting subjects for faculty! id " + facultyId, e);
        } finally {
            DaoHelper.closeResource(connection, preparedStatement);
        }
    }

    private boolean updateSubjectsForFacultyId(Faculty newFaculty) throws DAOException {
        LOGGER.log(Level.DEBUG, "Updating subjects for faculty id. Id: " + newFaculty.getId());
        return deleteSubjectsForFacultyId(newFaculty.getId()) && createSubjectsForFacultyId(newFaculty);
    }

    public Integer amountOfFilteredFaculties(ArrayList<Integer> listSubjects) throws DAOException {
        StringBuilder query = new StringBuilder("SELECT COUNT(*) AS amount_of_faculties FROM ");
        if (listSubjects.size() > AMOUNT_OF_SUBJECTS_FOR_ONE_FACULTY) {
            throw new DAOException("Size Of listSubjects is Incorrect!!!");
        } else if (!listSubjects.isEmpty()) {
            query.append("(" + SELECT_ALL_FROM_FACULTY);
            for (int i = 1; i <= listSubjects.size(); i++) {
                query.append(AND);
                query.append(FILTER_FACULTIES_BY_SUBJECT);
            }
            query.append(") AS X");
        } else {
            return countAmountOfFaculties();
        }
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(query.toString());
            if (preparedStatement != null) {
                for (int i = 0; i < listSubjects.size(); i++) {
                    preparedStatement.setInt(i + 1, listSubjects.get(i));
                }
                resultSet = preparedStatement.executeQuery();
            }
            if (resultSet != null && resultSet.next()) {
                return resultSet.getInt(FacultyColumn.AMOUNT_OF_FACULTIES_COLUMN.getColumnValue());
            }
            return 0;
        } catch (SQLException e) {
            throw new DAOException("There is a problem while selecting all faculties!", e);
        } finally {
            DaoHelper.closeResource(resultSet, connection, preparedStatement);
        }

    }


    @Override
    public Integer countAmountOfFaculties() throws DAOException {
        LOGGER.log(Level.INFO, "Selecting amount Of Faculties!!!");
        ProxyConnection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            statement = Objects.requireNonNull(connection).createStatement();
            resultSet = statement.executeQuery(COUNT_AMOUNT_OF_FACULTIES);
            if (resultSet.next()) {
                return resultSet.getInt(FacultyColumn.AMOUNT_OF_FACULTIES_COLUMN.getColumnValue());
            }
            return 0;
        } catch (SQLException e) {
            throw new DAOException("There is a problem while selecting all faculties!", e);
        } finally {
            DaoHelper.closeResource(resultSet, connection, statement);
        }
    }


    @Override
    public ArrayList<Faculty> filterFacultiesBySubjects(ArrayList<Integer> listSubjects, Integer fromPosition, boolean descOrder) throws DAOException {
        LOGGER.log(Level.INFO, "Filtering faculties by subjects! With ids " + listSubjects + " from position "
                + fromPosition + " order: " + descOrder);
        StringBuilder query = new StringBuilder(SELECT_ALL_FROM_FACULTY);
        if (listSubjects.size() > AMOUNT_OF_SUBJECTS_FOR_ONE_FACULTY) {
            throw new DAOException("Size Of listSubjects is Incorrect!!!");
        } else if (!listSubjects.isEmpty()) {
            for (int i = 1; i <= listSubjects.size(); i++) {
                query.append(AND);
                query.append(FILTER_FACULTIES_BY_SUBJECT);
            }
        }
        query.append(ORDER);
        if (!descOrder) {
            query.append(DESC);
        } else {
            query.append(ASC);
        }
        query.append(LIMIT_AMOUNT_FACULTIES);
        System.out.println(query.toString());
        ResultSet resultSet = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(String.valueOf(query));
            if (preparedStatement != null) {
                for (int i = 0; i < listSubjects.size(); i++) {
                    preparedStatement.setInt(i + 1, listSubjects.get(i));
                }
                preparedStatement.setInt(listSubjects.size() + 1, AMOUNT_FACULTIES_ON_ONE_PAGE);
                preparedStatement.setInt(listSubjects.size() + 2, fromPosition);
                resultSet = preparedStatement.executeQuery();
            }

            ArrayList<Faculty> facultyList = new ArrayList<>();
            if (resultSet != null) {
                while (resultSet.next()) {
                    ArrayList<Integer> arrayListSubjects = Arrays.stream(resultSet
                            .getString(FacultyColumn.SUBJECTS_COLUMN.getColumnValue()).split(", "))
                            .map(Integer::parseInt).collect(Collectors.toCollection(ArrayList::new));
                    facultyList.add(new Faculty(resultSet.getInt(FacultyColumn.ID_COLUMN.getColumnValue()),
                            resultSet.getString(FacultyColumn.NAME_COLUMN.getColumnValue()),
                            resultSet.getLong(FacultyColumn.DATE_COLUMN.getColumnValue()),
                            resultSet.getInt(FacultyColumn.ENTRY_PLAN_COLUMN.getColumnValue()),
                            resultSet.getInt(FacultyColumn.AMOUNT_ENTRANT_COLUMN.getColumnValue()),
                            resultSet.getBoolean(FacultyColumn.IS_UNAVAILABLE_COLUMN.getColumnValue()),
                            resultSet.getBoolean(FacultyColumn.ENROLLMENT_IS_OVER.getColumnValue()),
                            arrayListSubjects));
                }
            }
            return facultyList;
        } catch (SQLException e) {
            throw new DAOException("There is a problem while filtering faculties by subjects!", e);
        } finally {
            DaoHelper.closeResource(resultSet, connection, preparedStatement);
        }
    }

    @Override
    public Faculty filterFacultyByName(String value) throws DAOException {
        LOGGER.log(Level.DEBUG, "Finding faculty by name, where name: " + value);
        ResultSet resultSet = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(FILTER_FACULTIES_BY_NAME);
            if (preparedStatement != null) {
                preparedStatement.setString(1, value);
                resultSet = preparedStatement.executeQuery();
            }
            if (resultSet != null && resultSet.next()) {
                return new Faculty(resultSet.getInt(FacultyColumn.ID_COLUMN.getColumnValue()),
                        resultSet.getString(FacultyColumn.NAME_COLUMN.getColumnValue()),
                        resultSet.getLong(FacultyColumn.DATE_COLUMN.getColumnValue()),
                        resultSet.getInt(FacultyColumn.ENTRY_PLAN_COLUMN.getColumnValue()));
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException("There is a problem while filtering faculties by name!", e);
        } finally {
            DaoHelper.closeResource(resultSet, connection, preparedStatement);
        }
    }


    @Override
    public ArrayList<Integer> findFacultiesByDate(Long dateInMilliSec) throws DAOException {
        LOGGER.log(Level.INFO, "Selecting faculties by date!");
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.takeConnection();
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(SELECT_FACULTIES_BY_DATE);
            if (preparedStatement != null) {
                preparedStatement.setLong(1, dateInMilliSec);
                resultSet = preparedStatement.executeQuery();
            }
            ArrayList<Integer> facultyList = new ArrayList<>();
            while (resultSet != null && resultSet.next()) {
                facultyList.add(resultSet.getInt(FacultyColumn.ID_COLUMN.getColumnValue()));
            }
            return facultyList;
        } catch (SQLException e) {
            throw new DAOException("There is a problem while selecting faculties by date!", e);
        } finally {
            DaoHelper.closeResource(resultSet, connection, preparedStatement);
        }
    }
}