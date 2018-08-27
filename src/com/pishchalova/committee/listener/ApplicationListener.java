package com.pishchalova.committee.listener;

import com.pishchalova.committee.exception.PoolException;
import com.pishchalova.committee.pool.ConnectionPool;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.HashMap;

@WebListener()
public class ApplicationListener implements ServletContextListener {
    private static final Logger LOGGER = LogManager.getRootLogger();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            ConnectionPool.INSTANCE.init();
            LOGGER.log(Level.INFO, "Connection pool initialized successfully");
        } catch (PoolException e) {
            LOGGER.fatal("Problem with initializing ConnectionPool!");
        }
    //servletContextEvent.getServletContext().setAttribute("FacultySchedule", new HashMap<Integer, Long>());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            ConnectionPool.INSTANCE.closeConnectionPool();
        } catch (PoolException e) {
            LOGGER.fatal("Problem with destroying ConnectionPool!");
        }
        LOGGER.log(Level.INFO, "Connection pool destroyed successfully");
        servletContextEvent.getServletContext().removeAttribute("FacultySchedule");
    }
}
