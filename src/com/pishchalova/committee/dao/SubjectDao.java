package com.pishchalova.committee.dao;

import com.pishchalova.committee.entity.Subject;
import com.pishchalova.committee.exception.DAOException;

public interface SubjectDao extends AbstractDao<Integer, Subject> {
    Subject filterSubjectsByName(String name) throws DAOException;
}
