package com.pishchalova.committee.service;

import com.pishchalova.committee.dao.impl.EntrantDaoImpl;
import com.pishchalova.committee.entity.Entrant;
import com.pishchalova.committee.exception.DAOException;
import com.pishchalova.committee.exception.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class EntrantService {
    private static EntrantDaoImpl entrantDao = new EntrantDaoImpl();
    private static final Logger LOGGER = LogManager.getLogger(EntrantService.class);

    public final List<Entrant> getAllEntrants() throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to get all entrants!");
        try {
            return entrantDao.findAll();
        } catch (DAOException e) {
            throw new ServiceException("Problem with getting all entrants! (Entrant Service)", e);
        }
    }

    public final Entrant findEntrantById(Integer id) throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to get entrant by Id! Id: " + id);
        try {
            return entrantDao.findEntityById(id);
        } catch (DAOException e) {
            throw new ServiceException("Problem with getting entrant by Id!(Entrant Service) Id: " + id, e);
        }
    }

    public final List<Entrant> filterEntrantsByFaculty(Integer facultyId) throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to filter entrants by faculty. Id: " + facultyId);
        try {
            return entrantDao.filterEntityByFacultyId(facultyId);
        } catch (DAOException e) {
            throw new ServiceException("Problem with filtering entrants by facultyId! (Entrant Service) Id: " + facultyId.toString(), e);
        }
    }

    public final Entrant filterEntrantsByUserId(Integer userId) throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to filter entrants by user Id. Id: " + userId);
        try {
            return entrantDao.filterEntrantsByUserId(userId);
        } catch (DAOException e) {
            throw new ServiceException("Problem with filtering faculties by userId! (Entrant Service) user Id: " + userId, e);
        }
    }

    public final boolean addEntrant(Entrant entrant) throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to add new Entrant. " + entrant.toString());
        try {
            return entrantDao.create(entrant);
        } catch (DAOException e) {
            throw new ServiceException("Problem with adding new Entrant in system!(Entrant Service) " + entrant.toString(), e);
        }
    }

    public final boolean updateEntrant(Entrant entrant) throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to update Entrant. " + entrant.toString());
        try {
            return entrantDao.update(entrant);
        } catch (DAOException e) {
            throw new ServiceException("Problem with updating Entrant in system! (Entrant Service) With " + entrant.toString(), e);
        }
    }


    public final ArrayList<Integer> findSubjectsByEntrantId(Integer entrantId) throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to update Entrant. " + entrantId);
        try {
            return entrantDao.findSubjectsByEntrantId(entrantId);
        } catch (DAOException e) {
            throw new ServiceException("Problem with updating Entrant in system!  (Entrant Service) With " + entrantId, e);
        }
    }

    public final boolean deleteEntrant(Integer entrantId) throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to delete Entrant. Id: " + entrantId.toString());
        try {
            return entrantDao.delete(entrantId);
        } catch (DAOException e) {
            throw new ServiceException("Problem with deleting Faculty in system! (Entrant Service)" + entrantId.toString(), e);
        }
    }

    public final ArrayList<Integer> sortEntrantsByTotalMark(Integer facultyId) throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to sort Entrants by total mark. ");
        try {
            return entrantDao.sortEntrantsOfFaculty(facultyId);
        } catch (DAOException e) {
            throw new ServiceException("Problem with sorting Entrants by total mark in system!(Entrant Service) Faculty Id: " + facultyId, e);
        }
    }

    public final Integer getTotalMarkForEntrant(Integer entrantId) throws ServiceException {
        LOGGER.log(Level.INFO, "Trying to get total mark for entrant with id: " + entrantId);
        try {
            return entrantDao.getTotalMarkForEntrant(entrantId);
        } catch (DAOException e) {
            throw new ServiceException("Problem with getting total mark for entrant. (Entrant Service) Entrant Id: " + entrantId, e);
        }
    }
}