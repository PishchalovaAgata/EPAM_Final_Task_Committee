package com.pishchalova.committee.pool;


import com.mysql.jdbc.Driver;
import com.pishchalova.committee.exception.PoolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public enum ConnectionPool {
    INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger(ConnectionPool.class);

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("resource.database.db");
    private static final String URL = BUNDLE.getString("db.url");
    private static final String PASSWORD = BUNDLE.getString("db.password");
    private static final String USERNAME = BUNDLE.getString("db.username");

    private static final int INITIAL_POOL_SIZE = Integer.parseInt(BUNDLE.getString("db.poolsize"));
    private static final int MAX_SIZE = Integer.parseInt(BUNDLE.getString("db.maxsize"));
    private int poolSize;
    private final static int EXPANSION_CONSTANT = Integer.parseInt(BUNDLE.getString("db.expander"));
    private AtomicBoolean initialized = new AtomicBoolean(false);

    private LinkedBlockingQueue<ProxyConnection> availableConnections = new LinkedBlockingQueue<>();
    private ArrayDeque<ProxyConnection> unavailableConnections = new ArrayDeque<>();

    public int getAmountAvailableConnection() {
        return availableConnections.size();
    }


    public ProxyConnection takeConnection() {
        //if (!availableConnections.isEmpty()) {
            try {
                ProxyConnection connection;
                connection = availableConnections.take();

                unavailableConnections.push(connection);
                if (poolSize * 0.75 <= unavailableConnections.size()) {

                    expandAvailable();
                }
                return connection;
            } catch (InterruptedException e) {
                e.printStackTrace();//poolException&& todo
            }
       // }
        return null;
    }

    public void returnConnection(ProxyConnection connection) throws PoolException {
        try {
            if (!connection.getAutoCommit()) {
                connection.setAutoCommit(true);
            }
            unavailableConnections.remove(connection);
            availableConnections.put(connection);
        } catch (SQLException | InterruptedException e) {
            throw new PoolException("Exception returning connection", e);
        }
    }

    public final void init() throws PoolException {
        poolSize = INITIAL_POOL_SIZE;
        if (initialized.get()) {
            return;
        }
        try {
            DriverManager.registerDriver(new Driver());
            DriverManager.registerDriver(DriverManager.getDriver(URL));
            for (int i = 0; i < this.poolSize; i++) {
                ProxyConnection connection = new ProxyConnection(DriverManager.getConnection(URL, USERNAME, PASSWORD));
                availableConnections.add(connection);
            }
        } catch (SQLException e) {
            LOGGER.fatal("Problem with init ConnectionPool!!!", e);
        }
        initialized.set(true);
    }

    public final void closeConnectionPool() throws PoolException {
        try {
            ProxyConnection connection;
            while ((connection = unavailableConnections.poll()) != null) {
                connection.closeConnection();
                System.out.println("CLOse Connection!!");
            }
            closeConnectionQueue(availableConnections);
        } catch (SQLException e) {
            throw new PoolException("Couldn't close connection queue", e);
        } catch (InterruptedException e) {
            LOGGER.fatal("Problem with closing ConnectionPool!", e);
        }
        deregisterDrivers();
    }

    private void deregisterDrivers() {
        while (DriverManager.getDrivers().hasMoreElements()) {
            deregister(DriverManager.getDrivers().nextElement());
        }
    }

    private void deregister(java.sql.Driver driver) {
        try {
            DriverManager.deregisterDriver(driver);
        } catch (SQLException e) {

            LOGGER.fatal("Problem with deregister drivers!", e);
        }
    }

    private void closeConnectionQueue(BlockingQueue<ProxyConnection> queue) throws SQLException, InterruptedException {
        ProxyConnection connection;
        while ((connection = queue.poll()) != null) {
            if (!connection.getAutoCommit()) {
                connection.commit();
            }
            connection.closeConnection();
        }
    }

    private void expandAvailable() {
        if (poolSize < MAX_SIZE) {
            for (int i = 0; i < EXPANSION_CONSTANT; i++) {
                try {
                    ProxyConnection connection = new ProxyConnection(DriverManager.getConnection(URL, USERNAME, PASSWORD));
                    availableConnections.add(connection);
                    poolSize++;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}