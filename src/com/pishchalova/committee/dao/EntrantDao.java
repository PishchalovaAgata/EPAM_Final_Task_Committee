package com.pishchalova.committee.dao;

import com.pishchalova.committee.entity.Entrant;
import com.pishchalova.committee.exception.DAOException;

import java.util.ArrayList;


public interface EntrantDao extends AbstractDao<Integer, Entrant> {
    Entrant filterEntrantsByUserId(Integer value) throws DAOException;

    Integer getTotalMarkForEntrant(Integer entrantId) throws DAOException;

    ArrayList<Integer> sortEntrantsOfFaculty(Integer facultyId) throws DAOException;

    ArrayList<Entrant> filterEntityByFacultyId(Integer facultyId) throws DAOException;
}
