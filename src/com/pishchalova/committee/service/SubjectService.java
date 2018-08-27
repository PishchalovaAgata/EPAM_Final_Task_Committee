package com.pishchalova.committee.service;

import com.pishchalova.committee.dao.impl.SubjectDaoImpl;
import com.pishchalova.committee.entity.Faculty;
import com.pishchalova.committee.entity.Subject;
import com.pishchalova.committee.exception.DAOException;
import com.pishchalova.committee.exception.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;


public class SubjectService {
    private static SubjectDaoImpl subjectDao = new SubjectDaoImpl();
    private static final Logger LOGGER = LogManager.getLogger(SubjectService.class);

    public final ArrayList<Subject> getAllSubjects() throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to get all subjects!");
        try {
            return subjectDao.findAll();
        } catch (DAOException e) {
            throw new ServiceException("Problem with getting all subjects! (Subject Service)", e);
        }
    }

    public final boolean addSubject(Subject subject) throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to add new Subject. " + subject.toString());
        try {
            return subjectDao.create(subject);
        } catch (DAOException e) {
            throw new ServiceException("Problem with adding new Subject in system!(Subject Service) " + subject.toString(), e);
        }
    }

    public final boolean updateSubject(Subject subject) throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to update Subject. " + subject.toString());
        try {
            return subjectDao.update(subject);
        } catch (DAOException e) {
            throw new ServiceException("Problem with updating Subject in system! (Subject Service) With " + subject.toString(), e);
        }
    }

    public final boolean deleteSubject(Integer subjectId) throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to delete Subject. " + subjectId.toString());
        try {
            FacultyService facultyService = new FacultyService();
            ArrayList<Faculty> facultyArrayList = facultyService.filterFacultiesBySubjects(subjectId, false);
            if (facultyArrayList.isEmpty()) {
                return subjectDao.delete(subjectId);
            }
            return false;
        } catch (DAOException e) {
            throw new ServiceException("Problem with deleting Subject in system!(Subject Service) " + subjectId.toString(), e);
        }
    }

    public final Subject filterSubjectsByName(String name) throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to filter subjects by name. Name: " + name);
        try {
            return subjectDao.filterSubjectsByName(name);
        } catch (DAOException e) {
            throw new ServiceException("Problem with filtering subjects by name!(Subject Service) name: " + name, e);
        }
    }

    public final Subject filterSubjectsById(Integer id) throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to filter subjects by id. Id: " + id);
        try {
            return subjectDao.findEntityById(id);
        } catch (DAOException e) {
            throw new ServiceException("Problem with filtering subjects by id! (Subject Service) id: " + id, e);
        }
    }
}
