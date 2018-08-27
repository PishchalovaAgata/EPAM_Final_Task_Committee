package com.pishchalova.committee.util.helper;

import com.pishchalova.committee.exception.DAOException;
import com.pishchalova.committee.pool.ConnectionPool;
import com.pishchalova.committee.pool.ProxyConnection;

import java.sql.*;

public class DaoHelper {
    private static final ConnectionPool CONNECTION_POOL = ConnectionPool.INSTANCE;

    public static void closeResource(ResultSet rs, ProxyConnection proxyConnection, Statement st) throws DAOException {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new DAOException("Exception while closing result set", e);
            }
        }
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                throw new DAOException("Exception while closing statement", e);
            }
        }
        if (proxyConnection != null) {
            try {
                proxyConnection.close();
            } catch (SQLException e) {
                throw new DAOException("Exception while closing proxyConnection", e);
            }
        }
    }

    public static void closeResource(ProxyConnection con, Statement st) throws DAOException {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                throw new DAOException("Exception while closing statement", e);
            }
        }
        try {
            CONNECTION_POOL.returnConnection(con);
        } catch (Exception e) {
            throw new DAOException("Exception while returning connection", e);
        }
    }
}