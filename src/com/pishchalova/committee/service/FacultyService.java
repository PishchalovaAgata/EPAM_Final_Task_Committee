package com.pishchalova.committee.service;

import com.pishchalova.committee.dao.impl.FacultyDaoImpl;
import com.pishchalova.committee.entity.Faculty;
import com.pishchalova.committee.exception.DAOException;
import com.pishchalova.committee.exception.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class FacultyService {
    private static FacultyDaoImpl facultyDao = new FacultyDaoImpl();

    private static final Logger LOGGER = LogManager.getLogger(FacultyService.class);

    public final ArrayList<Faculty> filterFacultiesBySubjects(Integer fromPosition, boolean descOrder, Integer... subjectsId) throws ServiceException {
        LOGGER.log(Level.INFO, "Filtering faculties by subjects with ids " + Arrays.toString(subjectsId));
        try {
            ArrayList<Integer> arrayListSubjectsId = new ArrayList<>();
            Collections.addAll(arrayListSubjectsId, subjectsId);
            return facultyDao.filterFacultiesBySubjects(arrayListSubjectsId, fromPosition, descOrder);
        } catch (DAOException e) {
            throw new ServiceException("Problem with filtering subjects for faculty! (Faculty Service) Id: " + Arrays.toString(subjectsId), e);
        }
    }

    public final Integer getAmountOfFilteredFaculties(Integer... subjectsId) throws ServiceException {

        LOGGER.log(Level.INFO, "Getting amount of Filtering faculties by subjects with ids " + Arrays.toString(subjectsId));
        try {
            ArrayList<Integer> arrayListSubjectsId = new ArrayList<>();
            Collections.addAll(arrayListSubjectsId, subjectsId);
            return facultyDao.amountOfFilteredFaculties(arrayListSubjectsId);
        } catch (DAOException e) {
            throw new ServiceException("Problem with Getting amount of filtering subjects for faculty! (Faculty Service) Id: " + Arrays.toString(subjectsId), e);
        }
    }

    public final ArrayList<Integer> filterFacultiesByDate(Long dateInMilliSec) throws ServiceException {
        LOGGER.log(Level.INFO, "Filtering faculties by date! With date more than " + dateInMilliSec);
        try {
            return facultyDao.findFacultiesByDate(dateInMilliSec);
        } catch (DAOException e) {
            throw new ServiceException("Problem with Filtering faculties by date! (Faculty Service) Date: " + dateInMilliSec, e);
        }
    }


    public final Faculty findFacultyByName(String name) throws ServiceException {
        LOGGER.log(Level.INFO, "Finding faculty by name! Name: " + name);
        try {
            return facultyDao.filterFacultyByName(name);
        } catch (DAOException e) {
            throw new ServiceException("Problem with filtering faculties by name! (Faculty Service) name: " + name, e);
        }
    }


    public final Faculty findFacultyById(Integer facultyId) throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to filter faculties by id. Id: " + facultyId);
        try {
            return facultyDao.findEntityById(facultyId);
        } catch (DAOException e) {
            throw new ServiceException("Problem with filtering faculties by available feature! (Faculty Service) available feature: " + facultyId, e);
        }
    }


    public final boolean addFaculty(Faculty faculty) throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to add new Faculty. " + faculty.toString());
        try {
            return facultyDao.create(faculty);
        } catch (DAOException e) {
            throw new ServiceException("Problem with adding new Faculty in system!(Faculty Service) " + faculty.toString(), e);
        }
    }

    public final boolean updateFaculty(Faculty faculty) throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to update Faculty. " + faculty.toString());
        try {
            return facultyDao.update(faculty);
        } catch (DAOException e) {
            throw new ServiceException("Problem with updating Faculty in system!(Faculty Service) With " + faculty.toString(), e);
        }
    }

    public final boolean deleteFaculty(Integer facultyId) throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to delete Faculty. Id:" + facultyId.toString());
        try {
            return facultyDao.delete(facultyId);
        } catch (DAOException e) {
            throw new ServiceException("Problem with deleting Faculty in system! (Faculty Service) Id:  " + facultyId.toString(), e);
        }
    }

    public final Integer countAmountOfAllFaculties() throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to count amount of Faculty. ");
        try {
            return facultyDao.countAmountOfFaculties();
        } catch (DAOException e) {
            throw new ServiceException("Problem with counting amount of all faculties! (Faculty Service)", e);
        }
    }
}