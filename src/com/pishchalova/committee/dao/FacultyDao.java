package com.pishchalova.committee.dao;

import com.pishchalova.committee.entity.Faculty;
import com.pishchalova.committee.exception.DAOException;

import java.util.ArrayList;


public interface FacultyDao extends AbstractDao<Integer, Faculty> {
    Faculty filterFacultyByName(String value) throws DAOException;

    ArrayList<Faculty> filterFacultiesBySubjects(ArrayList<Integer> listSubjects, Integer fromPosition, boolean descOrder) throws DAOException;

    Integer countAmountOfFaculties() throws DAOException;

    ArrayList<Integer> findFacultiesByDate(Long dateInMilliSec) throws DAOException;

}
