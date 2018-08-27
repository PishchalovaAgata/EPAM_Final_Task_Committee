package com.pishchalova.committee.dao;

import com.pishchalova.committee.entity.Entity;
import com.pishchalova.committee.exception.DAOException;

import java.util.List;


public interface AbstractDao<K, T extends Entity> {
    public List<T> findAll() throws DAOException;

    public T findEntityById(K id) throws DAOException;

    public boolean delete(K id) throws DAOException;

    public boolean create(T entity) throws DAOException;

    public boolean update(T entity) throws DAOException;
}
